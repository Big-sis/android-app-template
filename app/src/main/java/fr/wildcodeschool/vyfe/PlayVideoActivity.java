package fr.wildcodeschool.vyfe;

import android.graphics.Color;
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
    HashMap<String, Integer> mTagColorList = new HashMap<>();
    TagRecyclerAdapter mAdapterTags;
    RelativeLayout timeLines;
    SeekbarAsync mAsync;
    long timeWhenStopped = 0;
    int mVideoDuration;
    int mWidth;
    private Chronometer mChrono;

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        mIdSession = getIntent().getStringExtra(ID_SESSION);

        mDatabase = SingletonFirebase.getInstance().getDatabase();
        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        // Enregistre la taille de l'écran pour la rentrer dans les paramètres des layouts/widgets
        Display display = getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();
        timeLines = findViewById(R.id.time_lines_container);
        timeLines.setLayoutParams(new FrameLayout.LayoutParams(mWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout linearTags = findViewById(R.id.linear_re_tags);
        linearTags.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Applique les paramètres à la seekBar
        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(mWidth - 200, LinearLayout.LayoutParams.MATCH_PARENT);
        seekBarParams.setMargins(200, 0, 0, 0);
        mSeekBar = findViewById(R.id.seek_bar_selected);
        mSeekBar.setLayoutParams(seekBarParams);
        // Rend la seekBar incliquable
        mSeekBar.setEnabled(false);

        // Charge le lien de la vidéo dans la videoView, et enregistre sa durée totale
        // (nécessaire au calculs des positions des tags sur la timeline)
        // Lance la méthode qui récupère les données des tags
        mVideoLink = getIntent().getStringExtra(FILE_NAME);
        mVideoSelected = findViewById(R.id.video_view_selected);
        mVideoSelected.setMinimumWidth((int) (mWidth * 0.8));
        mVideoSelected.setVideoPath(mVideoLink);
        mVideoSelected.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoDuration = mVideoSelected.getDuration();
                initTimeLines();
            }
        });

        // Requête qui récupère l'ID de la grille, nécessaire à la récupération des données des tags
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

        // Bouton play/pause
        mChrono = findViewById(R.id.chronometer_play);
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

        // Bouton replay que remet la vidéo, la seekBar et le chrono à 0
        // Recréé une instance de SeekbarAsync pour éviter les bug liés à la seekBar
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


        // Remet la vidéo, la seekBar et le chrono à 0 en fin de lecture de la vidéo
        // Oblige l'utilisateur à utiliser le bouton replay
        mVideoSelected.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIsPlayed = false;
                fbPlay.setVisibility(View.INVISIBLE);
                mSeekBar.setProgress(0);
                mVideoSelected.seekTo(0);
                mVideoSelected.pause();
                mChrono.setBase(0);
                mChrono.setBase(SystemClock.elapsedRealtime());
                mChrono.stop();
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
                Intent intent = new Intent(PlayVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Méthode qui créé les layouts et images des timelines en fonction des données des tags
    public void makeTimelines() {

        LinearLayout llMain = findViewById(R.id.ll_main_playvideo);
        int tagedLineSize = timeLines.getWidth() - getResources().getInteger(R.integer.title_length_timeline);
        int titleLength = getResources().getInteger(R.integer.title_length_timeline);

        // Créé une timeline par tags utilisés
        for (final TagModel tagModel : mTagedList) {

            String tagName = tagModel.getName();
            final RelativeLayout timeline = new RelativeLayout(PlayVideoActivity.this);
            timeline.setBackgroundColor(getResources().getColor(R.color.colorCharcoal));
            llMain.addView(timeline);
            TextView tvNameTimeline = new TextView(PlayVideoActivity.this);
            tvNameTimeline.setText(tagName);
            LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(
                    titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTv.setMargins(5, 5, 0, 5);
            tvNameTimeline.setLayoutParams(layoutParamsTv);
            tvNameTimeline.setTextColor(Color.WHITE);

            timeline.addView(tvNameTimeline, layoutParamsTv);
            ArrayList<TimeModel> timeList = tagModel.getTimes();

            // Créé une image par utilisation du tag en cour
            for (final TimeModel pair : timeList) {

                // Valeurs des temps récupérés sur Firebase
                final int first = pair.getStart();
                int second = pair.getEnd();

                // Evite les valeurs trop petites,
                // qui seraient arrondies à 0 dans les variables int "start" et "end"
                int firstMicro = first * getResources().getInteger(R.integer.second_to_micro);
                int secondMicro = second * getResources().getInteger(R.integer.second_to_micro);
                double startRatio = firstMicro / mVideoDuration;
                double endRatio = secondMicro / mVideoDuration;

                final int start = (int) (startRatio * tagedLineSize) / getResources().getInteger(R.integer.micro_to_milli);
                int end = (int) (endRatio * tagedLineSize) / getResources().getInteger(R.integer.micro_to_milli);

                final ImageView iv = new ImageView(PlayVideoActivity.this);
                iv.setMinimumHeight(10);
                iv.setMinimumWidth(end - start);
                // Permet de se déplacer dans la vidéo en cliquant sur les images
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer millis = first * getResources().getInteger(R.integer.micro_to_milli);
                        mVideoSelected.seekTo(millis);
                        long millisLong = first * getResources().getInteger(R.integer.micro_to_milli);
                        mChrono.setBase(SystemClock.elapsedRealtime() - millisLong);
                    }
                });

                // Récupère la couleur du tag en cour pour l'appliquer à l'image
                // TODO (V2) : ajouter les couleurs dans tagModel pour éviter d'avoir à faire une requête
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


    // Méthode qui récupère les données des tags sur Firebase
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

                // Lance la méthode qui créé les timelines losques toutes les données des tags ont été récupérées
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
