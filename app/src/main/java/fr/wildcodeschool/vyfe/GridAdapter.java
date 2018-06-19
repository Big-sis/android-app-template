package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    public ArrayList<SessionsModel> video;

    private ArrayList<SessionsModel> filterList;
    private CustomFilterVideo filter;


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


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(mContext, SelectedVideoActivity.class);
                intent.putExtra("titleSession", video.getName());
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
