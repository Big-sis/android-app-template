package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SelectedVideoActivity extends AppCompatActivity {

    ArrayList<TagModel> mTagModels = new ArrayList<>();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = mAuth.getCurrentUser().getUid();
    private String mIdSession = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        Button play = findViewById(R.id.bt_play);
        Button btnUpload = findViewById(R.id.bt_upload);
        Button edit = findViewById(R.id.btn_edit);
        final String titleSession = getIntent().getStringExtra("titleSession");



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

                //Firebase SESSION
                DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions");
                mIdSession = sessionRef.push().getKey();

                sessionRef.child(mIdSession).child("name").setValue(titleSession);
                sessionRef.child(mIdSession).child("author").setValue(mAuthUserId);
                sessionRef.child(mIdSession).child("videoLink").setValue("https://youtu.be/sFukyIIM1XI");
                sessionRef.child(mIdSession).child("date").setValue("Aujourd'hui");


            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlaySelectedVideo.class);
                intent.putExtra("idSession",mIdSession);
                startActivity(intent);
            }
        });

        RecyclerView recyclerTags = findViewById(R.id.re_tags);

        /*
        mTagModels.add(new TagModel(Color.parseColor("#ca62ff"), "test1"));
        mTagModels.add(new TagModel(Color.parseColor("#f91734"), "test2"));
        mTagModels.add(new TagModel(Color.parseColor("#1e8900"), "test3"));

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModels, "count");
        recyclerTags.setAdapter(adapter);
*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.video_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
}
