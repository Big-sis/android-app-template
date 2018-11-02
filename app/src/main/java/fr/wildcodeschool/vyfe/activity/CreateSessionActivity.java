package fr.wildcodeschool.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.wildcodeschool.vyfe.PrepareSessionActivity;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.RestartSession;
import fr.wildcodeschool.vyfe.adapter.TagRecyclerAdapter;
import fr.wildcodeschool.vyfe.adapter.TagsSetsSpinnerAdapter;
import fr.wildcodeschool.vyfe.helper.ApiHelperSpinner;
import fr.wildcodeschool.vyfe.helper.KeyboardHelper;
import fr.wildcodeschool.vyfe.helper.ScrollHelper;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TagSetModel;
import fr.wildcodeschool.vyfe.viewModel.CreateSessionViewModel;
import fr.wildcodeschool.vyfe.viewModel.CreateSessionViewModelFactory;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;
import fr.wildcodeschool.vyfe.viewModel.SingletonSessions;
import fr.wildcodeschool.vyfe.viewModel.SingletonTags;

public class CreateSessionActivity extends VyfeActivity {

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String ID_TAG_SET = "idTagSet";
    String titleTagSet = "";
    ScrollView scrollMain;
    private SingletonTags mSingletonTags = SingletonTags.getInstance();
    private ArrayList<TagModel> mTagModelListAdd = mSingletonTags.getmTagsListAdd();
    private FirebaseDatabase mDatabase;
    private String mIdGridImport;
    private SharedPreferences mSharedPrefVideoTitle;
    private EditText mEtVideoTitle;
    private int mWidth;
    private int mHeigth;
    private ArrayList<String> mNameTagSet = new ArrayList<>();
    private ArrayList<String> mIdTagSet = new ArrayList<>();
    private Intent intent;
    private CreateSessionViewModel viewModel;

    private TagRecyclerAdapter adapterImport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        viewModel = ViewModelProviders.of(this, new CreateSessionViewModelFactory(SingletonFirebase.getInstance().getUid())).get(CreateSessionViewModel.class);

        final SingletonSessions singletonSessions = SingletonSessions.getInstance();

        mDatabase = SingletonFirebase.getInstance().getDatabase();

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
        final String authUserId = SingletonFirebase.getInstance().getUid();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        final String[] str = {getString(R.string.arrow)};
        spinner.setMinimumWidth((int) (0.2 * mWidth));

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateSessionActivity.this, PrepareSessionActivity.class));
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

                TagSetModel tagSetModels;
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


        final RestartSession restartSession = getIntent().getParcelableExtra("restartSession");
        if (restartSession != null) {
            mEtVideoTitle.setText(restartSession.getNameTitleSession());
            final String idTagSetRestartSession = restartSession.getIdTagSet();

            //TODO: remplacer par nouvelle spinner
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(CreateSessionActivity.this,
                    R.layout.simple_spinner, mNameTagSet);


            adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
            spinner.setAdapter(adapterSpinner);
            spinner.setVisibility(View.VISIBLE);

            adapterImport.notifyDataSetChanged();
            recyclerViewImport.setVisibility(View.VISIBLE);

            spinner.setClickable(true);
            spinner.setEnabled(true);


            //TODO: faire marcher laffichage
            ApiHelperSpinner.getTag(CreateSessionActivity.this, recyclerViewImport, idTagSetRestartSession, new ApiHelperSpinner.TagsResponse() {
                @Override
                public void onSuccess(ArrayList<TagModel> tagModelArrayList) {
                    mTagModelListAdd = tagModelArrayList;
                    // adapterImport.notifyDataSetChanged();

                }

                @Override
                public void onError(String error) {

                }
            });
            //TODO: indiquer qu'il faut garder l'identifiant de la grille pour envoyer les données sur firebase


        }

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(CreateSessionActivity.this, RecordActivity.class);
                viewModel.getSession().setName(mEtVideoTitle.getText().toString());


                if (viewModel.getSession().getTags().isEmpty() || viewModel.getSession().getName().isEmpty()) {
                    Toast.makeText(CreateSessionActivity.this, "Vous devez indiquez un titre à votre session ET une grille d'observation", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList mTagModelFinal = (ArrayList) mTagModelListAdd.clone();
                    mSingletonTags.setmTagsList(mTagModelFinal);

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


}