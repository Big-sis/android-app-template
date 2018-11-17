package fr.wildcodeschool.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.helper.AuthHelper;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.repository.EditSessionPost;
import fr.wildcodeschool.vyfe.viewModel.InfoVideoViewModel;
import fr.wildcodeschool.vyfe.viewModel.InfoVideoViewModelFactory;

public class InfoVideoActivity extends VyfeActivity {

    FirebaseDatabase mDatabase;
    InfoVideoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_video);

        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnConfirmDelete = findViewById(R.id.btn_confirm_delete);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnEdit = findViewById(R.id.bt_edit);
        final ConstraintLayout confirmDelete = findViewById(R.id.confirm_delete);
        final EditText etDescription = findViewById(R.id.et_description);
        final EditText etVideoTitle = findViewById(R.id.et_video_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDatabase = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.infos_video);

        SessionModel sessionModel = getIntent().getParcelableExtra("SessionModel");

        if (sessionModel!= null) {
            viewModel = ViewModelProviders.of(this, new InfoVideoViewModelFactory( mAuth.getCurrentUser().getId(), sessionModel.getIdSession())).get(InfoVideoViewModel.class);
        }

        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {
                if(sessionModel!=null){
                etDescription.setText(sessionModel.getDescription());
                etVideoTitle.setText(sessionModel.getName());
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete.setVisibility(View.VISIBLE);
            }
        });


        //TODO : attention ici crash car il est en train de vouloir charger le modele alros qu'on le supprime
        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditSessionPost.deleteSession(viewModel.getSession().getValue());
                Toast.makeText(InfoVideoActivity.this, "Vidéo supprimée", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InfoVideoActivity.this, MyVideosActivity.class);
                startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getSession().getValue().setDescription(etDescription.getText().toString());
                viewModel.getSession().getValue().setName(etVideoTitle.getText().toString());
                EditSessionPost.sessionMapper(viewModel.getSession().getValue());
                Toast.makeText(InfoVideoActivity.this, R.string.description_added, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InfoVideoActivity.this, MyVideosActivity.class);
                startActivity(intent);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete.setVisibility(View.GONE);
            }
        });

    }

}
