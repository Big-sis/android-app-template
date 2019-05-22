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
import java.util.Collections;
import java.util.Objects;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TemplateModel;

public class TemplateRecyclerAdapter extends RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder> {

    ArrayList<TagModel> mtagModels;
    private String mFrom;
    private ArrayList<TemplateModel> mTemplates;
    private Boolean mShowNumber;

    public TemplateRecyclerAdapter(ArrayList<TagModel> tagModels, ArrayList<TemplateModel> templates, String from) {
        mTemplates = templates;
        mFrom = from;
        mtagModels = tagModels;
    }

    public TemplateRecyclerAdapter(ArrayList<TemplateModel> templates, String from) {
        mTemplates = templates;
        mFrom = from;
    }

    @Override
    public TemplateRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TemplateRecyclerAdapter.ViewHolder holder, final int position) {

        final TemplateModel template = mTemplates.get(position);
        int occurrences = 0;
        if (mtagModels != null) {
            ArrayList<String> idTags = new ArrayList<>();
            for (TagModel tagModel : mtagModels) {
                idTags.add(tagModel.getTemplateId());
            }
            occurrences = Collections.frequency(idTags, template.getId());
        }


        switch (mFrom) {
            case "create":
                holder.ivMenu.setVisibility(View.VISIBLE);
                holder.tvNum.setVisibility(View.INVISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
                break;
            case "start":
                holder.tvNum.setVisibility(View.INVISIBLE);
                holder.ivDelete.setVisibility(View.GONE);
                break;
            case "play":
                holder.tvNum.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.GONE);
                break;
            default:
                holder.tvNum.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.GONE);
                //TagsSetSession : les tags de la grille
                holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.viewForeground.setBackgroundResource(R.drawable.color_gradient_yellow);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.viewForeground.setBackgroundResource(R.drawable.color_gradient_grey);
                            }
                        }, Constants.SPLASH_TIME_OUT);
                    }
                });

                break;
        }

        //View
        holder.tvName.setText(template.getName());
        holder.ivColor.setBackgroundResource(ColorHelper.getInstance().findColorById(template.getColor().getId()).getImage());
        holder.tvNum.setText(String.valueOf(occurrences));

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ivDelete.setBackgroundResource(R.drawable.deletered);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteItem(position);
                        notifyDataSetChanged();
                        holder.ivDelete.setBackgroundResource(R.drawable.delete);
                    }
                }, Constants.SPLASH_TIME_OUT_400);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mTemplates == null) return 0;
        return mTemplates.size();
    }

    public void deleteItem(int position) {

        for (int i = position + 1; i < mTemplates.size(); i++) {
            mTemplates.get(i).setPosition(mTemplates.get(i).getPosition() - 1);
        }
        Objects.requireNonNull(mTemplates).remove(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivColor;
        TextView tvNum;
        LinearLayout viewForeground;
        ImageView ivMenu;
        ImageView ivDelete;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.tvNum = v.findViewById(R.id.tv_stats);
            this.viewForeground = v.findViewById(R.id.view_foreground);
            this.ivMenu = v.findViewById(R.id.iv_menu);
            this.ivDelete = v.findViewById(R.id.iv_delete);

        }
    }

}