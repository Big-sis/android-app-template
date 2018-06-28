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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectedVideoActivity extends AppCompatActivity {

    ArrayList<TagModel> mTagModels = new ArrayList<>();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    private String mIdSession = "";

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        Button play = findViewById(R.id.bt_play);
        Button btnUpload = findViewById(R.id.bt_upload);
        Button edit = findViewById(R.id.btn_edit);
        ImageView video = findViewById(R.id.vv_preview);
        TextView tvTitle = findViewById(R.id.tv_title);

        final String titleSession = getIntent().getStringExtra(TITLE_VIDEO);
        tvTitle.setText(titleSession);
        final String fileName = getIntent().getStringExtra(FILE_NAME);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedVideoActivity.this, InfoVideoActivity.class);
                startActivity(intent);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Date date = new Date();
                Date newDate = new Date(date.getTime());
                SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy HH:mm:SS Z");
                String stringdate = dt.format(newDate);
                //Firebase SESSION
                DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions");
                mIdSession = sessionRef.push().getKey();
                sessionRef.child(mIdSession).child("name").setValue(titleSession);
                sessionRef.child(mIdSession).child("author").setValue(mAuthUserId);
                sessionRef.child(mIdSession).child("videoLink").setValue(fileName);
                sessionRef.child(mIdSession).child("date").setValue(stringdate);*/


            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                intent.putExtra(ID_SESSION,mIdSession);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                intent.putExtra(ID_SESSION,mIdSession);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        RecyclerView recyclerTags = findViewById(R.id.re_tags);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

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