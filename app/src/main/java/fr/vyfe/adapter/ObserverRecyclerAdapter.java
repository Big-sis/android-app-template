package fr.vyfe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.vyfe.R;

public class ObserverRecyclerAdapter extends RecyclerView.Adapter<ObserverRecyclerAdapter.ViewHolder> {

    private List<String> mObservers;

    public ObserverRecyclerAdapter(List<String> observations) {
        mObservers = observations;

    }

    @Override
    public ObserverRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_observer, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ObserverRecyclerAdapter.ViewHolder holder, int position) {

        holder.tvName.setText(mObservers.get(position));

    }

    @Override
    public int getItemCount() {
        return mObservers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name_observer);

        }
    }
}