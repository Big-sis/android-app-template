package fr.wildcodeschool.vyfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity records in real time session with tags
 */


public class RecordActivity extends AppCompatActivity {

    public static final String ID_TAG_SET = "idTagSet";
    public static boolean RESTART = false;
    private static String mFileName = null;
    private static String mIdSession = null;
    final String mAuthUserId = SingletonFirebase.getInstance().getUid();
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    int mWidth;
    HashMap<String, RelativeLayout> mTimelines = new HashMap<>();
    HashMap<String, ArrayList<Pair<Integer, Integer>>> newTagList = new HashMap<>();
    private ArrayList<TagModel> mTagModels = new ArrayList<>();
    private Camera mCamera;
    private boolean mCamCondition = false;
    private FloatingActionButton mRecord;
    private MediaRecorder mRecorder = null;
    private CameraPreview mPreview;
    private boolean mBack;
    private boolean mActiveTag = false;
    private String mTitleSession;
    private SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private TagRecyclerAdapter mAdapterTags;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        final Chronometer chronometer = findViewById(R.id.chronometer);

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        mTitleSession = mSingletonSessions.getTitleSession();

        Date d = new Date();
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/" + d.getTime() + ".mp4";


        mSingletonSessions.setFileName(mFileName);

        int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = getCameraInstance(currentCameraId);

        mRecord = findViewById(R.id.bt_record);
        mRecord.setImageResource(R.drawable.icons8_appel_video_60);

        final Button btnBackMain = findViewById(R.id.btn_back_main);
        final Button btnPlay = findViewById(R.id.btn_play);
        Button btnRestart = findViewById(R.id.btn_restart);
        final ConstraintLayout sessionRecord = findViewById(R.id.session_record);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        final String idTagSet = getIntent().getStringExtra(ID_TAG_SET);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.record_session);


        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        recyclerTags.setAlpha(0.5f);

        final FrameLayout preview = findViewById(R.id.video_view);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double ratio = 1080d / 1920d;
                int previewWidth = preview.getWidth();
                int previewHeight = (int) Math.floor(previewWidth * ratio);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) preview.getLayoutParams();
                params.width = previewWidth;
                params.height = previewHeight;
                preview.setLayoutParams(params);
            }
        }, 30);

        mPreview = new CameraPreview(RecordActivity.this, mCamera,
                new CameraPreview.SurfaceCallback() {
                    @Override
                    public void onSurfaceCreated() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }).start();
                    }
                });
        preview.addView(mPreview);


        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActiveTag = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                startRecording();
                mBack = false;
                mRecord.setImageResource(R.drawable.icons8_arr_ter_96);
                recyclerTags.setAlpha(1);


                mRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActiveTag = false;
                        chronometer.stop();
                        stopRecording();
                        mRecord.setClickable(false);
                        sessionRecord.setVisibility(View.VISIBLE);
                        Date date = new Date();
                        Date newDate = new Date(date.getTime());
                        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy HH:mm");
                        String stringdate = dt.format(newDate);

                        String idAndroid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        HashCode hashCode = Hashing.sha256().hashString(idAndroid, Charset.defaultCharset());

                        //Firebase SESSION
                        DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions");
                        sessionRef.keepSynced(true);
                        mIdSession = sessionRef.push().getKey();
                        mSingletonSessions.setIdSession(mIdSession);
                        sessionRef.child(mIdSession).child("name").setValue(mTitleSession);
                        sessionRef.child(mIdSession).child("author").setValue(mAuthUserId);
                        sessionRef.child(mIdSession).child("videoLink").setValue(mFileName);
                        sessionRef.child(mIdSession).child("date").setValue(stringdate);
                        sessionRef.child(mIdSession).child("idSession").setValue(mIdSession);
                        sessionRef.child(mIdSession).child("idTagSet").setValue(idTagSet);

                        //TODO rajout firebase
                        sessionRef.child(mIdSession).child("idAndroid").setValue(hashCode.toString());



                        for (Map.Entry<String, ArrayList<Pair<Integer, Integer>>> entry : newTagList.entrySet()) {

                            String tagKey = sessionRef.child(mIdSession).child("tags").push().getKey();
                            sessionRef.child(mIdSession).child("tags").child(tagKey).child("tagName").setValue(entry.getKey());
                            ArrayList<TimeModel> times = new ArrayList<>();

                            for (Pair<Integer, Integer> pair : entry.getValue()) {

                                times.add(new TimeModel(pair.first, pair.second));


                            }
                            sessionRef.child(mIdSession).child("tags").child(tagKey).child("times").setValue(times);

                        }

