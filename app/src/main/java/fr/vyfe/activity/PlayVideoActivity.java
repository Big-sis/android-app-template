package fr.vyfe.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import fr.vyfe.helper.InternetConnexionHelper;
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
    private  ImageView ivSizeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final HashCode hashCode = Hashing.sha256().hashString(androidId, Charset.defaultCharset());
        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), sessionId,hashCode.toString())).get(PlayVideoViewModel.class);
        viewModel.init();

        setContentView(R.layout.activity_play_video);
        replaceFragment(R.id.scrollTimeline, TimelinePlayFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        VideocontainerLayout = findViewById(R.id.linear_layoutVideo);
        scrollView = findViewById(R.id.scrollTimeline);
        ivSizeContainer = findViewById(R.id.iv_arrow_size);
        mSeekBarTimer = findViewById(R.id.timer_seekbar);
        tvPositionSeek = findViewById(R.id.tv_position_seek);
        tvEndVideo = findViewById(R.id.tv_end_time);
        llprogressvideo = findViewById(R.id.ll_progress_video);
        llInfoProgress = findViewById(R.id.ll_info_progress);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initNavBar(navigationView, toolbar, drawerLayout);

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
                if(session.getTags()!=null) {
                    mAdapterTags = new TemplateRecyclerAdapter( session.getTags(),session.getTagsSet().getTemplates(), "play");
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


        viewModel.getTimelinesize().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer timelinesize) {
                // For adapte size device:
                // Video and tagSet is first module = sizePart1 (65% vertical)
                // Timeline and time seekBar is second module = sizePart2 (45% vertical)
                // if timeline height < 45%  , containertimeline height equal timeline height. And the video can take up more space
                final int videoContainerHeight = VideocontainerLayout.getMeasuredHeight();
                final int timelineContainerHeight = llInfoProgress.getMeasuredHeight();
                final int sizePartProgresse = llprogressvideo.getMeasuredHeight();
                final int sizePart2Create = timelinesize + sizePartProgresse;

                viewModel.setTimelineContainerHeight(timelineContainerHeight);
                viewModel.setVideoContainerHeight(videoContainerHeight);

                if (sizePart2Create < timelineContainerHeight) {
                    containerSizeAdapter(sizePart2Create, (videoContainerHeight + timelineContainerHeight) - sizePart2Create);
                    ivSizeContainer.setVisibility(View.GONE);
                }

            }
        });

        onClickEvent(llprogressvideo);
    }

    private void containerSizeAdapter(Integer sizePart2Create, Integer sizePart1) {

        LinearLayout.LayoutParams layoutParamsPart2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, sizePart2Create);

        LinearLayout.LayoutParams layoutParamsPart1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (sizePart1));

        llInfoProgress.setLayoutParams(layoutParamsPart2);
        VideocontainerLayout.setLayoutParams(layoutParamsPart1);
    }

    private void onClickEvent(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.isFullTimeline().getValue()) {
                    containerSizeAdapter(viewModel.getTimelineContainerHeight().getValue(), viewModel.getVideoContainerHeight().getValue());
                    ivSizeContainer.setBackground(getDrawable(R.drawable.caret_arrow_up));
                } else {
                    containerSizeAdapter((int)Math.round((viewModel.getVideoContainerHeight().getValue() + viewModel.getTimelineContainerHeight().getValue())*0.8),(int) Math.round((viewModel.getVideoContainerHeight().getValue() + viewModel.getTimelineContainerHeight().getValue())*0.2));
                    ivSizeContainer.setBackground(getDrawable(R.drawable.arrowdown));
                }

                if (viewModel.isFullTimeline().getValue()) viewModel.smallTimeline();
                else viewModel.fullTimline();
            }
        });

    }


}