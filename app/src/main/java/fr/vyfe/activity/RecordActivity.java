package fr.vyfe.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.WindowsAdapter;
import fr.vyfe.fragment.CooperationFragment;
import fr.vyfe.fragment.RecordPlayerFragment;
import fr.vyfe.fragment.TagSetRecordFragment;
import fr.vyfe.fragment.TimelineRecordFragment;
import fr.vyfe.viewModel.RecordVideoViewModel;
import fr.vyfe.viewModel.RecordVideoViewModelFactory;


/**
 * This activity records in real time session with tags
 */

public class RecordActivity extends VyfeActivity {

    public static final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK};
    private RecordVideoViewModel viewModel;
    private ConstraintLayout contrainOkRecord;
    private ConstraintLayout constraintErrorSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        //TODO: cmt utiliser mm fragment que timelineRealTime
        replaceFragment(R.id.scroll_timeline, TimelineRecordFragment.newInstance());
        replaceFragment(R.id.constraint_video_record, RecordPlayerFragment.newInstance());

        ViewPager viewPager = (ViewPager) findViewById(R.id.recording_view_pager);
        setViewPager(viewPager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.mytabs);
        tabLayout.setupWithViewPager(viewPager);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.record_session);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        viewModel = ViewModelProviders.of(this, new RecordVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), sessionId, getDisplayName())).get(RecordVideoViewModel.class);
        viewModel.init();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        constraintErrorSpace = findViewById(R.id.session_error_space);
        contrainOkRecord = findViewById(R.id.session_record);
        Button btnPlay = findViewById(R.id.btn_play);
        Button btnBackMain = findViewById(R.id.btn_back_main);
        Button btnRestart = findViewById(R.id.btn_restart);
        Button btnBackMainError = findViewById(R.id.btn_back_main_error);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this, SelectVideoActivity.class);
                intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.getSessionId());
                startActivity(intent);
            }
        });

        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
            }
        });

        btnBackMainError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this, CreateSessionActivity.class);
                intent.putExtra(Constants.SESSIONTITLE_EXTRA, viewModel.getSession().getValue().getName());
                startActivity(intent);

            }
        });

        viewModel.getStep().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals(RecordVideoViewModel.STEP_SAVE)) {
                    contrainOkRecord.setVisibility(View.VISIBLE);
                }
                if (step.equals(RecordVideoViewModel.STEP_ERROR)) {
                    constraintErrorSpace.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getAreTagsActive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isActiveTags) {
                if (isActiveTags) tabLayout.getTabAt(1).setIcon(R.drawable.users_group);
                else tabLayout.getTabAt(1).setIcon(null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        contrainOkRecord.setVisibility(View.INVISIBLE);
        final Intent intent = new Intent(this, CreateSessionActivity.class);
        saveAlertDialog(intent);
    }

    public void saveAlertDialog(final Intent intent) {
        if (viewModel.isRecording()) {

            //  sessionRecord.setVisibility(View.GONE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
            builder.setMessage(R.string.save_session)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.stop();
                            viewModel.save();

                            startActivity(intent);

                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.close();
                            viewModel.delete();
                            dialog.cancel();
                            startActivity(intent);
                        }
                    })
                    .show();

        } else {
            viewModel.delete();
            startActivity(intent);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                final Intent intentHome = new Intent(this, MainActivity.class);
                saveAlertDialog(intentHome);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewPager(ViewPager viewPager) {
        WindowsAdapter adapter = new WindowsAdapter(super.getSupportFragmentManager());
        adapter.addFragment(new TagSetRecordFragment(), getString(R.string.Grid));
        adapter.addFragment(new CooperationFragment(), getString(R.string.live));
        viewPager.setAdapter(adapter);
    }

    //TODO : if onBackPressed and children is SelectVideoActivity don't delete Session
}
