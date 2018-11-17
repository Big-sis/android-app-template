package fr.vyfe.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.model.UserModel;


/**
 * This activity allows the user to log in
 */
public class ConnexionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        final AuthHelper auth = AuthHelper.getInstance(this);
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

                    auth.signInWithEmailAndPassword(mail, pass, new AuthHelper.AuthListener() {
                        @Override
                        public void onSuccessLoggedIn(UserModel user) {
                            Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onLogginFailed(Exception e) {
                            Toast.makeText(ConnexionActivity.this, R.string.bad_authentifiaction, Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnexionActivity.this, ForgotPasswordActivity.class);
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
