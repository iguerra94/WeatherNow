package com.iguerra94.weathernow.utils.api.retrofit.model;

import java.util.List;

public class CurrentWeather {
    private String name; // City name
    private CountryDetails sys;
    private List<DayWeather> weather;
    private DayTemperature main;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CountryDetails getSys() {
        return sys;
    }

    public void setSys(CountryDetails sys) {
        this.sys = sys;
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
        return "CurrentWeatherDB{" +
                "name='" + name + '\'' +
                ", sys=" + sys +
                ", weather=" + weather +
                ", main=" + main +
                '}';
    }
}
