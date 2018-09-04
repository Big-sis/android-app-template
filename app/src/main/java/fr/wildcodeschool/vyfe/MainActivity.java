package fr.wildcodeschool.vyfe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    public static boolean mMulti = false;
    private static FirebaseDatabase mDatabase;
    private static String authUserId;
    private DatabaseReference licence;
    private Date date;
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private boolean mPermission;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String stringDate;

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

        mDatabase = SingletonFirebase.getInstance().getDatabase();
        authUserId = SingletonFirebase.getInstance().getUid();

        date = new Date();
        Date newDate = new Date(date.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy");
        stringDate = dt.format(newDate);


        licence = mDatabase.getReference(authUserId).child("licence");
        licence.keepSynced(true);
        licence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    licence.child("earlyLicence").setValue(stringDate);
                    licence.child("durationLicence").setValue("365");

                } else {

                    String valueEarlyLicence = dataSnapshot.child("earlyLicence").getValue().toString();
                    String valueDurationLicence = dataSnapshot.child("durationLicence").getValue().toString();

                    int restDays = countEarlyDuration(valueEarlyLicence) - Integer.parseInt(valueDurationLicence);

                    switch (restDays) {
                        case 30:
                            Toast.makeText(MainActivity.this, R.string.expired_month, Toast.LENGTH_SHORT).show();
                            break;
                        case 7:
                            Toast.makeText(MainActivity.this, R.string.expired_7_days, Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(MainActivity.this, R.string.expired_day, Toast.LENGTH_SHORT).show();
                            break;
                        default:


                    }

                    if (restDays < 0) {
                        Toast.makeText(MainActivity.this, R.string.expired_licence, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
                        MainActivity.this.startActivity(intent);
                        mAuth.signOut();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                mMulti = true;
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyVideoActivity.class);
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


    public int countEarlyDuration(String dateEarly) {
        String[] parts = dateEarly.split("-");
        int dayEarly = Integer.parseInt(parts[0]);
        int monthEarly = Integer.parseInt(parts[1]);
        int yearsEarly = Integer.parseInt(parts[2]);


        String[] parts2 = stringDate.split("-");
        int dayToday = Integer.parseInt(parts2[0]);
        int monthToday = Integer.parseInt(parts2[1]);
        int yearsToday = Integer.parseInt(parts2[2]);


        int years = yearsToday - yearsEarly;
        int month = monthToday - monthEarly;
        int day = dayToday - dayEarly;
        if (monthToday < monthEarly) {
            month = 12 - monthEarly + monthToday;
        }
        if (dayToday < dayEarly) {
            day = 31 - dayEarly + dayToday;
        }

        return (years * 365) + (month * 31) + day;


    }
}