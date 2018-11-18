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
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;

public class TagsSetsSpinnerAdapter extends BaseAdapter {
    Context context;
    List<TagSetModel> tagsSets;
    LayoutInflater inflter;


    public TagsSetsSpinnerAdapter(Context applicationContext, List<TagSetModel> tagsSets) {
        this.context = applicationContext;
        this.tagsSets = new ArrayList<>();
        TagSetModel fakeTag = new TagSetModel();
        fakeTag.setName(R.string.import_grid_arrow + applicationContext.getString(R.string.arrow));
        this.tagsSets.add(fakeTag);
        this.tagsSets.addAll(tagsSets);
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
