package fr.vyfe.helper;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.model.TagModel;


public class ConvertTagsToImageView {
    private int scrollingSpeed;
    private Context context;
    private RelativeLayout mTimeline;
    private int windowsSize;
    private int positionVideo;

    public ConvertTagsToImageView(Context context,RelativeLayout mTimeline, int windowsSize,String speed,int positionVideo  ) {
        this.context = context;
        this.scrollingSpeed = convertToSpeed(speed);
        this.mTimeline = mTimeline;
        this.windowsSize =  windowsSize;
        this.positionVideo = positionVideo;
    }


    public RelativeLayout getTimeline(  ArrayList<TagModel> tagModel ) {
        mTimeline.removeAllViews();
        for (TagModel tag : tagModel) {
            int startTagToSecond = tag.getStart() * 1000;
            int endTagToSecond = tag.getEnd() * 1000;
            int maxTime = positionVideo + windowsSize;

            if (startTagToSecond >= positionVideo || startTagToSecond <= maxTime
                    || startTagToSecond <= positionVideo && endTagToSecond >= positionVideo && endTagToSecond <= maxTime) {
                createIvTags(tag, positionVideo, scrollingSpeed);
            }
        }
        return mTimeline;
    }

    public void createIvTags(final TagModel tag, int position, int scrollingSpeed) {
        int startTagToSecond = tag.getStart() * 1000;
        int endTagToSecond = tag.getEnd() * 1000;

        ImageView iv = new ImageView(context);
        iv.setBackgroundResource(tag.getColor().getImage());
        int tagLength = (convertToDp(((endTagToSecond - startTagToSecond)) / scrollingSpeed));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                tagLength, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((convertToDp((startTagToSecond - position) / scrollingSpeed)), 16, 0, 16);
        iv.setMinimumHeight(40);
        iv.setLayoutParams(layoutParams);

        TextView tvName = new TextView(context);
        tvName.setText(tag.getName());
        tvName.setTextColor(context.getResources().getColor(R.color.colorWhiteTwo));
        tvName.setLayoutParams(layoutParams);
        mTimeline.addView(iv);
        mTimeline.addView(tvName);

    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    private int convertToSpeed(String speed) {
        switch (speed) {
            case Constants.LOW_SPEED:
                return 15;
            case Constants.MEDIUM_SPEED:
                return 7;
            case Constants.HIGHT_SPEED:
                return 1;
            default:
                return 1;
        }
    }
}
