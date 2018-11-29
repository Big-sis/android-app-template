package fr.vyfe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;

public abstract class VyfeActivity extends AppCompatActivity {

    protected static AuthHelper mAuth;
    protected Context context;

    public static void confirmedDisconnection(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.deconnected)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, ConnexionActivity.class);
                        context.startActivity(intent);
                        mAuth.signOut();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                confirmedDisconnection(this);
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
        context = this;
        mAuth = AuthHelper.getInstance(this);
        if (mAuth.getLicenseRemainingDays() == 0) {
            if (mAuth.getCurrentUser() != null)
                Toast.makeText(this, R.string.license, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ConnexionActivity.class);
            this.startActivity(intent);
            mAuth.signOut();
            finish();
        }

        //TODO: revoir l'ordre d'execution
        if(null==mAuth.getCurrentUser()){
            Toast.makeText(this, "Vous devez vous co", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ConnexionActivity.class);
            this.startActivity(intent);
            finish();}

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
                    ActivityCompat.requestPermissions((Activity) context, permissions, 1);
                }
            });
            popup.show();
            return false;
        }
        else
            return true;
    }


}
