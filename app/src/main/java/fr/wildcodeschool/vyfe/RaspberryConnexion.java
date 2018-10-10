package fr.wildcodeschool.vyfe;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;

public class RaspberryConnexion {
    private static String networkSSID;
    private static  WifiManager wifiManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setConnexionRaspberry(Context context, wifiResponse listener) {
        networkSSID = context.getString(R.string.networkSSID);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        boolean wifiRaspberryActive = getScanWifi(context);

        if (wifiRaspberryActive) {
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
        } else listener.onError();

    }

    public static boolean getScanWifi(Context context) {
        networkSSID = context.getString(R.string.networkSSID);
         wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        boolean wifiRaspberryActive = false;
        assert wifiManager != null;
        List<ScanResult> mScanResults = wifiManager.getScanResults();
        for (ScanResult t : mScanResults) {
            if (t.SSID.equals(networkSSID)) {
                wifiRaspberryActive = true;
            }
        }
        return wifiRaspberryActive;
    }

    interface wifiResponse {

        void onSuccess();

        void onError();
    }

}
