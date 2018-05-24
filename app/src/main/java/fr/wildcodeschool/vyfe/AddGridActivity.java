package fr.wildcodeschool.vyfe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddGridActivity extends AppCompatActivity {
    int finalcolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grid);
        final EditText etName = findViewById(R.id.et_name);
        final ImageView ivColor = findViewById(R.id.iv_color);

        Button btnChooseColor = findViewById(R.id.btn_chosse_color);
        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorChoose();
            }
        });

        final Button btnAleatory = findViewById(R.id.btn_aleatory);
        btnAleatory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                ivColor.setBackgroundColor(color);
                finalcolor = color;
            }
        });

        Button btnAddEvenement = findViewById(R.id.btn_add);
        btnAddEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueName = etName.getText().toString();
                ObservationItemsModel observationItemsModel = new ObservationItemsModel(finalcolor, valueName);

                RecyclerView listItems = findViewById(R.id.recycler_view);

                final ArrayList<ObservationItemsModel> observationItemsModels = new ArrayList<>();

                observationItemsModels.add(observationItemsModel);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddGridActivity.this, LinearLayoutManager.VERTICAL, false);
                listItems.setLayoutManager(layoutManager);

                final ObservationsRecyclerAdapter adapter = new ObservationsRecyclerAdapter(observationItemsModels);
                listItems.setAdapter(adapter);
            }
        });
    }

    public void openColorChoose() {
        final ColorPicker colorPicker = new ColorPicker(this);
        final ArrayList<String> colors = new ArrayList<>();
        //les couleurs seront insérées dans values avec la charte graphique
        colors.add(("#bd000d"));
        colors.add(("#5a00c5"));
        colors.add(("#1e8900"));
        colors.add(("#b7bb00"));
        colors.add(("#b664ba"));


        colors.add(("#f91734"));
        colors.add(("#932bf9"));
        colors.add(("#5bba25"));
        colors.add(("#eded42"));
        colors.add(("#ea94ed"));

        colors.add(("#ff5f5e"));
        colors.add(("#ca62ff"));
        colors.add(("#90ed59"));
        colors.add(("#ffff77"));
        colors.add(("#ffc6ff"));


        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setTitle(getString(R.string.choose_color))
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        ImageView ivColor = findViewById(R.id.iv_color);
                        ivColor.setBackgroundColor(color);
                        finalcolor = color;

                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();


    }
}
