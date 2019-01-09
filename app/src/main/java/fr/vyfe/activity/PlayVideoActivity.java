package fr.vyfe.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.fragment.TimelinePlayFragment;
import fr.vyfe.fragment.VideoPlayerFragment;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.PlayVideoViewModel;
import fr.vyfe.viewModel.PlayVideoViewModelFactory;

/**
 * This activity displays the video player with timeline and tag list
 */
public class PlayVideoActivity extends VyfeActivity implements LifecycleOwner {

    private LinearLayout containerLayout;
    private PlayVideoViewModel viewModel;
    private RecyclerView mRecyclerView;
    private TemplateRecyclerAdapter mAdapterTags;
    private ScrollView scrollView;
    private boolean UP = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), sessionId)).get(PlayVideoViewModel.class);
        viewModel.init();

        setContentView(R.layout.activity_play_video);
        replaceFragment(R.id.scrollTimeline, TimelinePlayFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        containerLayout = findViewById(R.id.linear_layoutVideo);
        scrollView = findViewById(R.id.scrollTimeline);
        final ImageView ivSizeContainer = findViewById(R.id.iv_arrow_size);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.re_tags_selected);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManagerTags);

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                getSupportActionBar().setTitle(session.getName());


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
                final int sizePart1 = containerLayout.getMeasuredHeight();
                final int sizePart2 = scrollView.getMeasuredHeight();
                viewModel.setSize2(sizePart2);
                viewModel.setSize1(sizePart1);

                if (timelinesize < sizePart2) {
                    containerSizeAdapter(timelinesize, sizePart1, sizePart2);
                }

            }
        });

        ivSizeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//TODO : enlever le boolean trouver maniere av idDrawable
                if (!UP) {
                    Toast.makeText(PlayVideoActivity.this, "fleche bas", Toast.LENGTH_SHORT).show();
                    chooseSize((viewModel.getSize1().getValue() + viewModel.getSize2().getValue()) / 2, (viewModel.getSize1().getValue() + viewModel.getSize2().getValue() / 2));
                    if (viewModel.getTimelinesize().getValue() < viewModel.getSize1().getValue()) {
                        containerSizeAdapter(viewModel.getTimelinesize().getValue(), viewModel.getSize1().getValue(), viewModel.getSize2().getValue());
                    }
                    UP = true;
                    ivSizeContainer.setBackground(getDrawable(R.drawable.caret_arrow_up));
                } else {
                    chooseSize(0, viewModel.getSize1().getValue() + viewModel.getSize2().getValue());
                    ivSizeContainer.setBackground(getDrawable(R.drawable.arrowdown));
                    UP = false;
                    Toast.makeText(PlayVideoActivity.this, "fleche haut", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void containerSizeAdapter(Integer timelinesize, Integer sizePart1, Integer sizePart2) {
        LinearLayout.LayoutParams layoutParamsPart2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, timelinesize);

        LinearLayout.LayoutParams layoutParamsPart1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (sizePart2 - timelinesize) + sizePart1);

        scrollView.setLayoutParams(layoutParamsPart2);
        containerLayout.setLayoutParams(layoutParamsPart1);
    }

    public void chooseSize(Integer part1, Integer part2) {
        LinearLayout.LayoutParams layoutParamsPart1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, part1);

        LinearLayout.LayoutParams layoutParamsPart2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, part2);


        containerLayout.setLayoutParams(layoutParamsPart1);
        scrollView.setLayoutParams(layoutParamsPart2);


    }
}