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

        // Réupération du lien de la video
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

        // Récupération des tags et de leurs temps
        DatabaseReference tagSessionRef = sessionRef.child("tags");
        tagSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    TagModel tagModel = tagSnapshot.getValue(TagModel.class);
                    String tagName = tagSnapshot.child("tagName").getValue(String.class);
                    tagModel.setName(tagName);
                    mTagedList.add(tagModel);
                    mIdTagSet = tagSnapshot.child("idTagSet").getValue(String.class);

                    final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
                    tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot tagsSnapshot : dataSnapshot.getChildren()) {
                                TagModel tagModel = tagsSnapshot.getValue(TagModel.class);
                                String coucou = "coucou";
                                if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                                    mTagColorList.put(tagModel.getName(), tagModel.getColor());
                                    String coucou2 = "coucou";

                                    for (TagModel taged : mTagedList) {
                                        ArrayList<TimeModel> tagTimeList = taged.getTimes();
                                        String tagedName = taged.getName();

                                        for (TagModel tag : mTagModels) {
                                            String tagName = tag.getName();

                                            if (tagedName.equals(tagName)) {
                                                tag.setTimes(tagTimeList);

                                            }
                                        }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*final DatabaseReference tagsSessionRef = mDatabase.getReference(mAuthUserId).child("tagsSession");
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
                        final DatabaseReference fkTagSetRef = mDatabase.getReference(mAuthUserId).child("tagsSession").child(mIdTagSession).child("fkTagSet");
                        fkTagSetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot fkTagSetSnapshot : dataSnapshot.getChildren()) {
                                    final String fkTagSet = fkTagSetSnapshot.getKey();
                                    final DatabaseReference idTagSetRef = fkTagSetRef.child(fkTagSet);
                                    idTagSetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {


                                            mTagList = (HashMap<String, ArrayList<TimeModel>>) dataSnapshot.getValue();
                                            mNewTagList = (HashMap<String, ArrayList<TimeModel>>) dataSnapshot.getValue();
                                            makeTimelines();
                                            final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
                                            tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot tagsSnapshot : dataSnapshot.getChildren()) {
                                                        TagModel tagModel = tagsSnapshot.getValue(TagModel.class);
                                                        String fkTagSet = tagModel.getFkTagSet();
                                                        if (mIdTagSet.contains(fkTagSet)) {
                                                            String tagName = tagModel.getName();
                                                            mTagColorList.put(tagName, tagModel.getColor());
                                                            tagModel.setTimes(tagTimeList);
                                                            mTagModels.add(tagModel);
                                                            mAdapterTags.notifyDataSetChanged();
                                                        }
                                                    }
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
        });*/


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
                fbPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                fbPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        //TODO : mettre valeur calculée
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width)) / 300.0;
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
                iv.setBackgroundColor(mTagColorList.get(tagName));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(200 + start, 20, 0, 20);
                RelativeLayout tagTimeLine = mTimelines.get(tagName);
                timeline.addView(iv, layoutParams);
            }

        }
    }
    /*private void initTimeline(final ArrayList<TagModel> listTag, RecyclerView rv) {
        //Init variable
        final Chronometer chronometer = findViewById(R.id.chronometer);

        //Ajout des differentes timelines au conteneur principal
        LinearLayout llMain = findViewById(R.id.ll_main);
        for (TagModel tagModel : listTag) {
            String name = tagModel.getName();
            //Ajout d'un Linear pour un tag
            final RelativeLayout timeline = new RelativeLayout(RecordActivity.this);
            timeline.setBackgroundColor(getResources().getColor(R.color.colorCharcoal));
            llMain.addView(timeline);
            mTimelines.put(name, timeline);


        }

        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String nameTag = listTag.get(position).getName();
                //init name Tag
                TextView tvNameTimeline = new TextView(RecordActivity.this);
                tvNameTimeline.setTextColor(Color.WHITE);

                boolean isFirstTitle = false;

                if (!newTagList.containsKey(nameTag)) {
                    ArrayList<Pair<Integer, Integer>> rTagList = new ArrayList<>();
                    newTagList.put(nameTag, rTagList);
                    isFirstTitle = true;
                }

                //rapport pour la presentation
                int rapport = 10;

                //Ici on pourra changer les caracteristiques des tags pour la V2. Pour l'instant carac = constantes
                int timeTag = 3 * rapport;
                int beforeTag = 6 * rapport;
                int titleLength = 200;

                //init image Tag
                ImageView iv = new ImageView(RecordActivity.this);
                iv.setMinimumHeight(10);
                iv.setBackgroundColor(listTag.get(position).getColor());

                //init chrono
                int timeActuel = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / (1000 / rapport));

                int startTime = Math.max(0, timeActuel - beforeTag);
                int endTime = timeActuel + timeTag;
                iv.setMinimumWidth(endTime - startTime);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(titleLength + startTime, 20, 0, 20);
                RelativeLayout timeline = mTimelines.get(nameTag);

                if (isFirstTitle) {
                    tvNameTimeline.setText(listTag.get(position).getName());
                    LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(
                            titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsTv.setMargins(5, 5, 0, 5);
                    tvNameTimeline.setLayoutParams(layoutParamsTv);
                    timeline.addView(tvNameTimeline, layoutParamsTv);
                }
                timeline.addView(iv, layoutParams);

                //Pour envoit sur firebase
                Pair<Integer, Integer> timePair = new Pair<>(startTime / rapport, endTime / rapport);
                newTagList.get(nameTag).add(timePair);

                //Scrool automatiquement suit l'ajout des tags
                final HorizontalScrollView scrollView = findViewById(R.id.horizontalScrollView);
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }*/


}


