package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    ArrayList<ObservationItemsModel> mObservationItemsModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);


        if (AddGridActivity.mAddEvent) {
            recyclerView.setVisibility(View.VISIBLE);
            mobservationItemsModels = getIntent().getExtras().getParcelableArrayList("list");
            RecyclerView listItems = findViewById(R.id.recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            listItems.setLayoutManager(layoutManager);
            final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(mobservationItemsModels);
            listItems.setAdapter(adapter);
        }



        FloatingActionButton fabAddMoment = findViewById(R.id.fab_add_moment);
        fabAddMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, AddGridActivity.class);
                startActivity(intent);
            }
        });

        Button buttonGo = findViewById(R.id.button_go);
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRecord = new Intent(StartActivity.this, RecordActivity.class);
                toRecord.putParcelableArrayListExtra("list", mObservationItemsModels);
                startActivity(toRecord);
            }
        });


    }
}
