package fr.vyfe.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.nio.charset.Charset;
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
 * A helper for getting android_id always the same way
 */
public class AndroidHelper {

    public static String getAndroidId(Context context) {
        String idAndroid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return Hashing.sha256().hashString(idAndroid, Charset.defaultCharset()).toString();
    }
}
