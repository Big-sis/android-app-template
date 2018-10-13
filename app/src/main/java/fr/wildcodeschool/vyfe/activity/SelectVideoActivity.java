package fr.wildcodeschool.vyfe.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.adapter.TagRecyclerAdapter;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;
import fr.wildcodeschool.vyfe.viewModel.SingletonSessions;

public class SelectVideoActivity extends VyfeActivity {

    ArrayList<TagModel> mTagModels = new ArrayList<>();
    ArrayList<TagModel> mTagedList = new ArrayList<>();
    FirebaseDatabase mDatabase;
    final String mAuthUserId = SingletonFirebase.getInstance().getUid();
    private String mIdTagSet;
    private SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private String mIdSession;
    private String mFilename;
    private String mTitleVideo;

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        mIdSession = mSingletonSessions.getIdSession();
        mFilename = mSingletonSessions.getFileName();
        mTitleVideo = mSingletonSessions.getTitleSession();


        mDatabase = SingletonFirebase.getInstance().getDatabase();
        Button play = findViewById(R.id.bt_play);
        Button edit = findViewById(R.id.btn_edit);
        ImageView video = findViewById(R.id.vv_preview);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);

        final DatabaseReference ref = mDatabase.getInstance().getReference(mAuthUserId).child("sessions");

        tvTitle.setText(mTitleVideo);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectVideoActivity.this, InfoVideoActivity.class);
                startActivity(intent);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runPlayVideoActivity();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runPlayVideoActivity();
            }
        });



        final DatabaseReference tagSessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("idTagSet");

        tagSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIdTagSet = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTagModels.clear();
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    TagModel tagModel = tagSnapshot.getValue(TagModel.class);

                    if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                        mTagModels.add(tagModel);
                    }
                }

                DatabaseReference tagedRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("tags");
                tagedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagedSnapshot : dataSnapshot.getChildren()) {
                            TagModel taged = tagedSnapshot.getValue(TagModel.class);
                            // TODO : trouver pourquoi la requête ne récupère pas le tagName avec le modèle
                            String tagedName = tagedSnapshot.child("tagName").getValue(String.class);
                            taged.setName(tagedName);
                            mTagedList.add(taged);
                        }

                        RecyclerView recyclerTags = findViewById(R.id.re_tags);
                        TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, mTagedList,"count");
                        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerTags.setLayoutManager(layoutManagerTags);
                        recyclerTags.setAdapter(adapterTags);
                        adapterTags.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mTitleVideo);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot video: dataSnapshot.getChildren()) {
                    SessionModel model = video.getValue(SessionModel.class);
                    if (mFilename.equals(model.getVideoLink())) {
                        if (video.hasChild("description")) {
                            tvDescription.setText(model.getDescription());
                        } else {
                            tvDescription.setText(R.string.no_description);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void runPlayVideoActivity(){
        Intent intent = new Intent(SelectVideoActivity.this, PlayVideoActivity.class);
        intent.putExtra(ID_SESSION, mSingletonSessions.getIdSession());
        startActivity(intent);
    }


}