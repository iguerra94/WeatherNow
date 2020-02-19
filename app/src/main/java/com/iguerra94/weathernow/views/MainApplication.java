package com.iguerra94.weathernow.views;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerForNetworkChangeEvents(this);
    }

    public static void registerForNetworkChangeEvents(final Context context) {
        NetworkStateChangeReceiver networkStateChangeReceiver = new NetworkStateChangeReceiver();
        context.registerReceiver(networkStateChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION));
    }

}