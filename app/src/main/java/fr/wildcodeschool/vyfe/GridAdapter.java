package fr.wildcodeschool.vyfe;

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

public class GridAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    public ArrayList<SessionsModel> video;

    private ArrayList<SessionsModel> filterList;
    private CustomFilterVideo filter;

    public static final String TITLE_VIDEO = "titleVideo";
    public final static String FILE_NAME = "filename";

    public GridAdapter(Context context, ArrayList<SessionsModel> video) {
        this.mContext = context;
        this.video = video;
        this.filterList = video;
    }

    @Override
    public int getCount() {
        return video.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return video.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SessionsModel video = this.video.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_video, null);
        }

        //Test avec seulement le nom
        final TextView tvName = convertView.findViewById(R.id.title_video);
        tvName.setText(video.getName());

        TextView date = convertView.findViewById(R.id.video_date);
        date.setText(video.getDate());

        ImageView videoStatus = convertView.findViewById(R.id.img_upload_video);

        String lRegex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        if (video.getVideoLink().equals(lRegex)) {
            videoStatus.setImageResource(R.drawable.icons8_cloud_v_rifi__96);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectedVideoActivity.class);
                intent.putExtra(TITLE_VIDEO, video.getName());
                intent.putExtra(FILE_NAME, video.getVideoLink());
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilterVideo(filterList, this);
        }
        return filter;
    }
}
