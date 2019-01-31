package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.vimeo.networking.Configuration;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoFile;
import com.vimeo.networking.model.error.VimeoError;
import com.vimeo.networking.model.playback.Play;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.helper.InternetConnexionHelper;
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
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                mVideoSelectedView.setVideoPath(session.getDeviceVideoLink());
                //TODO : verifier code
                //Verifier connexion internet
                if (InternetConnexionHelper.haveInternetConnection(getActivity()))
                    if (session.getDeviceVideoLink() == null && session.getServerVideoLink() != null) {
                        mVideoSelectedView.setVideoURI(Uri.parse(uploadVimeoMovieLink(session.getDeviceVideoLink(), "clientId", "clientServer", "scope", "testAccountStore")));
                    }
                    else Toast.makeText(getContext(), "Vous devez posseder une connexion Internet pour lire cette vidéo", Toast.LENGTH_SHORT).show();
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
                if (viewModel.isPlaying().getValue()) {
                    viewModel.pause();
                    mVideoSelectedView.pause();
                } else {
                    viewModel.play();
                    mVideoSelectedView.start();


                }

            }
        });
    }

    public String uploadVimeoMovieLink(String linkVimeo, String clientId, String clientServer, String scope, String testAccountStore) {
        //init Client Vimeo
        Configuration.Builder configBuilder =
                new Configuration.Builder(clientId, clientServer, scope, testAccountStore)
                        .setCacheDirectory(getActivity().getCacheDir());
        VimeoClient.initialize(configBuilder.build());

        //Upload link for ViewerVideo
        String uri = linkVimeo;// the video uri; if you have a Video, this is video.uri
        final String[] linkPlayer = new String[1];
        VimeoClient.getInstance().fetchNetworkContent(uri, new ModelCallback<Video>(Video.class) {
            @Override
            public void success(Video video) {
                // use the video

                Play play = video.getPlay();
                if (play != null) {
                    VideoFile dashFile = play.getDashVideoFile();
                    String dashLike = dashFile.getLink();
                    // load link

                    VideoFile hlsFile = play.getHlsVideoFile();
                    String hlsLink = hlsFile.getLink();
                    // load link

                    ArrayList<VideoFile> progressiveFiles = play.getProgressiveVideoFiles();
                    // pick a progressive file to play

                    linkPlayer[0] = dashLike;

                }
            }

            @Override
            public void failure(VimeoError error) {
                linkPlayer[0] = error.getMessage();
            }
        });
        return linkPlayer[0];
    }
}
