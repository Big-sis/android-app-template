package fr.wildcodeschool.vyfe;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import java.util.List;

public class RasberryConnexion {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setConnexion(Context context, wifiResponse listener) {
        String networkSSID = context.getString(R.string.networkSSID);
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //TODO Verifier si le rasberry est bien disponible (branché) alors:

        assert wifiManager != null;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                listener.onSuccess();
                break;
            }
        }

        //TODO: sinon:  listener.onError();
    }


    interface wifiResponse {

        void onSuccess();

        void onError();

    }


}
