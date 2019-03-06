package fr.vyfe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.R;
import fr.vyfe.model.TagSetModel;

public class TagSetSpinnerAdapter extends BaseAdapter {
    Context context;
    List<TagSetModel> tagsSets;
    LayoutInflater inflter;


    public TagSetSpinnerAdapter(Context applicationContext, List<TagSetModel> tagsSets) {
        this.context = applicationContext;
        TagSetModel fakeTagSet = new TagSetModel();
        fakeTagSet.setName(context.getString(R.string.import_grid_arrow)+ context.getString(R.string.arrow));
        if (tagsSets == null){
            this.tagsSets = new ArrayList<>();
            this.tagsSets.add(fakeTagSet);
        }
        else {
            this.tagsSets = tagsSets;
            this.tagsSets.add(0, fakeTagSet);
        }

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return tagsSets.size();
    }

    @Override
    public TagSetModel getItem(int position) {
        return tagsSets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.item_spinner_dropdown, null);
        TextView tvGrid = convertView.findViewById(R.id.tv_grid);
        tvGrid.setText(tagsSets.get(position).getName());
        return convertView;
    }
}

