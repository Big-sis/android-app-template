package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {

    ArrayList<ObservationItemsModel> mObservationItemsModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        RecyclerView recyclerTags = findViewById(R.id.re_tags);
        RecyclerView recyclerTime = findViewById(R.id.re_time_lines);

        mObservationItemsModels = getIntent().getExtras().getParcelableArrayList("list");

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);
        recyclerTime.setLayoutManager(layoutManagerTime);

        final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(mObservationItemsModels);
        recyclerTags.setAdapter(adapter);
        recyclerTime.setAdapter(adapter);


        FloatingActionButton btFinish = findViewById(R.id.bt_finish);
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelectedVideo = new Intent (RecordActivity.this, SelectedVideoActivity.class);

                startActivity(toSelectedVideo);
            }
        });

    }
}
