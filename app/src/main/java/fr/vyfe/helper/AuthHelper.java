package fr.vyfe.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import fr.vyfe.Constants;
import fr.vyfe.mapper.UserMapper;
import fr.vyfe.model.UserModel;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.UserRepository;

/**
 * Handles authentication, login, logout, current user with offline mode and license check
 */
public class AuthHelper {

    private static AuthHelper instance;
    private static SharedPreferences mySharedPreferences;
    private UserModel currentUser;
    private UserRepository userRepository;
    private long remainingDays = 0;

    private AuthHelper(Context context) {
        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            signOut();

        if (currentUser == null && mySharedPreferences.contains(Constants.BDDV2_CUSTOM_USERS_ID))
            this.currentUser = retrieveCurrentUser();
    }

    public static AuthHelper getInstance(Context context) {
        if (instance == null) instance = new AuthHelper(context);
        return instance;
    }

    public UserModel getCurrentUser() {
        return currentUser;
    }

    public void signOut() {
        currentUser = null;
        if (clearSharedPrefs())
            FirebaseAuth.getInstance().signOut();
    }

    public Task<UserModel> signInWithEmailAndPassword(String mail, String pass, final AuthProfileListener authProfileListener) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
                .continueWith(new Continuation<AuthResult, UserModel>() {
                    @Override
                    public UserModel then(@NonNull Task<AuthResult> task) throws Exception {
                        AuthResult result = task.getResult();
                        FirebaseUser user = result.getUser();

                        Task<GetTokenResult> resultCustom = user.getIdToken(false);
                        GetTokenResult tokenResultCustom = resultCustom.getResult();
                        HashMap<String, Object> customs = new HashMap<>(tokenResultCustom.getClaims());

                        currentUser = (new UserMapper()).map(customs);
                        userRepository = new UserRepository(currentUser.getCompany());
                        userRepository.addChildListener(currentUser.getId(), new BaseSingleValueEventListener.CallbackInterface<UserModel>() {
                            @Override
                            public void onSuccess(UserModel result) {

                                if (result.getLastName() != null) currentUser.setLastName(result.getLastName());
                                if (result.getFirstname() != null) currentUser.setFirstname(result.getFirstname());
                                saveCurrentUser();
                                authProfileListener.onSuccessProfile(currentUser);
                            }

                            @Override
                            public void onError(Exception e) {
                                authProfileListener.onProfileFailed(e);
                            }
                        });

                        return currentUser;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authProfileListener.onLogginFailed(e);
                        Log.d("err", e.getMessage());
                    }
                });
    }

    public int getLicenseRemainingDays() {
        if (currentUser == null) return 0;
        Date date = new Date();
        Date newDate = new Date(date.getTime());
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy", Locale.FRENCH);
        String todayDate = format.format(newDate);
        java.sql.Timestamp timeStampDate = new
                Timestamp(date.getTime());

        if (currentUser.getLicenseEnd() != null) {

            try {
                java.sql.Timestamp dateEndLicence = currentUser.getLicenseEnd();
                remainingDays = dateEndLicence.getTime() - timeStampDate.getTime();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return remainingDays >= 0 ? (int) remainingDays : 0;
        }

        return 0;
    }
    private void saveCurrentUser() {
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(Constants.BDDV2_CUSTOM_USERS_ID, currentUser.getId());
        editor.putString(Constants.BDDV2_CUSTOM_USERS_COMPANY, currentUser.getCompany());
        editor.putString(Constants.SHARED_PREF_USER_FIRSTNAME, currentUser.getFirstname());
        editor.putString(Constants.SHARED_PREF_USER_LASTNAME, currentUser.getLastName());
        editor.putString(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN, currentUser.getVimeoAccessToken());
        if (currentUser.getLicenseEnd() != null)
            editor.putString(Constants.BDDV2_CUSTOM_USERS_LICENSE_END, String.valueOf(currentUser.getLicenseEnd()));

        for (String role : currentUser.getRoles().keySet()) {
            editor.putString(role, String.valueOf(currentUser.getRoles().get(role)));
        }
        editor.apply();
    }
    private UserModel retrieveCurrentUser() {
        UserModel user = new UserModel();
        user.setId(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_ID, ""));
        user.setCompany(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_COMPANY, ""));
        user.setFirstname(mySharedPreferences.getString(Constants.SHARED_PREF_USER_FIRSTNAME, ""));
        user.setLastName(mySharedPreferences.getString(Constants.SHARED_PREF_USER_LASTNAME, ""));
        user.setVimeoAccessToken(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN, ""));
        try {
            user.setLicenceEnd(Timestamp.valueOf(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_LICENSE_END, "")));
        } catch (Exception e) {
            user.setLicenceEnd(null);
        }

        HashMap<String, Boolean> roles = new HashMap<>();
        roles.put(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN, Boolean.valueOf(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN, "")));
        roles.put(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER, Boolean.valueOf(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER, "")));
        roles.put(Constants.BDDV2_CUSTOM_USERS_ROLE_STUDENT, Boolean.valueOf(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_ROLE_STUDENT, "")));
        roles.put(Constants.BDDV2_CUSTOM_USERS_ROLE_OBSERVER, Boolean.valueOf(mySharedPreferences.getString(Constants.BDDV2_CUSTOM_USERS_ROLE_OBSERVER, "")));
        user.setRoles(roles);
        return user;
    }

    private boolean clearSharedPrefs() {
        if (mySharedPreferences == null) return false;
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(Constants.BDDV2_CUSTOM_USERS_ID);
        editor.remove(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN);
        editor.remove(Constants.SHARED_PREF_USER_FIRSTNAME);
        editor.remove(Constants.SHARED_PREF_USER_LASTNAME);
        editor.remove(Constants.BDDV2_CUSTOM_USERS_LICENSE_END);
        editor.remove(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN);
        editor.remove(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER);
        editor.remove(Constants.BDDV2_CUSTOM_USERS_ROLE_STUDENT);
        editor.remove(Constants.BDDV2_CUSTOM_USERS_ROLE_OBSERVER);
        return editor.commit();
    }
    public interface AuthProfileListener {
        void onSuccessProfile(UserModel user);

        void onProfileFailed(Exception e);

        void onLogginFailed(Exception e);
    }
}
