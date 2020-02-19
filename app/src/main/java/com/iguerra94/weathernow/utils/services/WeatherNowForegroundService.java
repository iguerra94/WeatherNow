package com.iguerra94.weathernow.utils.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iguerra94.weathernow.utils.AlarmManagerUtils;
import com.iguerra94.weathernow.utils.CalendarUtils;
import com.iguerra94.weathernow.utils.Constants;
import com.iguerra94.weathernow.utils.PermissionUtils;
import com.iguerra94.weathernow.utils.api.ApiUtils;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGetCurrentWeatherTaskResponse;
import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.location.LocationUtils;
import com.iguerra94.weathernow.utils.location.callbacks.ILocationTaskResponse;
import com.iguerra94.weathernow.utils.notifications.AppNotificationsReceiver;
import com.iguerra94.weathernow.utils.notifications.MyNotificationManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;

import java.util.Map;

import static com.iguerra94.weathernow.utils.Constants.NOTIFICATION_ID_FOREGROUND_SERVICE;

public class WeatherNowForegroundService extends Service implements ILocationTaskResponse, IRetrofitGetCurrentWeatherTaskResponse {

    private static final int APP_NOTIFICATIONS_INTENT_REQUEST_CODE = 200;

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    private static BroadcastReceiver receiver;
    private Location mLastKnownLocation;

    public WeatherNowForegroundService() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate()." + receiver);

        receiver = initNotificationsReceiver();
        registerNotificationsReceiver(receiver);
    }

    private BroadcastReceiver initNotificationsReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getDeviceLocationForAppNotification(context);
                Log.d(TAG_FOREGROUND_SERVICE, "LOCATION TRUE");
            }
        };
        return receiver;
    }

    private void getDeviceLocationForAppNotification(Context context) {
        boolean mLocationPermissionGranted = PermissionUtils.getLocationPermission(context);
        LocationUtils.getDeviceLocation(context, mLocationPermissionGranted, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopForegroundService();
            return START_NOT_STICKY;
        }

        assert intent.getAction() != null;

        if (intent.getAction().equals(Constants.ACTION_START_FOREGROUND_SERVICE)) {
            startForegroundService();
        } else {
            stopForegroundService();
        }

        return START_NOT_STICKY;
    }

    public static void startForegroundServiceIntent(Context context) {
        Intent intent = new Intent(context, WeatherNowForegroundService.class);
        intent.setAction(Constants.ACTION_START_FOREGROUND_SERVICE);
        context.startService(intent);
    }

    public static void stopForegroundServiceIntent(Context context) {
        Intent intent = new Intent(context, WeatherNowForegroundService.class);
        intent.setAction(Constants.ACTION_STOP_FOREGROUND_SERVICE);
        context.startService(intent);
    }

    private void startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        String currentTimeMinusTwoMinutes = CalendarUtils.getCurrentTimeMinusTwoMinutesFormattedToString();
        String appNotificationScheduleTime = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME, currentTimeMinusTwoMinutes);

        Log.d(TAG_FOREGROUND_SERVICE, appNotificationScheduleTime);
        String[] appNotificationScheduleTimeSplitted = appNotificationScheduleTime.split(":");

        int hourOfDay = Integer.parseInt(appNotificationScheduleTimeSplitted[0]);
        int minutes = Integer.parseInt(appNotificationScheduleTimeSplitted[1]);

        Intent intent = new Intent(this, AppNotificationsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, APP_NOTIFICATIONS_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManagerUtils.setupAlarmManager(this, hourOfDay, minutes, pendingIntent);

        startForeground(NOTIFICATION_ID_FOREGROUND_SERVICE,
                MyNotificationManager.getInstance(this.getApplicationContext()).prepareServiceNotification());
    }

    private void registerNotificationsReceiver(BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter(AppNotificationsReceiver.APP_NOTIFICATION_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    private void unregisterNotificationsReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

        unregisterNotificationsReceiver(receiver);
        AlarmManagerUtils.stopAlarmManager(this, APP_NOTIFICATIONS_INTENT_REQUEST_CODE);
    }

    @Override
    public void onLocationTaskDone(Location currentLocation) {
        if (currentLocation != null) {
            mLastKnownLocation = currentLocation;
            getCurrentWeatherByGeoCoords(this);
        }
    }

    private void getCurrentWeatherByGeoCoords(Context context) {
        Map<String, Object> weatherParams = ApiUtils.initRequestParamsByGeoCoords(context, mLastKnownLocation);
        ApiUtils.getCurrentWeatherByGeoCoords(context, weatherParams, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRetrofitGetCurrentWeatherTaskDone(CurrentWeather currentWeather) {
        MyNotificationManager.getInstance(this.getApplicationContext())
                .displayWeatherNotification(currentWeather);
    }

    @Override
    public void onRetrofitGetCurrentWeatherTaskFailure(int errorCode) {}
}