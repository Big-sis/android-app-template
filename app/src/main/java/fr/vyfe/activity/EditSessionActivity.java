package fr.vyfe.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.fragment.EditSessionFragment;
import fr.vyfe.viewModel.EditSessionViewModel;
import fr.vyfe.viewModel.EditSessionViewModelFactory;

public class EditSessionActivity extends VyfeActivity {
    private EditSessionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new EditSessionViewModelFactory(mAuth.getCurrentUser().getCompany())).get(EditSessionViewModel.class);
        viewModel.init(getIntent().getStringExtra(Constants.SESSIONMODELID_EXTRA));
        setContentView(R.layout.activity_edit_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.infos_video);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initNavBar(navigationView, toolbar, drawerLayout);
        replaceFragment(R.id.edit_session_fragment_container, EditSessionFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        if(viewModel.watchDataChange().getValue()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(EditSessionActivity.this);
            builder.setMessage(R.string.dont_save)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.quiet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            EditSessionActivity.super.onBackPressed();
                        }
                    })
                    .show();

        }else{
            super.onBackPressed();
        }
    }
}
