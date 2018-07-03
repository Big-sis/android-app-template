package fr.wildcodeschool.vyfe;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.content.Intent;
import android.support.annotation.ColorRes;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private String mIdTagSet;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    HashMap<String, RelativeLayout> mTimelines = new HashMap<>();
    HashMap<String, ArrayList<TimeModel>> mTagList = new HashMap<>();
    HashMap<String, ArrayList<TimeModel>> mNewTagList = new HashMap<>();
    HashMap<String, Integer> mTagColorList = new HashMap<>();
    TagRecyclerAdapter mAdapterTags = new TagRecyclerAdapter(mTagModels, "count");
    RelativeLayout timeLines;


    SeekbarAsync mAsync;
    long timeWhenStopped = 0;
    int mVideoDuration;

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";
    public static final String URL_TEST = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        final Chronometer chrono = findViewById(R.id.chronometer_play);
        timeLines = findViewById(R.id.time_lines_container);

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        final RecyclerView rvTags = findViewById(R.id.re_tags_selected);
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTags.setLayoutManager(layoutManagerTags);
        rvTags.setAdapter(mAdapterTags);


        mIdSession = getIntent().getStringExtra(ID_SESSION);


        // NE PAS SUPPRIMER POUR LE MOMENT
        // Test de récupération du lien avec données en dur :
        // mIdSession = "-LFw9OH4TpHhciKB2wRi";

        mVideoLink = getIntent().getStringExtra(FILE_NAME);

        mVideoSelected = findViewById(R.id.video_view_selected);
        mVideoSelected.setVideoPath(mVideoLink);
        mVideoSelected.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoDuration = mVideoSelected.getDuration();
                initTimeLines();
                String coucou = "coucou";
            }
        });


        // Réupération du lien de la video
        final DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIdTagSet = dataSnapshot.child("idTagSet").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Lien video en dur pour tester

        // Récupération des tags et de leurs temps





        mSeekBar = findViewById(R.id.seek_bar_selected);

        // Rend la seekbar indéplaceable au click
        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });



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
                    fbPlay.setBackgroundColor(getResources().getColor(R.color.colorLightGreenishBlue));
                    fbPlay.setImageResource(android.R.drawable.ic_media_play);

                } else if (mFirstPlay) {
                    mFirstPlay = false;
                    mIsPlayed = true;
                    mAsync = new SeekbarAsync(mSeekBar, mVideoSelected);
                    mAsync.execute();
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    fbPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                    fbPlay.setImageResource(android.R.drawable.ic_media_pause);


                } else {
                    mVideoSelected.start();
                    chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chrono.start();
                    mIsPlayed = true;
                    fbPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
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
                fbPlay.setEnabled(true);
                fbPlay.setVisibility(View.VISIBLE);
                fbPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                fbPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        mVideoSelected.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                chrono.stop();
                mIsPlayed = false;
                fbPlay.setEnabled(false);
                fbPlay.setVisibility(View.INVISIBLE);
            }
        });

        //TODO : mettre valeur calculée
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width)) / 300.0;


        timeLines.setLayoutParams(new FrameLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
        mSeekBar.setLayoutParams(new RelativeLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(PlayVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void makeTimelines() {

        LinearLayout llMain = findViewById(R.id.ll_main_playvideo);
        int tagedLineSize = timeLines.getWidth() - 200;
        int videoDuration = mVideoDuration;
        int titleLength = 200;


        // ArrayList<Pair<Integer, Integer>> timesList = hashMap.get(tagName);

        for (TagModel tagModel : mTagedList) {
            String tagName = tagModel.getName();
            final RelativeLayout timeline = new RelativeLayout(PlayVideoActivity.this);
            timeline.setBackgroundColor(getResources().getColor(R.color.colorCharcoal));
            llMain.addView(timeline);
            mTimelines.put(tagName, timeline);
            TextView tvNameTimeline = new TextView(PlayVideoActivity.this);

            tvNameTimeline.setText(tagName);
            LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(
                    titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTv.setMargins(5, 5, 0, 5);
            tvNameTimeline.setLayoutParams(layoutParamsTv);
            timeline.addView(tvNameTimeline, layoutParamsTv);
            ArrayList<TimeModel> value = tagModel.getTimes();


            for (TimeModel pair : value) {

                int start = pair.getStart();
                int end = pair.getEnd();

                ImageView iv = new ImageView(PlayVideoActivity.this);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(end - start);
                // TODO : Trouver pourquoi ca bug :)
                iv.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(200 + start, 20, 0, 20);
                timeline.setBackgroundResource(R.drawable.style_input);

                    timeline.addView(iv, layoutParams);
            }
        }
    }


    private void initTimeLines() {
        final DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession);
        DatabaseReference tagSessionRef = sessionRef.child("tags");
        tagSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTagedList.clear();
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    TagModel tagModel = tagSnapshot.getValue(TagModel.class);
                    String tagName = tagSnapshot.child("tagName").getValue(String.class);
                    tagModel.setName(tagName);
                    mTagedList.add(tagModel);
                    final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
                    tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mTagModels.clear();
                            for (DataSnapshot tagsSnapshot : dataSnapshot.getChildren()) {
                                String tagsName = tagsSnapshot.child("name").getValue(String.class);
                                TagModel tagModel = tagsSnapshot.getValue(TagModel.class);
                                String coucou = "coucou";
                                if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                                    mTagColorList.put(tagModel.getName(), tagModel.getColor());
                                    for (TagModel taged : mTagedList) {
                                        ArrayList<TimeModel> tagTimeList = taged.getTimes();
                                        String tagedName = taged.getName();
                                        for (TagModel tag : mTagModels) {
                                            String tagName = tag.getName();
                                            if (tagedName.equals(tagName)) {
                                                tag.setTimes(tagTimeList);
                                            }
                                        }
                                        if (!mTagModels.contains(tagModel)) {
                                            mTagModels.add(tagModel);
                                        }

                                    }

                                }
                                mAdapterTags.notifyDataSetChanged();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                makeTimelines();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}


