package fr.wildcodeschool.vyfe;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.constraint.ConstraintLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private String mIdTagSet;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    HashMap<String, RelativeLayout> mTimelines = new HashMap<>();
    HashMap<String, Integer> mTagColorList = new HashMap<>();
    TagRecyclerAdapter mAdapterTags;
    RelativeLayout timeLines;
    SeekbarAsync mAsync;
    long timeWhenStopped = 0;
    int mVideoDuration;
    private Chronometer mChrono;

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mChrono = findViewById(R.id.chronometer_play);

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);
        mDatabase = SingletonFirebase.getInstance().getDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        mIdSession = getIntent().getStringExtra(ID_SESSION);
        mVideoLink = getIntent().getStringExtra(FILE_NAME);

        mVideoSelected = findViewById(R.id.video_view_selected);
        mVideoSelected.setVideoPath(mVideoLink);
        mVideoSelected.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoDuration = mVideoSelected.getDuration();
                initTimeLines();
            }
        });

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

        mSeekBar = findViewById(R.id.seek_bar_selected);
        mSeekBar.setEnabled(false);

        final FloatingActionButton fbPlay = findViewById(R.id.bt_play_selected);
        fbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayed) {
                    mVideoSelected.pause();
                    timeWhenStopped = mChrono.getBase() - SystemClock.elapsedRealtime();
                    mChrono.stop();
                    mIsPlayed = false;
                    fbPlay.setBackgroundColor(getResources().getColor(R.color.colorLightGreenishBlue));
                    fbPlay.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    fbPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                    fbPlay.setImageResource(android.R.drawable.ic_media_pause);
                    mIsPlayed = true;
                    if (mFirstPlay) {
                        mFirstPlay = false;
                        mChrono.setBase(SystemClock.elapsedRealtime());
                        mChrono.start();
                        mAsync = new SeekbarAsync(mSeekBar, mVideoSelected);
                        mAsync.execute();
                    } else {
                        mVideoSelected.start();
                        mChrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                        mChrono.start();
                    }
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
                mChrono.setBase(0);
                mChrono.setBase(SystemClock.elapsedRealtime());
                mChrono.start();
                fbPlay.setEnabled(true);
                fbPlay.setVisibility(View.VISIBLE);
                fbPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                fbPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        mVideoSelected.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mChrono.stop();
                mIsPlayed = false;
                fbPlay.setEnabled(false);
                fbPlay.setVisibility(View.INVISIBLE);
                mSeekBar.setProgress(0);
                mVideoSelected.seekTo(0);
                mChrono.setBase(0);
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        seekBarParams.setMargins(200, 0, 0, 0);
        timeLines.setLayoutParams(new FrameLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
        mSeekBar.setLayoutParams(seekBarParams);
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
        int tagedLineSize = timeLines.getWidth() - getResources().getInteger(R.integer.title_length_timeline);
        int titleLength = getResources().getInteger(R.integer.title_length_timeline);

        for (final TagModel tagModel : mTagedList) {
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
            ArrayList<TimeModel> timeList = tagModel.getTimes();


            for (final TimeModel pair : timeList) {
                final int first = pair.getStart();
                int second = pair.getEnd();

                int firstMicro = first * getResources().getInteger(R.integer.second_to_micro);
                int secondMicro = second * getResources().getInteger(R.integer.second_to_micro);

                double startRatio = firstMicro / mVideoDuration;
                double endRatio = secondMicro / mVideoDuration;

                final int start = (int) (startRatio * tagedLineSize) / getResources().getInteger(R.integer.micro_to_milli);
                int end = (int) (endRatio * tagedLineSize) / getResources().getInteger(R.integer.micro_to_milli);

                final ImageView iv = new ImageView(PlayVideoActivity.this);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(end - start);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer millis = first * getResources().getInteger(R.integer.micro_to_milli);
                        mVideoSelected.seekTo(millis);
                        long millisLong = first * getResources().getInteger(R.integer.micro_to_milli);
                        mChrono.setBase(SystemClock.elapsedRealtime() - millisLong);
                    }
                });
                final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
                tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                            TagModel tagModel = tagSnapshot.getValue(TagModel.class);
                            if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                                mTagColorList.put(tagModel.getName(), tagModel.getColor());
                            }
                        }
                        iv.setBackgroundColor(mTagColorList.get(tagModel.getName()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(getResources().getInteger(R.integer.title_length_timeline) + start, 20, 0, 20);
                timeline.setBackgroundResource(R.drawable.style_input);
                timeline.addView(iv, layoutParams);
            }
        }
    }


    private void initTimeLines() {
        final DatabaseReference tagSessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("tags");
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
                                TagModel tagModel = tagsSnapshot.getValue(TagModel.class);
                                if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                                    if (!mTagModels.contains(tagModel)) {
                                        mTagModels.add(tagModel);
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
                for (TagModel tagModel : mTagModels) {
                    for (TagModel taged : mTagedList) {
                        ArrayList<TimeModel> timeList = taged.getTimes();
                        String tagedName = taged.getName();
                        if (tagedName.equals(tagModel.getName())) {
                            tagModel.setTimes(timeList);
                        }
                    }
                }
                RecyclerView rvTags = findViewById(R.id.re_tags_selected);
                mAdapterTags = new TagRecyclerAdapter(mTagModels, mTagedList, "count");
                RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                rvTags.setLayoutManager(layoutManagerTags);
                rvTags.setAdapter(mAdapterTags);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
