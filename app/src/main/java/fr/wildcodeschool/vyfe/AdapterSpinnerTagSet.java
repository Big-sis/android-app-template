package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.HashMap;


public  class AdapterSpinnerTagSet extends ArrayAdapter<String> {

    public AdapterSpinnerTagSet(Context applicationContext, HashMap<String,String> hashMapGrid) {
       super(applicationContext,R.layout.item_spinner_dropdown, new ArrayList<String>(hashMapGrid.values()));

    }


}
