package fr.vyfe.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.fragment.TimelinePlayFragment;
import fr.vyfe.fragment.VideoPlayerFragment;
import fr.vyfe.helper.TimeHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), sessionId)).get(PlayVideoViewModel.class);
        viewModel.init();

        setContentView(R.layout.activity_play_video);
        replaceFragment(R.id.scrollTimeline, TimelinePlayFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        VideocontainerLayout = findViewById(R.id.linear_layoutVideo);
        scrollView = findViewById(R.id.scrollTimeline);
        final ImageView ivSizeContainer = findViewById(R.id.iv_arrow_size);
        mSeekBarTimer = findViewById(R.id.timer_seekbar);
        tvPositionSeek = findViewById(R.id.tv_position_seek);
        tvEndVideo = findViewById(R.id.tv_end_time);
        llprogressvideo = findViewById(R.id.ll_progress_video);
        llInfoProgress = findViewById(R.id.ll_info_progress);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                tvEndVideo.setText(TimeHelper.mllsConvert(session.getDuration()));


            }
        });

        viewModel.getSeekPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mSeekBarTimer.setProgress(integer);
                tvPositionSeek.setText(TimeHelper.mllsConvert(integer));
            }
        });

        viewModel.getTagSet().observe(PlayVideoActivity.this, new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable final TagSetModel tagSet) {
                if (tagSet != null) {
                    viewModel.getSession().observe(PlayVideoActivity.this, new Observer<SessionModel>() {
                        @Override
                        public void onChanged(@Nullable SessionModel session) {
                            mAdapterTags = new TemplateRecyclerAdapter(tagSet.getTemplates(), session, "count");
                            mRecyclerView.setAdapter(mAdapterTags);
                        }
                    });

                }
            }
        });

        viewModel.getTimelinesize().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer timelinesize) {
                //TODO : For adapte size device
                // Video and tagSet is 1er module = sizePart1 (70% vertical)
                // Timeline and time seekBar is second module = sizePart2 (30% vertical)
                final int sizePart1 = VideocontainerLayout.getMeasuredHeight();
                final int sizePart2 = llInfoProgress.getMeasuredHeight();
                final int sizePartProgresse = llprogressvideo.getMeasuredHeight();
                final int sizePart2Create = timelinesize + sizePartProgresse;

                viewModel.setSize2(sizePart2);
                viewModel.setSize1(sizePart1);

                if (sizePart2Create < sizePart2) {
                    containerSizeAdapter(sizePart2Create, (sizePart1 + sizePart2) - sizePart2Create);
                    ivSizeContainer.setVisibility(View.GONE);
                }

            }
        });

        ivSizeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewModel.isFullTimeline().getValue()) {
                    containerSizeAdapter(viewModel.getSize2().getValue(), viewModel.getSize1().getValue());
                    ivSizeContainer.setBackground(getDrawable(R.drawable.caret_arrow_up));
                } else {
                    containerSizeAdapter(viewModel.getSize1().getValue() + viewModel.getSize2().getValue(), 0);
                    ivSizeContainer.setBackground(getDrawable(R.drawable.arrowdown));
                }

                if (viewModel.isFullTimeline().getValue()) viewModel.smallTimeline();
                else viewModel.fullTimline();

            }
        });

    }

    public void containerSizeAdapter(Integer sizePart2Create, Integer sizePart1) {

        LinearLayout.LayoutParams layoutParamsPart2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, sizePart2Create);

        LinearLayout.LayoutParams layoutParamsPart1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (sizePart1));

        llInfoProgress.setLayoutParams(layoutParamsPart2);
        VideocontainerLayout.setLayoutParams(layoutParamsPart1);
    }


}