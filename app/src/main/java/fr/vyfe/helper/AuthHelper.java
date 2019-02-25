package fr.vyfe.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import fr.vyfe.mapper.UserMapper;
import fr.vyfe.model.UserModel;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.UserRepository;

/**
 * Handles authentication, login, logout, current user with offline mode and license check
 */
public class AuthHelper {

    private static final String SHARED_PREF_USER_ID = "userId";
    private static final String SHARED_PREF_USER_COMPANY = "userCompany";
    private static final String SHARED_PREF_USER_FIRSTNAME = "userFirstname";
    private static final String SHARED_PREF_USER_LASTNAME = "userLastname";
    private static final String SHARED_PREF_USER_PROMO = "userPromo";
    private static final String SHARED_PREF_USER_LICENSE_END = "userLicenseEnd";
    private static final String SHARED_PREF_USER_ROLES_ADMIN = "admin";
    private static final String SHARED_PREF_USER_ROLES_TEACHER = "teacher";
    private static final String SHARED_PREF_USER_ROLES_STUDENT = "student";
    private static final String SHARED_PREF_USER_ROLES_OBSERVER = "observer";

    private static AuthHelper instance;
    private static SharedPreferences mySharedPreferences;
    private UserModel currentUser;
    private UserRepository userRepository;

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


    public Task<UserModel> signInWithEmailAndPassword(String mail, String pass, final AuthListener authListener, final AuthProfileListener authProfileListener) {
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
                        loadUser(currentUser.getCompany(),currentUser.getId(), authProfileListener );


                        return currentUser;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authListener.onLogginFailed(e);
                    }
                });

    }


    //TODO GERER ENVIRONNEMENT ex: ".getHttpsCallable("getCompanyAndUser_"+Constants.FIREBASE_DB_FUNCTIONSPRODUCTION+"?userId=" + userId)"
    //sinon "getCompanyAndUser?userId=" + userId
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
        java.sql.Timestamp timeStampDate = new
                Timestamp(date.getTime());

        if (currentUser.getLicenseEnd() != null) {
            long remainingDays = 0;
            try {
                java.sql.Timestamp  dateEndLicence = currentUser.getLicenseEnd();
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
        editor.putString(SHARED_PREF_USER_ID, currentUser.getId());
        editor.putString(SHARED_PREF_USER_COMPANY, currentUser.getCompany());
        editor.putString(SHARED_PREF_USER_FIRSTNAME, currentUser.getFirstname());
        editor.putString(SHARED_PREF_USER_LASTNAME, currentUser.getLastName());
        editor.putString(SHARED_PREF_USER_PROMO, currentUser.getPromo());
        if (currentUser.getLicenseEnd() != null)
            editor.putString(SHARED_PREF_USER_LICENSE_END, String.valueOf(currentUser.getLicenseEnd()));

        for (String role : currentUser.getRoles().keySet()) {
            editor.putString(role, String.valueOf(currentUser.getRoles().get(role)));
        }
        editor.apply();
    }

    private UserModel retrieveCurrentUser() {
        UserModel user = new UserModel();
        user.setId(mySharedPreferences.getString(SHARED_PREF_USER_ID, ""));
        user.setCompany(mySharedPreferences.getString(SHARED_PREF_USER_COMPANY, ""));
        user.setFirstname(mySharedPreferences.getString(SHARED_PREF_USER_FIRSTNAME, ""));
        user.setLastName(mySharedPreferences.getString(SHARED_PREF_USER_LASTNAME, ""));
        user.setPromo(mySharedPreferences.getString(SHARED_PREF_USER_PROMO, ""));
        try{
            user.setLicenceEnd(Timestamp.valueOf(mySharedPreferences.getString(SHARED_PREF_USER_LICENSE_END, "")));
        }
            catch (Exception e){}

        HashMap<String, Boolean> roles = new HashMap<>();
        roles.put(SHARED_PREF_USER_ROLES_ADMIN, Boolean.valueOf(mySharedPreferences.getString(SHARED_PREF_USER_ROLES_ADMIN, "")));
        roles.put(SHARED_PREF_USER_ROLES_TEACHER, Boolean.valueOf(mySharedPreferences.getString(SHARED_PREF_USER_ROLES_TEACHER, "")));
        roles.put(SHARED_PREF_USER_ROLES_STUDENT, Boolean.valueOf(mySharedPreferences.getString(SHARED_PREF_USER_ROLES_STUDENT, "")));
        roles.put(SHARED_PREF_USER_ROLES_OBSERVER, Boolean.valueOf(mySharedPreferences.getString(SHARED_PREF_USER_ROLES_OBSERVER, "")));
        user.setRoles(roles);
        return user;
    }

    private boolean clearSharedPrefs() {
        if (mySharedPreferences == null) return false;
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(SHARED_PREF_USER_ID);
        editor.remove(SHARED_PREF_USER_COMPANY);
        editor.remove(SHARED_PREF_USER_FIRSTNAME);
        editor.remove(SHARED_PREF_USER_LASTNAME);
        editor.remove(SHARED_PREF_USER_PROMO);
        editor.remove(SHARED_PREF_USER_LICENSE_END);
        editor.remove(SHARED_PREF_USER_ROLES_ADMIN);
        editor.remove(SHARED_PREF_USER_ROLES_TEACHER);
        editor.remove(SHARED_PREF_USER_ROLES_STUDENT);
        editor.remove(SHARED_PREF_USER_ROLES_OBSERVER);
        return editor.commit();
    }

    public interface AuthListener {

        void onLogginFailed(Exception e);
    }

    public interface AuthProfileListener {
        void onSuccessProfile(UserModel user);

        void onProfileFailed(Exception e);
    }

    private void loadUser(String company, String IdUser, final AuthProfileListener authProfileListener) {

        UserRepository userRepository = new UserRepository(company, IdUser);
        userRepository.addChildListener(IdUser,new BaseSingleValueEventListener.CallbackInterface<UserModel>() {
            @Override
            public void onSuccess(UserModel result) {

                currentUser.setLastName(result.getLastName());
                currentUser.setFirstname(result.getFirstname());
                saveCurrentUser();
                authProfileListener.onSuccessProfile(currentUser);
            }

            @Override
            public void onError(Exception e) {
                authProfileListener.onProfileFailed(e);
            }
        });

    }
}
