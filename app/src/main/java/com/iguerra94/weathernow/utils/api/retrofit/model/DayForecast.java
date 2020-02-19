package com.iguerra94.weathernow.utils.api.retrofit.model;

import java.util.List;

public class DayForecast {
    private Long dt; // Time of data calculation, unix, UTC
    private List<DayWeather> weather;
    private DayTemperature main;

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public List<DayWeather> getWeather() {
        return weather;
    }

    public void setWeather(List<DayWeather> weather) {
        this.weather = weather;
    }

    public DayTemperature getMain() {
        return main;
    }

    public void setMain(DayTemperature main) {
        this.main = main;
    }

    @Override
    public String toString() {
        return "DayForecast{" +
                "dt=" + dt +
                ", weather=" + weather +
                ", main=" + main +
                '}';
    }
}
