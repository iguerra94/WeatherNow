package com.iguerra94.weathernow.utils.asyncTasks.callbacks;

import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;

public interface IRetrieveCurrentWeatherDataFromLocalDBTaskResponse {
    void onRetrieveCurrentWeatherDataFromLocalDBTaskDone(CurrentWeatherDB currentWeatherDB);
}