package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.ArrayList;
import java.util.HashMap;

import fr.vyfe.R;
import fr.vyfe.adapter.ObserverRecyclerAdapter;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.mapper.UserMapper;
import fr.vyfe.model.UserModel;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class CooperationFragment extends Fragment {
    private RecordVideoViewModel viewModel;
    private Switch liveRecordingSwitch;
    private RecyclerView mRecyclerViewObservers;
    private ObserverRecyclerAdapter mObserverAdapter;
    private TextView tvNumber;
    private AuthHelper mAuth;

    public static CooperationFragment newInstance() {
        return new CooperationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cooperation, container, false);
        mRecyclerViewObservers = view.findViewById(R.id.rv_observer);
        liveRecordingSwitch = view.findViewById(R.id.switch_live_conexion);
        tvNumber = view.findViewById(R.id.tv_observer);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView.LayoutManager tagLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewObservers.setLayoutManager(tagLayoutManager);

        liveRecordingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (liveRecordingSwitch.isChecked())
                    viewModel.isTagsActive();
                else viewModel.isTagsInactive();
                viewModel.addActiveTags();
            }
        });

        viewModel.getStep().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals(RecordVideoViewModel.STEP_RECODRING)) {
                    liveRecordingSwitch.setEnabled(false);
                }
            }
        });

        viewModel.getObserversSession().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> idsObservers) {
                tvNumber.setText(getString(R.string.participants) + String.valueOf(idsObservers.size()));
                getNamesObservers(idsObservers, new IdentityResponse() {
                    @Override
                    public void onSucess(ArrayList<String> namesObservers) {
                        mObserverAdapter = new ObserverRecyclerAdapter(namesObservers);
                        mRecyclerViewObservers.setAdapter(mObserverAdapter);
                    }
                });

                if (idsObservers.size() == 0) {
                    mObserverAdapter = new ObserverRecyclerAdapter(new ArrayList<String>());
                    mRecyclerViewObservers.setAdapter(mObserverAdapter);
                }


            }
        });
    }

    public void getNamesObservers(final ArrayList<String> idsObservers, final CooperationFragment.IdentityResponse listener) {

        final ArrayList namesObservers = new ArrayList();
        for (String id : idsObservers) {
            final String[] nameObserver = {""};
            AuthHelper.getInstance(getContext()).getUser(id).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
                @Override
                public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> result = task.getResult();
                        UserModel currentUser = (new UserMapper()).map(result);
                        String firstNameTagger = currentUser.getFirstname();
                        String lastnameTagger = currentUser.getLastName();
                        if (firstNameTagger == null) firstNameTagger = "";
                        if (lastnameTagger == null) lastnameTagger = "";
                        if (firstNameTagger == null & lastnameTagger == null)
                            firstNameTagger = getString(R.string.name_unknow);
                        nameObserver[0] = firstNameTagger + " " + lastnameTagger;

                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseFunctionsException) {
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            Object details = ffe.getDetails();
                            nameObserver[0] = getString(R.string.unknow);
                        }
                    }
                    namesObservers.add(nameObserver[0]);
                    listener.onSucess(namesObservers);
                    if (namesObservers.size() == idsObservers.size())
                        listener.onSucess(namesObservers);
                }
            });

            if (namesObservers.size() == idsObservers.size()) listener.onSucess(namesObservers);
        }
    }

    interface IdentityResponse {
        void onSucess(ArrayList<String> namesObservers);
    }
}
