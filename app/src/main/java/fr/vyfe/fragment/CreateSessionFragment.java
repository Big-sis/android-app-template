package fr.vyfe.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.activity.CreateGridActivity;
import fr.vyfe.activity.CreateSessionActivity;
import fr.vyfe.activity.RecordActivity;
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.adapter.TagSetSpinnerAdapter;
import fr.vyfe.helper.KeyboardHelper;
import fr.vyfe.helper.ScrollHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.CreateSessionViewModel;


/**
 * This fragment handles Session configuration before recording
 */
public class CreateSessionFragment extends Fragment {
    private CreateSessionViewModel viewModel;
    private Button buttonBack;
    private Button buttonGo;
    private Button buttonGoMulti;
    private Button btnCreateGrid;
    private ConstraintLayout share;
    private RecyclerView recyclerViewImport;
    private Spinner spinner;
    private ScrollView scrollMain;
    private EditText mEtVideoTitle;
    private TagSetSpinnerAdapter tagSetsSpinnerAdapter;
    private SessionModel sessionRestart;

    public static CreateSessionFragment newInstance() {
        return new CreateSessionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionRestart = getActivity().getIntent().getParcelableExtra("restartSession");
        viewModel = ViewModelProviders.of(getActivity()).get(CreateSessionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_create_session, container, false);
        buttonBack = result.findViewById(R.id.button_back);
        buttonGo = result.findViewById(R.id.button_go);
        buttonGoMulti = result.findViewById(R.id.button_go_multi);
        btnCreateGrid = result.findViewById(R.id.btn_intent_create_grid);
        share = result.findViewById(R.id.layout_share);
        recyclerViewImport = result.findViewById(R.id.recycler_view_import);
        spinner = result.findViewById(R.id.spinner_session_infos);
        scrollMain = result.findViewById(R.id.scrool_main);
        mEtVideoTitle = result.findViewById(R.id.et_video_title2);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        if (sessionRestart != null) mEtVideoTitle.setText(sessionRestart.getName());

        if (((CreateSessionActivity) getActivity()).isMulti) buttonGo.setText(R.string.next);

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO startActivityForResult
                startActivity(new Intent(getContext(), CreateGridActivity.class));
            }
        });

        mEtVideoTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSessionName(s.toString());
            }
        });

        viewModel.getTagSets().observe(getActivity(), new Observer<ArrayList<TagSetModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TagSetModel> tagSetModels) {
                // Android spinner view is very tricky to bind to LiveDate objects.
                // This is the most efficient solution I've found to make it work,
                // it means rebuilding the sipnner and adapter after each data change event
                tagSetsSpinnerAdapter = new TagSetSpinnerAdapter(getContext(), tagSetModels);
                spinner.setAdapter(tagSetsSpinnerAdapter);
                if (sessionRestart != null && viewModel.getSelectedTagSet().getValue()==null && tagSetModels!=null) {
                    for (int i=0; i<tagSetModels.size(); i++ ) {
                        if (tagSetModels.get(i).getId().equals(sessionRestart.getTagSetId()))
                            spinner.setSelection(i+1);
                    }
                }
            }
        });

        viewModel.getSelectedTagSet().observe(getActivity(), new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSetModel) {
                if (tagSetModel != null){
                    RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerViewImport.setLayoutManager(layoutManagerImport);
                    recyclerViewImport.setAdapter(new TagRecyclerAdapter(tagSetModel.getTags(), "start"));
                    ScrollHelper.DownScroll(scrollMain);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    viewModel.setSelectedTagSet(null);
                } else {
                    viewModel.setSelectedTagSet(tagSetsSpinnerAdapter.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setSelectedTagSet(null);
            }

        });
        KeyboardHelper.CloseKeyboard(getContext(), spinner);

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), RecordActivity.class);

                if (viewModel.getSession().getTags() == null
                        || viewModel.getSession().getTags().isEmpty()
                        || viewModel.getSession().getName() == null
                        || viewModel.getSession().getName().isEmpty()) {
                    Toast.makeText(getContext(), R.string.tagset_title_warning, Toast.LENGTH_LONG).show();
                } else {
                    //TODO : à voir cmt on le gere quand la raspberry sera en place
                    if (((CreateSessionActivity) getActivity()).isMulti) {
                        share.setVisibility(View.VISIBLE);
                        buttonBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                share.setVisibility(View.GONE);
                            }
                        });
                        buttonGoMulti.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getContext(), RecordActivity.class));
                            }
                        });

                    } else {
                        intent.putExtra("SessionModel", viewModel.getSession());
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
