package com.iguerra94.weathernow.utils.asyncTasks.callbacks;

public interface IStoreDailyForecastDataFromLocalDBTaskResponse {
    void onStoreDailyForecastDataFromLocalDBTaskDone(boolean isDataStoredSuccessfully);
}