package fr.vyfe.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.vyfe.R;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TagModel;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    private List<TagModel> mTagModelList;
    private String mFrom;


    public TagRecyclerAdapter(List<TagModel> observations, String from) {
        mTagModelList = observations;
        mFrom = from;
    }


    @Override
    public TagRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagRecyclerAdapter.ViewHolder holder, int position) {

        List<TagModel> tagList = mTagModelList;


        TagModel tagModel = tagList.get(position);


        holder.tvName.setText(tagModel.getTagName());
        holder.ivColor.setBackgroundResource(ColorHelper.getInstance().findColorById(tagModel.getColor().getId()).getImage());
        //TODO: Creer classe fille
        if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("create")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.ivMenu.setVisibility(View.VISIBLE);
        } else if (mFrom.equals("record")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            int count = tagModel.getCount();
            holder.tvNum.setText(String.valueOf(count));
        } else if (mFrom.equals("timelines")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("count")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            if (mTagModelList != null) {
                holder.tvNum.setText(String.valueOf(tagModel.getCount()));
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
            this.ivMenu = v.findViewById(R.id.iv_menu);
        }
    }
}