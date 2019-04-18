package fr.vyfe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.model.UserModel;

public abstract class VyfeActivity extends AppCompatActivity {

    protected static AuthHelper mAuth;
    protected AppCompatActivity self;

    public void confirmDisconnection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setMessage(R.string.deconnected)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        self.finish();
                        finishAffinity();
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
        String firstName, lastName, displayName;
        if (currentUser.getFirstname()!=null) firstName = currentUser.getFirstname();
        else firstName = "";
        if(currentUser.getLastName()!=null) lastName = currentUser.getLastName();
        else lastName ="";
       return firstName +" " +lastName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                confirmDisconnection();
                return true;
            case R.id.home:
                Intent intentHome = new Intent(this, MainActivity.class);
                startActivity(intentHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        FirebaseApp.initializeApp(self);
        mAuth = AuthHelper.getInstance(this);

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
            if (null != mAuth.getCurrentUser().getRoles()&&!mAuth.getCurrentUser().getRoles().get(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER) && !mAuth.getCurrentUser().getRoles().get(Constants.BDDV2_CUSTOM_USERS_ROLE_STUDENT)) {
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

}
