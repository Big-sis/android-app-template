package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private byte[] inputData = new byte[0];
    private InputStream iStream = null;


    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);
        mIdSession = getIntent().getStringExtra(ID_SESSION);


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

        //En COMM pour ne pas utiliser nos connexions à API
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(fileName);
                final long length = file.length();

                //transformation du lien de stockage en vidéo
                try {
                    iStream = getContentResolver().openInputStream(Uri.fromFile(file));
                    inputData = getBytes(iStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //1er requete en POST , recuperation upload_link
                RequestQueue queue = Volley.newRequestQueue(SelectedVideoActivity.this);
                JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, "https://api.vimeo.com/me/videos", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject groups = response.getJSONObject("upload");
                            String uploadLink = (String) groups.get("upload_link");
                            //deuxieme requete pour joindre la video
                            uploadVideo(uploadLink);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Volley", "onResponse: " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", "onError: " + error);
                        Toast.makeText(SelectedVideoActivity.this, "erreur :" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("upload.approach", "post");
                        params.put("upload.redirect_url", "https://google.com");
                        return params;
                    }


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "Bearer c2af8349ffedd683748ee6a8e383202c");
                        params.put("Content-Type", "application/json");
                        params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                        return params;

                    }
                };
                queue.add(sr);
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

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot video: dataSnapshot.getChildren()) {
                    SessionsModel model = video.getValue(SessionsModel.class);
                    if (fileName.equals(model.getVideoLink())) {
                        if (video.hasChild("description")) {
                            tvDescription.setText(model.getDescription());
                        } else {
                            tvDescription.setText(R.string.no_description);
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
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(SelectedVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void uploadVideo(String url) {
        RequestQueue queue2 = Volley.newRequestQueue(SelectedVideoActivity.this);
        VolleyMultipartRequest sr2 = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                // Toast.makeText(ApiActivity.this, " response: " + response.data, Toast.LENGTH_LONG).show();
                Toast.makeText(SelectedVideoActivity.this, R.string.upload_video, Toast.LENGTH_SHORT).show();
                Log.d("Volley", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "onError: " + error);
                Toast.makeText(SelectedVideoActivity.this, getString(R.string.error) + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file_data", new DataPart("movie.mp4", inputData, "video/mp4"));

                return params;
            }

        };
        queue2.add(sr2);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}