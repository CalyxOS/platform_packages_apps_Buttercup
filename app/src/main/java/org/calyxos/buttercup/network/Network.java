package org.calyxos.buttercup.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class to get internet connection status
 * Created by Ese Udom on 7/14/2021.
 */
public class Network {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //TODO implement NetworkCallbacks

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
}
