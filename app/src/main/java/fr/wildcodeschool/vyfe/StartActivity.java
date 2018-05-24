package fr.wildcodeschool.vyfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RecyclerView listItems = findViewById(R.id.recycler_view);

        final ArrayList<ObservationItemsModel> observationItemsModels = new ArrayList<>();
        //insertion des observations a ajouter
        observationItemsModels.add(new ObservationItemsModel("Vert", "Se gratte la tête"));
        observationItemsModels.add(new ObservationItemsModel("Bleu", "Silence"));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listItems.setLayoutManager(layoutManager);

        final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(observationItemsModels);
        listItems.setAdapter(adapter);


    }
}
