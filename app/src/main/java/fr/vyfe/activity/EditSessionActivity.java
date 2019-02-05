package fr.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.EditSessionViewModel;
import fr.vyfe.viewModel.EditSessionViewModelFactory;

public class EditSessionActivity extends VyfeActivity {

    FirebaseDatabase mDatabase;
    EditSessionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_video);

        Button btnCancel = findViewById(R.id.btn_cancel);

        Button btnDelete = findViewById(R.id.btn_delete);
        final Button btnEdit = findViewById(R.id.bt_edit);
        final ConstraintLayout confirmDelete = findViewById(R.id.confirm_delete);
        final EditText etDescription = findViewById(R.id.et_description);
        final EditText etSessionTitle = findViewById(R.id.et_video_title);
        final TextView tvSave = findViewById(R.id.tv_video_save);
        final ImageView imageViewDelete = findViewById(R.id.iv_delete);

        final Button btnAppDelete = findViewById(R.id.btn_app_delete);
        final Button btnAllDelete = findViewById(R.id.btn_all_delete);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDatabase = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.infos_video);

        viewModel = ViewModelProviders.of(this, new EditSessionViewModelFactory(mAuth.getCurrentUser().getCompany())).get(EditSessionViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));
        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {
                //TODO voir condition si cest pas le mm device

               if(sessionModel.getDeviceVideoLink()!=null)btnAppDelete.setVisibility(View.VISIBLE);
               else  btnAppDelete.setVisibility(View.INVISIBLE);

                if (sessionModel != null) {
                    etDescription.setText(sessionModel.getDescription());
                    etSessionTitle.setText(sessionModel.getName());
                }

                etDescription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btnEdit.setClickable(true);
                        btnEdit.setEnabled(true);
                        btnEdit.setAlpha(1);
                        viewModel.setNewDescription(s.toString());
                    }
                });

                etSessionTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btnEdit.setAlpha(1);
                        viewModel.setNewName(s.toString());
                    }
                });
            }
        });



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete.setVisibility(View.VISIBLE);
                imageViewDelete.setVisibility(View.VISIBLE);
            }
        });

        btnAppDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: supprimer juste le lien pathApp Firebase
                File file =  new File(viewModel.getSession().getValue().getDeviceVideoLink());
                file.delete();
            }
        });


        btnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAppDelete.getText().toString().equals("Supprimer")) {
                    //TODO supprimer du serveur la video
                    viewModel.deleteSession().continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(@NonNull Task<Void> task) throws Exception {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditSessionActivity.this, R.string.delete_session, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditSessionActivity.this, MySessionsActivity.class);
                                startActivity(intent);
                            } else
                                Toast.makeText(EditSessionActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    });
                } else {

                    viewModel.editSession().continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(@NonNull Task<Void> task) throws Exception {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditSessionActivity.this, R.string.save_edit, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditSessionActivity.this, SelectVideoActivity.class);
                                intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.getSession().getValue().getId());
                                EditSessionActivity.this.startActivity(intent);
                            } else
                                Toast.makeText(EditSessionActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete.setVisibility(View.GONE);
                btnAppDelete.setText(getString(R.string.delete_app));
                tvSave.setText(R.string.confirm_delete_video);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAppDelete.setText(R.string.change_movie);
                tvSave.setText(R.string.confirm_change_movie);
                confirmDelete.setVisibility(View.VISIBLE);
                imageViewDelete.setVisibility(View.GONE);
                btnAllDelete.setVisibility(View.GONE);
            }
        });
    }
}
