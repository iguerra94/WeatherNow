package com.iguerra94.weathernow.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "current_weather")
public class CurrentWeatherDB {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "current_weather_id")
    public int id;

    @ColumnInfo(name = "city_name")
    public String cityName; // City name

    @ColumnInfo(name = "country")
    public String country; // Country

    @ColumnInfo(name = "description")
    public String description; // Current weather condition within the group of weather parameters (Rain, Snow, Extreme, etc).

    @ColumnInfo(name = "icon_uri")
    public String iconUri; // Current weather icon uri

    @ColumnInfo(name = "temp")
    public Long currentTemp; // Current temperature

    @ColumnInfo(name = "temp_min")
    public Long currentTempMin; // Current minimum temperature at the moment of calculation

    @ColumnInfo(name = "temp_max")
    public Long currentTempMax; // Current maximum temperature at the moment of calculation

    public CurrentWeatherDB() {
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentWeatherDB: { id: " + getId() +
                ", cityName: " + getCityName() +
                ", country: " + getCountry() +
                ", description: " + getDescription() +
                ", iconUri: " + getIconUri() +
                ", currentTemp: " + getCurrentTemp() +
                ", currentTempMin: " + getCurrentTempMin() +
                ", currentTempMax: " + getCurrentTempMax() +
                " }";
    }
}
