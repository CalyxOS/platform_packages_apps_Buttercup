package org.calyxos.buttercup.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Class to get internet connection status
 * Created by Ese Udom on 7/14/2021.
 */
public class Network {

    private final Context _context;

    public Network(Context context) {
        this._context = context;
    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return false;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    public static boolean isUsingWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = null;
        if (connManager != null) {
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        if (connManager != null) {
            return mWifi.isConnected();
        } else {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiMgr != null && wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                return wifiInfo.getNetworkId() != -1;
            } else {
                return false; // Wi-Fi adapter is OFF
            }
        }
    }
}
