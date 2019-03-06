package fr.vyfe.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;

public class MainActivity extends VyfeActivity {

    private WifiManager wifiManager;
    private String networkSSID;
    private String networkPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.vyfe_blanc);
        setSupportActionBar(toolbar);

        LinearLayout btnStartSession = findViewById(R.id.btn_start_session);
        LinearLayout btnVideos = findViewById(R.id.btn_videos);
        LinearLayout btnCreateGrid = findViewById(R.id.btn_create_grid);


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

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        networkSSID = MainActivity.this.getString(R.string.networkSSID);
        networkPass = MainActivity.this.getString(R.string.networkPass);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        MenuItem itemLogout = menu.findItem(R.id.logout);
        itemLogout.setVisible(true);
        MenuItem itemHome = menu.findItem(R.id.home);
        itemHome.setVisible(false);
        return true;
    }
}