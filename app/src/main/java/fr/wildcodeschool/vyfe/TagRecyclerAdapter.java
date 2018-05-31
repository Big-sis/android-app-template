package fr.wildcodeschool.vyfe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    private ArrayList<TagModel> mTagModelList;
    private String mFrom;

    public TagRecyclerAdapter(ArrayList<TagModel> observations, String from) {
        mTagModelList = observations;
        mFrom = from;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivColor;
        TextView tvNum;
        ProgressBar bar;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.tvNum = v.findViewById(R.id.tv_stats);
            this.bar = v.findViewById(R.id.progressBar);
        }
    }

    @Override
    public TagRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagRecyclerAdapter.ViewHolder holder, int position) {
        TagModel itineraryModel = mTagModelList.get(position);
        holder.tvName.setText(itineraryModel.getName());
        holder.ivColor.setBackgroundColor(itineraryModel.getColor());

        if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.bar.setVisibility(View.GONE);
        }
        else if (mFrom.equals("record")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.bar.setVisibility(View.GONE);
        }
        else if (mFrom.equals("timelines")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.bar.setVisibility(View.VISIBLE);
        }
        else if (mFrom.equals("count")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            holder.bar.setVisibility(View.GONE);
            // Pour tester :
            holder.tvNum.setText("10");
        }
    }

    @Override
    public int getItemCount() {
        return mTagModelList.size();
    }

}