/*
                        //FIREBASE TAGSSESSION
                        DatabaseReference tagsRef = mDatabase.getReference(mAuthUserId).child("tagsSession");
                        tagsRef.keepSynced(true);
                        String idTag = tagsRef.push().getKey();
                        tagsRef.child(idTag).child("fkSession").setValue(mIdSession);
                        tagsRef.child(idTag).child("fkTagSet").setValue(idTagSet);
                        tagsRef.child(idTag).child("fkTagSet").child(idTagSet).setValue(newTagList);*/

                    }
                });
            }
        });


        mAdapterTags = new TagRecyclerAdapter(mTagModels, "record");
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        recyclerTags.setAdapter(mAdapterTags);

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
                startActivity(intent);
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RESTART = true;
                //TODO: Modif lors du changement du Singleton
                String nameTitleSession = RestartSession.implementTitleGrid(mSingletonSessions.getTitleSession());

                RestartSession restartSession = new RestartSession(nameTitleSession,idTagSet);
                Intent intent = new Intent(RecordActivity.this, StartActivity.class);
                intent.putExtra("restartSession",restartSession );

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
                DisconnectionAlert.confirmedDisconnection(RecordActivity.this);

            case R.id.home:
                Intent intentHome = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mCamera.unlock();

        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
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
        final StopwatchView chronometer = findViewById(R.id.chronometer);

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
                if (mActiveTag) {
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
                    int rapport = getResources().getInteger(R.integer.rapport_timeline);

                    //Ici on pourra changer les caracteristiques des tags pour la V2. Pour l'instant carac = constantes
                    int durationTag = getResources().getInteger(R.integer.duration_tag) * rapport;
                    int beforeTag = getResources().getInteger(R.integer.before_tag) * rapport;
                    int titleLength = getResources().getInteger(R.integer.title_length_timeline);

                    //init image Tag
                    ImageView iv = new ImageView(RecordActivity.this);
                    RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                            titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsIv.setMargins(0, convertToDp(8), 0, convertToDp(8));
                    iv.setLayoutParams(layoutParamsIv);
                    iv.setMinimumHeight(50);
                    try {
                        iv.setBackgroundResource(ColorHelper.convertColor(listTag.get(position).getColorName()));
                    } catch (ColorNotFoundException e) {
                        e.printStackTrace();
                    }

                    //init chrono
                    int timeActuel = (int) ((chronometer.getTime() / (1000 / rapport)));
                    int startTime = Math.max(0, timeActuel - beforeTag);
                    int endTime = timeActuel + durationTag;
                    iv.setMinimumWidth(endTime - startTime);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(convertToDp(titleLength + startTime + convertToDp(15)), convertToDp(10), 0, convertToDp(10));
                    RelativeLayout timeline = mTimelines.get(nameTag);

                    if (isFirstTitle) {
                        tvNameTimeline.setText(listTag.get(position).getName());
                        RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                                convertToDp(titleLength), LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsTv.setMargins(convertToDp(15), convertToDp(8), convertToDp(8), convertToDp(8));
                        tvNameTimeline.setLayoutParams(layoutParamsTv);
                        tvNameTimeline.setTextSize(convertToDp(10));
                        timeline.addView(tvNameTimeline, layoutParamsTv);
                    }
                    timeline.addView(iv, layoutParams);

                    //Pour envoit sur firebase
                    Pair<Integer, Integer> timePair = new Pair<>(startTime / rapport, endTime / rapport);
                    newTagList.get(nameTag).add(timePair);
                    int count = listTag.get(position).getCount();
                    count++;
                    listTag.get(position).setCount(count);
                    mAdapterTags.notifyDataSetChanged();

                    //Scrool automatiquement suit l'ajout des tags
                    final HorizontalScrollView scrollView = findViewById(R.id.horizontal_scroll_view);
                    ScrollHelper.RigthScroll(scrollView);

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecordActivity.this, StartActivity.class);
        startActivity(intent);
    }
}
