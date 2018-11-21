package fr.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.VideoGridAdapter;
import fr.vyfe.helper.AndroidHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.MyVideosViewModel;
import fr.vyfe.viewModel.MyVideosViewModelFactory;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Activity presents cache memory videos and device videos
 * The user can select one and view it
 */
public class MyVideosActivity extends VyfeActivity {
    MyVideosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        viewModel = ViewModelProviders.of(this, new MyVideosViewModelFactory(mAuth.getCurrentUser().getCompany(), AndroidHelper.getAndroidId(this))).get(MyVideosViewModel.class);

        final GridView gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        EditText searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.colorWhiteTwo));
        searchText.setHintTextColor(getResources().getColor(R.color.colorWhiteTwo));
        ImageView closeSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        ImageView search = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        search.setImageResource(android.R.drawable.ic_menu_search);

        viewModel.getSessions().observe(this, new Observer<List<SessionModel>>() {
            @Override
            public void onChanged(@Nullable List<SessionModel> sessions) {
                gridView.setAdapter(new VideoGridAdapter(MyVideosActivity.this, (ArrayList<SessionModel>) sessions));
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
                viewModel.setFilter(s);
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyVideosActivity.this, SelectVideoActivity.class);
                SessionModel sessionModel = (SessionModel) parent.getItemAtPosition(position);
                intent.putExtra(Constants.SESSIONMODEL_EXTRA, sessionModel);
                MyVideosActivity.this.startActivity(intent);
            }
        });
    }

}