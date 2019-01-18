package fr.vyfe.activity;

import android.Manifest;
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

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.VideoGridAdapter;
import fr.vyfe.helper.AndroidHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.viewModel.MyVideosViewModel;
import fr.vyfe.viewModel.MyVideosViewModelFactory;

/**
 * Activity presents cache memory videos and device videos
 * The user can select one and view it
 */
public class MySessionsActivity extends VyfeActivity {
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private MyVideosViewModel viewModel;
    private GridView gridView;
    private VideoGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        viewModel = ViewModelProviders.of(this, new MyVideosViewModelFactory(mAuth.getCurrentUser().getCompany(), AndroidHelper.getAndroidId(this))).get(MyVideosViewModel.class);

        gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        EditText searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.colorWhiteTwo));
        searchText.setHintTextColor(getResources().getColor(R.color.colorWhiteTwo));
        ImageView closeSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        ImageView search = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        search.setImageResource(android.R.drawable.ic_menu_search);

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
                if (checkPersmissions(MySessionsActivity.PERMISSIONS)) viewModel.setFilter(s);
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MySessionsActivity.this, SelectVideoActivity.class);
                SessionModel sessionModel = (SessionModel) parent.getItemAtPosition(position);
                intent.putExtra(Constants.SESSIONMODELID_EXTRA, sessionModel.getId());
                MySessionsActivity.this.startActivity(intent);
            }
        });


        viewModel.getPermissions().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean!=null && aBoolean) {
                    adapter = new VideoGridAdapter(MySessionsActivity.this, new ArrayList<SessionModel>());
                    viewModel.getSessions().observe(MySessionsActivity.this, new Observer<List<SessionModel>>() {
                        @Override
                        public void onChanged(@Nullable List<SessionModel> sessions) {
                            gridView.setAdapter(new VideoGridAdapter(MySessionsActivity.this, (ArrayList<SessionModel>) sessions));
                            if (adapter != null) adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPersmissions(MySessionsActivity.PERMISSIONS)) {
            viewModel.permissionsAccepted();
        }
    }
}