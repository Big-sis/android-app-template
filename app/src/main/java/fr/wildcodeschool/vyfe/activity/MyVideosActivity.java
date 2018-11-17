package fr.wildcodeschool.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.adapter.VideoGridAdapter;
import fr.wildcodeschool.vyfe.helper.AuthHelper;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.viewModel.MyVideosViewModel;
import fr.wildcodeschool.vyfe.viewModel.MyVideosViewModelFactory;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Activity presents cache memory videos and device videos
 * The user can select one and view it
 */
public class MyVideosActivity extends VyfeActivity {
    VideoGridAdapter mVideoGridAdapter;
    MyVideosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        viewModel = ViewModelProviders.of(this, new MyVideosViewModelFactory( mAuth.getCurrentUser().getId())).get(MyVideosViewModel.class);

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


        viewModel.getSessions().observe(this, new Observer<List<SessionModel>>() {
            @Override
            public void onChanged(@Nullable List<SessionModel> tagSetModels) {
                if(tagSetModels!=null) {
                    ArrayList<SessionModel> userSessions = new ArrayList<>();
                    pBLoading.setVisibility(View.INVISIBLE);

                    File externalStorage = getExternalStoragePublicDirectory(DIRECTORY_MOVIES + "/" + "Vyfe");
                    final String racineExternalStorage = String.valueOf(externalStorage.getAbsoluteFile());
                    final String[] filesExternalStorage = externalStorage.list();

                    if ((filesExternalStorage != null)) {
                        for (String nameFileExternalStorage : filesExternalStorage) {
                            String nameCache = racineExternalStorage + "/" + nameFileExternalStorage;

                            for (SessionModel sessionModel : viewModel.getSessions().getValue()) {
                                if (sessionModel.getDeviceVideoLink().equals(nameCache)) {
                                    userSessions.add(sessionModel);
                                }
                            }
                        }
                    }

                    mVideoGridAdapter = new VideoGridAdapter(MyVideosActivity.this, userSessions);
                    gridView.setAdapter(mVideoGridAdapter);
                }
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
        if (mVideoGridAdapter != null) mVideoGridAdapter.notifyDataSetChanged();
    }

}