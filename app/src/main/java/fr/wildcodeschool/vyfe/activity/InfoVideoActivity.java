package fr.wildcodeschool.vyfe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;
import fr.wildcodeschool.vyfe.viewModel.SingletonSessions;

public class InfoVideoActivity extends VyfeActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase mDatabase;
    SingletonSessions mSingletonSessions = SingletonSessions.getInstance();

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
        final String fileName = mSingletonSessions.getFileName();
        mSingletonSessions.setFileName(fileName);
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabase = SingletonFirebase.getInstance().getDatabase();
        final DatabaseReference ref = mDatabase.getInstance().getReference(uid).child("sessions");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.infos_video);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot video: dataSnapshot.getChildren()) {
                    SessionModel model = video.getValue(SessionModel.class);
                    if (fileName.equals(model.getVideoLink())) {
                        etDescription.setText(model.getDescription());
                        etVideoTitle.setText(model.getName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete.setVisibility(View.VISIBLE);
            }
        });

        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot video: dataSnapshot.getChildren()) {
                            SessionModel model = video.getValue(SessionModel.class);
                            if (fileName.equals(model.getVideoLink())) {
                                video.getRef().removeValue();
                                Intent intent = new Intent(InfoVideoActivity.this, MyVideosActivity.class);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot video: dataSnapshot.getChildren()) {
                            SessionModel model = video.getValue(SessionModel.class);
                            if (fileName.equals(model.getVideoLink())) {
                                String description = etDescription.getText().toString();
                                video.getRef().child("description").setValue(description);
                                String title = etVideoTitle.getText().toString();
                                video.getRef().child("name").setValue(title);
                                Toast.makeText(InfoVideoActivity.this, R.string.description_added, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InfoVideoActivity.this, MyVideosActivity.class);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
