package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String ID_TAG_SET = "idTagSet";
    String titleTagSet = "";
    ScrollView scrollMain;
    private SingletonTags mSingletonTags = SingletonTags.getInstance();
    private ArrayList<TagModel> mTagModelListAdd = mSingletonTags.getmTagsListAdd();
    private SingletonTagsSets mSingletonTagsSets = SingletonTagsSets.getInstance();
    private ArrayList<TagSetsModel> mTagsSetsList = mSingletonTagsSets.getmTagsSetsList();
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mIdGridImport;
    private String mNameGrid;
    //private SharedPreferences mSharedPrefTagSet;
    private SharedPreferences mSharedPrefVideoTitle;
    private EditText mEtTagSet;
    private EditText mEtVideoTitle;
    private int mWidth;
    private int mHeigth;
    private ArrayList<String> mNameTagSet = new ArrayList<>();
    private ArrayList<String> mIdTagSet = new ArrayList<>();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


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
      //  spinner.setMinimumWidth((int) (0.2 * mWidth));

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,PreparedSessionActivity.class));
            }
        });

        //a enlever?
        String fromAdd = getIntent().getStringExtra("fromAdd");
        if (fromAdd == null) {
            mTagModelListAdd.clear();
        }


        //plus besoin de garder en memoire le titre sauf si rajoute la creation grille
        mSharedPrefVideoTitle = this.getSharedPreferences("VIDEOTITLE", Context.MODE_PRIVATE);
        String videoTitleShared = mSharedPrefVideoTitle.getString("VIDEOTITLE", "");
        if (!videoTitleShared.isEmpty()) {
            mEtVideoTitle.setText(videoTitleShared);
        }


        final String multiSession = getIntent().getStringExtra("multiSession");
        if ("multiSession".equals(multiSession)) {
            buttonGo.setText(R.string.next);
        }


        //TODO: creation fragment : avec import grille

        RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(StartActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerViewImport.setLayoutManager(layoutManagerImport);
        final TagRecyclerAdapter adapterImport = new TagRecyclerAdapter(mTagModelListAdd, "start");
        recyclerViewImport.setAdapter(adapterImport);

        mTagModelListAdd.clear();
        final HashMap<String, String> tagSetIds = new HashMap<>();
        ApiHelperSpinner.getSpinner(StartActivity.this, new ApiHelperSpinner.GridResponse() {
            @Override
            public void onSuccess(HashMap<String, String> hashMapTitleIdGrid) {
                tagSetIds.putAll(hashMapTitleIdGrid);
                mNameTagSet.clear();
                mNameTagSet.add(getString(R.string.import_grid_arrow) + str[0]);
                mIdTagSet.clear();
                mIdTagSet.add("0");
                for (Map.Entry<String, String> entry : tagSetIds.entrySet()) {
                    mNameTagSet.add(entry.getValue());
                    mIdTagSet.add(entry.getKey());
                }
                Log.d("Spinner", "onSuccess: " + String.valueOf(mNameTagSet.size()));
                adapterImport.notifyDataSetChanged();
                //pbLoad.setVisibility(View.GONE);

                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(StartActivity.this,
                        R.layout.simple_spinner, mNameTagSet);


                adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinner.setAdapter(adapterSpinner);

            }

            @Override
            public void onError(String error) {
                Toast.makeText(StartActivity.this, " erreur :" + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWait(String wait) {


            }

            @Override
            public void onFinish(String finish) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ScrollHelper.DownScroll(scrollMain);

                final String titlenameTagSetImport = mNameTagSet.get(i);
                mIdGridImport = mIdTagSet.get(i);
                if (mIdGridImport.equals("0")) {
                    return;
                }
                mTagModelListAdd.clear();
                adapterNotifyDataChange(adapterImport, adapterImport);


                if (mIdGridImport != null && !mIdGridImport.equals(getString(R.string.import_grid_arrow) + str[0])) {

                    ApiHelperSpinner.getTag(StartActivity.this, recyclerViewImport, mIdGridImport, new ApiHelperSpinner.TagsResponse() {
                        @Override
                        public void onSuccess(ArrayList<TagModel> tagModelArrayList) {
                            mTagModelListAdd = tagModelArrayList;
                            adapterImport.notifyDataSetChanged();
                            ScrollHelper.DownScroll(scrollMain);

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

                    titleTagSet = titlenameTagSetImport;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });
        KeyboardHelper.CloseKeyboard(StartActivity.this, spinner);


        final RestartSession restartSession = getIntent().getParcelableExtra("restartSession");
        if (restartSession != null) {
            mEtVideoTitle.setText(restartSession.getNameTitleSession());
            final String idTagSetRestartSession = restartSession.getIdTagSet();

            //TODO: remplacer par nouvelle spinner
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(StartActivity.this,
                    R.layout.simple_spinner, mNameTagSet);


            adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
            spinner.setAdapter(adapterSpinner);
            spinner.setVisibility(View.VISIBLE);

            adapterNotifyDataChange(adapterImport, adapterImport);
            recyclerViewImport.setVisibility(View.VISIBLE);

            spinner.setClickable(true);
            spinner.setEnabled(true);


            //TODO: faire marcher laffichage
            ApiHelperSpinner.getTag(StartActivity.this, recyclerViewImport, idTagSetRestartSession, new ApiHelperSpinner.TagsResponse() {
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

                intent = new Intent(StartActivity.this, RecordActivity.class);
                final String titleSession = mEtVideoTitle.getText().toString();

                singletonSessions.setTitleSession(titleSession);
                intent.putExtra(TITLE_VIDEO, titleSession);

                if (mTagModelListAdd.isEmpty() || titleSession.isEmpty()) {
                    Toast.makeText(StartActivity.this, "Vous devez indiquez un titre à votre session ET une grille d'observation", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList mTagModelFinal = (ArrayList) mTagModelListAdd.clone();
                    mSingletonTags.setmTagsList(mTagModelFinal);

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
                                cleanSharedPref();
                                startActivity(intent);

                            }
                        });

                    } else {
                        intent.putExtra(ID_TAG_SET, mIdGridImport);
                        cleanSharedPref();
                        startActivity(intent);
                    }
                }


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                DisconnectionAlert.confirmedDisconnection(StartActivity.this);
                return true;

            case R.id.home:
                Intent intentHome = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void adapterNotifyDataChange(TagRecyclerAdapter adapterImport, TagRecyclerAdapter adapternew) {
        adapterImport.notifyDataSetChanged();
        adapternew.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        cleanSharedPref();
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);


    }

    public void cleanSharedPref() {
        mSharedPrefVideoTitle.edit().putString("VIDEOTITLE", "").apply();
        mEtVideoTitle.setText("");
        mTagModelListAdd.clear();

    }


}