package fr.wildcodeschool.vyfe;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import android.os.Build;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.ListView;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    public static boolean mMulti = false;
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    // Operation = 24(day) * 60(hour) * 60(minute) * 1000 (millis)
    private static final int TIME_IN_DAYS = 86400000;

    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean mPermission;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String todayDate;
    private boolean firstMessage;
    private boolean secondMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.vyfe_blanc);
        setSupportActionBar(toolbar);

        LinearLayout btnStartSession = findViewById(R.id.btn_start_session);
        LinearLayout btnMultiSession = findViewById(R.id.btn_multi_session);
        LinearLayout btnVideos = findViewById(R.id.btn_videos);
        LinearLayout btnCreateGrid = findViewById(R.id.btn_create_grid);

        FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
        String authUserId = SingletonFirebase.getInstance().getUid();

        Date date = new Date();
        Date newDate = new Date(date.getTime());
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy", Locale.FRENCH);
        todayDate = format.format(newDate);

        DatabaseReference referenceLicence = mDatabase.getReference(authUserId).child("licence");
        referenceLicence.keepSynced(true);
        referenceLicence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long remainingDays = 0;
                String valueEndLicence = dataSnapshot.child("endLicence").getValue().toString();
                Date dateToday = null;
                Date dateEndLicence = null;
                try {
                    dateToday = format.parse(todayDate);
                    dateEndLicence = format.parse(valueEndLicence);
                    long difference = dateEndLicence.getTime() - dateToday.getTime();
                    remainingDays = difference / TIME_IN_DAYS;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (remainingDays < 30 && firstMessage) {
                    Toast.makeText(MainActivity.this, R.string.expired_month, Toast.LENGTH_SHORT).show();
                    firstMessage = false;
                }
                if (remainingDays < 7 && secondMessage) {
                    Toast.makeText(MainActivity.this, R.string.expired_7_days, Toast.LENGTH_SHORT).show();
                    secondMessage = false;
                }
                if (remainingDays <= 1) {
                    Toast.makeText(MainActivity.this, R.string.expired_day, Toast.LENGTH_SHORT).show();
                }
                if (remainingDays < 0) {
                    Toast.makeText(MainActivity.this, R.string.expired_licence, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
                    MainActivity.this.startActivity(intent);
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

        btnMultiSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String networkSSID = MainActivity.this.getString(R.string.networkSSID);
                String networkPass = MainActivity.this.getString(R.string.networkPass);

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + networkSSID + "\"";

                conf.wepKeys[0] = "\"" + networkPass + "\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

                final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);

                String connexion = wifiManager.getConnectionInfo().getSupplicantState().toString();


                if (connexion.equals("COMPLETED")) {
                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration i : list) {
                        if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();

                            break;
                        }
                        if (i.SSID == null) {
                            Toast.makeText(MainActivity.this, "Veuillez brancher le boitier de connexion", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.wifi_active)
                            .setPositiveButton(R.string.start_wifi, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wifiManager.setWifiEnabled(true);
                                }
                            })
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }


                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.putExtra("multiSession", "multiSession");

               // startActivity(intent);

            }
        });


        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyVideoActivity.class);
                startActivity(intent);
            }
        });

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreparedSessionActivity.class);
                startActivity(intent);
            }
        });

        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    permissions,
                    PERMISSIONS_REQUEST);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        MenuItem itemLogout = menu.findItem(R.id.logout);
        itemLogout.setVisible(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                DisconnectionAlert.confirmedDisconnection(MainActivity.this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quit_app)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        MainActivity.this.startActivity(intent);

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