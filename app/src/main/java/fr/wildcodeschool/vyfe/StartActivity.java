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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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
    private SharedPreferences mSharedPrefTagSet;
    private SharedPreferences mSharedPrefVideoTitle;
    private EditText mEtTagSet;
    private EditText mEtVideoTitle;
    private int mWidth;
    private int mHeigth;
    private ArrayList<String> mNameTagSet = new ArrayList<>();
    private ArrayList<String> mIdTagSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        final SingletonSessions singletonSessions = SingletonSessions.getInstance();

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        final Button buttonBack = findViewById(R.id.button_back);
        Button buttonGo = findViewById(R.id.button_go);
        final Button buttonGoMulti = findViewById(R.id.button_go_multi);
        final ConstraintLayout share = findViewById(R.id.layout_share);
        final LinearLayout fabAddMoment = findViewById(R.id.new_event);
        final RecyclerView recyclerTagList = findViewById(R.id.recycler_view);
        final RecyclerView recyclerViewImport = findViewById(R.id.recycler_view_import);
        final RadioButton radioButtonImport = findViewById(R.id.radio_button_insert);
        final RadioButton radioButtonNew = findViewById(R.id.radio_Button_new);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_session_infos);
        TextView tvAddTag = findViewById(R.id.tv_add_tag);
        final TextView tvTitleGridImport = findViewById(R.id.tv_title_grid_import);
        final ImageView ivAddTags = findViewById(R.id.fab_add_moment);
        scrollMain = findViewById(R.id.scrool_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mEtTagSet = findViewById(R.id.et_grid_title);
        mEtVideoTitle = findViewById(R.id.et_video_title2);
        Display display = getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();
        mHeigth = display.getHeight();
        final ProgressBar pbLoad = findViewById(R.id.pb_load);

        final String[] str = {getString(R.string.arrow)};
        spinner.setMinimumWidth((int) (0.2 * mWidth));

        String fromAdd = getIntent().getStringExtra("fromAdd");
        if (fromAdd == null) {
            mTagModelListAdd.clear();
        }

        //enregistrement données
        mSharedPrefTagSet = this.getSharedPreferences("TAGSET", Context.MODE_PRIVATE);
        mSharedPrefVideoTitle = this.getSharedPreferences("VIDEOTITLE", Context.MODE_PRIVATE);

        //tagSetShared des données
        String tagSetShared = mSharedPrefTagSet.getString("TAGSET", "");
        if (!tagSetShared.isEmpty()) {
            mEtTagSet.setText(tagSetShared);
        }
        String videoTitleShared = mSharedPrefVideoTitle.getString("VIDEOTITLE", "");
        if (!videoTitleShared.isEmpty()) {
            mEtVideoTitle.setText(videoTitleShared);
        }


        final HashMap<String, String> tagSetIds = new HashMap<>();

        final String authUserId = SingletonFirebase.getInstance().getUid();

        if (MainActivity.mMulti) {
            buttonGo.setText(R.string.next);
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        recyclerTagList.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModelListAdd, "start");
        recyclerTagList.setAdapter(adapter);


        RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(StartActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerViewImport.setLayoutManager(layoutManagerImport);
        final TagRecyclerAdapter adapterImport = new TagRecyclerAdapter(mTagModelListAdd, "start");
        recyclerViewImport.setAdapter(adapterImport);

        recyclerViewImport.setVisibility(View.INVISIBLE);

        radioButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardHelper.CloseKeyboard(StartActivity.this, radioButtonImport);
                ScrollHelper.DownScroll(scrollMain);
                spinner.setVisibility(View.VISIBLE);


                if (radioButtonImport.isChecked()) {
                    mTagModelListAdd.clear();
                    adapterNotifyDataChange(adapter, adapterImport);
                    recyclerTagList.setVisibility(View.GONE);
                    recyclerViewImport.setVisibility(View.VISIBLE);
                    tvTitleGridImport.setVisibility(View.VISIBLE);
                    radioButtonNew.setChecked(false);
                    spinner.setClickable(true);
                    spinner.setEnabled(true);
                    importGrid(mEtTagSet, fabAddMoment, ivAddTags, false);
                }
                pbLoad.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);

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
                        pbLoad.setVisibility(View.GONE);

                        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(StartActivity.this,
                                R.layout.simple_spinner, mNameTagSet);


                        adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
                        spinner.setAdapter(adapterSpinner);
                        spinner.setVisibility(View.VISIBLE);


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
                        adapterNotifyDataChange(adapter, adapterImport);


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


            }
        });

        final RestartSession restartSession = getIntent().getParcelableExtra("restartSession");
        if (restartSession != null) {
            radioButtonImport.setChecked(true);
            radioButtonNew.setChecked(false);
            tvTitleGridImport.setVisibility(View.VISIBLE);

            mEtVideoTitle.setText(restartSession.getNameTitleSession());
            final String idTagSetRestartSession = restartSession.getIdTagSet();

            //TODO: remplacer par nouvelle spinner
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(StartActivity.this,
                    R.layout.simple_spinner, mNameTagSet);


            adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
            spinner.setAdapter(adapterSpinner);
            spinner.setVisibility(View.VISIBLE);

            adapterNotifyDataChange(adapter, adapterImport);
            recyclerViewImport.setVisibility(View.VISIBLE);
            recyclerTagList.setVisibility(View.INVISIBLE);

            spinner.setClickable(true);
            spinner.setEnabled(true);
            importGrid(mEtTagSet, fabAddMoment, ivAddTags, false);

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

        radioButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardHelper.CloseKeyboard(StartActivity.this, radioButtonNew);
                if (radioButtonNew.isChecked()) {
                    tvTitleGridImport.setVisibility(View.GONE);
                    mTagModelListAdd.clear();
                    adapterNotifyDataChange(adapter, adapterImport);
                    recyclerTagList.setVisibility(View.VISIBLE);
                    recyclerViewImport.setVisibility(View.GONE);
                    radioButtonImport.setChecked(false);
                    spinner.setClickable(false);
                    spinner.setLongClickable(false);
                    spinner.setEnabled(false);
                    spinner.setVisibility(View.GONE);
                    importGrid(mEtTagSet, fabAddMoment, ivAddTags, true);
                }
                ScrollHelper.DownScroll(scrollMain);
            }
        });

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(StartActivity.this, RecordActivity.class);
                final String titleSession = mEtVideoTitle.getText().toString();
                singletonSessions.setTitleSession(titleSession);
                //intent.putExtra(TITLE_VIDEO, titleSession);


                if (radioButtonNew.isChecked()) {
                    titleTagSet = mEtTagSet.getText().toString();

                }
                if (radioButtonNew.isChecked() && titleTagSet.isEmpty()) {
                    Toast.makeText(StartActivity.this, R.string.choose_name_grid, Toast.LENGTH_LONG).show();
                } else {
                    if (mTagModelListAdd.isEmpty()) {
                        Toast.makeText(StartActivity.this, R.string.empty_grid, Toast.LENGTH_LONG).show();
                    } else {
                        ArrayList mTagModelFinal = (ArrayList) mTagModelListAdd.clone();
                        mSingletonTags.setmTagsList(mTagModelFinal);
                        //Firebase TAGSET
                        String idTagSet = "";

                        DatabaseReference idTagSetRef = mDatabase.getReference(authUserId).child("tagSets").child("name");
                        idTagSetRef.keepSynced(true);
                        /*
                        if (radioButtonImport.isChecked()) {
                            idTagSet = mIdGridImport;
                        } else {
                            idTagSet = idTagSetRef.push().getKey();
                        }*/
                        // TODO : éviter la création de nouvelles grilles à partir d'un import
                        idTagSet = idTagSetRef.push().getKey();

                        intent.putExtra(ID_TAG_SET, idTagSet);

                        DatabaseReference tagsSetRef = mDatabase.getReference(authUserId).child("tagSets").child(idTagSet).child("name");
                        tagsSetRef.keepSynced(true);
                        tagsSetRef.setValue(titleTagSet);
                        mTagsSetsList.add(new TagSetsModel(idTagSet, titleTagSet));
                        mSingletonTagsSets.setmTagsSetsList(mTagsSetsList);


                        //Firebase TAG
                        for (int i = 0; i < mTagModelListAdd.size(); i++) {

                            String colorTag = mTagModelListAdd.get(i).getColorName();
                            String nameTag = mTagModelListAdd.get(i).getName();
                            //V2 : choisir le temps, necessaire ???
                            String durationTag = String.valueOf(getResources().getInteger(R.integer.duration_tag));
                            String beforeTag = String.valueOf(getResources().getInteger(R.integer.before_tag));


                            DatabaseReference tagsRef = mDatabase.getReference(authUserId).child("tags");
                            tagsRef.keepSynced(true);
                            String idTag = tagsRef.push().getKey();
                            tagsRef.child(idTag).child("color").setValue(colorTag);
                            tagsRef.child(idTag).child("name").setValue(nameTag);
                            tagsRef.child(idTag).child("leftOffset").setValue(beforeTag);
                            tagsRef.child(idTag).child("rigthOffset").setValue(durationTag);
                            tagsRef.child(idTag).child("fkTagSet").setValue(idTagSet);

                        }

                        if (MainActivity.mMulti) {
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
                            MainActivity.mMulti = false;
                        } else {
                            if (titleSession.isEmpty()) {
                                Toast.makeText(StartActivity.this, R.string.title, Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(intent);
                            }
                        }


                        mSharedPrefTagSet.edit().putString("TAGSET", "").apply();
                        mEtTagSet.setText("");
                        mSharedPrefVideoTitle.edit().putString("VIDEOTITLE", "").apply();
                        mEtVideoTitle.setText("");
                        mTagModelListAdd.clear();

                    }
                }
            }
        });

        if (mTagModelListAdd.size() != 0) {
            tvAddTag.setText(R.string.edit_tags);
        }

        fabAddMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPressAddTags();
            }
        });

        ivAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPressAddTags();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public void onPressAddTags() {
        mSharedPrefTagSet.edit().putString("TAGSET", mEtTagSet.getText().toString()).apply();
        mSharedPrefVideoTitle.edit().putString("VIDEOTITLE", mEtVideoTitle.getText().toString()).apply();
        Intent intent = new Intent(StartActivity.this, AddGridActivity.class);
        startActivity(intent);
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

    public void importGrid(EditText titleGrid, LinearLayout fabAdd, ImageView ivAddTags, Boolean bolean) {
        titleGrid.setClickable(bolean);
        titleGrid.setLongClickable(bolean);
        titleGrid.setEnabled(bolean);
        fabAdd.setClickable(bolean);
        fabAdd.setLongClickable(bolean);
        fabAdd.setFocusable(bolean);
        ivAddTags.setClickable(bolean);
    }

    public void adapterNotifyDataChange(TagRecyclerAdapter adapterImport, TagRecyclerAdapter adapternew) {
        adapterImport.notifyDataSetChanged();
        adapternew.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {

        mSharedPrefTagSet.edit().putString("TAGSET", null).apply();
        mEtTagSet.setText("");
        mSharedPrefVideoTitle.edit().putString("VIDEOTITLE", null).apply();
        mEtVideoTitle.setText("");

        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);


    }


}