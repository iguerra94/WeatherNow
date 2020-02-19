package com.iguerra94.weathernow.utils.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iguerra94.weathernow.BuildConfig;
import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.db.entities.User;

import java.util.List;

public class SharedPrefsManager {

    private static SharedPrefsManager instance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private SharedPrefsManager() {}

    public static SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager();
        }
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return instance;
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPreferences;
    }

    public void saveString(String KEY, String VALUE) {
        editor.putString(KEY, VALUE);
        editor.apply();
    }

    public String readString(String KEY) {
        return sharedPreferences.getString(KEY, "");
    }

    public String readString(String KEY, String DEFAULT_VALUE) {
        return sharedPreferences.getString(KEY, DEFAULT_VALUE);
    }

    public void saveBoolean(String KEY, boolean VALUE) {
        editor.putBoolean(KEY, VALUE);
        editor.apply();
    }

    public boolean readBoolean(String KEY) {
        return sharedPreferences.getBoolean(KEY, false);
    }

    public boolean readBoolean(String KEY, boolean DEFAULT_VALUE) {
        return sharedPreferences.getBoolean(KEY, DEFAULT_VALUE);
    }

    public void saveLong(String KEY, long VALUE) {
        editor.putLong(KEY, VALUE);
        editor.apply();
    }

    public long readLong(String KEY) {
        return sharedPreferences.getLong(KEY, 0);
    }

    public void saveSearchedCurrentWeatherDBObject(String KEY, CurrentWeatherDB currentWeatherDB) {
        Gson gson = new Gson();
        String json = gson.toJson(currentWeatherDB);

        editor.putString(KEY, json);
        editor.apply();
    }

    public CurrentWeatherDB readSearchedCurrentWeatherDBObject(String KEY) {
        Gson gson = new Gson();

        String response = sharedPreferences.getString(KEY, "");

        return gson.fromJson(response, new TypeToken<CurrentWeatherDB>() {}.getType());
    }

    public void saveSearchedDailyForecastDBListObject(String KEY, List<DailyForecastDB> dailyForecastDBList) {
        Gson gson = new Gson();
        String json = gson.toJson(dailyForecastDBList);

        editor.putString(KEY, json);
        editor.apply();
    }

    public List<DailyForecastDB> readSearchedDailyForecastDBListObject(String KEY) {
        Gson gson = new Gson();

        String response = sharedPreferences.getString(KEY, "");

        return gson.fromJson(response, new TypeToken<List<DailyForecastDB>>() {}.getType());
    }

    public void resetUserSharedPrefsData(Context context) {
        SharedPrefsManager.getInstance(context).saveBoolean(SharedPrefsKeys.IS_USER_LOGGED, false);
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME, "");
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_FAMILY_NAME, "");
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_EMAIL, "");
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_PROFILE_IMAGE_URI, "");
    }

    public void saveUserDataInSharedPrefs(Context context, User user) {
        SharedPrefsManager.getInstance(context).saveBoolean(SharedPrefsKeys.IS_USER_LOGGED, true);
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME, user.getFirstName());
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_FAMILY_NAME, user.getLastName());
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_EMAIL, user.getEmail());
        SharedPrefsManager.getInstance(context).saveString(SharedPrefsKeys.CURRENT_USER_PROFILE_IMAGE_URI, user.getProfileImageUri());
    }
}