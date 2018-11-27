package fr.vyfe.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import fr.vyfe.Constants;
import fr.vyfe.model.VimeoTokenModel;
import fr.vyfe.service.UploadVideoService;
import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.SelectVideoViewModel;
import fr.vyfe.viewModel.SelectVideoViewModelFactory;

//TODO: Mise en place de Fragment?
public class SelectVideoActivity extends VyfeActivity {

    private SelectVideoViewModel viewModel;
    private IntentFilter mIntentFilter;
    private ImageView mIvUpload;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("link")) {
                String vimeoLink = intent.getStringExtra("videoLink");
                viewModel.getSession().getValue().setVideoLink(vimeoLink);
                viewModel.save();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        Button playBtn = findViewById(R.id.bt_play);
        Button editBtn = findViewById(R.id.btn_edit);
        ImageView videoBtn = findViewById(R.id.vv_preview);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        mIvUpload = findViewById(R.id.iv_upload);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("link");

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        viewModel = ViewModelProviders.of(this, new SelectVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId())).get(SelectVideoViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                RecyclerView recyclerTags = findViewById(R.id.re_tags);
                TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(session.getTags(), "count");
                RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerTags.setLayoutManager(layoutManagerTags);
                recyclerTags.setAdapter(adapterTags);

                if (session.getServerVideoLink() != null) {
                    upload.setClickable(false);
                    upload.setText(R.string.online);
                    upload.setAlpha(0.5f);
                }
              
                if (session.getDescription() != null) {
                    tvDescription.setText(session.getDescription());
                } else {
                    tvDescription.setText(R.string.no_description);
                }

                tvTitle.setText(session.getName());
            }
        });

        viewModel.getTagSet().observe(this, new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSetModel) {
                if (tagSetModel != null) {
                    TemplateRecyclerAdapter adapterTags = new TemplateRecyclerAdapter(tagSetModel.getTemplates(), "count");
                    recyclerTags.setAdapter(adapterTags);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean connexion = viewModel.getHaveInternetConnexion(SelectVideoActivity.this).getValue();
                assert connexion != null;
                if (connexion) {
                    viewModel.getVimeoToken().observe(SelectVideoActivity.this, new Observer<VimeoTokenModel>() {
                        @Override
                        public void onChanged(@Nullable VimeoTokenModel vimeoTokenModel) {
                            final AlertDialog.Builder popup = new AlertDialog.Builder(SelectVideoActivity.this);
                            if (vimeoTokenModel.getVimeoAccessToken() != null) {
                                viewModel.getSession().observe(SelectVideoActivity.this, new Observer<SessionModel>() {
                                    @Override
                                    public void onChanged(@Nullable SessionModel sessionModel) {
                                        if (viewModel.getSession().getValue().getVideoLink() == null) {

                                            Intent intent = new Intent(SelectVideoActivity.this, UploadVideoService.class);
                                            intent.putExtra("deviceUrl", viewModel.getSession().getValue().getDeviceVideoLink());
                                            intent.putExtra("vimeoToken", viewModel.getVimeoToken().getValue().getVimeoAccessToken());
                                            SelectVideoActivity.this.startService(intent);

                                            Toast.makeText(SelectVideoActivity.this, R.string.start_upload, Toast.LENGTH_LONG).show();
                                            mIvUpload.setVisibility(View.VISIBLE);
                                            Glide.with(SelectVideoActivity.this).load(R.drawable.animation_roue_white).into(mIvUpload);
                                        } else
                                            Toast.makeText(SelectVideoActivity.this, R.string.upload_vimeo, Toast.LENGTH_SHORT).show();
                                            mIvUpload.setVisibility(View.INVISIBLE);
                                    }
                                });


                            } else {
                                upload.setTextColor(Color.GRAY);
                                popup.setTitle(R.string.alert);
                                popup.setMessage(R.string.upload_contact);
                                popup.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                popup.show();
                            }
                        }
                    });
                } else
                    Toast.makeText(SelectVideoActivity.this, R.string.have_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        clickButton(playBtn, new Intent(this, PlayVideoActivity.class));
        clickButton(videoBtn, new Intent(this, PlayVideoActivity.class));
        clickButton(editBtn, new Intent(this, EditSessionActivity.class));
    }

    private void clickButton(View view, final Intent intent) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.getSessionId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

}