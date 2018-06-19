package fr.wildcodeschool.vyfe;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class PlaySelectedVideo extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels;
    private VideoView mVideoSelected;
    private SeekBar mSeekBar;
    private boolean isPlayed = false;
    private boolean firstPlay = true;
    private String mIdSession;
    private String mVideoLink;
    private SessionsModel mSessionModel;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_selected_video);


        // mIdSession = getIntent().getStringExtra("idSession");

        // Test de récupération du lien avec données en dur :
        mIdSession = "-LEsI0aNri8kcIgF6bgF";
        final DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("videoLink");
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVideoLink = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();
        mTagModels.add(new TagModel(-3318101, "nameTest1", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest2", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest3", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest4", null, null));

        RecyclerView rvTags = findViewById(R.id.re_tags_selected);
        RecyclerView rvTimeLines = findViewById(R.id.re_time_lines_selected);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvTags.setLayoutManager(layoutManagerTags);
        rvTimeLines.setLayoutManager(layoutManagerTime);

        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        final TagRecyclerAdapter adapterTime = new TagRecyclerAdapter(mTagModels, "timelines");
        rvTags.setAdapter(adapterTags);
        rvTimeLines.setAdapter(adapterTime);

        mSeekBar = findViewById(R.id.seek_bar_selected);
        mVideoSelected = findViewById(R.id.video_view_selected);


        //Test lecture video avec lien en dur :
        String URL = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";
        mVideoSelected.setVideoPath(URL);
        final FloatingActionButton fbPlay = findViewById(R.id.bt_play_selected);

        final SeekbarAsync async = new SeekbarAsync(mSeekBar, mVideoSelected);

        fbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayed) {
                    mVideoSelected.pause();
                    isPlayed = false;

                } else if (firstPlay) {
                    async.execute();
                    firstPlay = false;
                    isPlayed = true;

                } else {
                    mVideoSelected.start();
                    isPlayed = true;
                }
            }
        });
    }
}


