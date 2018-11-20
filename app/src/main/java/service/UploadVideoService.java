package service;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.activity.MainActivity;
import fr.wildcodeschool.vyfe.helper.LinkDeviceTranslateVideoHelper;
import fr.wildcodeschool.vyfe.repository.VolleyMultipartRequest;

public class UploadVideoService extends Service {

    private NotificationManager manager;
    public static  String videoLink;

    public UploadVideoService() {

    }

    public static void getLinkVimeo(final Context context, final String VimeoToken, final UploadVideoService.UrlResponse listener) {
        //1er requete en POST , recuperation upload_link
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, "https://api.vimeo.com/me/videos", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject groups = response.getJSONObject("upload");
                    String Vimeolink= (String) response.get("link");
                    String uploadLink = (String) groups.get("upload_link");
                    //ici on peut aussi recucuperer le lien ou l'utilisateur pourras visialiser la vidéo, elle est dans upoad ->link
                    //deuxieme requete pour joindre la video
                    //uploadVideo(uploadLink, context);
                    videoLink = Vimeolink;
                    listener.onSuccess(uploadLink);

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "onError: " + error);
                listener.onError(error.getMessage());
                // Toast.makeText(SelectedVideoActivity.this, "erreur :" + error.toString(), Toast.LENGTH_SHORT).DisconnectionAlert();
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
                params.put("Authorization", VimeoToken);
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                return params;
            }
        };
        queue.add(sr);

    }

    public static void uploadVideo(String url, final Context context, final byte[] inputData, final UploadVideoService.UploadResponse listener) {
        RequestQueue queue2 = Volley.newRequestQueue(context);
        VolleyMultipartRequest sr2 = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                // Toast.makeText(ApiActivity.this, " response: " + response.data, Toast.LENGTH_LONG).DisconnectionAlert();
                Toast.makeText(context, R.string.upload_video, Toast.LENGTH_LONG).show();

                listener.onSuccess();
                Log.d("Volley", "onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "onError: " + error);
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
        };
        queue2.add(sr2);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String name = (String) intent.getExtras().get("deviceUrl");
        String vimeoToken= (String) intent.getExtras().get("vimeoToken");
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        final byte[] byteVideo = LinkDeviceTranslateVideoHelper.getVideo(name, getApplication());
        getLinkVimeo(getApplicationContext(),vimeoToken, new UrlResponse() {
            @Override
            public void onSuccess(String url) {
               //TODO: charger le viewModel avec url
                //TODO: envoyer url sur firebase

                uploadVideo(url, getApplicationContext(), byteVideo, new UploadResponse() {
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

                        //TODO: ne fonctionne pas
                        Intent videoLinkIntent = new Intent();
                        videoLinkIntent.setAction("link");
                        videoLinkIntent.putExtra("videoLink",videoLink);
                       sendBroadcast(videoLinkIntent);
                        stopSelf();

                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getApplicationContext(), "Une erreur est survenue : " + error, Toast.LENGTH_LONG).show();
                        stopSelf();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), "Une erreur est survenue: " + error, Toast.LENGTH_LONG).show();
                stopSelf();
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
