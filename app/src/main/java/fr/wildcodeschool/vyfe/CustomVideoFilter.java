package fr.wildcodeschool.vyfe;

import android.widget.Filter;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.adapter.VideoGridAdapter;
import fr.wildcodeschool.vyfe.model.SessionModel;

public class CustomVideoFilter extends Filter {

    private ArrayList<SessionModel> filterList;
    private VideoGridAdapter adapter;

    public CustomVideoFilter(ArrayList<SessionModel> filterList, VideoGridAdapter adapter) {
        this.filterList = filterList;
        this.adapter = adapter;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
        Filter.FilterResults results = new Filter.FilterResults();

        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<SessionModel> filteredVideos = new ArrayList<>();

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
        adapter.mSessions = (ArrayList<SessionModel>) results.values;
        adapter.notifyDataSetChanged();
    }
}
