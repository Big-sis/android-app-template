package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private ArrayList<TagModel> mTagModels = new ArrayList<>();
    private Camera mCamera;
    private SurfaceView mCamView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mCamCondition = false;
    private FloatingActionButton mCap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        mCamView = findViewById(R.id.video_view);
        mSurfaceHolder = mCamView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.record_session);

        mCap = findViewById(R.id.bt_record_stop);
        mCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCap.setBackgroundColor(getResources().getColor(R.color.colorRosyPink));
                mCamera.takePicture(null, null, null, mPictureCallback);
            }
        });

        RecyclerView recyclerTags = findViewById(R.id.re_tags);
        RecyclerView recyclerTime = findViewById(R.id.re_time_lines);

        SingletonTags singletonTags = SingletonTags.getInstance();
        mTagModels = singletonTags.getmTagsList();

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);
        recyclerTime.setLayoutManager(layoutManagerTime);

        final TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, "record");
        final TagRecyclerAdapter adapterTime = new TagRecyclerAdapter(mTagModels, "timelines");
        recyclerTags.setAdapter(adapterTags);
        recyclerTime.setAdapter(adapterTime);

        FloatingActionButton btFinish = findViewById(R.id.bt_finish);
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelectedVideo = new Intent(RecordActivity.this, SelectedVideoActivity.class);
                startActivity(toSelectedVideo);
            }
        });
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream("/video_" + System.currentTimeMillis() + ".jpg");
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamCondition) {
            mCamera.stopPreview();
            mCamCondition = false;
        }

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                mCamCondition = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        mCamCondition = false;
    }
}
