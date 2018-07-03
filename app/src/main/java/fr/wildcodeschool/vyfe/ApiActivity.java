package fr.wildcodeschool.vyfe;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ApiActivity extends AppCompatActivity {

   private Uri mUri = null;
   private   byte[] inputData = new byte[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        //TODO a verifier lors de l'accès a API
        String fileName = "/storage/emulated/0/Android/data/fr.wildcodeschool.vyfe/cache/1530523544433.mp4";



        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(Uri.parse(new File(fileName).toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
          inputData = getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        File file = new File(fileName);
        final long length = file.length();
        Toast.makeText(this, String.valueOf(length), Toast.LENGTH_SHORT).show();


        //1er requete en POST , recuperation upload_link
        RequestQueue queue = Volley.newRequestQueue(ApiActivity.this);
        VolleyMultipartRequest sr = new VolleyMultipartRequest(Request.Method.POST, "https://api.vimeo.com/me/videos", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse( NetworkResponse response) {
                String resultResponse = new String(response.data);

            }

            /*

            @Override
            public void onResponse(JSONObject response) {
                //TODO recuperer dans lobjet upload -> upload.upload_link

                try {
                    JSONObject groups = response.getJSONObject("upload");
                  String uploadLink = (String) groups.get("upload_link");
                  uploadVideo(uploadLink);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ApiActivity.this, " response: " + response, Toast.LENGTH_LONG).show();
                Log.d("Volley", "onResponse: " + response);
            }*/
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
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
// file name could found file base or direct access from real path
// for now just get bitmap data from ImageView
                params.put("picture",new DataPart("picture.jpg", inputData,"image/jpeg"));

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

    }
    public void uploadVideo(String url){
        RequestQueue queue2 = Volley.newRequestQueue(ApiActivity.this);
        StringRequest sr2 = new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
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
