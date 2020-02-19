package com.iguerra94.weathernow.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;

public class NetworkUtils {

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void updateConnectionInfoSharedPrefs(Context context, boolean isConnected) {
        String connectionStatus = isConnected ? SharedPrefsValues.IS_CONNECTED_TO_INTERNET.CONNECTED : SharedPrefsValues.IS_CONNECTED_TO_INTERNET.NOT_CONNECTED;
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.IS_CONNECTED_TO_INTERNET, connectionStatus);
    }
}
