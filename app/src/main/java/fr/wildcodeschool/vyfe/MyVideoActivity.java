package fr.wildcodeschool.vyfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

/**
 * Activity presents cache memory videos and device videos
 * The user can select one and view it
 */
public class MyVideoActivity extends AppCompatActivity {
    SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    ArrayList<SessionsModel> mSessionsModelList = mSingletonSessions.getmSessionsList();
    FirebaseDatabase mDatabase;
    GridAdapter mGridAdapter = new GridAdapter(this, mSessionsModelList);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static ArrayList<String> mFilesName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        final GridView gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        String authUserId = SingletonFirebase.getInstance().getUid();
        EditText searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.colorWhiteTwo));
        searchText.setHintTextColor(getResources().getColor(R.color.colorWhiteTwo));
        ImageView closeSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        ImageView search = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        search.setImageResource(android.R.drawable.ic_menu_search);
        final ProgressBar pBLoading = findViewById(R.id.progress_bar_loading);

        /*
        DatabaseReference myRef = mDatabase.getReference(authUserId).child("sessions");
        myRef.keepSynced(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSessionsModelList.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(MyVideoActivity.this, R.string.havent_video, Toast.LENGTH_LONG).DisconnectionAlert();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    SessionsModel sessionsModel = snapshot.getValue(SessionsModel.class);
                    mSessionsModelList.add(sessionsModel);
                }
                mGridAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/

        ApiHelperVideo.getVideo(MyVideoActivity.this,gridView, new ApiHelperVideo.ForecastResponse() {
            @Override
            public void onSuccess(ArrayList<SessionsModel> result) {
                pBLoading.setVisibility(View.GONE);
                gridView.setAdapter(mGridAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MyVideoActivity.this, " erreur :"+ error, Toast.LENGTH_SHORT).show();
                pBLoading.setVisibility(View.GONE);
            }

            @Override
            public void onWait() {
                pBLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                pBLoading.setVisibility(View.GONE);
                gridView.setAdapter(mGridAdapter);
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_videos);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mGridAdapter.getFilter().filter(s);
                return false;
            }
        });
        mGridAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                DisconnectionAlert.confirmedDisconnection(MyVideoActivity.this);
                return true;
            case R.id.home:
                Intent intentHome = new Intent(MyVideoActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}