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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import java.util.Collections;
import java.util.Comparator;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.ConvertTagsToImageView;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class VideoPlayerFragment extends Fragment {

    Runnable mRunnable;
    private PlayVideoViewModel viewModel;
    private VideoView mVideoSelectedView;
    private Handler mHandler;
    private ImageView mPlayPause;
    private String mAccessToken;
    private SharedPreferences sharedPreferences;
    private ImageView mButtonPlayPauseVideo;
    private SeekBar mSeekBar;
    private ImageView ivOpenTimeline;
    private ImageView mIvExtend;
    private RelativeLayout mTimeline;
    private int widthTimeline;


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
        mButtonPlayPauseVideo = view.findViewById(R.id.button_play_video);
        mSeekBar = view.findViewById(R.id.seekbar_video);
        ivOpenTimeline = view.findViewById(R.id.iv_view_timeline_video);
        mIvExtend = view.findViewById(R.id.iv_extend);
        mTimeline = view.findViewById(R.id.timeline);
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
        widthTimeline = mTimeline.getWidth();
        mAccessToken = sharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN, "");

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                if ((session.getDeviceVideoLink() == null) || session.getIdAndroid() == null || (!session.getIdAndroid().equals(viewModel.getAndroidId()) && session.getDeviceVideoLink() != null)) {
                    if (InternetConnexionHelper.haveInternetConnection(getActivity()))

                        uploadVimeoMovieLink(session.getServerVideoLink());
                    else
                        Toast.makeText(getContext(), R.string.add_connexion, Toast.LENGTH_SHORT).show();
                } else {
                    mVideoSelectedView.setVideoPath(session.getDeviceVideoLink());
                }

                if (session == null) return;
                mSeekBar.setMax(session.getDuration());

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
                    mButtonPlayPauseVideo.setBackgroundResource(R.drawable.round_pause_button);

                } else {
                    mVideoSelectedView.pause();
                    mPlayPause.setBackgroundResource(R.drawable.play_button);
                    mButtonPlayPauseVideo.setBackgroundResource(R.drawable.play_button);
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

        mButtonPlayPauseVideo.setOnClickListener(new View.OnClickListener() {
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

        //Manager Full screen
        mIvExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewModel.isFullScreen().getValue().booleanValue()) {
                    viewModel.fullMovie();
                    viewModel.openTimelineVideo();
                    mIvExtend.setBackgroundResource(R.drawable.reduce);

                } else {
                    viewModel.miniMovie();
                    viewModel.closeTimelineVideo();
                    mIvExtend.setBackgroundResource(R.drawable.extend);
                }
            }
        });

        ivOpenTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.isOpenTimelineVideo() != null && viewModel.isOpenTimelineVideo().getValue()) {
                    viewModel.closeTimelineVideo();

                } else {
                    viewModel.openTimelineVideo();
                }
            }
        });

        viewModel.isOpenTimelineVideo().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean && viewModel.isFullScreen().getValue()) {
                    ivOpenTimeline.setBackgroundResource(R.drawable.arrowdown);
                    ivOpenTimeline.setAlpha(0.9f);
                    mTimeline.setVisibility(View.VISIBLE);
                    mTimeline.setAlpha(0.9f);
                    mSeekBar.setVisibility(View.VISIBLE);

                } else {
                    ivOpenTimeline.setBackgroundResource(R.drawable.caret_arrow_up);
                    mTimeline.setVisibility(View.GONE);
                    mSeekBar.setVisibility(View.GONE);
                }
            }
        });

        viewModel.isFullScreen().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    ivOpenTimeline.setVisibility(View.VISIBLE);
                    mPlayPause.setVisibility(View.GONE);
                    mButtonPlayPauseVideo.setVisibility(View.VISIBLE);
                } else {
                    ivOpenTimeline.setVisibility(View.GONE);
                    mPlayPause.setVisibility(View.VISIBLE);
                    mButtonPlayPauseVideo.setVisibility(View.GONE);
                    viewModel.closeTimelineVideo();
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewModel.setSeekPosition(i);
                viewModel.setVideoPosition(i);
                viewModel.isMoveSeek().setValue(false);
                ConstraintLayout.LayoutParams seekBarParams = new ConstraintLayout.LayoutParams(40, 40);
                seekBarParams.setMargins(i, 0, 0, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                viewModel.isMoveSeek().setValue(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.isMoveSeek().setValue(true);
            }
        });

        viewModel.getVideoPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer positionVideo) {
                mSeekBar.setProgress(positionVideo);

                if (viewModel.getSession().getValue() != null && viewModel.getSession().getValue().getTags() != null) {
                    Collections.sort(viewModel.getSession().getValue().getTags(), new Comparator<TagModel>() {
                        @Override
                        public int compare(TagModel o1, TagModel o2) {
                            return o1.getStart() - o2.getStart();
                        }
                    });
                    new ConvertTagsToImageView(getContext(), mTimeline, Constants.SIZE_WINDOWS_TIMELINE, Constants.LOW_SPEED, positionVideo).getTimeline(viewModel.getSession().getValue().getTags());
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
