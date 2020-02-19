package com.iguerra94.weathernow.utils.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.splash_screen.SplashActivity;

import java.util.Calendar;
import java.util.Random;

import static com.iguerra94.weathernow.utils.Constants.NOTIFICATION_ID_FOREGROUND_SERVICE;

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        this.mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayWeatherNotification(CurrentWeather weatherData) {
        NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = new Random().nextInt(60000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupWeatherNotificationChannels(mNotificationManager);
        }

        String notificationTitle = createNotificationTitle(weatherData);
        String notificationBody = createNotificationBody(weatherData);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, mCtx.getString(R.string.default_notification_channel_id))
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSmallIcon(R.drawable.ic_notification)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                .setColor(mCtx.getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true);

        Intent intent = new Intent(mCtx, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(notificationId, mBuilder.build());
        }
    }

    private String createNotificationUserGreet() {
        String notificationUserGreet;

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        String appLanguageLocale = SharedPrefsManager.getInstance(mCtx).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        String currentUserGivenName = SharedPrefsManager.getInstance(mCtx).readString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME);

        if (currentHour >= 6 && currentHour < 12) {
            String notitication_user_greet_time_day = appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                    mCtx.getResources().getString(R.string.notitication_user_greet_time_day_language_spanish) : mCtx.getResources().getString(R.string.notitication_user_greet_time_day_language_english);

            notificationUserGreet = notitication_user_greet_time_day + " " + currentUserGivenName;
        } else if (currentHour >= 12 && currentHour < 20) {
            String notitication_user_greet_time_afternoon = appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                    mCtx.getResources().getString(R.string.notitication_user_greet_time_afternoon_language_spanish) : mCtx.getResources().getString(R.string.notitication_user_greet_time_afternoon_language_english);

            notificationUserGreet = notitication_user_greet_time_afternoon + " " + currentUserGivenName;
        } else {
            String notitication_user_greet_time_night = appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                    mCtx.getResources().getString(R.string.notitication_user_greet_time_night_language_spanish) : mCtx.getResources().getString(R.string.notitication_user_greet_time_night_language_english);

            notificationUserGreet = notitication_user_greet_time_night + " " + currentUserGivenName;
        }

        return notificationUserGreet;
    }

    private String createNotificationTitle(CurrentWeather weatherData) {
        String notificationUserGreet = createNotificationUserGreet();

        String notificationTitle = notificationUserGreet + ". ";

        String appLanguageLocale = SharedPrefsManager.getInstance(mCtx).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);

        notificationTitle += appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                mCtx.getResources().getString(R.string.notitication_current_temp_pre_message_language_spanish) + Math.round(weatherData.getMain().getTemp()) + "° " + mCtx.getResources().getString(R.string.notitication_connector_in_language_spanish) + weatherData.getName() :
                mCtx.getResources().getString(R.string.notitication_current_temp_pre_message_language_english) + Math.round(weatherData.getMain().getTemp()) + "° " + mCtx.getResources().getString(R.string.notitication_connector_in_language_english) + weatherData.getName();

        return notificationTitle;
    }

    private String createNotificationBody(CurrentWeather weatherData) {
        String appLanguageLocale = SharedPrefsManager.getInstance(mCtx).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);

        return appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                mCtx.getResources().getString(R.string.notitication_current_max_and_min_temp_pre_message_language_spanish) + "max: " + Math.round(weatherData.getMain().getTempMax()) + "°, min: " + Math.round(weatherData.getMain().getTempMin()) + "°" :
                mCtx.getResources().getString(R.string.notitication_current_max_and_min_temp_pre_message_language_english) + "max: " + Math.round(weatherData.getMain().getTempMax()) + "°, min: " + Math.round(weatherData.getMain().getTempMin()) + "°";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupWeatherNotificationChannels(NotificationManager mNotificationManager){
        NotificationChannel mChannel = new NotificationChannel(mCtx.getString(R.string.default_notification_channel_id),
                mCtx.getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);

        mChannel.setDescription(mCtx.getString(R.string.default_notification_channel_description));
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);

        mChannel.setVibrationPattern(new long[]{100,200,300});

        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @SuppressLint("WrongConstant")
    public Notification prepareServiceNotification() {
        NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                mNotificationManager.getNotificationChannel(mCtx.getString(R.string.foreground_channel_id)) == null) {
            setupForegroundServiceNotificationChannels(mCtx.getString(R.string.foreground_channel_id), mNotificationManager);
        }

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(mCtx, mCtx.getString(R.string.foreground_channel_id));
        } else {
            mBuilder = new NotificationCompat.Builder(mCtx);
        }

        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(mCtx, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, NOTIFICATION_ID_FOREGROUND_SERVICE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        return mBuilder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupForegroundServiceNotificationChannels(String notificationChannelId, NotificationManager mNotificationManager){
        // The user-visible name of the channel.
        CharSequence name = mCtx.getString(R.string.default_notification_channel_name);
        int importance = NotificationManager.IMPORTANCE_MIN;
        NotificationChannel mChannel = new NotificationChannel(notificationChannelId, name, importance);

        mChannel.setDescription(mCtx.getString(R.string.default_notification_channel_description));

        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}