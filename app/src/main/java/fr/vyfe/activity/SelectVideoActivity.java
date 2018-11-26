package fr.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.SelectVideoViewModel;
import fr.vyfe.viewModel.SelectVideoViewModelFactory;


public class SelectVideoActivity extends VyfeActivity {

    private SelectVideoViewModel viewModel;

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

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        viewModel = ViewModelProviders.of(this, new SelectVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId())).get(SelectVideoViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {

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

}