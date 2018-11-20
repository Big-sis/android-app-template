package fr.vyfe.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import fr.vyfe.CameraPreview;
import fr.vyfe.R;
import fr.vyfe.view.StopwatchView;
import fr.vyfe.viewModel.RecordVideoViewModel;

import static android.os.Environment.DIRECTORY_MOVIES;

public class RecordVideoFragment extends Fragment {

    private RecordVideoViewModel viewModel;
    private Camera mCamera;
    private FloatingActionButton mRecord;
    private FrameLayout preview;
    private CameraPreview mPreview;
    private StopwatchView chronometer;
    private long freeSpace;
    private MediaRecorder mRecorder;

    //TODO: affichage video flou ???

    public static RecordVideoFragment newInstance() {
        return new RecordVideoFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_video, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);

        int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = getCameraInstance(currentCameraId);



        mPreview = new CameraPreview(getActivity(), mCamera,
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

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //TODO: apl StopwatchView ??? chronometer.getTime()
                viewModel.setTimeChronometer(SystemClock.elapsedRealtime() - chronometer.getBase());
            }
        });

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.playRecord();

                mRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.stop();
                    }
                });
            }
        });

        viewModel.isRecording().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals("recording")) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    startRecording();
                    mRecord.setImageResource(R.drawable.icons8_arr_ter_96);
                } else if (step.equals("stop")) {
                    closeRecord();
                    String sessionId = viewModel.save();
                    if (sessionId != null) {
                        Toast.makeText(getActivity(), "saved ", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        viewModel.error();
                    }



                } else if (step.equals("close")){
                    closeRecord();

                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);
        preview = view.findViewById(R.id.video_view);
        TextView tvSpace = view.findViewById(R.id.tv_space);
        chronometer = view.findViewById(R.id.chronometer);

        //Param Videos
        double ratio = 1080d / 1920d;
        int previewWidth = preview.getWidth();
        int previewHeight = (int) Math.floor(previewWidth * ratio);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) preview.getLayoutParams();
        params.width = previewWidth;
        params.height = previewHeight;
        preview.setLayoutParams(params);

        //TODO: mettre espace dispo dans futurs parametres
        //Stockage dispo
        long totalSpace = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).getTotalSpace();
        freeSpace = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).getFreeSpace();
        long sizefreeSpace = freeSpace * 100 / totalSpace;
        tvSpace.setAlpha(.5f);
        tvSpace.setText(String.format("%s%s%s", getString(R.string.free_storage), String.valueOf(sizefreeSpace), getString(R.string.pourcentage)));

        if (sizefreeSpace < 10) {
            tvSpace.setText(String.format("%s%s", tvSpace.getText(), getString(R.string.fullstorage)));
        }

        mRecord = view.findViewById(R.id.bt_record);
        mRecord.setImageResource(R.drawable.icons8_appel_video_60);

    }

    public void closeRecord() {
        viewModel.stop();
        chronometer.stop();
        stopRecording();
        mRecord.setClickable(false);
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mCamera.unlock();

        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        mRecorder.setOutputFile(viewModel.getSession().getDeviceVideoLink());

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

}
