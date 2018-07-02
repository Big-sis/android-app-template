package fr.wildcodeschool.vyfe;

import android.content.Context;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SelectedVideoActivity extends AppCompatActivity {

    ArrayList<TagModel> mTagModels = new ArrayList<>();
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String mAuthUserId = SingletonFirebase.getInstance().getUid();
    private String mIdSession = "";
    private SessionsModel sessionsModel;



    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);


        mDatabase = SingletonFirebase.getInstance().getDatabase();
        mIdSession = getIntent().getStringExtra("idSession");
        Button play = findViewById(R.id.bt_play);
        Button btnUpload = findViewById(R.id.bt_upload);
        Button edit = findViewById(R.id.btn_edit);
        Button btnApi = findViewById(R.id.btn_api);
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
                intent.putExtra(ID_SESSION, mIdSession);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                intent.putExtra(ID_SESSION, mIdSession);
                intent.putExtra(FILE_NAME, fileName);
                intent.putExtra(TITLE_VIDEO, titleSession);
                startActivity(intent);
            }
        });

        RecyclerView recyclerTags = findViewById(R.id.re_tags);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleSession);

        btnApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNewComment(SelectedVideoActivity.this, fileName);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(SelectedVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void postNewComment(final Context context, final String video) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://api.vimeo.com/users/6ea7fbd6c8045daff1df570fd3b8f4f12eef2e9c", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, " response: " + response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "erreur :" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("upload.approach","post");
                params.put("upload.redirect_url",video);




                return params;


            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer c2af8349ffedd683748ee6a8e383202c");
                params.put("Content-Type","application/json");
                params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                //params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }




}