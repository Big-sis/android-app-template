package fr.wildcodeschool.vyfe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ObservationsRecyclerAdapter extends RecyclerView.Adapter<ObservationsRecyclerAdapter.ViewHolder> {
    private ArrayList<ObservationItemsModel> mObservations;

    public ObservationsRecyclerAdapter(ArrayList<ObservationItemsModel> observations) {
        mObservations = observations;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivColor;
        Button btnDelete;

        public ViewHolder(View v) {
            super(v);
            // rajouter la couleur
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.btnDelete = v.findViewById(R.id.btn_delete);
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
        holder.ivColor.setBackgroundColor(itineraryModel.getColor());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // texte en dur mais qu'il sera remplacer par la suppression de ligne
                Toast.makeText(view.getContext(), "évènement supprimé", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return mObservations.size();
    }
}
