package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;


public class RecordActivity extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels = new ArrayList<>();
    private Camera mCamera;
    private boolean mCamCondition = false;
    private FloatingActionButton mRecord;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private CameraPreview mPreview;
    HashMap<String, LinearLayout> mTimelines = new HashMap<>();

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";

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
        final FloatingActionButton btFinish = findViewById(R.id.bt_finish);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.record_session);


        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        recyclerTags.setAlpha(0.5f);

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                mRecord.setImageResource(R.drawable.icons8_arr_ter_96);
                recyclerTags.setAlpha(1);

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


                mRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chronometer.stop();
                        stopRecording();
                        recyclerTags.setAlpha(0.5f);
                        mRecord.setClickable(false);
                        btFinish.setVisibility(View.VISIBLE);
                        mRecord.setAlpha(0.5f);

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
                Date date = new Date();
                Date newDate = new Date(date.getTime());
                SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy HH:mm:SS Z");
                String stringdate = dt.format(newDate);

                //Firebase SESSION
                DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions");
                String mIdSession = sessionRef.push().getKey();
                sessionRef.child(mIdSession).child("name").setValue(titleSession);
                sessionRef.child(mIdSession).child("author").setValue(mAuthUserId);
                sessionRef.child(mIdSession).child("videoLink").setValue(mFileName);
                sessionRef.child(mIdSession).child("date").setValue(stringdate);
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
                intent.putExtra(TITLE_VIDEO, titleSession);
                intent.putExtra(FILE_NAME, mFileName);
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
        //pour envoit sur firebase
        final int[] totalTime = {0};

        //Ajout des tags à la timeline associée
        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Accrocher vous pour la suite

                //Ici on pourras changer les caracteristique des tags pour la V2. Pour l'instant carac = constantes
                //Attention rapport de 10 ex durée = 5s =>50
                int leftOffsetTag = 30;
                int rigthOffsetTag = 60;

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
                    if ((timeActuel < rigthOffsetTag)) {
                        iv.setMinimumWidth(leftOffsetTag);
                        shortTagBefore[position] = true;
                        //TODO: voir si besoin de créer une taille spécifique au tag qui donnerait: taille tag = timeActuel et margeTag = 0;
                    } else {
                        timeActuel = timeActuel - rigthOffsetTag;
                        iv.setMinimumWidth(rigthOffsetTag + leftOffsetTag);
                        shortTagBefore[position] = false;
                    }
                    margeTag[position] = timeActuel;
                    titleTimeline[position] = false;
                } else {
                    /** Si Nbr tag > 1 **/

                    /**Si pas place : Création du tag av seulement sa durée**/
                    if (((timeActuel - previousTime[position]) < rigthOffsetTag)) {
                        iv.setMinimumWidth(leftOffsetTag);

                        //Calcul de la marge en fonction du tag precedent
                        if (!shortTagBefore[position]) {
                            margeTag[position] = timeActuel - previousTime[position] - leftOffsetTag - rigthOffsetTag;
                        } else {
                            margeTag[position] = timeActuel - previousTime[position] - leftOffsetTag;
                        }
                        shortTagBefore[position] = true;
                    } else {
                        /**Si place creation du tag av duration + beforeTime**/
                        timeActuel = timeActuel - rigthOffsetTag;
                        iv.setMinimumWidth(rigthOffsetTag + leftOffsetTag);

                        //Calcul de la marge en fonction du tag precedent
                        if (!shortTagBefore[position]) {
                            margeTag[position] = timeActuel - previousTime[position] - leftOffsetTag - rigthOffsetTag;
                        } else {
                            margeTag[position] = timeActuel - previousTime[position] - leftOffsetTag;
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
                //Pour envoit sur firebase
                totalTime[0] += previousTime[position];

                //TODO: envoyer sur firebase

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
