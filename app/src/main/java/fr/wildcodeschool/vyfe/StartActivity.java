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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    private SingletonTags mSingletonTags = SingletonTags.getInstance();
    private ArrayList<TagModel> mTagModelList = mSingletonTags.getmTagsList();

    private SingletonTagsSets mSingletonTagsSets = SingletonTagsSets.getInstance();
    private ArrayList<TagSetsModel> mTagsSetsList = mSingletonTagsSets.getmTagsSetsList();

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String mIdGridImport;
    private String mNameGrid;


    public static final String TITLE_VIDEO = "titleVideo";
    public static final String ID_TAG_SET = "idTagSet";

    private SharedPreferences mSharedPrefTagSet;
    private SharedPreferences mSharedPrefVideoTitle;

    private EditText mEtTagSet;
    private EditText mEtVideoTitle;

    private int mWidth;
    String titleTagSet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        final Button buttonBack = findViewById(R.id.button_back);
        Button buttonGo = findViewById(R.id.button_go);
        final Button buttonGoMulti = findViewById(R.id.button_go_multi);
        final ConstraintLayout share = findViewById(R.id.layout_share);
        final ImageView fabAddMoment = findViewById(R.id.fab_add_moment);
        final RecyclerView recyclerTagList = findViewById(R.id.recycler_view);
        final RecyclerView recyclerViewImport = findViewById(R.id.recycler_view_import);
        final RadioButton radioButtonImport = findViewById(R.id.radio_button_insert);
        final RadioButton radioButtonNew = findViewById(R.id.radio_Button_new);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_session_infos);
        TextView tvAddTag = findViewById(R.id.tv_add_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mEtTagSet = findViewById(R.id.et_grid_title);
        mEtVideoTitle = findViewById(R.id.et_video_title);
        Display display = getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();

        final String[] str = {getString(R.string.arrow)};
        spinner.setMinimumWidth((int) (0.22 * mWidth));


        //enregistrement données
        mSharedPrefTagSet = this.getSharedPreferences("TAGSET", Context.MODE_PRIVATE);
        mSharedPrefVideoTitle = this.getSharedPreferences("VIDEOTITLE", Context.MODE_PRIVATE);

        //tagSetShared des données
        String tagSetShared = mSharedPrefTagSet.getString("TAGSET", "");
        mEtTagSet.setText(tagSetShared);
        String videoTitleShared = mSharedPrefVideoTitle.getString("VIDEOTITLE", "");
        mEtVideoTitle.setText(videoTitleShared);


        final HashMap<String, String> hashMapTitleIdGrid = new HashMap<>();

        final String authUserId = SingletonFirebase.getInstance().getUid();

        if (MainActivity.mMulti) {
            buttonGo.setText(R.string.next);
        }

        final ArrayList<String> nameTagSet = new ArrayList<>();

        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
                R.layout.simple_spinner, nameTagSet);

        adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapterSpinner);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        recyclerTagList.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModelList, "start");
        recyclerTagList.setAdapter(adapter);


        RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(StartActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerViewImport.setLayoutManager(layoutManagerImport);
        final TagRecyclerAdapter adapterImport = new TagRecyclerAdapter(mTagModelList, "start");
        recyclerViewImport.setAdapter(adapterImport);

        recyclerViewImport.setVisibility(View.INVISIBLE);

        //TODO gerer lapparition des recyclers
        radioButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                if (radioButtonImport.isChecked()) {
                    mTagModelList.clear();

                    adapterNotifyDataChange(adapter, adapterImport);

                    recyclerTagList.setVisibility(View.GONE);
                    recyclerViewImport.setVisibility(View.VISIBLE);

                    radioButtonNew.setChecked(false);
                    spinner.setClickable(true);
                    importGrid(mEtTagSet, fabAddMoment, false);
                }
                //tagSetShared données pour mettre spinner
                DatabaseReference myRef = mDatabase.getReference(authUserId).child("tagSets");
                myRef.keepSynced(true);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            nameTagSet.add(getString(R.string.havent_grid));
                        } else {
                            nameTagSet.clear();


                            byte spbyte[] = new byte[0];
                            try {
                                spbyte = str[0].getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            try {
                                str[0] = new String(spbyte, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            nameTagSet.add(getString(R.string.import_grid_arrow) + str[0]);

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                mNameGrid = (String) snapshot.child("name").getValue().toString();
                                String idGrid = (String) snapshot.getKey().toString();
                                hashMapTitleIdGrid.put(mNameGrid, idGrid);
                                nameTagSet.add(mNameGrid);

                            }
                            adapterSpinner.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String titlenameTagSetImport = nameTagSet.get(i);
                        mIdGridImport = hashMapTitleIdGrid.get(titlenameTagSetImport);
                        mTagModelList.clear();
                        adapterNotifyDataChange(adapter, adapterImport);

                        if (mIdGridImport != null && !mIdGridImport.equals(getString(R.string.import_grid_arrow) + str[0])) {
                            //tagSetShared des tags
                            DatabaseReference myRefTag = mDatabase.getReference(authUserId).child("tags");
                            myRefTag.keepSynced(true);
                            myRefTag.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() == 0) {
                                        Toast.makeText(StartActivity.this, R.string.havent_tag,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        if (snapshot.child("fkTagSet").getValue().toString() != null
                                                && snapshot.child("fkTagSet").getValue().toString().equals(mIdGridImport)) {
                                            String name = (String) snapshot.child("name").getValue();
                                            int color = Integer.parseInt(snapshot.child("color").getValue().toString());
                                            mTagModelList.add(new TagModel(color, name, null, null));
                                            mSingletonTags.setmTagsList(mTagModelList);
                                        }
                                    }
                                    adapterImport.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

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


        radioButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonNew.isChecked()) {
                    mTagModelList.clear();
                    adapterNotifyDataChange(adapter, adapterImport);
                    recyclerTagList.setVisibility(View.VISIBLE);
                    recyclerViewImport.setVisibility(View.GONE);
                    radioButtonImport.setChecked(false);
                    spinner.setClickable(false);
                    spinner.setLongClickable(false);
                    importGrid(mEtTagSet, fabAddMoment, true);
                }
            }
        });

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(StartActivity.this, RecordActivity.class);
                final String titleSession = mEtVideoTitle.getText().toString();
                intent.putExtra(TITLE_VIDEO, titleSession);

                //Firebase TAGSET

                DatabaseReference idTagSetRef = mDatabase.getReference(authUserId).child("tagSets").child("name");
                idTagSetRef.keepSynced(true);
                String idTagSet = idTagSetRef.push().getKey();


                if (radioButtonNew.isChecked()) {
                    titleTagSet = mEtTagSet.getText().toString();


                }
                if (radioButtonNew.isChecked() && titleTagSet.isEmpty()) {
                    Toast.makeText(StartActivity.this, R.string.choose_name_grid, Toast.LENGTH_LONG).show();
                } else {
                    if (mTagModelList.isEmpty()) {
                        Toast.makeText(StartActivity.this, R.string.empty_grid, Toast.LENGTH_SHORT).show();
                    } else {

                        intent.putExtra(ID_TAG_SET, idTagSet);

                        DatabaseReference tagsSetRef = mDatabase.getReference(authUserId).child("tagSets").child(idTagSet).child("name");
                        tagsSetRef.keepSynced(true);
                        tagsSetRef.setValue(titleTagSet);
                        mTagsSetsList.add(new TagSetsModel(idTagSet, titleTagSet));
                        mSingletonTagsSets.setmTagsSetsList(mTagsSetsList);

                        for (int i = 0; i < mTagsSetsList.size(); i++) {
                            nameTagSet.add(mTagsSetsList.get(i).getName());
                            adapterSpinner.notifyDataSetChanged();

                        }
                        //Firebase TAG
                        for (int i = 0; i < mTagModelList.size(); i++) {

                            int colorTag = mTagModelList.get(i).getColor();
                            String nameTag = mTagModelList.get(i).getName();
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
                    }
                }
            }
        });

        if (mTagModelList.size() != 0) {
            tvAddTag.setText(R.string.edit_tags);
        }

        fabAddMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPrefTagSet.edit().putString("TAGSET", mEtTagSet.getText().toString()).apply();
                mSharedPrefVideoTitle.edit().putString("VIDEOTITLE", mEtVideoTitle.getText().toString()).apply();
                Intent intent = new Intent(StartActivity.this, AddGridActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(StartActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;

            case R.id.home:
                Intent intentHome = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void importGrid(EditText titleGrid, ImageView fabAdd, Boolean bolean) {
        titleGrid.setClickable(bolean);
        titleGrid.setLongClickable(bolean);
        titleGrid.setEnabled(bolean);
        fabAdd.setClickable(bolean);
        fabAdd.setLongClickable(bolean);
        fabAdd.setFocusable(bolean);
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
