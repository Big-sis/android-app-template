package fr.wildcodeschool.vyfe.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.PrepareSessionActivity;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.model.LicenceModel;
import fr.wildcodeschool.vyfe.viewModel.MainViewModel;
import fr.wildcodeschool.vyfe.viewModel.MainViewModelFactory;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends VyfeActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String todayDate;
    private boolean firstMessage;
    private boolean secondMessage;
    private MainViewModel viewModel;
    private FirebaseFunctions mFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.vyfe_blanc);
        setSupportActionBar(toolbar);

        mFunctions = FirebaseFunctions.getInstance();

        LinearLayout btnStartSession = findViewById(R.id.btn_start_session);
        LinearLayout btnMultiSession = findViewById(R.id.btn_multi_session);
        LinearLayout btnVideos = findViewById(R.id.btn_videos);
        LinearLayout btnCreateGrid = findViewById(R.id.btn_create_grid);

        Date date = new Date();
        Date newDate = new Date(date.getTime());
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy", Locale.FRENCH);
        todayDate = format.format(newDate);

//       TEst  String a = getComapgny(SingletonFirebase.getInstance().getUid()).getResult();


        viewModel = ViewModelProviders.of(this, new MainViewModelFactory(SingletonFirebase.getInstance().getUid())).get(MainViewModel.class);
        viewModel.getLicence(new MainViewModel.ForecastResponse() {
            @Override
            public void onSuccess(LiveData<List<LicenceModel>> license) {
                if (license.getValue().get(0).getEnd() == null) {
                    Toast.makeText(MainActivity.this, R.string.license, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);MainActivity.this.startActivity(intent);
                    mAuth.signOut();
                } else {
                    long remainingDays = 0;
                    Date dateToday = null;
                    Date dateEndLicence = null;
                    try {
                        dateToday = format.parse(todayDate);
                        dateEndLicence = format.parse(   license.getValue().get(0).getEnd());
                        long difference = dateEndLicence.getTime() - dateToday.getTime();
                        remainingDays = difference / Constants.DAY_TO_MILLISECOND_FACTOR;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (remainingDays < 30 && firstMessage) {
                        Toast.makeText(MainActivity.this, R.string.expired_month, Toast.LENGTH_LONG).show();
                        firstMessage = false;
                    }
                    if (remainingDays < 7 && secondMessage) {
                        Toast.makeText(MainActivity.this, R.string.expired_7_days, Toast.LENGTH_LONG).show();
                        secondMessage = false;
                    }
                    if (remainingDays <= 1) {
                        Toast.makeText(MainActivity.this, R.string.expired_day, Toast.LENGTH_LONG).show();
                    }
                    if (remainingDays < 0) {
                        Toast.makeText(MainActivity.this, R.string.expired_licence, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
                        MainActivity.this.startActivity(intent);
                        mAuth.signOut();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, getString(R.string.problem)+error, Toast.LENGTH_SHORT).show();
            }
        });


        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                startActivity(intent);
            }
        });

        btnMultiSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                intent.putExtra("multiSession", "multiSession");

                startActivity(intent);
            }
        });

        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyVideosActivity.class);
                startActivity(intent);
            }
        });

        btnCreateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrepareSessionActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        MenuItem itemLogout = menu.findItem(R.id.logout);
        itemLogout.setVisible(true);
        return true;
    }

    //TODO a faire fonctionner
    private Task<String> getComapgny(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("userId", text);
        data.put("get", true);

        return mFunctions
                .getHttpsCallable("https://us-central1-vyfe-project.cloudfunctions.net/getCompany")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
}