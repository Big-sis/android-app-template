package fr.vyfe.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.helper.TusAndroidUpload;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.SelectVideoViewModel;
import fr.vyfe.viewModel.SelectVideoViewModelFactory;
import io.tus.android.client.TusPreferencesURLStore;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

//TODO: Mise en place de Fragment?
public class SelectVideoActivity extends VyfeActivity {
    private static String TAG = "SelectVideoActivity";
    ImageView videoMiniatureView;
    private SelectVideoViewModel viewModel;
    private IntentFilter mIntentFilter;
    private Button uploadButton;
    private TusClient client;
    private String uploadURL;
    private TusUpload upload;
    private String vimeoToken;
    private UploadTask uploadTask;
    private String serverVideoLink;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private EditText pxUpload;
    private LinearLayout containerUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.info_movie);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initNavBar(navigationView, toolbar, drawerLayout);

        Button playBtn = findViewById(R.id.bt_play);
        Button editBtn = findViewById(R.id.img_button_edit);
        final TextView tvTitle = findViewById(R.id.tv_title);
        final TextView tvDescription = findViewById(R.id.tv_description);
        final TextView gridTextView = findViewById(R.id.tv_grid);
        final RecyclerView recyclerTags = findViewById(R.id.re_tags);
        uploadButton = findViewById(R.id.btn_upload);
        videoMiniatureView = findViewById(R.id.vv_preview);
        progressBar = findViewById(R.id.progress_upload);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pxUpload = findViewById(R.id.tv_pourcentage_upload);
        containerUpload = findViewById(R.id.container_upload);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (sharedPreferences.contains(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN))
            vimeoToken = sharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN, "");

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("link");

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        viewModel = ViewModelProviders.of(this, new SelectVideoViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId())).get(SelectVideoViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));


        viewModel.getSession().observe(this, new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable final SessionModel session) {
                if (session != null) {

                    //Create grid
                    TagSetModel tagSetModel = session.getTagsSet();
                    if (tagSetModel != null && session.getTags()!=null) {
                        TemplateRecyclerAdapter adapterTags = new TemplateRecyclerAdapter(session.getTags(),session.getTagsSet().getTemplates(), "play");
                        recyclerTags.setAdapter(adapterTags);
                    }

                   if(tagSetModel != null && tagSetModel.getName() !=null)
                    gridTextView.setText(tagSetModel.getName());


                    //View Upload
                    if (session.getServerVideoLink() != null) {
                        uploadButton.setTextColor(Color.GRAY);
                        uploadButton.setClickable(false);
                        uploadButton.setText(R.string.online);
                        uploadButton.setAlpha(0.5f);
                    } else {
                        uploadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (InternetConnexionHelper.haveInternetConnection(SelectVideoActivity.this)) {
                                    final AlertDialog.Builder popup = new AlertDialog.Builder(SelectVideoActivity.this);
                                    if (null == vimeoToken) {
                                        uploadButton.setTextColor(Color.GRAY);
                                        popup.setTitle(R.string.alert);
                                        popup.setMessage(R.string.upload_contact);
                                        popup.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        popup.show();
                                    } else {
                                        startUpload(new File(session.getDeviceVideoLink()));
                                    }
                                } else
                                    Toast.makeText(SelectVideoActivity.this, R.string.have_internet_connection, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if (session.getDescription() != null && !session.getDescription().equals("")) {
                        tvDescription.setText(session.getDescription());
                    } else {
                        tvDescription.setText(R.string.no_description);
                    }

                    tvTitle.setText(session.getName());

                    //AFfichage miniature
                    videoMiniatureView.setImageBitmap(session.getThumbnail());
                    if (InternetConnexionHelper.haveInternetConnection(SelectVideoActivity.this) && session.getThumbnailUrl() != null) {
                        new fr.vyfe.helper.DownloadImageTask((ImageView) findViewById(R.id.vv_preview)).execute(session.getThumbnailUrl());
                    }



                }
            }
        });


        clickButton(playBtn, new Intent(this, PlayVideoActivity.class));
        clickButton(videoMiniatureView, new Intent(this, PlayVideoActivity.class));
        clickButton(editBtn, new Intent(this, EditSessionActivity.class));
    }

    private void clickButton(View view, final Intent intent) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(Constants.SESSIONMODELID_EXTRA, viewModel.getSessionId());
                startActivity(intent);
            }
        });
    }

    private void setUploadProgress(int progress) {
        progressBar.setProgress(progress);
        pxUpload.setText(String.valueOf(progress) + " %");
    }

    private void startUpload(File file) {
        Toast.makeText(SelectVideoActivity.this, R.string.start_upload, Toast.LENGTH_LONG).show();
        containerUpload.setVisibility(View.VISIBLE);
        uploadButton.setVisibility(View.GONE);
        try {
            SharedPreferences pref = getSharedPreferences("tus", 0);
            client = new TusClient();
            client.setUploadCreationURL(new URL(Constants.VIMEO_API_VIDEOS_ENDPOINT));
            client.enableResuming(new TusPreferencesURLStore(pref));
            upload = new TusAndroidUpload(file, SelectVideoActivity.this);
            getTusLink(this, vimeoToken, upload.getSize(), new UrlResponse() {
                @Override
                public void onSuccess(String url) {
                    uploadURL = url;
                    resumeUpload();
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            });
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void resumeUpload() {
        try {
            uploadTask = new UploadTask(this, client, upload);
            uploadTask.execute(uploadURL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void getTusLink(final Context context, final String VimeoToken, final long size, final UrlResponse listener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(
                Request.Method.POST,
                Constants.VIMEO_API_VIDEOS_ENDPOINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject groups = jsonObject.getJSONObject("upload");
                            String Vimeolink = (String) jsonObject.get("link");
                            String uploadLink = (String) groups.get("upload_link");
                            serverVideoLink = Vimeolink;
                            listener.onSuccess(uploadLink);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("upload.approach", "tus");
                params.put("upload.size", String.valueOf(size));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + VimeoToken);
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    //TODO: behaviour isn't usual
    // Once rectif onBackPressed  in RecordActivity :  intent direction will be parentActivity
    @Override
    public void onBackPressed() {
        this.startActivity(new Intent(this, MainActivity.class));
    }

    private interface UrlResponse {

        void onSuccess(String url);

        void onError(Exception error);
    }

    private class UploadTask extends AsyncTask<String, Long, Void> {
        private TusClient client;
        private TusUpload upload;
        private SelectVideoActivity activity;

        public UploadTask(SelectVideoActivity activity, TusClient client, TusUpload upload) {
            this.activity = activity;
            this.client = client;
            this.upload = upload;
        }

        @Override
        protected void onPreExecute() {
            activity.setUploadProgress(0);
        }

        @Override
        protected void onProgressUpdate(Long... updates) {
            long uploadedBytes = updates[0];
            long totalBytes = updates[1];
            activity.setUploadProgress((int) ((double) uploadedBytes / totalBytes * 100));
        }

        @Override
        protected void onPostExecute(Void param) {
            viewModel.setServerVideoLink(serverVideoLink);
            containerUpload.setVisibility(View.GONE);
            uploadButton.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                TusUploader uploader = client.beginOrResumeUploadFromURL(upload, new URL(params[0]));
                long totalBytes = upload.getSize();
                long uploadedBytes = uploader.getOffset();

                // Upload file in 1MiB chunks
                uploader.setChunkSize(1024 * 1024);

                while (!isCancelled() && uploader.uploadChunk() > 0) {
                    uploadedBytes = uploader.getOffset();
                    publishProgress(uploadedBytes, totalBytes);
                }

                uploader.finish();

            } catch (Exception e) {
                cancel(true);
            }

            return null;
        }
    }
}