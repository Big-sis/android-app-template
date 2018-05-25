package fr.wildcodeschool.vyfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {

    ArrayList<ObservationItemsModel> mobservationItemsModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        RecyclerView recyclerTags = findViewById(R.id.re_tags);
        RecyclerView recyclerTime = findViewById(R.id.re_time_lines);

        mobservationItemsModels = getIntent().getExtras().getParcelableArrayList("list");



        //insertion des observations a ajouter
        /*final ArrayList<ObservationItemsModel> tagsList = new ArrayList<>();
        tagsList.add(new ObservationItemsModel(0, "Se gratte la tête"));
        tagsList.add(new ObservationItemsModel(0, "Silence"));*/

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager layoutManagerTime = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerTags.setLayoutManager(layoutManagerTags);
        recyclerTime.setLayoutManager(layoutManagerTime);

        final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(mobservationItemsModels);
        recyclerTags.setAdapter(adapter);
        recyclerTime.setAdapter(adapter);



    }
}
