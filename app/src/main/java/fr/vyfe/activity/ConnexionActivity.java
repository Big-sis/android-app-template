package fr.vyfe.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.idlingResource.SimpleIdlingResource;
import fr.vyfe.model.UserModel;


/**
 * This activity allows the user to log in
 */

public class ConnexionActivity extends AppCompatActivity {
    private static final int PASSWORD_HIDDEN = 1;
    private static final int PASSWORD_VISIBLE = 2;
    private int mPasswordVisibility = PASSWORD_HIDDEN;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        final AuthHelper auth = AuthHelper.getInstance(this);
        final EditText inputMail = findViewById(R.id.et_mail);
        final EditText inputPass = findViewById(R.id.et_password);
        Button forgotPassword = findViewById(R.id.tv_lost_password);
        final TextView btnCreateAccount = findViewById(R.id.btn_create_account);
        ImageView ivShowPassword = findViewById(R.id.iv_show_password);


        ivShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPasswordVisibility == PASSWORD_HIDDEN) {
                    inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPasswordVisibility = PASSWORD_VISIBLE;
                } else {
                    inputPass.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordVisibility = PASSWORD_HIDDEN;
                }
            }
        });


        Button connexion = findViewById(R.id.btn_connect);
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
                        public void onLogginFailed(Exception e) {
                            final Snackbar snackbar = Snackbar.make(ConnexionActivity.this.findViewById(R.id.linear_layout_add), R.string.bad_authentifiaction, Snackbar.LENGTH_INDEFINITE).setDuration(9000).setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    inputMail.setText("");
                                    inputPass.setText("");
                                    inputMail.requestFocus();
                                }
                            });
                            View snackBarView = snackbar.getView();
                            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setMaxLines(3);
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            snackbar.setDuration(7000);
                            snackbar.show();

                        }
                    }, new AuthHelper.AuthProfileListener() {
                        @Override
                        public void onSuccessProfile(UserModel user) {
                            HashMap<String, Boolean> rolesUser =  user.getRoles();
                            if(rolesUser.get(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN)){
                                Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                final Snackbar snackbar = Snackbar.make(ConnexionActivity.this.findViewById(R.id.linear_layout_add), R.string.havent_roles_teacher, Snackbar.LENGTH_INDEFINITE).setDuration(9000).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                View snackBarView = snackbar.getView();
                                TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setMaxLines(3);
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                snackbar.setDuration(8000);
                                snackbar.show();
                               }
                        }

                        @Override
                        public void onProfileFailed(Exception e) {

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

        btnCreateAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://vyfe.fr/"));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


}
