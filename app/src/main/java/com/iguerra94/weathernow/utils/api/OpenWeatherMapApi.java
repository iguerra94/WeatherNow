package com.iguerra94.weathernow.utils.api;

import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.api.retrofit.model.DailyForecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    @GET("weather")
    Call<CurrentWeather> getCurrentWeatherByGeoCoords(@Query("lat") Double lat, @Query("lon") Double lon, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appid);

    @GET("weather")
    Call<CurrentWeather> getCurrentWeatherByCityName(@Query("q") String cityName, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appid);

    @GET("forecast")
    Call<DailyForecast> get5DayForecastByGeoCoords(@Query("lat") Double lat, @Query("lon") Double lon, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appid);

    @GET("forecast")
    Call<DailyForecast> get5DayForecastByCityName(@Query("q") String cityName, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appid);
}