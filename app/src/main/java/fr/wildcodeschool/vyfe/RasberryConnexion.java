package fr.wildcodeschool.vyfe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class RasberryConnexion {

    private static WifiManager wifiManager;
    private static String networkSSID;

    public static void setConnexion(Context context, rasberryResponse listener) {
        networkSSID = context.getString(R.string.networkSSID);

        //Connexion Rasberry
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                listener.onSuccess(true);
                break;
            }

        }

    }
    public static  void getConnexion(Context context, RasberryConnexion.rasberryResponse listener) {
        @SuppressLint("WrongConstant") ConnectivityManager connManager = (ConnectivityManager) context.getSystemService("connectivity");
        assert connManager != null;
        String wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getExtraInfo();

        if (wifi!=null && wifi.equals('"'+networkSSID+'"')) {
            listener.onConnected();
        }
    }

    interface rasberryResponse {

        void onSuccess(boolean connexion);

        void onError(boolean connexion);

        void onConnected();

    }




}
