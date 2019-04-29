package fr.vyfe.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
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

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.activity.CreateGridActivity;
import fr.vyfe.activity.CreateSessionActivity;
import fr.vyfe.activity.RecordActivity;
import fr.vyfe.adapter.TagSetSpinnerAdapter;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.helper.KeyboardHelper;
import fr.vyfe.helper.ScrollHelper;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.CreateSessionViewModel;

import static android.content.Context.CONNECTIVITY_SERVICE;


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

    public static CreateSessionFragment newInstance() {
        return new CreateSessionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instancie les objets
        viewModel = ViewModelProviders.of(getActivity()).get(CreateSessionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Instancie la vue et les composant gaphique
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

        mEtVideoTitle.setText(viewModel.getSessionName());

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
                // Android spinner view is very tricky to bind to LiveData objects.
                // This is the most efficient solution I've found to make it work,
                // it means rebuilding the sipnner and adapter after each data change event
                tagSetsSpinnerAdapter = new TagSetSpinnerAdapter(getContext(), tagSetModels);
                spinner.setAdapter(tagSetsSpinnerAdapter);
                if (viewModel.getSelectedTagSet().getValue() != null && tagSetModels != null) {
                    for (int i = 0; i < tagSetModels.size(); i++) {
                        if (tagSetModels.get(i).getId() != null && tagSetModels.get(i).getId().equals(viewModel.getSelectedTagSet().getValue().getId()))
                            spinner.setSelection(i);
                    }
                }
            }
        });

        viewModel.getSelectedTagSet().observe(getActivity(), new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSetModel) {
                RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerViewImport.setLayoutManager(layoutManagerImport);
                if (tagSetModel != null) {
                    recyclerViewImport.setAdapter(new TemplateRecyclerAdapter(tagSetModel.getTemplates(), "start"));
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

        if (((CreateSessionActivity) getActivity()).isMulti) {
            buttonGo.setText(R.string.next);
            WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final String networkSSID = getContext().getString(R.string.networkSSID);
            String wifiManagerConnectionInfo = wifiManager.getConnectionInfo().getSSID();
            if (wifiManagerConnectionInfo.equals('"' + networkSSID + '"')) {
                Toast.makeText(getContext(), "Vous êtes co au rasberry", Toast.LENGTH_SHORT).show();
            }


            ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
            assert connManager != null;
            final String wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getExtraInfo();
            if (wifi != null && wifi.equals('"' + networkSSID + '"')) {
                Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(getContext(), "non", Toast.LENGTH_SHORT).show();
        }


        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), RecordActivity.class);
                if (viewModel.getSelectedTagSet().getValue() == null
                        || viewModel.getSessionName() == null
                        || viewModel.getSessionName().isEmpty()) {
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
                        try {
                            intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.pushSession());
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

}
