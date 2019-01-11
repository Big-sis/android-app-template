package fr.vyfe.adapter;


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
import fr.vyfe.activity.MySessionsActivity;
import fr.vyfe.activity.VyfeActivity;
import fr.vyfe.helper.TimeHelper;
import fr.vyfe.model.SessionModel;

public class VideoGridAdapter extends BaseAdapter implements Filterable {

    private final VyfeActivity mContext;
    public ArrayList<SessionModel> mSessions;

    private ArrayList<SessionModel> filterList;
    private CustomVideoFilter filter;

    private SessionModel mSession;

    public VideoGridAdapter(VyfeActivity context, ArrayList<SessionModel> sessions) {
        this.mContext = context;
        this.mSessions = sessions;
        this.filterList = sessions;
    }

    @Override
    public int getCount() {
        return mSessions != null ? mSessions.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SessionModel getItem(int position) {
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
        tvDate.setText(mContext.getString(R.string.date_session)+mSession.getFormatDate());

        ImageView videoStatus = convertView.findViewById(R.id.img_upload_video);

        if(mSession.getServerVideoLink()!= null){
           videoStatus.setImageResource(R.drawable.uploadtocloudblanc);
        }

        TextView tvDuration = convertView.findViewById(R.id.video_duration);
        tvDuration.setText(mContext.getString(R.string.duration_session)+ TimeHelper.mllsConvert(mSession.getDuration()));
      
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
