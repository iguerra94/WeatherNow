package com.iguerra94.weathernow.utils.asyncTasks.callbacks;

import com.iguerra94.weathernow.db.entities.DailyForecastDB;

import java.util.List;

public interface IRetrieveDailyForecastDataFromLocalDBTaskResponse {
    void onRetrieveDailyForecastDataFromLocalDBTaskDone(List<DailyForecastDB> dailyForecastDBList);
}