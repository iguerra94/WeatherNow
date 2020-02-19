package com.iguerra94.weathernow.utils.api.callbacks;

import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;

public interface IRetrofitGetCurrentWeatherTaskResponse {
    void onRetrofitGetCurrentWeatherTaskDone(CurrentWeather currentWeather);
    void onRetrofitGetCurrentWeatherTaskFailure(int errorCode);
}