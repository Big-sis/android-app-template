package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.view.StopwatchView;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class VideoPlayerFragment extends Fragment {

    private PlayVideoViewModel viewModel;
    private StopwatchView mChronoView;
    private FloatingActionButton mPlayButtonView;
    private VideoView mVideoSelectedView;
    private FloatingActionButton mButtonReplay;
    private Handler mHandler;


    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                mVideoSelectedView.setVideoPath(session.getDeviceVideoLink());
            }
        });

        viewModel.getVideoPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                //mVideoSelectedView.seekTo(position);
                //mChronoView.setTime(position);
            }
        });

        viewModel.isPlaying().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {
                if (isPlaying){
                    mVideoSelectedView.start();
                    mChronoView.start();
                    mPlayButtonView.setBackgroundColor(getResources().getColor(R.color.colorFadedOrange));
                    mPlayButtonView.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mVideoSelectedView.pause();
                    mChronoView.stop();
                    mPlayButtonView.setBackgroundColor(getResources().getColor(R.color.colorLightGreenishBlue));
                    mPlayButtonView.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });

        mHandler = new Handler();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mVideoSelectedView != null) {
                    int position = mVideoSelectedView.getCurrentPosition();
                    viewModel.setVideoPosition(position/1000);
                }
                mHandler.postDelayed(this, 20);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);
        mVideoSelectedView = view.findViewById(R.id.video_view_selected);
        mChronoView = view.findViewById(R.id.chronometer_play);
        mPlayButtonView = view.findViewById(R.id.bt_play_selected);

        mPlayButtonView.setImageResource(android.R.drawable.ic_media_play);
        mButtonReplay = view.findViewById(R.id.bt_replay);

        // Bouton play/pause
        mPlayButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.isPlaying().getValue()) {
                    viewModel.pause();
                } else {
                    viewModel.play();
                }
            }
        });

        // Bouton replay remet la vidéo, la seekBar et le chrono à 0
        mButtonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.play();
                viewModel.setVideoPosition(0);
            }
        });

        // Remet la vidéo à zéro quand la lecture est terminée
        mVideoSelectedView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                viewModel.pause();
                viewModel.setVideoPosition(0);
            }
        });






    }




}
