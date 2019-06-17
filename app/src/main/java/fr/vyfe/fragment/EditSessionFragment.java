package fr.vyfe.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import java.io.File;

import fr.vyfe.R;
import fr.vyfe.activity.MySessionsActivity;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.EditSessionViewModel;

public class EditSessionFragment extends Fragment {

    private EditSessionViewModel viewModel;
    private EditText etSessionTitle;
    private EditText etDescription;
    private Button btnEdit;
    private Button btnDelete;
    private View confirmEditModal;
    private Button btnCancelEdit;
    private Button btnEditConfirm;
    private View confirmDeleteModal;
    private Button btnCancelDelete;
    private TextView tvSave;
    private TextView tvDelete;
    private ConstraintLayout containerBtnAppDelete;
    private ConstraintLayout containerBtnAllDelete;

    public static EditSessionFragment newInstance() {
        return new EditSessionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(EditSessionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_edit_session, container, false);

        etSessionTitle = result.findViewById(R.id.et_video_title);
        etDescription = result.findViewById(R.id.et_description);

        btnEdit = result.findViewById(R.id.img_button_edit);
        btnDelete = result.findViewById(R.id.img_btn_delete);

        tvSave = result.findViewById(R.id.tv_save);
        tvDelete = result.findViewById(R.id.tv_delete);


        //Edit
        confirmEditModal = result.findViewById(R.id.confirm_action_edit);
        btnCancelEdit = result.findViewById(R.id.btn_cancel_edit);
        btnEditConfirm = result.findViewById(R.id.btn_edit_confirm);

        //Delete
        confirmDeleteModal = result.findViewById(R.id.confirm_action_delete);
        btnCancelDelete = result.findViewById(R.id.btn_cancel_delete);
        containerBtnAppDelete = result.findViewById(R.id.container_app_delete);
        containerBtnAllDelete = result.findViewById(R.id.container_all_delete);

        return result;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable final SessionModel session) {
                if (session != null) {
                    if (session.getDescription() != null)
                        etDescription.setText(session.getDescription());
                    if (session.getName() != null) etSessionTitle.setText(session.getName());
                    etDescription.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
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
                            viewModel.setNewName(s.toString());
                        }
                    });
                    onClickDeleteData(btnDelete, session);
                    onClickDeleteData(tvDelete, session);
                }
            }
        });

        viewModel.watchDataChange().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasDataChanged) {
                if (hasDataChanged != null && hasDataChanged) {
                    onClickSaveData(btnEdit);
                    onClickSaveData(tvSave);
                }
            }
        });

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmEditModal.setVisibility(View.GONE);
                setFocusableAction(true);
            }
        });
        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteModal.setVisibility(View.GONE);
                setFocusableAction(true);
            }
        });

        btnEditConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getNewName() != null && viewModel.getNewName().equals("") && !viewModel.getNewDescription().equals("")) {
                    etSessionTitle.setBackgroundResource(R.drawable.style_input_error);
                    confirmEditModal.setVisibility(View.GONE);
                    setFocusableAction(true);
                    Toast.makeText(getContext(), R.string.add_title_grid, Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.updateSession().continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(@NonNull Task<Void> task) throws Exception {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), R.string.save_edit, Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            } else
                                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    });
                }
            }
        });

        containerBtnAppDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getSession().getValue().getServerVideoLink() == null) {
                    final AlertDialog.Builder popup = new AlertDialog.Builder(getContext());
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
                                        Toast.makeText(getContext(), R.string.delete_session, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getContext(), MySessionsActivity.class);
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                            });
                        }
                    });
                    popup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmDeleteModal.setVisibility(View.GONE);
                        }
                    });
                    popup.show();

                } else setDeleteFile(viewModel.getSession().getValue().getDeviceVideoLink());
            }
        });


        containerBtnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO supprimer du serveur la video
                if (viewModel.getSession().getValue().getDeviceVideoLink() != null) {
                    File file = new File(viewModel.getSession().getValue().getDeviceVideoLink());
                    if (file != null) file.delete();
                }
                viewModel.onCleared();
                viewModel.deleteSession().continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.delete_session, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), MySessionsActivity.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        return null;
                    }
                });

                confirmDeleteModal.setVisibility(View.GONE);
            }
        });
    }

    public void setDeleteFile(String linkFile) {
        File file = new File(linkFile);
        if (file != null) file.delete();
        viewModel.deleteLinkAppSession().continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(@NonNull Task<Void> task) throws Exception {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), R.string.save_edit, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), MySessionsActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                return null;
            }
        });

        confirmDeleteModal.setVisibility(View.GONE);
    }

    public void onClickSaveData(View view) {
        view.setClickable(true);
        view.setEnabled(true);
        view.setAlpha(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmEditModal.setVisibility(View.VISIBLE);
                confirmDeleteModal.setVisibility(View.GONE);
                setFocusableAction(false);
            }
        });

    }

    public void onClickDeleteData(View view, final SessionModel session) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteModal.setVisibility(View.VISIBLE);
                confirmEditModal.setVisibility(View.GONE);
                setFocusableAction(false);
                if (session.getDeviceVideoLink() != null) {
                    containerBtnAppDelete.setVisibility(View.VISIBLE);
                    containerBtnAppDelete.setAlpha(1f);
                    containerBtnAppDelete.setClickable(true);
                } else {
                    containerBtnAppDelete.setClickable(false);
                    containerBtnAppDelete.setAlpha(0.4f);
                }
            }
        });

    }

    public void setFocusableAction(Boolean bool) {
        etDescription.setFocusable(bool);
        etSessionTitle.setFocusable(bool);
        etDescription.setFocusableInTouchMode(bool);
        etSessionTitle.setFocusableInTouchMode(bool);
    }
}
