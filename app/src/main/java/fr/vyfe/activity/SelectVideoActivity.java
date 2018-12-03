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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import fr.vyfe.Constants;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.model.CompanyModel;
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
    private Button uploadButton;
    ImageView videoMiniatureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.info_movie);

        Button playBtn = findViewById(R.id.bt_play);
        Button editBtn = findViewById(R.id.btn_edit);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);
        final TextView gridTextView = findViewById(R.id.tv_grid);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        mIvUpload = findViewById(R.id.iv_upload);
        uploadButton = findViewById(R.id.btn_upload);
        videoMiniatureView = findViewById(R.id.vv_preview);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("link");

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        viewModel = ViewModelProviders.of(this, new SelectVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId())).get(SelectVideoViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable final SessionModel session) {
                if (session != null) {
                    if (session.getServerVideoLink() != null) {
                        uploadButton.setTextColor(Color.GRAY);
                        uploadButton.setClickable(false);
                        uploadButton.setText(R.string.online);
                        uploadButton.setAlpha(0.5f);
                    }
                    else {
                        uploadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (InternetConnexionHelper.haveInternetConnection(SelectVideoActivity.this)) {
                                    final AlertDialog.Builder popup = new AlertDialog.Builder(SelectVideoActivity.this);
                                    if (null == viewModel.getCompany().getValue().getVimeoAccessToken()) {
                                        uploadButton.setTextColor(Color.GRAY);
                                        popup.setTitle(R.string.alert);
                                        popup.setMessage(R.string.upload_contact);
                                        popup.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        popup.show();
                                    }
                                    else {
                                        Intent intent = new Intent(SelectVideoActivity.this, UploadVideoService.class);
                                        intent.putExtra(Constants.SESSIONMODEL_EXTRA, session);
                                        intent.putExtra(Constants.VIMEO_TOKEN_EXTRA, viewModel.getCompany().getValue().getVimeoAccessToken());
                                        intent.putExtra(Constants.COMPANYID_EXTRA, mAuth.getCurrentUser().getCompany());
                                        SelectVideoActivity.this.startService(intent);

                                        Toast.makeText(SelectVideoActivity.this, R.string.start_upload, Toast.LENGTH_LONG).show();
                                        mIvUpload.setVisibility(View.VISIBLE);
                                        Glide.with(SelectVideoActivity.this).load(R.drawable.animation_roue_white).into(mIvUpload);
                                    }
                                }
                                else
                                    Toast.makeText(SelectVideoActivity.this, R.string.have_internet_connection, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if (session.getDescription() != null) {
                        tvDescription.setText(session.getDescription());
                    } else {
                        tvDescription.setText(R.string.no_description);
                    }

                    tvTitle.setText(session.getName());

                    //AFfichage miniature
                    videoMiniatureView.setImageBitmap(session.getThumbnail());

                }
            }
        });

        viewModel.getTagSet().observe(this, new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSetModel) {
                if (tagSetModel != null) {
                    TemplateRecyclerAdapter adapterTags = new TemplateRecyclerAdapter(tagSetModel.getTemplates(), "count");
                    recyclerTags.setAdapter(adapterTags);
                }

                assert tagSetModel != null;
                gridTextView.setText(tagSetModel.getName());
            }
        });

        clickButton(playBtn, new Intent(this, PlayVideoActivity.class));
        clickButton(videoMiniatureView, new Intent(this, PlayVideoActivity.class));
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
}