package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {



    SingletonTags mSingletonTags = SingletonTags.getInstance();
    ArrayList<TagModel> mTagModelList = mSingletonTags.getmTagsList();

    SingletonTagsSets mSingletonTagsSets = SingletonTagsSets.getInstance();
    ArrayList<TagSetsModel> mTagsSetsList = mSingletonTagsSets.getmTagsSetsList();

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String mIdGridImport;

    public boolean mAddTag = false;


    public static final String TITLE_VIDEO = "titleVideo";
    public static final String ID_TAG_SET = "idTagSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Button buttonBack = findViewById(R.id.button_back);
        Button buttonGo = findViewById(R.id.button_go);
        final Button buttonGoMulti = findViewById(R.id.button_go_multi);
        final ConstraintLayout share = findViewById(R.id.layout_share);
        final FloatingActionButton fabAddMoment = findViewById(R.id.fab_add_moment);
        final RecyclerView recyclerTagList = findViewById(R.id.recycler_view);
        final RecyclerView recyclerViewImport = findViewById(R.id.recycler_view_import);
        final RadioButton radioButtonImport = findViewById(R.id.radio_button_insert);
        final RadioButton radioButtonNew = findViewById(R.id.radio_Button_new);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_session_infos);
        TextView tvAddTag = findViewById(R.id.tv_add_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        final EditText etTagSet = findViewById(R.id.et_grid_title);
        final EditText etVideoTitle = findViewById(R.id.et_video_title);

        final HashMap<String, String> hashMapTitleIdGrid = new HashMap<>();

        final String authUserId = mAuth.getCurrentUser().getUid();

        if (MainActivity.mMulti) {
            buttonGo.setText(R.string.next);
        }


        final ArrayList<String> nameTagSet = new ArrayList<>();

        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nameTagSet);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        //TODO gerer lapparition des recyclers
        radioButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonImport.isChecked()) {
                    radioButtonNew.setChecked(false);
                    spinner.setClickable(true);
                    importGrid(etTagSet, fabAddMoment, false);
                }
                //recup données pour mettre spinner
                DatabaseReference myRef = mdatabase.getReference(authUserId).child("tag_sets");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            nameTagSet.add(getString(R.string.havent_grid));
                        } else {
                            nameTagSet.clear();
                            nameTagSet.add(getString(R.string.import_grid));

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String nameGrid = (String) snapshot.child("name").getValue().toString();
                                String idGrid = (String) snapshot.getKey().toString();
                                hashMapTitleIdGrid.put(nameGrid, idGrid);
                                nameTagSet.add(nameGrid);
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

                        RecyclerView.LayoutManager layoutManagerImport = new LinearLayoutManager(StartActivity.this, LinearLayoutManager.VERTICAL, false);
                        recyclerViewImport.setLayoutManager(layoutManagerImport);
                        final TagRecyclerAdapter adapterImport = new TagRecyclerAdapter(mTagModelList, "start");
                        recyclerViewImport.setAdapter(adapterImport);

                        if (mIdGridImport != null && !mIdGridImport.equals(R.string.import_grid)) {
                            //recup des tags
                            DatabaseReference myRefTag = mdatabase.getReference(authUserId).child("tags");
                            myRefTag.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() == 0) {
                                        Toast.makeText(StartActivity.this, R.string.havent_tag, Toast.LENGTH_SHORT).show();
                                    }
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        if (snapshot.child("fkTagSet").getValue().toString() != null && snapshot.child("fkTagSet").getValue().toString().equals(mIdGridImport)) {
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
                    radioButtonImport.setChecked(false);
                    spinner.setClickable(false);
                    spinner.setLongClickable(false);
                    importGrid(etTagSet, fabAddMoment, true);
                }
            }
        });


        //TODO: en fct du radio button selectionner envoyer telles ou telles arraylist

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, RecordActivity.class);
                final String titleSession = etVideoTitle.getText().toString();
                intent.putExtra(TITLE_VIDEO, titleSession);

                //Firebase TAGSET
                DatabaseReference idTagSetRef = mdatabase.getReference(authUserId).child("tag_sets").child("name");
                final String idTagSet = idTagSetRef.push().getKey();
                String titleTagSet = etTagSet.getText().toString();
                intent.putExtra(ID_TAG_SET, idTagSet);

                DatabaseReference TagsSetRef = mdatabase.getReference(authUserId).child("tag_sets").child(idTagSet).child("name");
                TagsSetRef.setValue(titleTagSet);
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
                    //V2 : choisir le temps
                    String rigthOffset = "30";
                    String leftOffset = "60";

                    DatabaseReference tagsRef = mdatabase.getReference(authUserId).child("tags");
                    String idTag = tagsRef.push().getKey();
                    tagsRef.child(idTag).child("color").setValue(colorTag);
                    tagsRef.child(idTag).child("name").setValue(nameTag);
                    tagsRef.child(idTag).child("leftOffset").setValue(leftOffset);
                    tagsRef.child(idTag).child("rigthOffset").setValue(rigthOffset);
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
                    startActivity(intent);
                }
            }
        });

        recyclerTagList.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModelList, "start");
        recyclerTagList.setAdapter(adapter);


        if (mTagModelList.size() != 0) {
            tvAddTag.setText(R.string.edit_tags);
        }

        fabAddMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGridDialog.openCreateTags(StartActivity.this);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void importGrid(EditText titleGrid, FloatingActionButton fabAdd, Boolean bolean) {
        titleGrid.setClickable(bolean);
        titleGrid.setLongClickable(bolean);
        titleGrid.setEnabled(bolean);
        fabAdd.setClickable(bolean);
        fabAdd.setLongClickable(bolean);
        fabAdd.setFocusable(bolean);
    }
}
