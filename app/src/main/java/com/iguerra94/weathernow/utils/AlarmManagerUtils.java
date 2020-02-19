package com.iguerra94.weathernow.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.iguerra94.weathernow.utils.notifications.AppNotificationsReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmManagerUtils {
    public static void setupAlarmManager(Context context, int hourOfDay, int minutes, PendingIntent pendingIntent) {
        Calendar updatedCalendar = Calendar.getInstance();
        updatedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        updatedCalendar.set(Calendar.MINUTE, minutes);
        updatedCalendar.set(Calendar.SECOND, 0);
        updatedCalendar.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updatedCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void stopAlarmManager(Context context, int intentRequestCode) {
        Intent intent = new Intent(context, AppNotificationsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentRequestCode, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
