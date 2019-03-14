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

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TemplateModel;

public class TemplateRecyclerAdapter extends RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder> {

    private String mFrom;
    private ArrayList<TemplateModel> mTemplates;
    private Boolean mShowNumber;

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


        switch (mFrom) {
            case "create":
                holder.ivMenu.setVisibility(View.VISIBLE);
                holder.tvNum.setVisibility(View.INVISIBLE);
                break;
            case "start":
                holder.tvNum.setVisibility(View.INVISIBLE);
                break;
            case "play":
                holder.tvNum.setVisibility(View.VISIBLE);
                break;
            default:
                holder.tvNum.setVisibility(View.VISIBLE);
                //TagsSetSession : les tags de la grille
                holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        template.incrCount();
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
        if (template.getCount() == null) holder.tvNum.setText("0");
        else
            holder.tvNum.setText(String.valueOf(template.getCount()));

    }

    @Override
    public int getItemCount() {
        if (mTemplates==null) return 0;
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