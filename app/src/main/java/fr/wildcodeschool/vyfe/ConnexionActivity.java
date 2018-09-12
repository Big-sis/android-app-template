package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
        final ImageView ivFlag = findViewById(R.id.iv_flag);
        final boolean[] frenchFlag = {true};
        final String[] language = {""};


        ivFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frenchFlag[0]) {
                    language[0] = "en";
                    ivFlag.setBackgroundResource(R.drawable.icons8_grande_bretagne_96);
                    frenchFlag[0] = false;

                } else {
                    language[0] = "fr";
                    ivFlag.setBackgroundResource(R.drawable.icons8_france_96);
                    frenchFlag[0] = true;
                }
                //TODO: implementer le string-en (en attente traduction Alex)
                ChangeLanguage(language[0]);
            }
        });

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

    private void ChangeLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }
}
