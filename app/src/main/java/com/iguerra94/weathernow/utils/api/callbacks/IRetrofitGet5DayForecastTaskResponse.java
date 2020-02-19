package com.iguerra94.weathernow.utils.api.callbacks;

import com.iguerra94.weathernow.utils.api.retrofit.model.DailyForecast;

public interface IRetrofitGet5DayForecastTaskResponse {
    void onRetrofitGet5DayForecastTaskDone(DailyForecast dailyForecast);
}