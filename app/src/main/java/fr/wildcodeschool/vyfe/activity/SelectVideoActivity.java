package fr.wildcodeschool.vyfe.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.adapter.TagRecyclerAdapter;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagSetModel;
import fr.wildcodeschool.vyfe.viewModel.SelectVideoViewModel;
import fr.wildcodeschool.vyfe.viewModel.SelectVideoViewModelFactory;


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
        String userId = FirebaseAuth.getInstance().getUid();

        //recuperation de la session enregistré auparavant
        sessionModel = getIntent().getParcelableExtra("SessionModel");

        //mon viewModel n'est pas completé??
        viewModel = ViewModelProviders.of(this, new SelectVideoViewModelFactory(userId, sessionModel.getIdSession(), sessionModel.getIdTagSet())).get(SelectVideoViewModel.class);

        // Je voudrais recuperer la grille complete des tags
        List<TagSetModel> tagSet = viewModel.getTagSets().getValue();

        //les tags utilisés et le temps mais tt est null
        SessionModel session = viewModel.getSession().getValue();


        RecyclerView recyclerTags = findViewById(R.id.re_tags);
                                            //le recycler prendra en param la grille et les tags de la session
        TagRecyclerAdapter adapterTags = new TagRecyclerAdapter( sessionModel.getTags(),"count");
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);
        recyclerTags.setAdapter(adapterTags);


        if (sessionModel.getDescription() != null) {
            tvDescription.setText(sessionModel.getDescription());
        } else {
            tvDescription.setText(R.string.no_description);
        }

        tvTitle.setText(sessionModel.getName());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectVideoActivity.this, InfoVideoActivity.class);
                startActivity(intent);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runPlayVideoActivity();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runPlayVideoActivity();
            }
        });

    }

    private void runPlayVideoActivity(){
        Intent intent = new Intent(SelectVideoActivity.this, PlayVideoActivity.class);
        intent.putExtra(ID_SESSION, sessionModel.getIdSession());
        startActivity(intent);
    }


}