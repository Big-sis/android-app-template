package fr.wildcodeschool.vyfe.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import fr.wildcodeschool.vyfe.ColorNotFoundException;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.helper.ColorHelper;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TimeModel;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    private List<TagModel> mTagModelList;
    private List<TagModel> mTagedList;
    private String mFrom;

    public TagRecyclerAdapter(List<TagModel> observations, String from) {
        mTagModelList = observations;
        mFrom = from;
    }

    public TagRecyclerAdapter(List<TagModel> mTagModelList, List<TagModel> mTagedList, String mFrom) {
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
        TagModel tagModel = mTagModelList.get(position);
        holder.tvName.setText(tagModel.getName());
        try {
            holder.ivColor.setBackgroundResource(ColorHelper.convertColor(tagModel.getColor()));
        } catch (ColorNotFoundException e) {

            e.getMessage();
            Log.d("BEUG", "onBindViewHolder: "+ e.getMessage());
        }


        if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("create")){
            holder.tvNum.setVisibility(View.GONE);
            holder.ivMenu.setVisibility(View.VISIBLE);
        }
        else if (mFrom.equals("record")) {
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
        ImageView ivMenu;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.tvNum = v.findViewById(R.id.tv_stats);
            this.viewForeground = v.findViewById(R.id.view_foreground);
            this.ivMenu =v.findViewById(R.id.iv_menu);
        }
    }
}