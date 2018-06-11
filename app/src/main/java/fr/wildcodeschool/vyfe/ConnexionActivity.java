package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConnexionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);


        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final EditText inputMail = findViewById(R.id.et_mail);
        final EditText inputPass = findViewById(R.id.et_password);

        Button connexion = findViewById(R.id.btn_connected);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = inputMail.getText().toString();
                String pass = inputPass.getText().toString();

                auth.signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(ConnexionActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ConnexionActivity.this, "mauvaise identifiaction", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                //TODO: verifier la présence des données une fois API corrigée
                apiTagSetsRecover();
                apiSessionsRecover();
                Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void apiTagSetsRecover() {

        final SingletonTagsSets mSingletonTagsSets = SingletonTagsSets.getInstance();
        final ArrayList<TagSetsModel> mTagsSetsModelList = mSingletonTagsSets.getmTagsSetsList();
        RequestQueue requestQueue = Volley.newRequestQueue(ConnexionActivity.this);
        String urlApi = "http://ns347471.ip-5-39-76.eu/vyfe-api-stub/tag_sets";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlApi, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray tagSets = response.getJSONArray("tag_sets");
                            for (int i = 0; i < 1; i++) {
                                JSONObject tagSetsInfos = (JSONObject) tagSets.get(i);
                                int idTagSet = tagSetsInfos.getInt("id");
                                String nameTagSet = tagSetsInfos.getString("name");
                                TagSetsModel tagSetsModel = new TagSetsModel(idTagSet, nameTagSet);
                                mTagsSetsModelList.add(tagSetsModel);

                            }
                            mSingletonTagsSets.setmTagsSetsList(mTagsSetsModelList);
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

    public void apiSessionsRecover() {
        final SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
        final ArrayList<SessionsModel> mSessionsModelList = mSingletonSessions.getmSessionsList();

        RequestQueue requestQueue = Volley.newRequestQueue(ConnexionActivity.this);
        String urlApi = "http://ns347471.ip-5-39-76.eu/vyfe-api-stub/Sessions";
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
                                String nameSession = sessionsInfos.getString("name");
                                String authorSession = sessionsInfos.getString("author");
                                String fkGroupSession = sessionsInfos.getString("fk_group");
                                String videoLinkSession = sessionsInfos.getString("video_link");
                                String dateSession = sessionsInfos.getString("date");

                                SessionsModel sessionsModel = new SessionsModel(id, nameSession, authorSession, fkGroupSession, videoLinkSession, dateSession);
                                mSessionsModelList.add(sessionsModel);
                            }
                            mSingletonSessions.setmSessionsList(mSessionsModelList);
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
