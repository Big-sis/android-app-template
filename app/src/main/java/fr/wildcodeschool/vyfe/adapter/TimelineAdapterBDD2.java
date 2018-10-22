package fr.wildcodeschool.vyfe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import fr.wildcodeschool.vyfe.ColorNotFoundException;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.helper.ColorHelper;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TagsSessionBB2;
import fr.wildcodeschool.vyfe.model.TimeModel;
import fr.wildcodeschool.vyfe.model.TimesTaggerSessionBB2;

public class TimelineAdapterBDD2 extends RecyclerView.Adapter<TimelineAdapterBDD2.ViewHolder> {

    private List<TagsSessionBB2> mList;
    private static int mVideoDuration;

    public TimelineAdapterBDD2(List<TagsSessionBB2> list, int videoDuration) {
        mList = list;
        mVideoDuration = videoDuration;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivColor;
        LinearLayout viewForeground;
        ImageView ivMenu;
        LinearLayout mLlMain;

        Context context;

        public ViewHolder(View v) {
            super(v);
            this.tvName = v.findViewById(R.id.tv_name);
            this.ivColor = v.findViewById(R.id.iv_color);
            this.viewForeground = v.findViewById(R.id.view_foreground);
            this.ivMenu = v.findViewById(R.id.iv_menu);
            this.mLlMain = v.findViewById(R.id.ll_main_playvideo);

            this.context = v.getContext();
        }

        public void bind(TagsSessionBB2 tag){

            String tagName = tag.getName();

            //Creation de chaque etage de tags sur la timeline
            final RelativeLayout timelineRow = new RelativeLayout(context);
            //mLlMain.addView(timelineRow);

            TextView tvNameTimeline = new TextView(context);
            tvNameTimeline.setText(tagName);


            //Creation des noms pour chaque timeline
            RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTv.setMargins(convertToDp(15), convertToDp(8), 0, convertToDp(8));
            tvNameTimeline.setLayoutParams(layoutParamsTv);
            tvNameTimeline.setTextColor(Color.WHITE);
            tvNameTimeline.setMinimumHeight(convertToDp(25));


            TimesTaggerSessionBB2 tagger= tag.getTaggerIds();
            // Créé une image par utilisation du tag en cour
            for (final TimeModel tagTime : tagger.getTimes()) {

                double start = convertIntoTimelineViewRef(tagTime.getStart(), timelineRow.getWidth());
                double end = convertIntoTimelineViewRef(tagTime.getEnd(), timelineRow.getWidth());

                final ImageView tagImageView = new ImageView(context);
                RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParamsIv.setMargins((int) Math.floor(start), convertToDp(8), 0, convertToDp(8));

                tagImageView.setLayoutParams(layoutParamsIv);
                tagImageView.setMinimumHeight(convertToDp(25));
                tagImageView.setMinimumWidth(Math.max(convertToDp(50), (int) (end - start)));

                try {
                    tagImageView.setBackgroundResource(ColorHelper.convertColor(tag.getColor()));
                } catch (ColorNotFoundException e) {
                    e.getMessage();
                    Log.d("BEUG", "onBindViewHolder: "+ e.getMessage());
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                timelineRow.setLayoutParams(layoutParams);
                timelineRow.setBackgroundColor(context.getResources().getColor(R.color.colorCharcoalGrey));
                timelineRow.addView(tagImageView);
            }
        }

        private int convertToDp(int size) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
        }

        // Convert tag start and end into timeline view reference
        private double convertIntoTimelineViewRef(int value, int timelineWidth) {
            int micro = value * context.getResources().getInteger(R.integer.second_to_micro);
            double ratio = micro / mVideoDuration;
            return (ratio * timelineWidth) / context.getResources().getInteger(R.integer.micro_to_milli);
        }
    }
}