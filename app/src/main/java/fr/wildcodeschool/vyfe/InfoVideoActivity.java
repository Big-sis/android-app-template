package fr.wildcodeschool.vyfe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class InfoVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_video);

        final Spinner spinner=findViewById(R.id.spin_folder);
        //creer array utiliser un adapterSpinner pour rentrer les donner du spinner arrays
        final ArrayAdapter<CharSequence> adapterSpinner=ArrayAdapter.createFromResource(this, R.array.select_folder, android.R.layout.simple_spinner_item);
        //specifier le layout a utiliser lors affichage donné
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //appliquer ladapter au spinner
        spinner.setAdapter(adapterSpinner);
    }
}
