package com.iguerra94.weathernow.utils.api;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.iguerra94.weathernow.BuildConfig;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGet5DayForecastTaskResponse;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGetCurrentWeatherTaskResponse;
import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.api.retrofit.model.DailyForecast;
import com.iguerra94.weathernow.utils.api.retrofit.model.DayForecast;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static final String WEATHER_ICONS_BASE_URL = "https://openweathermap.org/img/wn/";

    public static class REQUEST_LANGUAGE {
        public static final String SPANISH = "es";
        public static final String ENGLISH = "en";
    }

    public static class REQUEST_UNITS {
        public static final String METRIC = "metric";
        public static final String IMPERIAL = "imperial";
    }

    public static class RESPONSE_CODES {
        public static final int BAD_REQUEST = 400;
        public static final int NOT_FOUND = 404;
    }

    private static Retrofit instance;

    private static Retrofit getApiClient() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    public static Map<String, Object> initRequestParamsByGeoCoords(Context context, Location location) {
        Map<String, Object> requestParamsByGeoCoords = new HashMap<>();

        String appLanguageLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        String appTempUnit = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_TEMPERATURE_UNIT, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);

        requestParamsByGeoCoords.put("lat", location.getLatitude());
        requestParamsByGeoCoords.put("lon", location.getLongitude());
        requestParamsByGeoCoords.put("units", appTempUnit.equals(SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS) ?
                ApiUtils.REQUEST_UNITS.METRIC : ApiUtils.REQUEST_UNITS.IMPERIAL);
        requestParamsByGeoCoords.put("lang", appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                ApiUtils.REQUEST_LANGUAGE.SPANISH : ApiUtils.REQUEST_LANGUAGE.ENGLISH);
        requestParamsByGeoCoords.put("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY);

        return requestParamsByGeoCoords;
    }

    public static Map<String, Object> initRequestParamsByCityName(Context context, String cityName) {
        Map<String, Object> requestParamsByCityName = new HashMap<>();

        String appLanguageLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        String appTempUnit = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_TEMPERATURE_UNIT, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);

        requestParamsByCityName.put("q", cityName);
        requestParamsByCityName.put("units", appTempUnit.equals(SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS) ?
                ApiUtils.REQUEST_UNITS.METRIC : ApiUtils.REQUEST_UNITS.IMPERIAL);
        requestParamsByCityName.put("lang", appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                ApiUtils.REQUEST_LANGUAGE.SPANISH : ApiUtils.REQUEST_LANGUAGE.ENGLISH);
        requestParamsByCityName.put("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY);

        return requestParamsByCityName;
    }

    public static CurrentWeather getCurrentWeatherByGeoCoords(Context context, Map<String, Object> params, IRetrofitGetCurrentWeatherTaskResponse callback) {
        Retrofit apiClient = getApiClient();

        OpenWeatherMapApi api = apiClient.create(OpenWeatherMapApi.class);

        Double lat = (Double) params.get("lat");
        Double lon = (Double) params.get("lon");
        String units = (String) params.get("units");
        String lang = (String) params.get("lang");
        String appid = (String) params.get("appid");

        Call<CurrentWeather> call = api.getCurrentWeatherByGeoCoords(lat, lon, units, lang, appid);

        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Codigo: " + response.code() + ", Mensaje: " + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                CurrentWeather currentWeather = response.body();
                callback.onRetrofitGetCurrentWeatherTaskDone(currentWeather);
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }

    public static CurrentWeather getCurrentWeatherByCityName(Context context, Map<String, Object> params, IRetrofitGetCurrentWeatherTaskResponse callback) {
        Retrofit apiClient = getApiClient();

        OpenWeatherMapApi api = apiClient.create(OpenWeatherMapApi.class);

        String cityName = (String) params.get("q");
        String units = (String) params.get("units");
        String lang = (String) params.get("lang");
        String appid = (String) params.get("appid");

        Call<CurrentWeather> call = api.getCurrentWeatherByCityName(cityName, units, lang, appid);

        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (!response.isSuccessful()) {
                    callback.onRetrofitGetCurrentWeatherTaskFailure(response.code());
                    return;
                }

                CurrentWeather currentWeather = response.body();
                callback.onRetrofitGetCurrentWeatherTaskDone(currentWeather);
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
//                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("API: ", "FAILURE");
            }
        });

        return null;
    }

    public static DayForecast get5DayForecastByGeoCoords(Context context, Map<String, Object> params, IRetrofitGet5DayForecastTaskResponse callback) {
        Retrofit apiClient = getApiClient();

        OpenWeatherMapApi api = apiClient.create(OpenWeatherMapApi.class);

        Double lat = (Double) params.get("lat");
        Double lon = (Double) params.get("lon");
        String units = (String) params.get("units");
        String lang = (String) params.get("lang");
        String appid = (String) params.get("appid");

        Call<DailyForecast> call = api.get5DayForecastByGeoCoords(lat, lon, units, lang, appid);

        call.enqueue(new Callback<DailyForecast>() {
            @Override
            public void onResponse(Call<DailyForecast> call, Response<DailyForecast> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Codigo: " + response.code() + ", Mensaje: " + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                DailyForecast dailyForecast = response.body();
                callback.onRetrofitGet5DayForecastTaskDone(dailyForecast);
            }

            @Override
            public void onFailure(Call<DailyForecast> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }

    public static DayForecast get5DayForecastByCityName(Context context, Map<String, Object> params, IRetrofitGet5DayForecastTaskResponse callback) {
        Retrofit apiClient = getApiClient();

        OpenWeatherMapApi api = apiClient.create(OpenWeatherMapApi.class);

        String cityName = (String) params.get("q");
        String units = (String) params.get("units");
        String lang = (String) params.get("lang");
        String appid = (String) params.get("appid");

        Call<DailyForecast> call = api.get5DayForecastByCityName(cityName, units, lang, appid);

        call.enqueue(new Callback<DailyForecast>() {
            @Override
            public void onResponse(Call<DailyForecast> call, Response<DailyForecast> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Codigo: " + response.code() + ", Mensaje: " + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                DailyForecast dailyForecast = response.body();
                callback.onRetrofitGet5DayForecastTaskDone(dailyForecast);
            }

            @Override
            public void onFailure(Call<DailyForecast> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }
}