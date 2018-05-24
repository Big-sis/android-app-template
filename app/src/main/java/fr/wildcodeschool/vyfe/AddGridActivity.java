package fr.wildcodeschool.vyfe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddGridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grid);
        final EditText etName = findViewById(R.id.et_name);

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
                etName.setBackgroundColor(color);
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
                .setTitle("Choisir votre couleur")
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        EditText etName = findViewById(R.id.et_name);
                        etName.setBackgroundColor(color);

                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();


    }
}
