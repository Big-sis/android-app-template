package fr.vyfe.helper;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.vyfe.Constants;
import fr.vyfe.R;

public class OpenInfoHelper {

    public static void setOnClick(String info, ConstraintLayout constraintLayout, Context context) {
        final TextView tvInfo = constraintLayout.findViewById(R.id.tv_information);
        ImageView ivInfo = constraintLayout.findViewById(R.id.iv_info);

        switch (info) {
            case Constants.INFO_TAGSET:
                info = context.getString(R.string.info_tagSet);
                break;
            case Constants.INFO_SESSION:
                info = context.getString(R.string.info_sessions);
                break;
            case Constants.INFO_CREATE_TAGSET:
                info = context.getString(R.string.info_create_tagSet);
                break;
            case Constants.INFO_VIDEO:
                info = context.getString(R.string.info_video);
                break;
            default:
                break;

        }

        tvInfo.setText(info);
        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvInfo.getVisibility() == View.GONE) {
                    tvInfo.setVisibility(View.VISIBLE);
                } else tvInfo.setVisibility(View.GONE);
            }
        });
    }
}
