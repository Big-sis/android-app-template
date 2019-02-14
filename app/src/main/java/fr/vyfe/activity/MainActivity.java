package fr.vyfe.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import fr.vyfe.R;
import fr.vyfe.RaspberryConnexion;
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
        LinearLayout btnMultiSession = findViewById(R.id.btn_multi_session);
        LinearLayout btnVideos = findViewById(R.id.btn_videos);
        LinearLayout btnCreateGrid = findViewById(R.id.btn_create_grid);

        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", true);
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);

        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                startActivity(intent);
            }
        });

        btnMultiSession.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //ajout rasberry à la liste wifi
                confWifi();

                //verififcation wifi actif
                // String connexionWifi = wifiManager.getConnectionInfo().getSupplicantState().toString();

                boolean wifiEnabled = wifiManager.isWifiEnabled();
                if (!wifiEnabled) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.wifi_active)
                            .setPositiveButton(R.string.start_wifi, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wifiManager.setWifiEnabled(true);
                                    Toast.makeText(MainActivity.this, "Vous pouvez à present lancer une session", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                } else {

                    String wifiManagerConnectionInfo = wifiManager.getConnectionInfo().getSSID();
                    if (!wifiManagerConnectionInfo.equals('"' + networkSSID + '"'))
                        connexionRasberry();
                    else {
                        Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                        intent.putExtra("multiSession", "multiSession");
                        startActivity(intent);
                    }
                }
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
                Intent intent = new Intent(MainActivity.this, CreateGridActivity.class);
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
        int remainingDays = mAuth.getLicenseRemainingDays();
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
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void connexionRasberry() {

        networkSSID = MainActivity.this.getString(R.string.networkSSID);

        RaspberryConnexion.setConnexionRaspberry(this, new RaspberryConnexion.wifiResponse() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                intent.putExtra("multiSession", "multiSession");
                startActivity(intent);
            }

            @Override
            public void onError() {
                final ImageView progressRoue = findViewById(R.id.logo_multi_session);


                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Pour lancer une session multiple \nVous devez brancher le boitier de connexion Vyfe\n\n Brancher le boitier?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "La connexion prend environ 1 min ...", Toast.LENGTH_LONG).show();
                                progressRoue.setMaxWidth(convertToDp(100));
                                progressRoue.setMaxHeight(convertToDp(100));
                                //TODO : convert to smaller file
                                //Glide.with(MainActivity.this).load(R.drawable.roue2).into(progressRoue);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                }, 60000);
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
        });
    }


    public void confWifi() {
        networkSSID = MainActivity.this.getString(R.string.networkSSID);
        networkPass = MainActivity.this.getString(R.string.networkPass);

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.wepKeys[0] = "\"" + networkPass + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        final WifiManager wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }
}