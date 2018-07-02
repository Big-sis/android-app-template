package fr.wildcodeschool.vyfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ApiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        //TODO a verifier lors de l'accès a API
        String fileName = "/storage/emulated/0/Android/data/fr.wildcodeschool.vyfe/cache/1530523544433.mp4";

        File file = new File(fileName);
        final long length = file.length();
        Toast.makeText(this, String.valueOf(length), Toast.LENGTH_SHORT).show();


        //1er requete en POST , recuperation upload_link
        RequestQueue queue = Volley.newRequestQueue(ApiActivity.this);
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, "https://api.vimeo.com/me/videos", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO recuperer dans lobjet upload -> upload.upload_link

                try {
                    JSONObject groups = response.getJSONObject("upload");
                  String uploadLink = (String) groups.get("upload_link");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ApiActivity.this, " response: " + response, Toast.LENGTH_LONG).show();
                Log.d("Volley", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "onError: " + error);
                Toast.makeText(ApiActivity.this, "erreur :" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("upload.approach", "tus");
                params.put("upload.size", String.valueOf(length));

                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "mettre token");
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");

                return params;
            }
        };
        queue.add(sr);


        //2eme etape envoit de la video
        RequestQueue queue2 = Volley.newRequestQueue(ApiActivity.this);
        StringRequest sr2 = new StringRequest(Request.Method.PATCH, "mettre url recup", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO recuperer dans lobjet Upload-Offset
                //si upload-offset = length video recu
                Toast.makeText(ApiActivity.this, " response: " + response, Toast.LENGTH_LONG).show();
                Log.d("Volley", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "onError: " + error);
                Toast.makeText(ApiActivity.this, "erreur :" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("upload.approach", "tus");
                //params.put("upload.size", String.valueOf(length));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "mettre token");
                params.put("Accept", "application/vnd.vimeo.*+json;version=3.4");
                params.put("Tus-Resumable", "1.0.0");
                params.put("Upload-Offset", "0");
                params.put("Content-Type", "application/offset+octet-stream");

                return params;
            }
        };
        queue2.add(sr2);
    }
}
