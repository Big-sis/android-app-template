package fr.wildcodeschool.vyfe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class SelectedVideoActivity extends AppCompatActivity {

    ArrayList<ObservationItemsModel> mObservationItemsModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        RecyclerView recyclerTags = findViewById(R.id.re_tags);

        mObservationItemsModels.add(new ObservationItemsModel(Color.parseColor("#ca62ff"), "test1"));
        mObservationItemsModels.add(new ObservationItemsModel(Color.parseColor("#f91734"), "test2"));
        mObservationItemsModels.add(new ObservationItemsModel(Color.parseColor("#1e8900"), "test3"));

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);

        final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(mObservationItemsModels, "count");
        recyclerTags.setAdapter(adapter);


    }
}
