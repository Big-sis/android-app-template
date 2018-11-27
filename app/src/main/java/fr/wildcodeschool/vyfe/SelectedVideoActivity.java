package fr.wildcodeschool.vyfe;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedVideoActivity extends AppCompatActivity {

    public static final String TITLE_VIDEO = "titleVideo";
    public static final String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";
    final String mAuthUserId = SingletonFirebase.getInstance().getUid();
    ArrayList<TagModel> mTagModels = new ArrayList<>();
    ArrayList<TagModel> mTagedList = new ArrayList<>();
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mIdTagSet;
    private SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private String mIdSession;
    private String mFilename;
    private String mTitleVideo;
    private byte[] inputData = new byte[0];
    private InputStream iStream = null;
    private TagRecyclerAdapter mAdapterTags = new TagRecyclerAdapter(mTagModels, "record");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        mIdSession = mSingletonSessions.getIdSession();
        mFilename = mSingletonSessions.getFileName();
        mTitleVideo = mSingletonSessions.getTitleSession();


        mDatabase = SingletonFirebase.getInstance().getDatabase();
        Button play = findViewById(R.id.bt_play);
        Button btnUpload = findViewById(R.id.bt_upload);
        Button edit = findViewById(R.id.btn_edit);
        ImageView video = findViewById(R.id.vv_preview);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);

        final DatabaseReference ref = mDatabase.getInstance().getReference(mAuthUserId).child("sessions");

        tvTitle.setText(mTitleVideo);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedVideoActivity.this, InfoVideoActivity.class);
                startActivity(intent);
            }
        });

        //En COMM pour ne pas utiliser nos connexions à API
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*File file = new File(fileName);
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
                            //ici on peut aussi recucuperer le lien ou l'utilisateur pourras visialiser la vidéo, elle est dans upoad ->link
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
                        Toast.makeText(SelectedVideoActivity.this, "erreur :" + error.toString(), Toast.LENGTH_SHORT).DisconnectionAlert();
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
                        params.put("Authorization", getResources().getString(R.string.VIMEO_TOKEN));
                        params.put("Content-Type", "application/json");
                        params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                        return params;

                    }

                };
                queue.add(sr);*/
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                startActivity(intent);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedVideoActivity.this, PlayVideoActivity.class);
                startActivity(intent);
            }
        });


        final DatabaseReference tagSessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("idTagSet");

        tagSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIdTagSet = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTagModels.clear();
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    TagModel tagModel = tagSnapshot.getValue(TagModel.class);

                    if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                        mTagModels.add(tagModel);
                    }
                }

                DatabaseReference tagedRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("tags");
                tagedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagedSnapshot : dataSnapshot.getChildren()) {
                            TagModel taged = tagedSnapshot.getValue(TagModel.class);
                            // TODO : trouver pourquoi la requête ne récupère pas le tagName avec le modèle
                            String tagedName = tagedSnapshot.child("tagName").getValue(String.class);
                            taged.setName(tagedName);
                            mTagedList.add(taged);
                        }

                        RecyclerView recyclerTags = findViewById(R.id.re_tags);
                        TagRecyclerAdapter adapterTags = new TagRecyclerAdapter(mTagModels, mTagedList, "count");
                        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerTags.setLayoutManager(layoutManagerTags);
                        recyclerTags.setAdapter(adapterTags);
                        adapterTags.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mTitleVideo);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot video : dataSnapshot.getChildren()) {
                    SessionsModel model = video.getValue(SessionsModel.class);
                    if (mFilename.equals(model.getVideoLink())) {
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

        //AFfichage miniature
        ImageView videoView = findViewById(R.id.vv_preview);
        videoView.setImageBitmap(ImageViewSessionHelper.thumbnailSession(mFilename));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                DisconnectionAlert.confirmedDisconnection(SelectedVideoActivity.this);
                return true;

            case R.id.home:
                Intent intentHome = new Intent(SelectedVideoActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void uploadVideo(String url) {
        RequestQueue queue2 = Volley.newRequestQueue(SelectedVideoActivity.this);
        VolleyMultipartRequest sr2 = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                // Toast.makeText(ApiActivity.this, " response: " + response.data, Toast.LENGTH_LONG).DisconnectionAlert();
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

    //methode conv String en tableau pour envoit
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