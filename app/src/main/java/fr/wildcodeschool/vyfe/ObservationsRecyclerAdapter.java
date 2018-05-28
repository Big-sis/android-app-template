package fr.wildcodeschool.vyfe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ObservationsRecyclerAdapter extends RecyclerView.Adapter<ObservationsRecyclerAdapter.ViewHolder> {

    private ArrayList<ObservationItemsModel> mObservations;
    private String mFrom;

    public ObservationsRecyclerAdapter(ArrayList<ObservationItemsModel> observations, String from) {
        mObservations = observations;
        mFrom = from;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivColor;

        TextView tvNum;
        Button btnDelete;
        ProgressBar bar;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.btnDelete = v.findViewById(R.id.btn_delete);
            this.tvNum = v.findViewById(R.id.tv_num);
            this.bar = v.findViewById(R.id.progressBar);

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
                // texte en dur mais qui sera remplacé par la suppression de ligne
                Toast.makeText(view.getContext(), "évènement supprimé", Toast.LENGTH_SHORT).show();
            }
        });

        if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.bar.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        else if (mFrom.equals("record")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.bar.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else if (mFrom.equals("timelines")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.bar.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else if (mFrom.equals("count")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            holder.bar.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            // Pour tester :
            holder.tvNum.setText("10");
        }
    }

    @Override
    public int getItemCount() {
        return mObservations.size();
    }

}
