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
import fr.vyfe.model.ObserverModel;
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
                else {
                    viewModel.isTagsInactive();
                    if(viewModel.getSession().getValue().getDuration()<=0){
                        viewModel.deleteObservers();
                    }
                }
                viewModel.activeCooperation();
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

        viewModel.getObserversSession().observe(this, new Observer<ArrayList<ObserverModel>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ObserverModel> idsObservers) {
                ArrayList<String > ids = new ArrayList<String>();
                if(idsObservers!=null) {
                    for (ObserverModel id : idsObservers) {
                        ids.add(id.getNameObserver());
                    }
                    mObserverAdapter = new ObserverRecyclerAdapter(ids);
                    mRecyclerViewObservers.setAdapter(mObserverAdapter);
                    tvNumber.setText(getString(R.string.participants) + String.valueOf(idsObservers.size()));
                    viewModel.getSession().getValue().setObservers(idsObservers);

                }else {
                    mObserverAdapter = new ObserverRecyclerAdapter(new ArrayList<String>());
                    mRecyclerViewObservers.setAdapter(mObserverAdapter);
                    tvNumber.setText(getString(R.string.participants) + String.valueOf(0));
                }

            }
        });
    }
}
