package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class VideoPlayerFragment extends Fragment {

    Runnable mRunnable;
    private PlayVideoViewModel viewModel;
    private VideoView mVideoSelectedView;
    private Handler mHandler;
    private ImageView mPlayPause;

    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        mVideoSelectedView = view.findViewById(R.id.video_view_selected);
        mPlayPause = view.findViewById(R.id.button_play);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mVideoSelectedView != null) {
                    int position = mVideoSelectedView.getCurrentPosition();
                    viewModel.setVideoPosition(position);
                }
                mHandler.postDelayed(this, 20);
            }
        };

        mHandler.post(mRunnable);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                mVideoSelectedView.setVideoPath(session.getDeviceVideoLink());

            }
        });

        viewModel.isMoveSeek().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    mVideoSelectedView.seekTo(viewModel.getSeekPosition().getValue());
                }
            }
        });

        viewModel.isPlaying().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {

                if (isPlaying) {
                    mVideoSelectedView.start();
                    mPlayPause.setBackgroundResource(R.drawable.round_pause_button);

                } else {
                    mVideoSelectedView.pause();
                    mPlayPause.setBackgroundResource(R.drawable.play_button);
                }
            }
        });


        // Remet la vidéo à zéro quand la lecture est terminée
        mVideoSelectedView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                viewModel.init();
                mVideoSelectedView.seekTo(0);

            }
        });

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          if(viewModel.isPlaying().getValue()){
              viewModel.pause();
              mVideoSelectedView.pause();
          }else {
              viewModel.play();
              mVideoSelectedView.start();


          }

            }
        });
    }
}
