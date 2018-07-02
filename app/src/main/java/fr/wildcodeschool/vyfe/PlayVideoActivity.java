package fr.wildcodeschool.vyfe;

import android.os.SystemClock;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayVideoActivity extends AppCompatActivity {

    private ArrayList<TagModel> mTagedList = new ArrayList<>();
    private ArrayList<TagModel> mTagModels = new ArrayList<>();
    private VideoView mVideoSelected;
    private SeekBar mSeekBar;
    private boolean mIsPlayed = false;
    private boolean mFirstPlay = true;
    private String mIdSession;
    private String mVideoLink;
    private String mIdTagSession;
    private ArrayList<String> mIdTagSet = new ArrayList<>();
    private SessionsModel mSessionModel;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    HashMap<String, LinearLayout> mTimelines = new HashMap<>();
    HashMap<String, ArrayList<Pair<Integer, Integer>>> mTagList = new HashMap<>();
    HashMap<String, ArrayList<TagModel>> mTagModelsList = new HashMap<>();
    TagRecyclerAdapter mAdapterTags = new TagRecyclerAdapter(mTagModels, "count");



    SeekbarAsync mAsync;
    long timeWhenStopped = 0;

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        final Chronometer chrono = findViewById(R.id.chronometer_play);

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        final RecyclerView rvTags = findViewById(R.id.re_tags_selected);
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTags.setLayoutManager(layoutManagerTags);
        rvTags.setAdapter(mAdapterTags);



        mIdSession = getIntent().getStringExtra("idSession");


        // NE PAS SUPPRIMER POUR LE MOMENT
        // Test de récupération du lien avec données en dur :
       // mIdSession = "-LFw9OH4TpHhciKB2wRi";

        mVideoSelected = findViewById(R.id.video_view_selected);

        final DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVideoLink = dataSnapshot.child("videoLink").getValue(String.class);
                mVideoSelected.setVideoPath(mVideoLink);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Lien video en dur pour tester
        String URL = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";




        final DatabaseReference tagsSessionRef = mDatabase.getReference(mAuthUserId).child("tagsSession");
        tagsSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(PlayVideoActivity.this, "Vous n'avez pas de tags enregistrés", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot tagsSessionSnapshot : dataSnapshot.getChildren()) {
                    String fkSession = tagsSessionSnapshot.child("fkSession").getValue().toString();
                    if (fkSession.equals(mIdSession)) {
                        mIdTagSession = tagsSessionSnapshot.getKey();
                        final DatabaseReference fkTagSet = mDatabase.getReference(mAuthUserId).child("tagsSession").child(mIdTagSession).child("fkTagSet");
                        fkTagSet.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mTagList.clear();
                               for (DataSnapshot fkTagSetSnapshot : dataSnapshot.getChildren()) {
                                   String fkTagSet = fkTagSetSnapshot.getKey();
                                   mIdTagSet.add(fkTagSet);
                                   mTagedList = (ArrayList<TagModel>) fkTagSetSnapshot.getValue();
                                   final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
                                   tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           for (DataSnapshot tagsSnapshot : dataSnapshot.getChildren()) {
                                               TagModel tagModel = tagsSnapshot.getValue(TagModel.class);
                                               String fkTagSet = tagModel.getFkTagSet();
                                               if (mIdTagSet.contains(fkTagSet)) {
                                                   mTagModels.add(tagsSnapshot.getValue(TagModel.class));
                                                   mAdapterTags.notifyDataSetChanged();

                                               }
                                           }
                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                       }
                                   });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








        mSeekBar = findViewById(R.id.seek_bar_selected);

        // Rend la seekbar indéplaceable au click
        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        final String fileName = getIntent().getStringExtra(FILE_NAME);

/*
        File file = new File(fileName);
        if(file.exists()) {
            Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
        }
*/
        // mVideoSelected.setVideoPath(fileName);
        final FloatingActionButton fbPlay = findViewById(R.id.bt_play_selected);


        fbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayed) {
                    mVideoSelected.pause();
                    timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                    chrono.stop();
                    mIsPlayed = false;
                    fbPlay.setBackgroundResource(R.color.colorLightGreenishBlue);
                    fbPlay.setImageResource(android.R.drawable.ic_media_play);

                } else if (mFirstPlay) {
                    mFirstPlay = false;
                    mIsPlayed = true;
                    mAsync = new SeekbarAsync(mSeekBar, mVideoSelected);
                    mAsync.execute();
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    fbPlay.setBackgroundResource(R.color.colorFadedOrange);
                    fbPlay.setImageResource(android.R.drawable.ic_media_pause);


                } else {
                    mVideoSelected.start();
                    chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chrono.start();
                    mIsPlayed = true;
                    fbPlay.setBackgroundResource(R.color.colorFadedOrange);
                    fbPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        FloatingActionButton btReplay = findViewById(R.id.bt_replay);
        btReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstPlay = false;
                mIsPlayed = true;
                mVideoSelected.seekTo(0);
                mSeekBar.setProgress(0);
                mAsync = new SeekbarAsync(mSeekBar, mVideoSelected);
                mAsync.execute();
                mVideoSelected.start();
                chrono.setBase(0);
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();
                fbPlay.setBackgroundResource(R.color.colorFadedOrange);
                fbPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        //TODO : mettre valeur calculée
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width)) / 300.0;
        int height = (int) (ratio * 50);
        RelativeLayout timeLines = findViewById(R.id.time_lines_container);

        timeLines.setLayoutParams(new FrameLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
        mSeekBar.setLayoutParams(new RelativeLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Intent intent = new Intent(PlayVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


