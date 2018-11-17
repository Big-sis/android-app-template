package fr.vyfe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import fr.vyfe.R;

public class CustomAdapter extends BaseAdapter {

    Context context;
    int color[];
    String[] colorName;
    String[] nameDrawable;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, int[] color, String[] colorName, String[] nameDrawable) {
        this.context = applicationContext;
        this.color = color;
        this.colorName = colorName;
        this.nameDrawable =nameDrawable;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return color.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_color, null);
        ImageView ivColor = (ImageView) view.findViewById(R.id.iv_color);
        TextView tvColorNames = (TextView) view.findViewById(R.id.tv_color);
        ivColor.setImageResource(color[i]);
        tvColorNames.setText(colorName[i]);
        return view;
    }
}