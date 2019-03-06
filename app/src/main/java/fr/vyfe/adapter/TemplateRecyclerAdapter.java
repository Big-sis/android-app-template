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
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;

public class TemplateRecyclerAdapter extends RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder> {

    private String mFrom;
    private SessionModel mSession;
    private TagSetModel mTagSet;


    public TemplateRecyclerAdapter(SessionModel mSession, String from) {
        this.mSession = mSession;
        mFrom = from;
    }

    public TemplateRecyclerAdapter(TagSetModel tagSetModel, String from) {
        this.mTagSet = tagSetModel;
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

        ArrayList<TemplateModel> tagsTagsSets = new ArrayList<>();
        TemplateModel template = new TemplateModel();


        if (mFrom.equals("create")) {
            holder.ivMenu.setVisibility(View.VISIBLE);
            holder.tvNum.setVisibility(View.GONE);
            if (mTagSet != null) {
                tagsTagsSets = mTagSet.getTemplates();
                template = tagsTagsSets.get(position);
            }
        } else if (mFrom.equals("start")) {
            holder.tvNum.setVisibility(View.INVISIBLE);
            if (mTagSet != null) {
                tagsTagsSets = mTagSet.getTemplates();
                template = tagsTagsSets.get(position);
            }
        } else {
            holder.tvNum.setVisibility(View.VISIBLE);
            //TagsSetSession : les tags de la grille
            tagsTagsSets = mSession.getTagsSet().getTemplates();
            template = tagsTagsSets.get(position);
            //TagsSession timeline
            ArrayList<TagModel> tagsTimeline = mSession.getTags();

            for (TagModel tag : tagsTimeline) {
                if (template.getId().equals(tag.getTemplateId())) {
                    template.incrCount();
                }
            }

            if (template.isTouch()) {
                holder.viewForeground.setBackgroundResource(R.drawable.color_gradient_yellow);
                template.setTouch(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.viewForeground.setBackgroundResource(R.drawable.color_gradient_grey);
                    }
                }, Constants.SPLASH_TIME_OUT);
            }

        }
        //View
        holder.tvName.setText(template.getName());
        holder.ivColor.setBackgroundResource(ColorHelper.getInstance().findColorById(template.getColor().getId()).getImage());
        holder.tvNum.setText(String.valueOf(template.getCount()));
    }

    @Override
    public int getItemCount() {
        if (mTagSet == null && mSession != null && mSession.getTagsSet().getTemplates() != null)
            return mSession.getTagsSet().getTemplates().size();
        if (mTagSet == null && mSession == null) return 0;
        if (mTagSet.getTemplates() == null) return 0;
        return mTagSet.getTemplates().size();
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