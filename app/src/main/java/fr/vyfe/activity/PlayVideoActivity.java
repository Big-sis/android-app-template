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
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.fragment.TimelineFragment;
import fr.vyfe.fragment.VideoPlayerFragment;
import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.PlayVideoViewModel;
import fr.vyfe.viewModel.PlayVideoViewModelFactory;

/**
 * This activity displays the video player with timeline and tag list
 */
public class PlayVideoActivity extends VyfeActivity implements LifecycleOwner {

    private PlayVideoViewModel viewModel;
    private RecyclerView mRecyclerView;
    private TagRecyclerAdapter mAdapterTags;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(mAuth.getCurrentUser().getId())).get(PlayVideoViewModel.class);
        viewModel.init((SessionModel) getIntent().getParcelableExtra(Constants.SESSIONMODEL_EXTRA));

        setContentView(R.layout.activity_play_video);
        //TODO: affichage des tags ne fonctionne pas
        replaceFragment(R.id.scrollTimeline, TimelineFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(viewModel.getSession().getName());


        mRecyclerView = findViewById(R.id.re_tags_selected);

        mAdapterTags = new TagRecyclerAdapter(viewModel.getSession().getTags(), "count");
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManagerTags);

        mRecyclerView.setAdapter(mAdapterTags);
    }
}