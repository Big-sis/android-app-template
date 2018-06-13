package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyVideoActivity extends AppCompatActivity {
    SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    ArrayList<SessionsModel> mSessionsModelList = mSingletonSessions.getmSessionsList();
    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    GridAdapter mGridAdapter = new GridAdapter(this, mSessionsModelList);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        final GridView gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        String authUserId = mAuth.getCurrentUser().getUid();

        DatabaseReference myRef = mdatabase.getReference(authUserId).child("sessions");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSessionsModelList.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(MyVideoActivity.this, R.string.havent_video, Toast.LENGTH_LONG).show();
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
        });

        gridView.setAdapter(mGridAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mes vidéos");

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
        switch (item.getItemId()){
            case R.id.logout:
                Intent intent = new Intent(MyVideoActivity.this, ConnexionActivity.class);
                startActivity(intent);
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
