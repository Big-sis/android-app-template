package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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

public class PlayVideoActivity extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels;
    private VideoView mVideoSelected;
    private SeekBar mSeekBar;
    private boolean mIsPlayed = false;
    private boolean mFirstPlay = true;
    private String mIdSession;
    private String mVideoLink;
    private SessionsModel mSessionModel;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    HashMap<String, LinearLayout> mTimelines = new HashMap<>();


    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        // NE PAS SUPPRIMER POUR LE MOMENT
        /* Test de récupération du lien avec données en dur :
        mIdSession = "-LFRtUEoDalCtBKJq-l0";
        final DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("videoLink");
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVideoLink = dataSnapshot.getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();
        mTagModels.add(new TagModel(-3318101, "nameTest1", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest2", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest3", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest4", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest5", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest6", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest7", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest8", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest9", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest10", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest11", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest12", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest13", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest14", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest15", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest16", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest17", null, null));
        mTagModels.add(new TagModel(-3318101, "nameTest18", null, null));
        */

        RecyclerView rvTags = findViewById(R.id.re_tags_selected);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvTags.setLayoutManager(layoutManagerTags);


        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        rvTags.setAdapter(adapterTags);

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
                    fbPlay.setImageResource(android.R.drawable.ic_media_play);

                } else if (mFirstPlay) {
                    async.execute();
                    mFirstPlay = false;
                    mIsPlayed = true;
                    fbPlay.setImageResource(android.R.drawable.ic_media_pause);

                } else {
                    mVideoSelected.start();
                    mIsPlayed = true;
                    fbPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        //TODO : mettre valeur calculée
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width)) / 300.0;
        int height = (int) (ratio * 50);
        RelativeLayout timeLines = findViewById(R.id.time_lines_container);

        timeLines.setLayoutParams(new FrameLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
        mSeekBar.setLayoutParams(new RelativeLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));




/*        DatabaseReference tagsSessionRe = mDatabase.getReference(mAuthUserId).child("tagsSession").child(String.valueOf(idTagSession));
        tagsSessionRe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(PlayVideoActivity.this, "Vous n'avez pas de tags enregistrés", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot tagsSessionSnapshot : dataSnapshot.getChildren()) {
                    String fkSession = tagsSessionSnapshot.getValue().toString();
                    Toast.makeText(PlayVideoActivity.this, fkSession, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

*/

        /*
        if (fkSession[0].

                equals(mIdSession))

        {
            final String idTagSession = tagsSessionSnapshot.getKey();
            final DatabaseReference fkTagSetRef = mDatabase.getReference(mAuthUserId).child("tagsSession").child(idTagSession).child("fkTagSet");
            fkTagSetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot fkTagSetSnapshot : dataSnapshot.getChildren()) {
                        String idTagSet = fkTagSetSnapshot.getKey();
                        DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tagsSession").child(idTagSession).child("fkTagSet").child(idTagSet);
                        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                                    TagModel tagModel = (TagModel) tagSnapshot.getValue();
                                    mTagModels.add(tagModel);
                                    Log.i("tagList", tagModel.toString());
                                }
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
        }*/


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
}