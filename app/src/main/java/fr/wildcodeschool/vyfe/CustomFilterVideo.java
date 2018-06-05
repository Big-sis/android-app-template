package fr.wildcodeschool.vyfe;

import android.widget.Filter;

import java.util.ArrayList;

public class CustomFilterVideo extends Filter {

    private ArrayList<SessionsModel> filterList;
    private GridAdapter adapter;

    public CustomFilterVideo(ArrayList<SessionsModel> filterList, GridAdapter adapter) {
        this.filterList = filterList;
        this.adapter = adapter;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
        Filter.FilterResults results = new Filter.FilterResults();

        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<SessionsModel> filteredVideos = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                    filteredVideos.add(filterList.get(i));
                }
            }

            results.count = filteredVideos.size();
            results.values = filteredVideos;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
        adapter.video = (ArrayList<SessionsModel>) results.values;
        adapter.notifyDataSetChanged();
    }
}
