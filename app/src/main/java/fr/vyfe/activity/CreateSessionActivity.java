package fr.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.R;
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.adapter.TagsSetsSpinnerAdapter;
import fr.vyfe.helper.KeyboardHelper;
import fr.vyfe.helper.ScrollHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.UserModel;
import fr.vyfe.viewModel.CreateSessionViewModel;
import fr.vyfe.viewModel.CreateSessionViewModelFactory;

public class CreateSessionActivity extends VyfeActivity {

    ScrollView scrollMain;
    private ArrayList<TagModel> mTagModelListAdd;
    private EditText mEtVideoTitle;
    private int mWidth;
    private int mHeigth;
    private Intent intent;
    private CreateSessionViewModel viewModel;
    private TagRecyclerAdapter adapterImport;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        currentUser = mAuth.getCurrentUser();
        mTagModelListAdd = new ArrayList<>();

        viewModel = ViewModelProviders.of(this, new CreateSessionViewModelFactory( currentUser.getId(), currentUser.getCompany())).get(CreateSessionViewModel.class);

        final Button buttonBack = findViewById(R.id.button_back);
        Button buttonGo = findViewById(R.id.button_go);
        final Button buttonGoMulti = findViewById(R.id.button_go_multi);
        Button btnCreateGrid = findViewById(R.id.btn_intent_create_grid);
        final ConstraintLayout share = findViewById(R.id.layout_share);
        final RecyclerView recyclerViewImport = findViewById(R.id.recycler_view_import);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_session_infos);
        scrollMain = findViewById(R.id.scrool_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mEtVideoTitle = findViewById(R.id.et_video_title2);
        Display display = getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();
        mHeigth = display.getHeight();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        spinner.setMinimumWidth((int) (0.2 * mWidth));

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateSessionActivity.this, CreateGridActivity.class));
            }
        });

        final String multiSession = getIntent().getStringExtra("multiSession");
        if ("multiSession".equals(multiSession)) {
            buttonGo.setText(R.string.next);
        }


        viewModel.getTagSets().observe(this, new Observer<List<TagSetModel>>() {
            @Override
            public void onChanged(@Nullable List<TagSetModel> tagSetModels) {
                TagsSetsSpinnerAdapter adapterTagsSetsSpinner = new TagsSetsSpinnerAdapter(CreateSessionActivity.this, tagSetModels);
                spinner.setAdapter(adapterTagsSetsSpinner);
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                final TagSetModel tagSetModels;
                if (position == 0) {
                    mTagModelListAdd = new ArrayList<>();
                } else {
                    tagSetModels = viewModel.getTagSets().getValue().get(position - 1);
                    mTagModelListAdd = tagSetModels.getTags();
                    viewModel.getSession().setTags(mTagModelListAdd);
                    viewModel.getSession().setIdTagSet(tagSetModels.getId());
                }

                RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(CreateSessionActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerViewImport.setLayoutManager(layoutManagerImport);
                adapterImport = new TagRecyclerAdapter(mTagModelListAdd, "start");
                recyclerViewImport.setAdapter(adapterImport);


                ScrollHelper.DownScroll(scrollMain);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        KeyboardHelper.CloseKeyboard(CreateSessionActivity.this, spinner);

        //TODO voir selection spinner avec positionTagSetSpinner
        SessionModel sessionRestart = getIntent().getParcelableExtra("restartSession");
        if (sessionRestart != null) {
            mEtVideoTitle.setText(sessionRestart.getName());
            final String idTagSetRestartSession = sessionRestart.getIdTagSet();
            int positionTagSetSpinner = positionGridSpinner(idTagSetRestartSession);
            //TODO: methode ne fonctionne pas ????
            spinner.setSelection(positionTagSetSpinner);
        }

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(CreateSessionActivity.this, RecordActivity.class);
                viewModel.getSession().setName(mEtVideoTitle.getText().toString());

                if (viewModel.getSession().getTags().isEmpty() || viewModel.getSession().getName().isEmpty()) {
                    Toast.makeText(CreateSessionActivity.this, R.string.tagset_title_warning, Toast.LENGTH_LONG).show();
                } else {
                    //TODO : à voir cmt on le gere quand la raspberry sera en place
                    if ("multiSession".equals(multiSession)) {
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
                                startActivity(intent);

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

    public int positionGridSpinner(final String tagSetSession) {
        final int[] position = new int[1];
        viewModel.getTagSets().observe(this, new Observer<List<TagSetModel>>() {
            @Override
            public void onChanged(@Nullable List<TagSetModel> tagSetModels) {
                for (int i = 0; i < tagSetModels.size(); i++) {
                    String idTagSetSpinner = tagSetModels.get(i).getId();
                    if (idTagSetSpinner.equals(tagSetSession)) {
                        position[0] = i;

                    }
                }

            }
        });
        return position[0];
    }

}