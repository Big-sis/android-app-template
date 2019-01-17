package fr.vyfe.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import fr.vyfe.Constants;
import fr.vyfe.mapper.UserMapper;
import fr.vyfe.model.UserModel;

/**
 * Handles authentication, login, logout, current user with offline mode and license check
 */
public class AuthHelper {

    private static final String SHARED_PREF_USER_ID = "userId";
    private static final String SHARED_PREF_USER_COMPANY = "userCompany";
    private static final String SHARED_PREF_USER_EMAIL = "userEmail";
    private static final String SHARED_PREF_USER_FIRSTNAME = "userFirstname";
    private static final String SHARED_PREF_USER_LASTNAME = "userLastname";
    private static final String SHARED_PREF_USER_PROMO = "userPromo";
    private static final String SHARED_PREF_USER_LICENSE_END = "userLicenseEnd";
    private static final String SHARED_PREF_USER_ROLES = "userRoles";

    private static AuthHelper instance;
    private static SharedPreferences mySharedPreferences;
    private UserModel currentUser;

    private AuthHelper(Context context) {
        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            signOut();
        if (currentUser == null && mySharedPreferences.contains(SHARED_PREF_USER_ID))
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

    public Task<Task<HashMap<String, Object>>> signInWithEmailAndPassword(String mail, String pass, final AuthListener authListener) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
                .continueWith(new Continuation<AuthResult, UserModel>() {
                    @Override
                    public UserModel then(@NonNull Task<AuthResult> task) throws Exception {
                        AuthResult result = task.getResult();
                        FirebaseUser user = result.getUser();
                        UserModel userModel = new UserModel(user.getUid());
                        return userModel;
                    }
                })
                .continueWith(new Continuation<UserModel, Task<HashMap<String, Object>>>() {
                    @Override
                    public Task<HashMap<String, Object>> then(@NonNull Task<UserModel> userTask) throws Exception {
                        return fetchCompanyAndUser(userTask.getResult().getId())
                                .addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<HashMap<String, Object>> fireTask) {
                                        if (fireTask.isSuccessful()) {
                                            HashMap<String, Object> result = fireTask.getResult();
                                            currentUser = (new UserMapper()).map(result);
                                            saveCurrentUser();
                                            authListener.onSuccessLoggedIn(currentUser);
                                        } else {
                                            Exception e = fireTask.getException();
                                            if (e instanceof FirebaseFunctionsException) {
                                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                                FirebaseFunctionsException.Code code = ffe.getCode();
                                                Object details = ffe.getDetails();
                                            }

                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authListener.onLogginFailed(e);
                    }
                });


    }

    private Task<HashMap<String, Object>> fetchCompanyAndUser(String userId) {
        return FirebaseFunctions.getInstance()
                .getHttpsCallable("getCompanyAndUser?userId=" + userId)
                .call()
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HttpsCallableResult result = task.getResult();
                        return (HashMap<String, Object>) result.getData();
                    }
                });
    }

    public int getLicenseRemainingDays() {
        if (currentUser == null) return 0;
        Date date = new Date();
        Date newDate = new Date(date.getTime());
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy", Locale.FRENCH);
        String todayDate = format.format(newDate);

        if (currentUser.getLicenseEnd() != null) {
            long remainingDays = 0;
            Date dateToday = null;
            Date dateEndLicence = null;
            try {
                dateToday = format.parse(todayDate);
                dateEndLicence = currentUser.getLicenseEnd();
                long difference = dateEndLicence.getTime() - dateToday.getTime();
                remainingDays = difference / Constants.DAY_TO_MILLISECOND_FACTOR;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return remainingDays >= 0 ? (int) remainingDays : 0;
        }

        return 0;

    }

    private void saveCurrentUser() {
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(SHARED_PREF_USER_ID, currentUser.getId());
        editor.putString(SHARED_PREF_USER_COMPANY, currentUser.getCompany());
        editor.putString(SHARED_PREF_USER_EMAIL, currentUser.getEmail());
        editor.putString(SHARED_PREF_USER_FIRSTNAME, currentUser.getFirstname());
        editor.putString(SHARED_PREF_USER_LASTNAME, currentUser.getLastName());
        editor.putString(SHARED_PREF_USER_PROMO, currentUser.getPromo());
        if (currentUser.getLicenseEnd() != null)
            editor.putString(SHARED_PREF_USER_LICENSE_END, DateFormat.format("dd-MM-yy", currentUser.getLicenseEnd()).toString());
        editor.putString(SHARED_PREF_USER_ROLES, Arrays.toString(currentUser.getRoles()));
        editor.apply();
    }

    private UserModel retrieveCurrentUser() {
        UserModel user = new UserModel();
        user.setId(mySharedPreferences.getString(SHARED_PREF_USER_ID, ""));
        user.setCompany(mySharedPreferences.getString(SHARED_PREF_USER_COMPANY, ""));
        user.setEmail(mySharedPreferences.getString(SHARED_PREF_USER_EMAIL, ""));
        user.setFirstname(mySharedPreferences.getString(SHARED_PREF_USER_FIRSTNAME, ""));
        user.setLastName(mySharedPreferences.getString(SHARED_PREF_USER_LASTNAME, ""));
        user.setPromo(mySharedPreferences.getString(SHARED_PREF_USER_PROMO, ""));
        try {
            user.setLicenceEnd((new SimpleDateFormat("dd-MM-yy")).parse(mySharedPreferences.getString(SHARED_PREF_USER_LICENSE_END, "")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setRoles(mySharedPreferences.getString(SHARED_PREF_USER_ROLES, "").split(","));
        return user;
    }

    private boolean clearSharedPrefs() {
        if (mySharedPreferences == null) return false;
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(SHARED_PREF_USER_ID);
        editor.remove(SHARED_PREF_USER_COMPANY);
        editor.remove(SHARED_PREF_USER_EMAIL);
        editor.remove(SHARED_PREF_USER_FIRSTNAME);
        editor.remove(SHARED_PREF_USER_LASTNAME);
        editor.remove(SHARED_PREF_USER_PROMO);
        editor.remove(SHARED_PREF_USER_LICENSE_END);
        editor.remove(SHARED_PREF_USER_ROLES);
        return editor.commit();
    }

    public interface AuthListener {
        void onSuccessLoggedIn(UserModel user);

        void onLogginFailed(Exception e);
    }
}
