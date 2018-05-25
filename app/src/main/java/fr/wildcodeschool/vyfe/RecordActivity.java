package fr.wildcodeschool.vyfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        RecyclerView recycler = findViewById(R.id.list_tags);

        final ArrayList<ObservationItemsModel> tagsList = new ArrayList<>();
        //insertion des observations a ajouter
        tagsList.add(new ObservationItemsModel(0, "Se gratte la tête"));
        tagsList.add(new ObservationItemsModel(0, "Silence"));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);

        final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(tagsList);
        recycler.setAdapter(adapter);



    }
}
