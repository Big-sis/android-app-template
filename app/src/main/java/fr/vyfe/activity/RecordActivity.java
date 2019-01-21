package fr.vyfe.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.RecordAdapter;
import fr.vyfe.fragment.LiveSessionFragment;
import fr.vyfe.fragment.RecordPlayerFragment;
import fr.vyfe.fragment.TagSetRecordFragment;
import fr.vyfe.fragment.TimelineRecordFragment;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.RecordVideoViewModel;
import fr.vyfe.viewModel.RecordVideoViewModelFactory;


/**
 * This activity records in real time session with tags
 */

public class RecordActivity extends VyfeActivity {

    private RecordVideoViewModel viewModel;
    private ConstraintLayout contrainOkRecord;
    private ConstraintLayout constraintErrorSpace;
    public static final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //TODO: cmt utiliser mm fragment que timelineRealTime
        replaceFragment(R.id.scroll_timeline, TimelineRecordFragment.newInstance());
        replaceFragment(R.id.constraint_video_record, RecordPlayerFragment.newInstance());

        ViewPager viewPager = (ViewPager) findViewById(R.id.recording_view_pager);
        setViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mytabs);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.record_session);

        String sessionId = getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA);
        viewModel = ViewModelProviders.of(this, new RecordVideoViewModelFactory(mAuth.getCurrentUser().getId(), mAuth.getCurrentUser().getCompany(), sessionId)).get(RecordVideoViewModel.class);
        viewModel.init();

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
                intent.putExtra(Constants.TAGSETID_EXTRA,viewModel.getSession().getValue().getTagSetId());
                startActivity(intent);

            }
        });

        viewModel.getStep().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals("save")) {
                    contrainOkRecord.setVisibility(View.VISIBLE);
                }
                if (step.equals("error")) {
                    constraintErrorSpace.setVisibility(View.VISIBLE);
                }
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
            startActivity(intent);}
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
        RecordAdapter adapter =  new RecordAdapter(super.getSupportFragmentManager());
        adapter.addFragment(new TagSetRecordFragment(), "Grille");
        adapter.addFragment(new LiveSessionFragment(),"Live");
        viewPager.setAdapter(adapter);
    }

}
