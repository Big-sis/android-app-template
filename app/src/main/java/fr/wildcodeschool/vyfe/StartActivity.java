package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    ArrayList<TagModel> mTagModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.start_session);

        RecyclerView listItems = findViewById(R.id.recycler_view);
        final RadioButton radioButtonImport = findViewById(R.id.radio_button_insert);
        final RadioButton radioButtonNew = findViewById(R.id.radio_Button_new);

        final Spinner spinner=findViewById(R.id.spinner_session_infos);
        final ArrayAdapter<CharSequence> adapterSpinner=ArrayAdapter.createFromResource(this, R.array.select_folder, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);
        //TODO: recuperation données API pour afficher spinner + recyclerview

        radioButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioButtonImport.isChecked()){
                    radioButtonNew.setChecked(false);
                    //TODO: affichier l'accès aux elements de imports grilles
                }
            }
        });

        radioButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioButtonNew.isChecked()){
                    radioButtonImport.setChecked(false);
                    //TODO: affichier l'accès aux elements de création grilles
                }

            }
        });

        //TODO: en fct du radio button selectionner envoyer telles ou telles arraylist


        if (AddGridActivity.mAddEvent) {
            listItems.setVisibility(View.VISIBLE);
            mTagModels = getIntent().getExtras().getParcelableArrayList("list");
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            listItems.setLayoutManager(layoutManager);
            final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModels, "start");
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
                toRecord.putParcelableArrayListExtra("list", mTagModels);
                startActivity(toRecord);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
}
