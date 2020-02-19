package com.iguerra94.weathernow.utils.api.retrofit.model;

import java.util.List;

public class DailyForecast {
    private List<DayForecast> list;

    public List<DayForecast> getDailyForecast() {
        return list;
    }

    public void setDailyForecast(List<DayForecast> dailyForecast) {
        this.list = dailyForecast;
    }

    @Override
    public String toString() {
        return "DailyForecastDB{" +
                "dailyForecast=" + list +
                '}';
    }
}
