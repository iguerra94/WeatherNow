package com.iguerra94.weathernow.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AppNotificationsReceiver extends BroadcastReceiver {

    public static final String APP_NOTIFICATION_ACTION = "com.iguerra94.weathernow.AppNotificationAction";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent appNotificationIntent = new Intent(APP_NOTIFICATION_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(appNotificationIntent);
    }

}