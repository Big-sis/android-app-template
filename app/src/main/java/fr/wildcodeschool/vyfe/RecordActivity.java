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
import android.widget.Toast;
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
        final String titleSession = getIntent().getStringExtra("titleSession");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.record_session);

        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
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
        //Init variable
        final Chronometer chronometer = findViewById(R.id.chronometer);
        final boolean[] titleTimeline = new boolean[listTag.size()];
        final int[] margeTag = new int[listTag.size()];
        final int[] previousTime = new int[listTag.size()];
        final boolean[] shortTagBefore = new boolean[listTag.size()];
        for (int i = 0; i < titleTimeline.length; i++) {
            titleTimeline[i] = true;
            margeTag[i] = 0;
        }

        //Ajout des differentes timelines au conteneur principal
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

        //Ajout des tags à la timeline associée
        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Accrocher vous pour la suite

                //Ici on pourras changer les caracteristique des tags pour la V2. Pour l'instant carac = constantes
                //Attention rapport de 10 ex durée = 5s =>50
                int durationTag = 30;
                int beforeTimeTag = 60;

                //init image Tag
                ImageView iv = new ImageView(RecordActivity.this);
                iv.setMinimumHeight(10);
                iv.setBackgroundColor(listTag.get(position).getColor());

                //init chrono
                int timeActuel = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 100);

                //init name Tag
                TextView tvNameTimeline = new TextView(RecordActivity.this);
                tvNameTimeline.setTextColor(Color.WHITE);

                /**Si 1er Tag **/
                if (titleTimeline[position]) {
                    //Ajout titre à la timeline
                    tvNameTimeline.setText(listTag.get(position).getName());
                    LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(
                            200, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsTv.setMargins(5, 25, 0, 25);
                    tvNameTimeline.setLayoutParams(layoutParamsTv);

                    //param 1er tag
                    /** le temps total du tag ne peut pas être créer car pas assez de temps entre click et les 2tag**/
                    if ((timeActuel < beforeTimeTag)) {
                        iv.setMinimumWidth(durationTag);
                        shortTagBefore[position] = true;
                        //TODO: voir si besoin de créer une taille spécifique au tag qui donnerait: taille tag = timeActuel et margeTag = 0;
                    } else {
                        timeActuel = timeActuel - beforeTimeTag;
                        iv.setMinimumWidth(beforeTimeTag + durationTag);
                        shortTagBefore[position] = false;
                    }
                    margeTag[position] = timeActuel;
                    titleTimeline[position] = false;
                } else {
                    /** Si Nbr tag > 1 **/

                    /**Si pas place : Création du tag av seulement sa durée**/
                    if (((timeActuel - previousTime[position]) < beforeTimeTag)) {
                        iv.setMinimumWidth(durationTag);

                        //Calcul de la marge en fonction du tag precedent
                        if (!shortTagBefore[position]) {
                            margeTag[position] = timeActuel - previousTime[position] - durationTag - beforeTimeTag;
                        } else {
                            margeTag[position] = timeActuel - previousTime[position] - durationTag;
                        }
                        shortTagBefore[position] = true;
                    } else {
                        /**Si place creation du tag av duration + beforeTime**/
                        timeActuel = timeActuel - beforeTimeTag;
                        iv.setMinimumWidth(beforeTimeTag + durationTag);

                        //Calcul de la marge en fonction du tag precedent
                        if (!shortTagBefore[position]) {
                            margeTag[position] = timeActuel - previousTime[position] - durationTag - beforeTimeTag;
                        } else {
                            margeTag[position] = timeActuel - previousTime[position] - durationTag;
                        }
                        shortTagBefore[position] = false;
                    }
                }

                //Ajout du tag et titre à la timeline
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(margeTag[position], 40, 0, 40);
                LinearLayout timeline = mTimelines.get(listTag.get(position).getName());
                timeline.addView(iv, layoutParams);
                timeline.addView(tvNameTimeline, 0);

                previousTime[position] = timeActuel;
                //TODO : recuperer previousTime à chaque tag pour envoyer sur firebase

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
