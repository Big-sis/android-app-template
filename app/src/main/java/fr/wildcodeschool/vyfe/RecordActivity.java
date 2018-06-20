package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;


public class RecordActivity extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels = new ArrayList<>();
    private Camera mCamera;
    private boolean mCamCondition = false;
    private FloatingActionButton mRecord;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private CameraPreview mPreview;
    HashMap<String, LinearLayout> mTimelines = new HashMap<>();
    TextView timerTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        final Chronometer chronometer = findViewById(R.id.chronometer);


        Date d = new Date();
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/" + d.getTime() + ".mp4";

        int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = getCameraInstance(currentCameraId);

        mRecord = findViewById(R.id.bt_record);

        final Button btnBackMain = findViewById(R.id.btn_back_main);
        final Button btnPlay = findViewById(R.id.btn_play);
        final ConstraintLayout sessionRecord = findViewById(R.id.session_record);
        FloatingActionButton btFinish = findViewById(R.id.bt_finish);
        final ImageView ivCheck = findViewById(R.id.iv_check);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        final TextView tvVideoSave = findViewById(R.id.tv_video_save);
        final TextView tvWait = findViewById(R.id.wait);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        final String titleSession = getIntent().getStringExtra("titleSession");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.record_session);

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.start();

                /*
                mPreview = new CameraPreview(RecordActivity.this, mCamera,
                        new CameraPreview.SurfaceCallback() {
                            @Override
                            public void onSurfaceCreated() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startRecording();
                                    }
                                }).start();
                            }
                        });
                FrameLayout preview = findViewById(R.id.video_view);
                preview.addView(mPreview);

                */
                mRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chronometer.stop();
                        // stopRecording();

                    }
                });
            }
        });

        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        final TagRecyclerAdapter adapterTime = new TagRecyclerAdapter(mTagModels, "timelines");
        recyclerTags.setAdapter(adapterTags);


        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionRecord.setVisibility(View.VISIBLE);
                ivCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ivCheck.setImageResource(R.drawable.icons8_coche_96);
                        tvVideoSave.setText(R.string.video_save);
                        tvWait.setVisibility(View.INVISIBLE);
                        btnBackMain.setVisibility(View.VISIBLE);
                        btnPlay.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionRecord.setVisibility(View.GONE);
                Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this, SelectedVideoActivity.class);
                intent.putExtra("titleSession", titleSession);
                intent.putExtra("fileName", mFileName);
                startActivity(intent);
            }
        });

        initTimeline(mTagModels, recyclerTags);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(RecordActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Pour obtenir une instance de la caméra
    public static Camera getCameraInstance(int currentCameraId) {
        Camera c = null;
        try {
            c = Camera.open(currentCameraId);
        } catch (Exception e) {
            e.getMessage();
        }
        return c;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mCamera.unlock();

        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        mRecorder.setOutputFile(mFileName);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.getMessage();
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    private void initTimeline(final ArrayList<TagModel> listTag, RecyclerView rv) {

        //init variable
        final Chronometer chronometer = findViewById(R.id.chronometer);
        final boolean[] titleTimeline = new boolean[listTag.size()];
        final int[] time = new int[listTag.size()];
        for (int i = 0; i < titleTimeline.length; i++) {
            titleTimeline[i] = true;

        }


        //ajout des differentes timelines au conteneur principal
        LinearLayout llMain = findViewById(R.id.ll_main);
        for (TagModel tagModel : listTag) {
            //TODO: empecher la repetition de nom pour les tags
            String name = tagModel.getName();
            //Ajout d'un Linear pour un tag
            final LinearLayout timeline = new LinearLayout(RecordActivity.this);
            timeline.setBackgroundResource(R.drawable.style_input);
            llMain.addView(timeline);
            mTimelines.put(name, timeline);

        }

        //ajout des tags à la timeline associée
        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                for (int i = 0; i < titleTimeline.length; i++) {
                    time[i] = (int) (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                }
                //init image Tag
                ImageView iv = new ImageView(RecordActivity.this);
                iv.setMinimumWidth(10);
                iv.setMinimumHeight(10);
                iv.setBackgroundColor(listTag.get(position).getColor());

                //init name Tag
                TextView tvName = new TextView(RecordActivity.this);
                tvName.setTextColor(Color.WHITE);

                //1er click :Apparition  du nom et marge du tag
                if (titleTimeline[position]) {
                    //titre tag pour le 1er click du tag
                    tvName.setText(listTag.get(position).getName());
                    LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(
                            200, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsTv.setMargins(5, 25, 0, 25);
                    tvName.setLayoutParams(layoutParamsTv);

                }else {
                    //TODO: trouver algo pour le 2eme espacement
                }


                //init LinearLayout timeline
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(time[position], 40, 0, 40);
                LinearLayout timeline = mTimelines.get(listTag.get(position).getName());

                //ajout des differents elements timeline
                timeline.addView(iv, layoutParams);
                timeline.addView(tvName, 0);


                //annule ajout titre si deja inscrit
                titleTimeline[position] = false;

                //test chrono
                timerTextView.setText(String.valueOf(time[position]));

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


    }

}
