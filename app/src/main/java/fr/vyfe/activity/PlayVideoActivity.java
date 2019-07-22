package fr.vyfe.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.fragment.TimelinePlayFragment;
import fr.vyfe.fragment.VideoPlayerFragment;
import fr.vyfe.helper.TimeHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.PlayVideoViewModel;
import fr.vyfe.viewModel.PlayVideoViewModelFactory;

/**
 * This activity displays the video player with timeline and tag list
 */
public class PlayVideoActivity extends VyfeActivity implements LifecycleOwner {

    private LinearLayout VideocontainerLayout;
    private PlayVideoViewModel viewModel;
    private RecyclerView mRecyclerView;
    private TemplateRecyclerAdapter mAdapterTags;
    private ScrollView scrollView;
    private SeekBar mSeekBarTimer;
    private TextView tvPositionSeek;
    private TextView tvEndVideo;
    private LinearLayout llprogressvideo;
    private LinearLayout llInfoProgress;
    private ConstraintLayout mContainerPlayer;
    private ImageView ivSizeTimeline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final HashCode hashCode = Hashing.sha256().hashString(androidId, Charset.defaultCharset());
        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), sessionId, hashCode.toString())).get(PlayVideoViewModel.class);
        viewModel.init();

        setContentView(R.layout.activity_play_video);
        replaceFragment(R.id.scrollTimeline, TimelinePlayFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        VideocontainerLayout = findViewById(R.id.linear_layoutVideo);
        scrollView = findViewById(R.id.scrollTimeline);
        mSeekBarTimer = findViewById(R.id.timer_seekbar);
        tvPositionSeek = findViewById(R.id.tv_position_seek);
        tvEndVideo = findViewById(R.id.tv_end_time);
        llprogressvideo = findViewById(R.id.ll_progress_video);
        llInfoProgress = findViewById(R.id.ll_info_progress);
        mContainerPlayer = findViewById(R.id.constraint_video_player);
        ivSizeTimeline = findViewById(R.id.iv_arrow_size);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initNavBar(navigationView, toolbar, drawerLayout);

        viewModel.setPlayerLayoutParamsMutableLiveData(mContainerPlayer.getLayoutParams());

        mRecyclerView = findViewById(R.id.re_tags_selected);

        mSeekBarTimer.getProgressDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN);
        mSeekBarTimer.getThumb().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN);
        mSeekBarTimer.setEnabled(false);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManagerTags);

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                getSupportActionBar().setTitle(session.getName());
                mSeekBarTimer.setMax(session.getDuration());
                tvEndVideo.setText(TimeHelper.formatMillisecTime(session.getDuration()));

                //Create Grid
                if (session.getTags() != null && session.getTagsSet() != null) {
                    mAdapterTags = new TemplateRecyclerAdapter(session.getTags(), session.getTagsSet().getTemplates(), "play");
                    mRecyclerView.setAdapter(mAdapterTags);
                }
            }
        });

        viewModel.getSeekPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer seekPosition) {
                mSeekBarTimer.setProgress(seekPosition);
                tvPositionSeek.setText(TimeHelper.formatMillisecTime(seekPosition));
            }
        });

        onClickEvent(llprogressvideo);
        viewModel.isFullTimeline().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFullTimeline) {

                if (isFullTimeline) {
                    VideocontainerLayout.setLayoutParams(getLayoutParam(2.0f));
                    llInfoProgress.setLayoutParams(getLayoutParam(8.0f));
                } else {
                    VideocontainerLayout.setLayoutParams(getLayoutParam(6.5f));
                    llInfoProgress.setLayoutParams(getLayoutParam(3.5f));
                }
            }
        });

        viewModel.isFullScreen().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFullScreen) {
                if (isFullScreen) {
                    ivSizeTimeline.setBackground(getDrawable(R.drawable.caret_arrow_up));
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1.0f
                    );
                    mContainerPlayer.setLayoutParams(param);
                    llInfoProgress.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);

                } else {
                    ivSizeTimeline.setBackground(getDrawable(R.drawable.arrowdown));
                    llInfoProgress.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mContainerPlayer.setLayoutParams(viewModel.getPlayerLayoutParamsMutableLiveData().getValue());
                }
            }
        });
    }

    private void onClickEvent(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.isFullTimeline().getValue()) viewModel.smallTimeline();
                else viewModel.fullTimline();
            }
        });
    }

    public LinearLayout.LayoutParams getLayoutParam(float size){
       return  new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                size
        );
    }
}