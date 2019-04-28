package fr.vyfe.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import fr.vyfe.activity.VyfeActivity;

public class NetworkChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (isOnline(context)) {
                VyfeActivity.dialog(true,context);
            } else {
                VyfeActivity.dialog(false,context);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {

        NetworkInfo network = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        return (network!=null && network.isConnected());


    }


}
