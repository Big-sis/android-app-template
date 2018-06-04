package fr.wildcodeschool.vyfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MyVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        final GridView gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        final ArrayList<SessionsModel> sessionsModelsList = new ArrayList<>();

        final GridAdapter gridAdapter = new GridAdapter(this, sessionsModelsList);
        gridView.setAdapter(gridAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                gridAdapter.getFilter().filter(s);
                return false;
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(MyVideoActivity.this);

        String urlApi = "http://ns347471.ip-5-39-76.eu/vyfe-api-stub/GET/Sessions";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlApi, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray sessions = response.getJSONArray("sessions");
                            for (int i = 0; i < sessions.length(); i++) {
                                JSONObject sessionsInfos = (JSONObject) sessions.get(i);
                                int id = sessionsInfos.getInt("id");
                                String name = sessionsInfos.getString("name");
                                String author = sessionsInfos.getString("author");
                                String fkGroup = sessionsInfos.getString("fk_group");
                                String videoLink = sessionsInfos.getString("video_link");
                                String date = sessionsInfos.getString("date");
                                SessionsModel sessionsModel = new SessionsModel(id, name, author, fkGroup, videoLink, date);

                                sessionsModelsList.add(sessionsModel);
                                Toast.makeText(MyVideoActivity.this, name, Toast.LENGTH_LONG).show();
                                gridAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Afficher l'erreur
                        Log.d("VOLLEY_ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
