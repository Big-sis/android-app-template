package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectedVideoActivity extends AppCompatActivity {

    ArrayList<TagModel> mTagModels = new ArrayList<>();
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = SingletonFirebase.getInstance().getUid();
    private String mIdSession = "";
    private SessionsModel sessionsModel;

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        mDatabase = SingletonFirebase.getInstance().getDatabase();
        Button play = findViewById(R.id.bt_play);
        Button btnUpload = findViewById(R.id.bt_upload);
        Button edit = findViewById(R.id.btn_edit);
        ImageView video = findViewById(R.id.vv_preview);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);

        final DatabaseReference ref = mDatabase.getInstance().getReference(mAuthUserId).child("sessions");

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);
        final String fileName = getIntent().getStringExtra(FILE_NAME);
        tvTitle.setText(titleSession);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedVideoActivity.this, InfoVideoActivity.class);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        RecyclerView recyclerTags = findViewById(R.id.re_tags);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot video: dataSnapshot.getChildren()) {
                    SessionsModel model = video.getValue(SessionsModel.class);
                    if (fileName.equals(model.getVideoLink())) {
                        if (video.hasChild("description")) {
                            tvDescription.setText(model.getDescription());
                        } else {
                            tvDescription.setText(R.string.description);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                Intent intent = new Intent(SelectedVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}