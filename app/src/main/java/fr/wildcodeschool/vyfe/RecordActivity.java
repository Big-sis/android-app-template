package fr.wildcodeschool.vyfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;


public class RecordActivity extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels = new ArrayList<>();
    private Camera mCamera;
    private boolean mCamCondition = false;
    private FloatingActionButton mRecord;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = SingletonFirebase.getInstance().getUid();
    private static String mFileName = null;
    private static String mIdSession = null;
    private MediaRecorder mRecorder = null;
    private CameraPreview mPreview;
    private boolean mBack;

    HashMap<String, RelativeLayout> mTimelines = new HashMap<>();
    HashMap<String, ArrayList<Pair<Integer, Integer>>> newTagList = new HashMap<>();


    public static final String TITLE_VIDEO = "titleVideo";
    public final static String FILE_NAME = "filename";
    public final static String ID_SESSION = "idSession";
    public static final String ID_TAG_SET = "idTagSet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        final Chronometer chronometer = findViewById(R.id.chronometer);

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        Date d = new Date();
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/" + d.getTime() + ".mp4";

        int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = getCameraInstance(currentCameraId);

        mRecord = findViewById(R.id.bt_record);

        final Button btnBackMain = findViewById(R.id.btn_back_main);
        final Button btnPlay = findViewById(R.id.btn_play);
        final ConstraintLayout sessionRecord = findViewById(R.id.session_record);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);
        final String idTagSet = getIntent().getStringExtra(ID_TAG_SET);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                                        mBack = false;
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
                        mRecord.setClickable(false);
                        sessionRecord.setVisibility(View.VISIBLE);
                        Date date = new Date();
                        Date newDate = new Date(date.getTime());
                        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy HH:mm");
                        String stringdate = dt.format(newDate);

                        //TODO: obliger l'utilisateur a arreter l'enregistreement avant d'envoyer sur firebase

                        //Firebase SESSION
                        DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions");
                        sessionRef.keepSynced(true);
                        mIdSession = sessionRef.push().getKey();
                        sessionRef.child(mIdSession).child("name").setValue(titleSession);
                        sessionRef.child(mIdSession).child("author").setValue(mAuthUserId);
                        sessionRef.child(mIdSession).child("videoLink").setValue(mFileName);
                        sessionRef.child(mIdSession).child("date").setValue(stringdate);
                        sessionRef.child(mIdSession).child("idSession").setValue(mIdSession);
                        sessionRef.child(mIdSession).child("idTagSet").setValue(idTagSet);


                        for (Map.Entry<String, ArrayList<Pair<Integer, Integer>>> entry : newTagList.entrySet()) {

                            String tagKey = sessionRef.child(mIdSession).child("tags").push().getKey();
                            sessionRef.child(mIdSession).child("tags").child(tagKey).child("tagName").setValue(entry.getKey());
                            ArrayList<TimeModel> times = new ArrayList<>();

                            for (Pair<Integer, Integer> pair : entry.getValue()) {

                                times.add(new TimeModel(pair.first, pair.second));


                            }
                            sessionRef.child(mIdSession).child("tags").child(tagKey).child("times").setValue(times);

                        }


                        //FIREBASE TAGSSESSION
                        DatabaseReference tagsRef = mDatabase.getReference(mAuthUserId).child("tagsSession");
                        tagsRef.keepSynced(true);
                        String idTag = tagsRef.push().getKey();
                        tagsRef.child(idTag).child("fkSession").setValue(mIdSession);
                        tagsRef.child(idTag).child("fkTagSet").setValue(idTagSet);
                        tagsRef.child(idTag).child("fkTagSet").child(idTagSet).setValue(newTagList);

                    }
                });
            }
        });


        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        recyclerTags.setAdapter(adapterTags);

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
                intent.putExtra(ID_SESSION, mIdSession);
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
                int rapport = getResources().getInteger(R.integer.rapport_timeline);

                //Ici on pourra changer les caracteristiques des tags pour la V2. Pour l'instant carac = constantes
                int durationTag = getResources().getInteger(R.integer.duration_tag) * rapport;
                int beforeTag = getResources().getInteger(R.integer.before_tag) * rapport;
                int titleLength = getResources().getInteger(R.integer.title_length_timeline);

                //init image Tag
                ImageView iv = new ImageView(RecordActivity.this);
                iv.setMinimumHeight(10);
                iv.setBackgroundColor(listTag.get(position).getColor());

                //init chrono
                int timeActuel = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / (1000 / rapport));

                int startTime = Math.max(0, timeActuel - beforeTag);
                int endTime = timeActuel + durationTag;
                iv.setMinimumWidth(endTime - startTime);


                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(convertToDp(titleLength + startTime), convertToDp(10), 0, convertToDp(10));
                RelativeLayout timeline = mTimelines.get(nameTag);

                if (isFirstTitle) {
                    tvNameTimeline.setText(listTag.get(position).getName());
                    LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(
                            convertToDp(titleLength), LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsTv.setMargins(convertToDp(15), convertToDp(5), 0, convertToDp(0));
                    tvNameTimeline.setLayoutParams(layoutParamsTv);
                    timeline.addView(tvNameTimeline, layoutParamsTv);
                }
                timeline.addView(iv, layoutParams);

                //Pour envoit sur firebase
                Pair<Integer, Integer> timePair = new Pair<>(startTime / rapport, endTime / rapport);
                newTagList.get(nameTag).add(timePair);

                //Scrool automatiquement suit l'ajout des tags
                final HorizontalScrollView scrollView = findViewById(R.id.horizontal_scroll_view);
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

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }
}
