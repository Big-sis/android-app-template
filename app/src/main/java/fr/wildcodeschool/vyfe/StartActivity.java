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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    SingletonTags mSingletonTags = SingletonTags.getInstance();
    ArrayList<TagModel> mTagModelList = mSingletonTags.getmTagsList();

    SingletonTagsSets mSingletonTagsSets = SingletonTagsSets.getInstance();
    ArrayList<TagSetsModel> mTagsSetsList = mSingletonTagsSets.getmTagsSetsList();

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Button buttonBack = findViewById(R.id.button_back);
        Button buttonGo = findViewById(R.id.button_go);
        final Button buttonGoMulti = findViewById(R.id.button_go_multi);
        final ConstraintLayout share = findViewById(R.id.layout_share);
        FloatingActionButton fabAddMoment = findViewById(R.id.fab_add_moment);
        RecyclerView recyclerTagList = findViewById(R.id.recycler_view);
        final RadioButton radioButtonImport = findViewById(R.id.radio_button_insert);
        final RadioButton radioButtonNew = findViewById(R.id.radio_Button_new);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_session_infos);
        TextView tvAddTag = findViewById(R.id.tv_add_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);

        final EditText etTagSet = findViewById(R.id.et_grid_title);

        final EditText etVideoTitle = findViewById(R.id.et_video_title);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String authUserId = auth.getCurrentUser().getUid();


        if (MainActivity.mMulti) {
            buttonGo.setText(R.string.next);
        }

        final ArrayList<String> nameTagSet = new ArrayList<>();

        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nameTagSet);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.start_session);


        radioButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonImport.isChecked()) {
                    radioButtonNew.setChecked(false);
                    //TODO: affichier l'accès aux elements: imports grilles
                }
            }
        });

        radioButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonNew.isChecked()) {
                    radioButtonImport.setChecked(false);
                    //TODO: affichier l'accès aux elements: création grilles
                }

            }
        });

        //TODO: en fct du radio button selectionner envoyer telles ou telles arraylist

        fabAddMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, AddGridActivity.class);
                startActivity(intent);
            }
        });


        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, RecordActivity.class);
                final String titleSession = etVideoTitle.getText().toString();
                intent.putExtra("titleSession", titleSession);

                //Firebase TAGSET
                DatabaseReference idTagSetRef = mdatabase.getReference(authUserId).child("tag_sets").child("name");
                final String idTagSet = idTagSetRef.push().getKey();
                String titleTagSet = etTagSet.getText().toString();

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
                    String rigthOffset = "3000";

                    DatabaseReference tagsRef = mdatabase.getReference(authUserId).child("tags");
                    String idTag = tagsRef.push().getKey();
                    tagsRef.child(idTag).child("color").setValue(colorTag);
                    tagsRef.child(idTag).child("name").setValue(nameTag);
                    tagsRef.child(idTag).child("rigth_offset").setValue(rigthOffset);
                    tagsRef.child(idTag).child("fk_tag_set").setValue(idTagSet);
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
            public void onClick(View view) {
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
}
