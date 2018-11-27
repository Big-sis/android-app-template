package fr.vyfe.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnexionHelper {
    public static boolean haveInternetConnection(Context context){
        NetworkInfo network = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (network==null || !network.isConnected())
        {
            return false;
        }
        return true;
    }
}
