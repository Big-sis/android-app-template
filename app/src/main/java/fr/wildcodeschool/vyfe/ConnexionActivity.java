package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class ConnexionActivity extends AppCompatActivity {
    /**
     * This activity allows the user to log in
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final EditText inputMail = findViewById(R.id.et_mail);
        final EditText inputPass = findViewById(R.id.et_password);
        Button forgotPassword = findViewById(R.id.tv_lost_password);
        final TextView btnCreateAccount = findViewById(R.id.btn_create_account);


        Button connexion = findViewById(R.id.btn_connected);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = inputMail.getText().toString();
                String pass = inputPass.getText().toString();

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(ConnexionActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(ConnexionActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null) {
                    Toast.makeText(ConnexionActivity.this, R.string.active_wifi, Toast.LENGTH_SHORT).show();

                } else {

                    auth.signInWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(ConnexionActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ConnexionActivity.this, R.string.bad_authentifiaction, Toast.LENGTH_SHORT).show();
                                    } else {
                                        SingletonFirebase.getInstance().logUser(auth.getCurrentUser().getUid());
                                        Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }

            }
        });

        if (auth.getCurrentUser() != null) {
            SingletonFirebase.getInstance().logUser(auth.getCurrentUser().getUid());
            Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnexionActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCreateAccount.setText(R.string.contact);
            }
        });

    }

}
