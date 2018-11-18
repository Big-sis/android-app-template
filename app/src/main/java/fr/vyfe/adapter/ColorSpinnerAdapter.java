package fr.vyfe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fr.vyfe.R;
import fr.vyfe.model.ColorModel;

public class ColorSpinnerAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflter;
    ArrayList<ColorModel> colors;


    public ColorSpinnerAdapter(Context applicationContext, ArrayList<ColorModel> colors) {
        this.context = applicationContext;
        this.colors = colors;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public ColorModel getItem(int i) {
        return colors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_color, null);
        ImageView ivColor = (ImageView) view.findViewById(R.id.iv_color);
        TextView tvColorNames = (TextView) view.findViewById(R.id.tv_color);
        ivColor.setImageResource(getItem(i).getImage());
        tvColorNames.setText(getItem(i).getName());
        return view;
    }
}