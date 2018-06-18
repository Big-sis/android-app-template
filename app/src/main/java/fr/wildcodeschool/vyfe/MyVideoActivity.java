package fr.wildcodeschool.vyfe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.GridView;

import java.util.ArrayList;

public class MyVideoActivity extends AppCompatActivity {
    SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    ArrayList<SessionsModel> mSessionsModelList = mSingletonSessions.getmSessionsList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        final GridView gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);

        final GridAdapter gridAdapter = new GridAdapter(this, mSessionsModelList);
        gridView.setAdapter(gridAdapter);

        String urlTest = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";

        SessionsModel testSession = new SessionsModel("nameTest", "autorTest", "fkGroupTest", urlTest, "Aujourd'hui");
        mSessionsModelList.add(testSession);

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
        gridAdapter.notifyDataSetChanged();


    }


}
