package fr.wildcodeschool.vyfe.activity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.helper.ApiHelperVideo;
import fr.wildcodeschool.vyfe.adapter.VideoGridAdapter;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;
import fr.wildcodeschool.vyfe.viewModel.SingletonSessions;

/**
 * Activity presents cache memory videos and device videos
 * The user can select one and view it
 */
public class MyVideosActivity extends VyfeActivity {
    SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    ArrayList<SessionModel> mSessionsModelList = mSingletonSessions.getmSessionsList();
    FirebaseDatabase mDatabase;
    VideoGridAdapter mVideoGridAdapter = new VideoGridAdapter(this, mSessionsModelList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        mDatabase = SingletonFirebase.getInstance().getDatabase();

        final GridView gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        EditText searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.colorWhiteTwo));
        searchText.setHintTextColor(getResources().getColor(R.color.colorWhiteTwo));
        ImageView closeSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        ImageView search = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        search.setImageResource(android.R.drawable.ic_menu_search);
        final ProgressBar pBLoading = findViewById(R.id.progress_bar_loading);

        ApiHelperVideo.getVideo(this, gridView, new ApiHelperVideo.ForecastResponse() {
            @Override
            public void onSuccess(ArrayList<SessionModel> result) {
                pBLoading.setVisibility(View.GONE);
                gridView.setAdapter(mVideoGridAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MyVideosActivity.this, " erreur :"+ error, Toast.LENGTH_SHORT).show();
                pBLoading.setVisibility(View.GONE);
            }

            @Override
            public void onWait() {
                pBLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                pBLoading.setVisibility(View.GONE);
                gridView.setAdapter(mVideoGridAdapter);
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_videos);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mVideoGridAdapter.getFilter().filter(s);
                return false;
            }
        });
        mVideoGridAdapter.notifyDataSetChanged();
    }

}