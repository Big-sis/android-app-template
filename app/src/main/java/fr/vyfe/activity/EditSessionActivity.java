package fr.vyfe.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private FirebaseDatabase mDatabase;
    private EditSessionViewModel viewModel;
    private ConstraintLayout confirmActionDelete;
    private Button btnAppDelete;
    private boolean isChangeData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_video);


        final EditText etDescription = findViewById(R.id.et_description);
        final EditText etSessionTitle = findViewById(R.id.et_video_title);

        Button btnDelete = findViewById(R.id.btn_delete);
        final Button btnEdit = findViewById(R.id.btn_edit);

        //Edit
        final ConstraintLayout confirmActionEdit = findViewById(R.id.confirm_action_edit);
        Button btnCancelEdit = findViewById(R.id.btn_cancel_edit);
        Button btnEditConfirm = findViewById(R.id.btn_edit_confirm);

        //Delete
        confirmActionDelete = findViewById(R.id.confirm_action_delete);
        Button btnCancelDelete = findViewById(R.id.btn_cancel_delete);
        btnAppDelete = findViewById(R.id.btn_app_delete);
        Button btnAllDelete = findViewById(R.id.btn_all_delete);

        Toolbar toolbar = findViewById(R.id.toolbar);
        mDatabase = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.infos_video);

        viewModel = ViewModelProviders.of(this, new EditSessionViewModelFactory(mAuth.getCurrentUser().getCompany())).get(EditSessionViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));
        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {
                if (sessionModel != null) {
                    if(sessionModel.getDescription()!=null)etDescription.setText(sessionModel.getDescription());
                    if(sessionModel.getName()!=null)etSessionTitle.setText(sessionModel.getName());


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
                            isChangeData = true;
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


            }
        });

        onClickVisibilityView(btnDelete, confirmActionDelete, confirmActionEdit);
        onClickVisibilityView(btnEdit, confirmActionEdit, confirmActionDelete);
        onClickGoneView(btnCancelEdit, btnCancelDelete, confirmActionDelete, confirmActionEdit);

        btnEditConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              saveData();
            }
        });


        btnAppDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.getSession().getValue().getServerVideoLink()==null){
                    final AlertDialog.Builder popup = new AlertDialog.Builder(EditSessionActivity.this);
                        popup.setTitle(R.string.alert);
                        popup.setMessage(R.string.info_delete);
                        popup.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setDeleteFile(viewModel.getSession().getValue().getDeviceVideoLink());
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
                            }
                        });
                        popup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmActionDelete.setVisibility(View.GONE);
                            }
                        });
                        popup.show();

                }else setDeleteFile(viewModel.getSession().getValue().getDeviceVideoLink());
            }
        });


        btnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO supprimer du serveur la video
                if(viewModel.getSession().getValue().getDeviceVideoLink()!=null){
                    File file = new File(viewModel.getSession().getValue().getDeviceVideoLink());
                    if(file!=null)file.delete();
                }

                viewModel.stopListener();
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
            }
        });


    }

    public void onClickVisibilityView(Button btn, final View visibilityView, final View invisibilityView) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibilityView.setVisibility(View.VISIBLE);
                invisibilityView.setVisibility(View.GONE);

                if (viewModel.getSession().getValue().getDeviceVideoLink() != null && confirmActionDelete.getVisibility() == View.VISIBLE)
                    btnAppDelete.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onClickGoneView(Button btn1, Button btn2, final View visibilityView, final View invisibilityView) {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibilityView.setVisibility(View.GONE);
                invisibilityView.setVisibility(View.GONE);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibilityView.setVisibility(View.GONE);
                invisibilityView.setVisibility(View.GONE);
            }
        });
    }

    public void setDeleteFile(String linkFile) {
        File file = new File(linkFile);
        if(file!=null)file.delete();
        viewModel.deleteLinkAppSession().continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(@NonNull Task<Void> task) throws Exception {
                if (task.isSuccessful()) {
                    Toast.makeText(EditSessionActivity.this, R.string.save_edit, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditSessionActivity.this, MySessionsActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(EditSessionActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                return null;
            }
        });

        //TODO: supprimer juste le lien pathApp Firebase
    }


    public void saveData(){
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
    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(EditSessionActivity.this, SelectVideoActivity.class);
        intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.getSession().getValue().getId());
        if(isChangeData){
            final AlertDialog.Builder builder = new AlertDialog.Builder(EditSessionActivity.this);
            builder.setMessage(R.string.dont_save)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          saveData();
                        }
                    })
                    .setNegativeButton(R.string.quiet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            EditSessionActivity.this.startActivity(intent);
                        }
                    })
                    .show();

        }else{
            EditSessionActivity.this.startActivity(intent);
        }
    }
}
