package fr.wildcodeschool.vyfe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ObservationsRecyclerAdapter extends RecyclerView.Adapter<ObservationsRecyclerAdapter.ViewHolder> {
    private ArrayList<ObservationItemsModel> mObservations;

    public ObservationsRecyclerAdapter(ArrayList<ObservationItemsModel> observations) {
        mObservations = observations;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvColor, tvName;

        public ViewHolder(View v) {
            super(v);
            // rajouter la couleur
            this.tvName = v.findViewById(R.id.tv_name);
        }
    }


    @Override
    public ObservationsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_observation, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ObservationsRecyclerAdapter.ViewHolder holder, int position) {
        ObservationItemsModel itineraryModel = mObservations.get(position);
        holder.tvName.setText(itineraryModel.getName());
        //rajouter pour la couleur


    }


    @Override
    public int getItemCount() {
        return mObservations.size();
    }
}
