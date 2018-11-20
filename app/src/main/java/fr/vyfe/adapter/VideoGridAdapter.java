package fr.vyfe.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.CustomVideoFilter;
import fr.vyfe.R;
import fr.vyfe.activity.SelectVideoActivity;
import fr.vyfe.model.SessionModel;

public class VideoGridAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    public ArrayList<SessionModel> mSessions;

    private ArrayList<SessionModel> filterList;
    private CustomVideoFilter filter;

    private SessionModel mSession;

    public VideoGridAdapter(Context context, ArrayList<SessionModel> sessions) {
        this.mContext = context;
        this.mSessions = sessions;
        this.filterList = sessions;
    }

    @Override
    public int getCount() {
        return mSessions.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mSessions.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mSession = this.mSessions.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_video, null);
        }

        //Test avec seulement le nom
        final TextView tvName = convertView.findViewById(R.id.title_video);
        tvName.setText(mSession.getName());

        final TextView tvDate = convertView.findViewById(R.id.video_date);
        tvDate.setText(mSession.getFormatDate());

        ImageView videoStatus = convertView.findViewById(R.id.img_upload_video);

        String lRegex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

       if (mSession.getDeviceVideoLink().equals(lRegex)) {
            videoStatus.setImageResource(R.drawable.icons8_cloud_v_rifi__96);
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectVideoActivity.class);
                intent.putExtra("SessionModel", mSession);
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomVideoFilter(filterList, this);
        }
        return filter;
    }
}
