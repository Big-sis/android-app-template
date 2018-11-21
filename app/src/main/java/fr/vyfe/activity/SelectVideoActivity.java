package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.SelectVideoViewModel;


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

        viewModel = ViewModelProviders.of(this).get(SelectVideoViewModel.class);
        viewModel.init((SessionModel) getIntent().getParcelableExtra(Constants.SESSIONMODEL_EXTRA));

        RecyclerView recyclerTags = findViewById(R.id.re_tags);
        TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(viewModel.getSession().getTags(), "count");
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);
        recyclerTags.setAdapter(adapterTags);

        if (viewModel.getSession().getDescription() != null) {
            tvDescription.setText(viewModel.getSession().getDescription());
        } else {
            tvDescription.setText(R.string.no_description);
        }

        tvTitle.setText(viewModel.getSession().getName());


        clickButton(playBtn, new Intent(this, PlayVideoActivity.class));
        clickButton(videoBtn, new Intent(this, PlayVideoActivity.class));
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectVideoActivity.this, EditSessionActivity.class);
                intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.getSession().getId());
                startActivity(intent);
            }
        });
    }

    public void clickButton(View view, final Intent intent) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(Constants.SESSIONMODEL_EXTRA, viewModel.getSession());
                startActivity(intent);
            }
        });
    }

}