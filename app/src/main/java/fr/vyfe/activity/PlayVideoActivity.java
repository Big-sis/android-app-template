package fr.vyfe.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import fr.vyfe.Constants;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.fragment.TimelinePlayFragment;
import fr.vyfe.fragment.VideoPlayerFragment;
import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.PlayVideoViewModel;
import fr.vyfe.viewModel.PlayVideoViewModelFactory;

/**
 * This activity displays the video player with timeline and tag list
 */
public class PlayVideoActivity extends VyfeActivity implements LifecycleOwner {

    private PlayVideoViewModel viewModel;
    private RecyclerView mRecyclerView;
    private TemplateRecyclerAdapter mAdapterTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), sessionId)).get(PlayVideoViewModel.class);
        viewModel.init();

        setContentView(R.layout.activity_play_video);
        //TODO: affichage des tags ne fonctionne pas
        replaceFragment(R.id.scrollTimeline, TimelinePlayFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.re_tags_selected);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManagerTags);

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                getSupportActionBar().setTitle(session.getName());

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
            }
        });


    }
}