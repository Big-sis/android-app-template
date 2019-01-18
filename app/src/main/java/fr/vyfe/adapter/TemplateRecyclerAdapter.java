package fr.vyfe.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TemplateModel;

public class TemplateRecyclerAdapter extends RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder> {

    private List<TemplateModel> mTemplates;
    private String mFrom;
    private SessionModel mSession;


    public TemplateRecyclerAdapter(List<TemplateModel> observations, String from) {
        mTemplates = observations;
        mFrom = from;
    }

    public TemplateRecyclerAdapter(List<TemplateModel> observations, SessionModel mSession, String from) {
        mTemplates = observations;
        this.mSession = mSession;
        mFrom = from;
    }


    @Override
    public TemplateRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TemplateRecyclerAdapter.ViewHolder holder, int position) {

        List<TemplateModel> templateList = mTemplates;
        TemplateModel template = templateList.get(position);

        if (null != mSession && null != mSession.getTags()) {
            ArrayList<TagModel> tags = mSession.getTags();
            for (int i = 0; i < tags.size(); i++) {
                if (template.getId().equals(tags.get(i).getTemplateId())) {
                    template.incrCount();
                }

            }
        }

        holder.tvName.setText(template.getName());
        holder.ivColor.setBackgroundResource(ColorHelper.getInstance().findColorById(template.getColor().getId()).getImage());
        //TODO: Creer classe fille
        if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("create")) {
            holder.tvNum.setVisibility(View.GONE);
            holder.ivMenu.setVisibility(View.VISIBLE);
        } else if (mFrom.equals("record")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            holder.tvNum.setText(String.valueOf(template.getCount()));

            if (template.isTouch()) {
                holder.viewForeground.setBackgroundResource(R.drawable.color_gradient_yellow);
                templateList.get(position).setTouch(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.viewForeground.setBackgroundResource(R.drawable.color_gradient_grey);
                    }
                }, Constants.SPLASH_TIME_OUT);
            }
        } else if (mFrom.equals("timelines")) {
            holder.tvNum.setVisibility(View.GONE);
        } else if (mFrom.equals("count")) {
            holder.tvNum.setVisibility(View.VISIBLE);
            holder.tvNum.setVisibility(View.VISIBLE);
            holder.tvNum.setText(String.valueOf(template.getCount()));
        }
    }

    @Override
    public int getItemCount() {
        return mTemplates.size();
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