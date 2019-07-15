package fr.vyfe.helper;


import android.content.Context;
import android.provider.Settings;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * A helper for getting android_id always the same way
 */
public class AndroidHelper {

    public static String getAndroidId(Context context) {
        String idAndroid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return Hashing.sha256().hashString(idAndroid, Charset.defaultCharset()).toString();
    }
}