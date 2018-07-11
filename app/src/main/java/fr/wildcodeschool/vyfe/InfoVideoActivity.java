package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoVideoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final String FILE_NAME = "filename";
    public static final String TITLE_VIDEO = "titleVideo";
    FirebaseDatabase mDatabase;

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
        final String fileName = getIntent().getStringExtra(FILE_NAME);
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabase = SingletonFirebase.getInstance().getDatabase();
        final DatabaseReference ref = mDatabase.getInstance().getReference(uid).child("sessions");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.infos_video);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot video: dataSnapshot.getChildren()) {
                    SessionsModel model = video.getValue(SessionsModel.class);
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
                            SessionsModel model = video.getValue(SessionsModel.class);
                            if (fileName.equals(model.getVideoLink())) {
                                video.getRef().removeValue();
                                Intent intent = new Intent(InfoVideoActivity.this, MyVideoActivity.class);
                                intent.putExtra(FILE_NAME, fileName);
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
                            SessionsModel model = video.getValue(SessionsModel.class);
                            if (fileName.equals(model.getVideoLink())) {
                                String description = etDescription.getText().toString();
                                video.getRef().child("description").setValue(description);
                                Toast.makeText(InfoVideoActivity.this, R.string.description_added, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InfoVideoActivity.this, MyVideoActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Intent intent = new Intent(InfoVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;

            case R.id.home:
                Intent intentHome = new Intent(InfoVideoActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
