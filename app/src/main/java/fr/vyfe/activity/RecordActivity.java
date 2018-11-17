package fr.vyfe.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.vyfe.R;
import fr.vyfe.RestartSession;
import fr.vyfe.fragment.RecordVideoFragment;
import fr.vyfe.fragment.TagsSetFragment;
import fr.vyfe.fragment.TimelineRealTimeFragment;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.RecordVideoViewModel;


/**
 * This activity records in real time session with tags
 */

public class RecordActivity extends VyfeActivity {

    private final String mAuthUserId = mAuth.getCurrentUser().getId();
    private RecordVideoViewModel viewModel;
    private ConstraintLayout contrainOkRecord;
    private ConstraintLayout constraintErrorSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //TODO: cmt utiliser mm fragment que timelineRealTime
        replaceFragment(R.id.scroll_timeline, TimelineRealTimeFragment.newInstance());
        replaceFragment(R.id.scroll_tagset, TagsSetFragment.newInstance());
        replaceFragment(R.id.constraint_video_record, RecordVideoFragment.newInstance());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.record_session);

        Date date = new Date();
        Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy HH:mm");
        String stringdate = dt.format(newDate);

        String idAndroid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        HashCode hashCode = Hashing.sha256().hashString(idAndroid, Charset.defaultCharset());

        SessionModel session = getIntent().getParcelableExtra("SessionModel");
        viewModel = ViewModelProviders.of(RecordActivity.this).get(RecordVideoViewModel.class);
        viewModel.getSession().setIdTagSet(session.getIdTagSet());
        viewModel.getSession().setName(session.getName());
        viewModel.getSession().setTags(session.getTags());

        viewModel.getSession().setAuthor(mAuthUserId);
        viewModel.getSession().setDate(stringdate);
        viewModel.getSession().setIdAndroid(hashCode.toString());

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
                intent.putExtra("SessionModel", viewModel.getSession());
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
                String nameTitleSession = RestartSession.implementTitleGrid(viewModel.getSession().getName());

                viewModel.getSession().setName(nameTitleSession);

                Intent intent = new Intent(RecordActivity.this, CreateSessionActivity.class);
                intent.putExtra("restartSession", viewModel.getSession());

                startActivity(intent);

            }
        });

        viewModel.isRecording().observe(this, new Observer<String>() {
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
        if (viewModel.isRecording().getValue().equals("recording")) {

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
                            dialog.cancel();
                            startActivity(intent);
                        }
                    })
                    .show();

        } else startActivity(intent);
    }

}
