package fr.vyfe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.activity.MainActivity;
import fr.vyfe.helper.LinkDeviceTranslateVideoHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.VolleyMultipartRequest;


public class UploadVideoService extends Service {

    String name;
    byte[] byteVideo = null;
    private NotificationManager manager;
    private SessionModel session;
    private SessionRepository repository;

    public UploadVideoService() {
    }
    //first POST requete => upload_link and link
    public void getTusLink(final Context context, final String VimeoToken, final String size, final UploadVideoService.UrlResponse listener) {

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
                            session.setServerVideoLink(Vimeolink);
                            listener.onSuccess(uploadLink);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("upload.approach", "tus");
                params.put("upload.size", size);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", VimeoToken);
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

    //Second PATCH requete => upload movie
    public void uploadVideo(final String url, final Context context, final byte[] inputData, final long lengthByte, final String uploadOffset, final UploadVideoService.UploadResponse listener) {
        RequestQueue queue2 = Volley.newRequestQueue(context);
        VolleyMultipartRequest sr2 = new VolleyMultipartRequest(Request.Method.PATCH, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if (response.statusCode == 200) {
                    repository.update(session);
                }
                JSONObject jsonObject = new JSONObject(response.headers);

                try {
                    String newUploadOffset = jsonObject.getString("Upload-Offset");
                    byte [] restUploadMovie =  LinkDeviceTranslateVideoHelper.convertVideotobytes(name, getApplication(),Integer.parseInt(newUploadOffset));
                    uploadVideo(url, context, restUploadMovie, lengthByte, newUploadOffset, listener);

                    // Si jamais tt nest pas telechargé relance une requete
                   /** if (Long.valueOf(newUploadOffset) < lengthByte) {

                        // creation du nouveau inputData (sans la partie deja telechargée)
                        byte [] restUploadMovie =  LinkDeviceTranslateVideoHelper.convertVideotobytes(name, getApplication(),Integer.parseInt(newUploadOffset));

                       // Envoit de la requete
                        uploadVideo(url, context, restUploadMovie, lengthByte, newUploadOffset, listener);

                    } else {
                        Toast.makeText(context, "Fichier téléchargé ", Toast.LENGTH_SHORT).show();
                        listener.onSuccess();
                    }**/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
                Toast.makeText(context, context.getString(R.string.error) + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file_data", new DataPart("movie.mp4", inputData, "video/mp4"));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Tus-Resumable", "1.0.0");
                params.put("Upload-Offset", uploadOffset);
                params.put("Content-Type", "application/offset+octet-stream");
                params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                return params;
            }
        };
        queue2.add(sr2);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        session = intent.getParcelableExtra(Constants.SESSIONMODEL_EXTRA);
        name = session.getDeviceVideoLink();
        String vimeoToken = (String) intent.getExtras().get(Constants.VIMEO_TOKEN_EXTRA);
        repository = new SessionRepository(intent.getStringExtra(Constants.COMPANYID_EXTRA));
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        byteVideo = LinkDeviceTranslateVideoHelper.convertVideotobytes(name, getApplication(),0);
        File file = new File(name);
        int size = byteVideo.length;
        final long octectsSize = file.length();
        final String uploadOffset = "0";


        getTusLink(getApplicationContext(), vimeoToken, String.valueOf(octectsSize*8), new UrlResponse() {
            @Override
            public void onSuccess(String url) {
                uploadVideo(url, getApplicationContext(), byteVideo, octectsSize, uploadOffset, new UploadResponse() {
                    @Override
                    public void onSuccess() {
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.animation_roue)
                                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                        .setContentTitle("Téléchargement")
                                        .setContentText("Votre vidéo a été mise en ligne");

                        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        manager.notify(0, builder.build());

                        stopSelf();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getApplicationContext(), "Une erreur est survenue : " + error, Toast.LENGTH_LONG).show();
                        Log.d("TAGG", "onError: " + error);
                        stopSelf();
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("TAG", "Service onBind");
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "Service onDestroy");
    }

    public interface UrlResponse {

        void onSuccess(String url);

        void onError(String error);
    }

    public interface UploadResponse {
        void onSuccess();

        void onError(String error);
    }
}
