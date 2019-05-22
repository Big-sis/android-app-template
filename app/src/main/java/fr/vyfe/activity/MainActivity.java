package fr.vyfe.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;

public class MainActivity extends VyfeActivity {


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.vyfe_blanc);
        setSupportActionBar(toolbar);


        TextView tvHelloUser = findViewById(R.id.hello_user);
        LinearLayout btnStartSession = findViewById(R.id.btn_start_session);
        LinearLayout btnVideos = findViewById(R.id.btn_videos);
        LinearLayout btnCreateGrid = findViewById(R.id.btn_create_grid);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initNavBar(navigationView, toolbar, drawerLayout);

        tvHelloUser.setText(getResources().getString(R.string.hello_user) +" "+ getDisplayName()+",");

        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                startActivity(intent);
            }
        });


        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MySessionsActivity.class);
                startActivity(intent);
            }
        });

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TagSetsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toastLicenseExpireWarning(mAuth);
    }

    private void toastLicenseExpireWarning(AuthHelper mAuth) {
        long remainingDays = mAuth.getLicenseRemainingDays();
        if (remainingDays <= 1) {
            Toast.makeText(this, R.string.expired_day, Toast.LENGTH_LONG).show();
        } else if (remainingDays < 7) {
            Toast.makeText(this, R.string.expired_7_days, Toast.LENGTH_LONG).show();
        } else if (remainingDays < 30) {
            Toast.makeText(this, R.string.expired_month, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quit_app)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}