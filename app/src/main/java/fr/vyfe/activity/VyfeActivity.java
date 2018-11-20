package fr.vyfe.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;

public abstract class VyfeActivity extends AppCompatActivity {

    protected static AuthHelper mAuth;

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

    public static void confirmedDisconnection(final Context context){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = AuthHelper.getInstance(this);
        if(mAuth.getLicenseRemainingDays()==0) {
            if (mAuth.getCurrentUser() != null)
                Toast.makeText(this, R.string.license, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ConnexionActivity.class);
            this.startActivity(intent);
            mAuth.signOut();
            finish();
        }
    }
}
