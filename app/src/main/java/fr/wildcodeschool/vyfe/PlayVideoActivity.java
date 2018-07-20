package fr.wildcodeschool.vyfe;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
    private SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private String mIdSession = mSingletonSessions.getIdSession();
    private String mVideoLink = mSingletonSessions.getFileName();
    private String mTitleSession = mSingletonSessions.getTitleSession();
    private String mIdTagSet;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    HashMap<String, Integer> mTagColorList = new HashMap<>();
    TagRecyclerAdapter mAdapterTags;
    RelativeLayout timeLines;
    Runnable mRunnable;
    long timeWhenStopped = 0;
    int mVideoDuration;
    int mWidth;
    int mLastEnd;
    private Chronometer mChrono;
    private LinearLayout mLlMain;
    private  FloatingActionButton mPlay;
    private ConstraintLayout mConstraintVideo;
    private ProgressBar mLoadProgressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        mLlMain = findViewById(R.id.ll_main_playvideo);
        mConstraintVideo = findViewById(R.id.constraint_video);

        mLoadProgressBar = findViewById(R.id.load_progressbar);

        mDatabase = SingletonFirebase.getInstance().getDatabase();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mTitleSession);

        // Enregistre la taille de l'écran pour la rentrer dans les paramètres des layouts/widgets
        Display display = getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();
        timeLines = findViewById(R.id.time_lines_container);
        timeLines.setLayoutParams(new FrameLayout.LayoutParams(mWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout linearTags = findViewById(R.id.linear_re_tags);
        linearTags.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Applique les paramètres à la seekBar

        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        seekBarParams.setMargins( convertToDp(185), 0, 0, 0);
        mSeekBar = findViewById(R.id.seek_bar_selected);
        mSeekBar.setLayoutParams(seekBarParams);

        // Rend la seekBar incliquable
        mSeekBar.setEnabled(false);

        mVideoSelected = findViewById(R.id.video_view_selected);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double ratio = 1080d / 1920d;
                int previewWidth = mVideoSelected.getWidth();
                int previewHeight = (int) Math.floor(previewWidth * ratio);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mVideoSelected.getLayoutParams();
                params.width = previewWidth;
                params.height = previewHeight;
                mVideoSelected.setLayoutParams(params);
            }
        }, 30);

        mChrono = findViewById(R.id.chronometer_play);
        mPlay = findViewById(R.id.bt_play_selected);

        // Charge le lien de la vidéo dans la videoView, et enregistre sa durée totale
        // (nécessaire au calculs des positions des tags sur la timeline)
        // Lance la méthode qui récupère les données des tags
        mVideoSelected.setVideoPath(mVideoLink);
        mVideoSelected.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoDuration = mVideoSelected.getDuration();
                mChrono.setBase(SystemClock.elapsedRealtime());
                mChrono.start();
                final Handler handler = new Handler();
                mSeekBar.setMax(mVideoDuration);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int position = mVideoSelected.getCurrentPosition();
                        mSeekBar.setProgress(position);
                        handler.postDelayed(mRunnable, 30);
                    }
                };

                handler.post(mRunnable);
                timeWhenStopped = 0;
                mChrono.stop();
                mPlay.setImageResource(android.R.drawable.ic_media_play);
                mIsPlayed = false;

                // TODO : ajouter écran de chargement
                ApiHelperPlay.getTags(mTagedList, mTagModels, new ApiHelperPlay.TagsResponse() {
                    @Override
                    public void onSuccess() {
                        makeTimelines();
                        RecyclerView rvTags = findViewById(R.id.re_tags_selected);
                        mAdapterTags = new TagRecyclerAdapter(mTagModels, mTagedList, "count");
                        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        rvTags.setLayoutManager(layoutManagerTags);
                        rvTags.setAdapter(mAdapterTags);

                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PlayVideoActivity.this, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWait() {


                    }

                    @Override
                    public void onFinish() {
                        mAdapterTags.notifyDataSetChanged();


                    }
                });
            }
        });



        // Bouton play/pause
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayed) {
                    mVideoSelected.pause();
                    timeWhenStopped = mChrono.getBase() - SystemClock.elapsedRealtime();
                    mChrono.stop();
                    mIsPlayed = false;
                    mPlay.setBackgroundColor(getResources().getColor(R.color.colorLightGreenishBlue));
                    mPlay.setImageResource(android.R.drawable.ic_media_play);

                } else {
                    mPlay.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                    mPlay.setImageResource(android.R.drawable.ic_media_pause);
                    mIsPlayed = true;
                    mVideoSelected.start();
                    mChrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    mChrono.start();
                }
            }
        });

        // Bouton replay que remet la vidéo, la seekBar et le chrono à 0
        // Recréé une instance de SeekbarAsync pour éviter les bug liés à la seekBar
        FloatingActionButton btReplay = findViewById(R.id.bt_replay);
        btReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPlayed = true;
                mVideoSelected.seekTo(0);
                mSeekBar.setProgress(0);
                mVideoSelected.start();
                mChrono.setBase(0);
                mChrono.setBase(SystemClock.elapsedRealtime());
                mChrono.start();
                mPlay.setVisibility(View.VISIBLE);
                mPlay.setImageResource(android.R.drawable.ic_media_pause);
                mPlay.setBackgroundColor(getResources().getColor(R.color.color1));
            }
        });


        mVideoSelected.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIsPlayed = false;
                timeWhenStopped = 0;
                mVideoSelected.seekTo(0);
                mSeekBar.setProgress(0);
                mChrono.setBase(0);
                mChrono.setBase(SystemClock.elapsedRealtime());
                mChrono.stop();
                mPlay.setVisibility(View.VISIBLE);
                mPlay.setImageResource(android.R.drawable.ic_media_play);
                mPlay.setBackgroundColor(getResources().getColor(R.color.color1));
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
            //TODO: arreter la video pour eviter crash
            case R.id.logout:
                mSeekBar.setProgress(mVideoDuration);
                Intent intent = new Intent(PlayVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
            case R.id.home:
                mSeekBar.setProgress(mVideoDuration);
                Intent intentHome = new Intent(PlayVideoActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Méthode qui créé les layouts et images des timelines en fonction des données des tags
    public void makeTimelines() {

        int tagedLineSize = mWidth - convertToDp(getResources().getInteger(R.integer.title_length_timeline));
        int titleLength = convertToDp(getResources().getInteger(R.integer.title_length_timeline));

        // Créé une timeline par tags utilisés
        for (final TagModel tagModel : mTagedList) {

            String tagName = tagModel.getName();
            final RelativeLayout timeline = new RelativeLayout(PlayVideoActivity.this);
            mLlMain.addView(timeline);
            TextView tvNameTimeline = new TextView(PlayVideoActivity.this);
            tvNameTimeline.setText(tagName);
            RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                    titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTv.setMargins(convertToDp(15), convertToDp(8), 0, convertToDp(8));
            tvNameTimeline.setLayoutParams(layoutParamsTv);
            tvNameTimeline.setTextSize(convertToDp(10));
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

                final double start =  (startRatio * tagedLineSize) / getResources().getInteger(R.integer.micro_to_milli);
                double end = (endRatio * tagedLineSize) / getResources().getInteger(R.integer.micro_to_milli);

                final ImageView iv = new ImageView(PlayVideoActivity.this);
                RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParamsIv.setMargins((int) Math.floor(titleLength + start), 8, 0, 8);

                iv.setLayoutParams(layoutParamsIv);
                iv.setMinimumHeight(50);
                iv.setMinimumWidth(50);

                iv.setMaxWidth((int) Math.floor(end - start));
                // Permet de se déplacer dans la vidéo en cliquant sur les images
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPlay.setVisibility(View.VISIBLE);
                        Integer millis = first * getResources().getInteger(R.integer.micro_to_milli);
                        long millisLong = first * getResources().getInteger(R.integer.micro_to_milli);
                        mVideoSelected.seekTo(millis);
                        mChrono.setBase(SystemClock.elapsedRealtime() - millisLong);
                        timeWhenStopped = mChrono.getBase() - SystemClock.elapsedRealtime();
                        mSeekBar.setProgress(millis);
                    }
                });

                ApiHelperPlay.getColors(mTagColorList, new ApiHelperPlay.ColorResponse() {
                    @Override
                    public void onSuccess() {
                        iv.setBackgroundColor(mTagColorList.get(tagModel.getName()));

                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PlayVideoActivity.this, "error : " + error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWait() {

                    }

                    @Override
                    public void onFinish() {


                    }
                });


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                timeline.setLayoutParams(layoutParams);
                timeline.setBackgroundColor(getResources().getColor(R.color.colorCharcoalGrey));
                timeline.addView(iv);
                mLastEnd = (int) end;
            }

            RelativeLayout lastRelative = new RelativeLayout(PlayVideoActivity.this);
            RelativeLayout.LayoutParams lastParams = new RelativeLayout.LayoutParams(mVideoDuration - mLastEnd - titleLength, LinearLayout.LayoutParams.MATCH_PARENT);
            lastParams.setMargins(convertToDp(mLastEnd), 20, 0, 20);
            lastRelative.setLayoutParams(lastParams);
            timeline.addView(lastRelative);

            //Thumb adapter à la Timeline
            ViewTreeObserver vto = mSeekBar.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    Drawable thumb = getResources().getDrawable(R.drawable.thumb_blue);
                    int h = mLlMain.getMeasuredHeight();
                    int w = 15;
                    Bitmap bmpOrg = ((BitmapDrawable)thumb).getBitmap();
                    Drawable newThumb = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmpOrg,w,h,true));
                    newThumb.setBounds(0, 0, newThumb.getIntrinsicWidth(), newThumb.getIntrinsicHeight());
                    mSeekBar.setThumb(newThumb);
                    mSeekBar.getViewTreeObserver().removeOnPreDrawListener(this);

                    return true;
                }
            });

        }
        mLoadProgressBar.setVisibility(View.INVISIBLE);
        mConstraintVideo.setVisibility(View.VISIBLE);
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        //TODO: arreter la video pour eviter crash
        Intent intent = new Intent(PlayVideoActivity.this,SelectedVideoActivity.class);
        startActivity(intent);

    }
}
