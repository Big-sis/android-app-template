package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    public ArrayList<SessionsModel> video;

    private ArrayList<SessionsModel> filterList;
    private CustomFilterVideo filter;

    private SingletonSessions mSingletonSessions;

    public static final String TITLE_VIDEO = "titleVideo";
    public final static String FILE_NAME = "filename";
    public static final String ID_SESSION = "idSession";

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
        mSingletonSessions = SingletonSessions.getInstance();

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_video, null);
        }

        //Test avec seulement le nom
        final TextView tvName = convertView.findViewById(R.id.title_video);
        tvName.setText(video.getName());

        final TextView tvDate = convertView.findViewById(R.id.video_date);
        tvDate.setText(video.getDate());

        ImageView videoStatus = convertView.findViewById(R.id.img_upload_video);

        String lRegex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        if (video.getVideoLink().equals(lRegex)) {
            videoStatus.setImageResource(R.drawable.icons8_cloud_v_rifi__96);
        }

        //AFfichage miniature video
        ImageView videoView = convertView.findViewById(R.id.img_item_video);
        videoView.setImageBitmap(ImageViewSessionHelper.thumbnailSession(video.getVideoLink()));


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingletonSessions.setFileName(video.getVideoLink());
                mSingletonSessions.setIdSession(video.getIdSession());
                mSingletonSessions.setTitleSession(video.getName());
                Intent intent = new Intent(mContext, SelectedVideoActivity.class);
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
