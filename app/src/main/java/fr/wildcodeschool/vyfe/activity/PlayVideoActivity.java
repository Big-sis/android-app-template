package fr.wildcodeschool.vyfe.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import fr.wildcodeschool.vyfe.fragment.TimelineFragment;
import fr.wildcodeschool.vyfe.fragment.VideoPlayerFragment;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.viewModel.PlayVideoViewModel;
import fr.wildcodeschool.vyfe.viewModel.PlayVideoViewModelFactory;

/**
 * This activity displays the video player with timeline and tag list
 */
public class PlayVideoActivity extends VyfeActivity implements LifecycleOwner {

    private String sessionId;
    private PlayVideoViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userId = FirebaseAuth.getInstance().getUid();
        sessionId = getIntent().getStringExtra(SelectVideoActivity.ID_SESSION);
        viewModel = ViewModelProviders.of(this, new PlayVideoViewModelFactory(userId, sessionId)).get(PlayVideoViewModel.class);
        viewModel.loadSession(sessionId);

        setContentView(R.layout.activity_play_video);
        replaceFragment(R.id.scrollTimeline, TimelineFragment.newInstance());
        replaceFragment(R.id.constraint_video_player, VideoPlayerFragment.newInstance());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {
                getSupportActionBar().setTitle(sessionModel.getName());
            }
        });
    }
}