package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.vimeo.networking.model.VideoList;
import com.vimeo.networking.model.error.VimeoError;

import java.util.ArrayList;

import fr.vyfe.Constants;
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
    private String mAccessToken;
    private SharedPreferences sharedPreferences;

    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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

        mAccessToken = sharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN, "");

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                if ((session.getDeviceVideoLink() == null) || (!session.getIdAndroid().equals(viewModel.getAndroidId()) && session.getDeviceVideoLink() != null)) {
                    if (InternetConnexionHelper.haveInternetConnection(getActivity()))

                        uploadVimeoMovieLink(session.getServerVideoLink());
                    else
                        Toast.makeText(getContext(), R.string.add_connexion, Toast.LENGTH_SHORT).show();
                } else {
                    mVideoSelectedView.setVideoPath(session.getDeviceVideoLink());
                }
            }
        });

        viewModel.getLinkPlayer().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String link) {
                if (link != null) {
                    mVideoSelectedView.setVideoURI(Uri.parse(link));
                }
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

    public String uploadVimeoMovieLink(final String linkVimeo) {
        final String[] linkPlayer = new String[1];
        //init Client Vimeo

        VimeoClient.initialize(new Configuration.Builder(mAccessToken).build());

        VimeoClient.getInstance().fetchNetworkContent(Constants.VIME_DIRECTION_ME_VIDEO, new ModelCallback<VideoList>(VideoList.class) {
            @Override
            public void success(VideoList videoList) {
                // It's good practice to always make sure that the values the API sends us aren't null
                if (videoList != null && videoList.data != null) {
                    //Search linkMovie
                    for (Video video : videoList.data) {
                        if (video.link.equals(linkVimeo)) {
                            ArrayList<VideoFile> videoFiles = video.files;
                            if (videoFiles != null && !videoFiles.isEmpty()) {
                                VideoFile videoFile = videoFiles.get(0);
                                viewModel.setLinkPlayer(videoFile.getLink());
                            }
                        }
                    }
                }
            }

            @Override
            public void failure(VimeoError error) {
                String errorMessage = error.getDeveloperMessage();
                viewModel.setLinkPlayer(null);
            }
        });

        return linkPlayer[0];
    }
}