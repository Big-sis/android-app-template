package fr.wildcodeschool.vyfe;


import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    private ArrayList<TagModel> mTagModelList;
    private ArrayList<TagModel> mTagedList;
    private String mFrom;

    public TagRecyclerAdapter(ArrayList<TagModel> observations, String from) {
        mTagModelList = observations;
        mFrom = from;
    }

    public TagRecyclerAdapter(ArrayList<TagModel> mTagModelList, ArrayList<TagModel> mTagedList, String mFrom) {
        this.mTagModelList = mTagModelList;
        this.mTagedList = mTagedList;
        this.mFrom = mFrom;
    }

    @Override
    public TagRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagRecyclerAdapter.ViewHolder holder, int position) {
        int colorFile = 0;
        TagModel tagModel = mTagModelList.get(position);
        switch (tagModel.getColorName()){
            case "color_gradient_blue_dark":
                colorFile = R.drawable.color_gradient_blue_dark;
                break;
            case"color_gradient_blue_ligh":
                colorFile =R.drawable.color_gradient_blue_light;
                break;
            case"color_gradient_faded_orange":
                colorFile =R.drawable.color_gradient_faded_orange;
                break;
            case"color_gradient_green":
                colorFile =R.drawable.color_gradient_green;
                break;
            case"color_gradient_grey":
                colorFile =R.drawable.color_gradient_grey;
                break;
            case"color_gradient_rosy":
                colorFile =R.drawable.color_gradient_rosy;
                break;

        }

        holder.tvName.setText(tagModel.getName());
        holder.ivColor.setBackgroundResource(colorFile);


        if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("record")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            int count = tagModel.getCount();
            holder.tvNum.setText(String.valueOf(count));
        } else if (mFrom.equals("timelines")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("count")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            for (TagModel taged : mTagedList) {
                ArrayList<TimeModel> timeList = taged.getTimes();
                String tagedName = taged.getName();
                if (tagedName.equals(tagModel.getName())) {
                    tagModel.setTimes(timeList);
                }
            }
            if (!(tagModel.getTimes() == null)) {
                int count = tagModel.getTimes().size();
                holder.tvNum.setText(String.valueOf(count));
            } else {
                holder.tvNum.setText("0");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTagModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivColor;
        TextView tvNum;
        LinearLayout viewForeground;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.tvNum = v.findViewById(R.id.tv_stats);
            this.viewForeground = v.findViewById(R.id.view_foreground);
        }
    }
}