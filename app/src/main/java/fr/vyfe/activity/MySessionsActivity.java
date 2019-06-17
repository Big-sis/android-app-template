package fr.vyfe.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.adapter.VideoGridAdapter;
import fr.vyfe.helper.OpenInfoHelper;
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

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final HashCode hashCode = Hashing.sha256().hashString(androidId, Charset.defaultCharset());
        viewModel = ViewModelProviders.of(this, new MyVideosViewModelFactory(mAuth.getCurrentUser().getCompany(), mAuth.getCurrentUser().getId(), hashCode.toString())).get(MyVideosViewModel.class);

        gridView = findViewById(R.id.grid_videos);
        SearchView searchView = findViewById(R.id.search_video);
        EditText searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.colorWhiteTwo));
        searchText.setHintTextColor(getResources().getColor(R.color.colorWhiteTwo));
        ImageView closeSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        ImageView search = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        search.setImageResource(android.R.drawable.ic_menu_search);
        final Button btnDeleteAll = findViewById(R.id.btn_delete_all);
        final Button btnCancel = findViewById(R.id.btn_cancel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_videos);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        initNavBar(navigationView, toolbar, drawerLayout);

        ConstraintLayout containerInfo = findViewById(R.id.info);
        OpenInfoHelper.setOnClick(Constants.INFO_VIDEO,containerInfo,this);

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


        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<SessionModel, Boolean> selectedSessions = viewModel.getmSelectedSessions();
                int nbrDelete = 0;
                for (Map.Entry<SessionModel, Boolean> map : selectedSessions.entrySet()) {
                    if (map.getValue()) {
                        nbrDelete++;
                    }
                }
                String message = nbrDelete > 1 ? getString(R.string.do_you_want) + " " + String.valueOf(nbrDelete) + " " + getString(R.string.videos) : getResources().getString(R.string.ask_delete_selection);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MySessionsActivity.this);
                builder.setMessage(message)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                for (Map.Entry<SessionModel, Boolean> map : selectedSessions.entrySet()) {
                                    if (map.getValue()) {
                                        viewModel.deleteSession(map.getKey().getId()).continueWith(new Continuation<Void, Void>() {
                                            @Override
                                            public Void then(@NonNull Task<Void> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(self, R.string.error, Toast.LENGTH_LONG).show();
                                                }
                                                return null;
                                            }
                                        });
                                    }
                                }
                                viewModel.cancelSelected();
                                resetGridView();
                                Toast.makeText(self, R.string.delete_session, Toast.LENGTH_LONG).show();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.cancelSelected();
                                dialog.cancel();
                                resetGridView();

                            }
                        })
                        .show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.cancelSelected();
                resetGridView();
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent selectVideoIntent = new Intent(MySessionsActivity.this, SelectVideoActivity.class);
                final SessionModel sessionModel = (SessionModel) parent.getItemAtPosition(position);
                Intent playVideoIntent = new Intent(MySessionsActivity.this, PlayVideoActivity.class);

                selectVideoIntent.putExtra(Constants.SESSIONMODELID_EXTRA, sessionModel.getId());
                playVideoIntent.putExtra(Constants.SESSIONMODELID_EXTRA, sessionModel.getId());

                switch (view.getId()) {
                    case R.id.img_button_play:
                        MySessionsActivity.this.startActivity(playVideoIntent);
                        break;
                    case R.id.img_button_edit:
                        MySessionsActivity.this.startActivity(selectVideoIntent);
                        break;
                    case R.id.img_btn_delete:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MySessionsActivity.this);
                        builder.setMessage(getResources().getString(R.string.delete_all))
                                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        viewModel.deleteSession(sessionModel.getId()).continueWith(new Continuation<Void, Void>() {
                                            @Override
                                            public Void then(@NonNull Task<Void> task) throws Exception {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(self, R.string.delete_session, Toast.LENGTH_LONG).show();
                                                } else
                                                    Toast.makeText(self, R.string.error, Toast.LENGTH_SHORT).show();
                                                return null;
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                })
                                .show();

                        break;


                    case R.id.checkbox_delete:
                        viewModel.selectedSession(sessionModel);
                }

            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                viewModel.inProgress();
                SessionModel sessionModel = (SessionModel) parent.getItemAtPosition(position);
                viewModel.longSelectedSession(sessionModel);

                return true;
            }
        });

        viewModel.isCancelSelection().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isCancel) {
                if (isCancel) {
                    btnDeleteAll.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                } else {
                    btnDeleteAll.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getPermissions().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    adapter = new VideoGridAdapter(MySessionsActivity.this, new ArrayList<SessionModel>());
                    viewModel.getSessions().observe(MySessionsActivity.this, new Observer<List<SessionModel>>() {
                        @Override
                        public void onChanged(@Nullable List<SessionModel> sessions) {
                            resetGridView();
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

    public void resetGridView() {
        viewModel.initSelectedSession();
        gridView.setAdapter(new VideoGridAdapter(MySessionsActivity.this, (ArrayList<SessionModel>) viewModel.getSessions().getValue()));
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}