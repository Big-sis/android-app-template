package fr.wildcodeschool.vyfe;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

public class PlayVideoActivity extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels;
    private VideoView mVideoSelected;
    private SeekBar mSeekBar;
    private boolean mIsPlayed = false;
    private boolean mFirstPlay = true;
    private String mIdSession;
    private String mVideoLink;
    private SessionsModel mSessionModel;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    HashMap<String, LinearLayout> mTimelines = new HashMap<>();
    final int[] mMarge = {0};

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(titleSession);

          SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        RecyclerView rvTags = findViewById(R.id.re_tags_selected);
        RecyclerView rvTimeLines = findViewById(R.id.re_time_lines_selected);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvTags.setLayoutManager(layoutManagerTags);
        rvTimeLines.setLayoutManager(layoutManagerTime);

        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        final TagRecyclerAdapter adapterTime = new TagRecyclerAdapter(mTagModels, "timelines");
        rvTags.setAdapter(adapterTags);
        rvTimeLines.setAdapter(adapterTime);

        mSeekBar = findViewById(R.id.seek_bar_selected);

        // Rend la seekbar indéplaceable au click
        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mVideoSelected = findViewById(R.id.video_view_selected);


        final String fileName = getIntent().getStringExtra(FILE_NAME);

/*
        File file = new File(fileName);
        if(file.exists()) {
            Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
        }
*/
        mVideoSelected.setVideoPath(fileName);
        final FloatingActionButton fbPlay = findViewById(R.id.bt_play_selected);

        final SeekbarAsync async = new SeekbarAsync(mSeekBar, mVideoSelected);

        fbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayed) {
                    mVideoSelected.pause();
                    mIsPlayed = false;

                } else if (mFirstPlay) {
                    async.execute();
                    mFirstPlay = false;
                    mIsPlayed = true;

                } else {
                    mVideoSelected.start();
                    mIsPlayed = true;
                }
            }
        });

        initTimeline(mTagModels ,rvTags);

    }

    private void initTimeline(final ArrayList<TagModel> listTag, RecyclerView rv) {

        LinearLayout llMain = findViewById(R.id.ll_main_playvideo);
        for (TagModel tagModel : listTag) {
            //TODO: empecher la repetition de nom pour les tags
            String name = tagModel.getName();
            //Ajout d'un Linear pour un tag
            final LinearLayout timeline = new LinearLayout(PlayVideoActivity.this);
            timeline.setBackgroundResource(R.drawable.style_input);
            llMain.addView(timeline);
            mTimelines.put(name, timeline);

        }

        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                ImageView iv = new ImageView(PlayVideoActivity.this);
                //TODO: associer à l'image la couleur du tag
                iv.setBackgroundResource(R.drawable.ico);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(mMarge[0], 0, 0, 0);
                LinearLayout timeline = mTimelines.get(listTag.get(position).getName());
                timeline.addView(iv, layoutParams);
                mMarge[0] += 55;
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }
}


