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

import fr.vyfe.R;
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.SelectVideoViewModel;
import fr.vyfe.viewModel.SelectVideoViewModelFactory;


public class SelectVideoActivity extends VyfeActivity {

    public static final String ID_SESSION = "idSession";

    private SessionModel sessionModel;
    private SelectVideoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        Button play = findViewById(R.id.bt_play);
        Button edit = findViewById(R.id.btn_edit);
        ImageView video = findViewById(R.id.vv_preview);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);

        sessionModel = getIntent().getParcelableExtra("SessionModel");

        if (sessionModel != null) {
            viewModel = ViewModelProviders.of(this, new SelectVideoViewModelFactory( mAuth.getCurrentUser().getId(), sessionModel.getIdSession())).get(SelectVideoViewModel.class);
        }

        //TODO : Je passerais tt par des intent au lieu dapl mapper (moins long?)?
        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {
                RecyclerView recyclerTags = findViewById(R.id.re_tags);
                TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(sessionModel.getTags(), "count");
                RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerTags.setLayoutManager(layoutManagerTags);
                recyclerTags.setAdapter(adapterTags);
            }
        });

        if (sessionModel.getDescription() != null) {
            tvDescription.setText(sessionModel.getDescription());
        } else {
            tvDescription.setText(R.string.no_description);
        }

        tvTitle.setText(sessionModel.getName());


        clickButton(play, new Intent(SelectVideoActivity.this, PlayVideoActivity.class));
        clickButton(video,new Intent(SelectVideoActivity.this, PlayVideoActivity.class));
        clickButton(edit, new Intent(SelectVideoActivity.this, InfoVideoActivity.class));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clickButton(View view, final Intent intent) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(ID_SESSION, sessionModel.getIdSession());
                intent.putExtra("SessionModel", sessionModel);
                startActivity(intent);
            }
        });
    }

}