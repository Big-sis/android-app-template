package fr.vyfe.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.CustomVideoFilter;
import fr.vyfe.R;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {

        mSession = this.mSessions.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_video, null);
        }

        final TextView tvName = convertView.findViewById(R.id.title_video);
        final TextView tvDate = convertView.findViewById(R.id.video_date);
        TextView tvDuration = convertView.findViewById(R.id.video_duration);

        ImageView ivAvailableDevice = convertView.findViewById(R.id.iv_available_device);
        ImageView ivAvailablePlateforme = convertView.findViewById(R.id.iv_available_plateforme);

        ImageButton imgBtnPlay = convertView.findViewById(R.id.img_button_play);
        ImageButton imgBtnEdit = convertView.findViewById(R.id.img_button_edit);
        ImageButton imgBtnDelete = convertView.findViewById(R.id.img_btn_delete);
        final CheckBox checkBoxDelete = convertView.findViewById(R.id.checkbox_delete);

        tvName.setText(mSession.getName());
        tvDate.setText(mContext.getString(R.string.date_session) + mSession.getFormatDate());
        tvDuration.setText(mContext.getString(R.string.duration_session) + TimeHelper.formatMillisecTime(mSession.getDuration()));


        if (mSession.getDeviceVideoLink() != null)
            ivAvailableDevice.setBackgroundResource(R.drawable.dispo);
        else ivAvailableDevice.setBackgroundResource(R.drawable.nodispo);

        if (mSession.getServerVideoLink() != null)
            ivAvailablePlateforme.setBackgroundResource(R.drawable.dispo);
        else ivAvailablePlateforme.setBackgroundResource(R.drawable.nodispo);

        setOnClickAction(imgBtnPlay, parent, position);
        setOnClickAction(imgBtnDelete, parent, position);
        setOnClickAction(imgBtnEdit, parent, position);
        setOnClickAction(checkBoxDelete,parent,position);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkBoxDelete.setVisibility(View.VISIBLE);
                checkBoxDelete.setChecked(true);
                ((GridView) parent).performItemClick(v, position, 0);
                return false;
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

    public void setOnClickAction(final View view, final View parent, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);

            }
        });

    }
}
