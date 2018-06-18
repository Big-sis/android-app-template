package fr.wildcodeschool.vyfe;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Random;

public class PlaySelectedVideo extends AppCompatActivity {

    private ArrayList<TagModel> mTagModels;
    private VideoView mVideoSelected;
    private SeekBar mSeekBar;
    private SeekBar mSeekBarTest;
    private boolean isPlayed = false;
    private boolean firstPlay = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_selected_video);



        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();
        mTagModels.add(new TagModel(-3318101, "nameTest1", null, null));

        RecyclerView rvTags = findViewById(R.id.rv_tags);
        RecyclerView rvTimeLines = findViewById(R.id.rv_time_lines);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvTags.setLayoutManager(layoutManagerTags);
        rvTimeLines.setLayoutManager(layoutManagerTime);

        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        final TagRecyclerAdapter adapterTime = new TagRecyclerAdapter(mTagModels, "timelines");
        rvTags.setAdapter(adapterTags);
        rvTimeLines.setAdapter(adapterTime);

        mSeekBar = findViewById(R.id.seekBar);


        View inflatedView = getLayoutInflater().inflate(R.layout.item_tag, null);
         mSeekBarTest = inflatedView.findViewById(R.id.seek_bar_marker);



        mVideoSelected = findViewById(R.id.vv_selected_video);
        String URL = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";
        mVideoSelected.setVideoPath(URL);
        final FloatingActionButton fbPlay = findViewById(R.id.fb_play);

        final MyAnsync async = new MyAnsync();

        fbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayed) {
                    mVideoSelected.pause();
                    isPlayed = false;

                } else if (firstPlay) {
                    async.execute();
                    firstPlay = false;
                    isPlayed = true;

                } else {
                    mVideoSelected.start();
                    isPlayed = true;
                }
            }
        });

    }

    private class MyAnsync extends AsyncTask<Void, Integer, Void> {
        int duration = 0;
        int current = 0;
        @Override
        protected Void doInBackground(Void... voids) {
            mVideoSelected.start();
            duration = mVideoSelected.getDuration();
            do {
                current = mVideoSelected.getCurrentPosition();
                try {
                    publishProgress((int) (current * 100 / duration));
                    if(mSeekBar.getProgress() >= 100 || mSeekBarTest.getProgress() >= 100){

                        break;
                    }
                } catch (Exception e) {
                }
            } while (mSeekBar.getProgress() <= 100 || mSeekBarTest.getProgress() <= 100);

            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mSeekBar.setProgress(values[0]);
            mSeekBarTest.setProgress(values[0]);
        }
    }
}
