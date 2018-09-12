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
    // Operation = 24(day) * 60(hour) * 60(minute) * 1000 (millis)
    private static final int TIME_IN_DAYS = 86400000;
    public static boolean mMulti = false;
    private static FirebaseDatabase mDatabase;
    private static String authUserId;
    private DatabaseReference referenceLicence;
    private Date date;
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
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        todayDate = format.format(newDate);
        final long[] remainingDays = {0};

        referenceLicence = mDatabase.getReference(authUserId).child("licence");
        referenceLicence.keepSynced(true);
        referenceLicence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    referenceLicence.child("startLicence").setValue(todayDate);
                    String endLicence = endLicence(todayDate);
                    referenceLicence.child("endLicence").setValue(endLicence);

                } else {
                    String valueEndLicence = dataSnapshot.child("endLicence").getValue().toString();
                    Date dateToday = null;
                    Date dateEndLicence = null;
                    try {
                        dateToday = format.parse(todayDate);
                        dateEndLicence = format.parse(valueEndLicence);
                        long difference = dateEndLicence.getTime() - dateToday.getTime();
                        remainingDays[0] = difference / TIME_IN_DAYS;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (remainingDays[0] < 30 && firstMessage) {
                        Toast.makeText(MainActivity.this, R.string.expired_month, Toast.LENGTH_SHORT).show();
                        firstMessage = false;
                    }
                    if (remainingDays[0] < 7 && secondMessage) {
                        Toast.makeText(MainActivity.this, R.string.expired_7_days, Toast.LENGTH_SHORT).show();
                        secondMessage = false;
                    }
                    if (remainingDays[0] <= 1) {
                        Toast.makeText(MainActivity.this, R.string.expired_day, Toast.LENGTH_SHORT).show();
                    }
                    if (remainingDays[0] < 0) {
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

    public String endLicence(String date) {
        String[] parts = date.split("-");
        String day = parts[0];
        String month = parts[1];
        String years = parts[2];
        int yearsNew = Integer.parseInt(years) + 1;
        return day + "-" + month + "-" + String.valueOf(yearsNew);

    }

}