package com.iguerra94.weathernow.utils.api.retrofit.model;

public class DayWeather {
    private String description; // CurrentWeatherDB condition within the group of weather parameters (Rain, Snow, Extreme, etc).
    private String icon; // CurrentWeatherDB icon id

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "DayWeather{" +
                "description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
