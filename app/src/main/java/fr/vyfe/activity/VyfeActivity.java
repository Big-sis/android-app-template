package fr.vyfe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;


import fr.vyfe.BuildConfig;
import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.helper.NetworkChangeReceiver;
import fr.vyfe.model.UserModel;
import fr.vyfe.helper.FirebaseRemoteHelper;


public abstract class VyfeActivity extends AppCompatActivity {

    protected static AuthHelper mAuth;
    static MenuItem menuInternet;
    protected AppCompatActivity self;
    private BroadcastReceiver mNetworkReceiver;
    private boolean isUpload;
    private String buildTypes;


    public static void dialog(boolean value, Context context) {
        if (value) {
            menuInternet.setIcon(context.getResources().getDrawable(R.drawable.wifi));

        } else {
            menuInternet.setIcon(context.getResources().getDrawable(R.drawable.nowifi));
        }
    }

    public void confirmDisconnection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setMessage(R.string.deconnected)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), ConnexionActivity.class);
                        getApplicationContext().startActivity(intent);
                        finish();
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

    protected void replaceFragment(int view, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(view, fragment);
        transaction.commit();
    }

    public String getDisplayName() {
        UserModel currentUser = mAuth.getCurrentUser();
        String firstName, lastName;
        if (currentUser != null && currentUser.getFirstname() != null)
            firstName = currentUser.getFirstname();
        else firstName = "";
        if (currentUser != null && currentUser.getLastName() != null)
            lastName = currentUser.getLastName();
        else lastName = "";
        return firstName + " " + lastName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        menuInternet = menu.findItem(R.id.internet);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                confirmDisconnection();
                return true;
            case R.id.home:
                Intent intentHome = new Intent(this, MainActivity.class);
                if (!isCurrentActivity(self.toString(), "MainActivity")) startActivity(intentHome);
                return true;
            case R.id.internet:
                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        FirebaseApp.initializeApp(self);
        mAuth = AuthHelper.getInstance(this);

     // if(BuildConfig.BUILD_TYPE !=null)  buildTypes = BuildConfig.BUILD_TYPE;
        isUpload = new FirebaseRemoteHelper().initRemote(self);

        if (isUpload) {
            final AlertDialog.Builder popup = new AlertDialog.Builder(this);
            popup.setTitle(R.string.upload_app);
            popup.setMessage(R.string.info_upload);
            popup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=fr.vyfe"));
                    startActivity(i);
                }
            });
            popup.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAuth.signOut();
                }
            });
            popup.show();
        }


        if (null == mAuth.getCurrentUser()) {
            Toast.makeText(this, R.string.ask_connection, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ConnexionActivity.class);
            this.startActivity(intent);
            finish();
        }

        if (mAuth.getLicenseRemainingDays() == 0) {
            if (mAuth.getCurrentUser() != null)
                Toast.makeText(this, R.string.no_license_available, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ConnexionActivity.class);
            this.startActivity(intent);
            mAuth.signOut();
            finish();
        }

        if (mAuth.getCurrentUser() != null) {
            if (null != mAuth.getCurrentUser().getRoles() && !mAuth.getCurrentUser().getRoles().get(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER)) {
                if (mAuth.getCurrentUser().getRoles().get(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN)) {
                    Toast.makeText(this, R.string.no_license_available, Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, R.string.havent_roles_teacher, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ConnexionActivity.class);
                this.startActivity(intent);
                mAuth.signOut();
                finish();
            }
        }
    }

    public boolean checkPersmissions(final String[] permissions) {
        int loop = permissions.length;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
                loop--;
        }
        if (loop != 0) {
            final AlertDialog.Builder popup = new AlertDialog.Builder(this);
            popup.setTitle(R.string.ask_permissions);
            popup.setMessage(R.string.ask_permissions_message);
            popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) self, permissions, 1);
                }
            });
            popup.show();
            return false;
        } else
            return true;
    }

    public void initNavBar(final NavigationView mNavigationView, Toolbar toolbar, final DrawerLayout drawerLayout) {

        MenuItem versionItem = mNavigationView.getMenu().findItem(R.id.version);
        if(buildTypes!=null && (buildTypes.equals("dev")||buildTypes.equals("staging")))
            versionItem.setTitle(getResources().getString(R.string.version) + new FirebaseRemoteHelper().getVersionInfo(self)+" , Env : "+ buildTypes);
        else versionItem.setTitle(getResources().getString(R.string.version) + new FirebaseRemoteHelper().getVersionInfo(self));


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        Intent intentMain = new Intent(self, MainActivity.class);
                        if (isCurrentActivity(self.toString(), "MainActivity")) {
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        } else self.startActivity(intentMain);

                        return true;
                    case R.id.tag_set:
                        Intent intentCreateGrid = new Intent(self, TagSetsActivity.class);
                        if (isCurrentActivity(self.toString(), "TagSetsActivity")) {
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        } else self.startActivity(intentCreateGrid);

                        return true;
                    case R.id.session:
                        Intent intentCreateSession = new Intent(self, CreateSessionActivity.class);
                        if (isCurrentActivity(self.toString(), "CreateSessionActivity")) {
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        } else self.startActivity(intentCreateSession);
                        return true;

                    case R.id.movie:
                        Intent intentMovie = new Intent(self, MySessionsActivity.class);
                        if (isCurrentActivity(self.toString(), "MySessionsActivity")) {
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        } else self.startActivity(intentMovie);
                        return true;
                }
                return true;
            }
        });

        toolbar.setNavigationIcon(R.drawable.menu_burger);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public Boolean isCurrentActivity(String activity, String intentActivity) {
        String[] separatedActivity = activity.split(".activity.");
        String[] nameActivity = separatedActivity[1].split("@");
        return nameActivity[0].equals(intentActivity);
    }
}
