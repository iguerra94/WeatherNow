package com.iguerra94.weathernow.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "daily_forecast")
public class DailyForecastDB {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "day_forecast_id")
    public int id;

    @ColumnInfo(name = "dt")
    public Long dt; // Time of data calculation, unix, UTC

    @ColumnInfo(name = "description")
    public String description; // Weather condition of that day within the group of weather parameters (Rain, Snow, Extreme, etc).

    @ColumnInfo(name = "icon_uri")
    public String iconUri; // Current weather icon uri

    @ColumnInfo(name = "temp_min")
    public Long tempMin; // Minimum temperature of that day

    @ColumnInfo(name = "temp_max")
    public Long tempMax; // Maximum temperature of that day

    public DailyForecastDB(Long dt, String description, String iconUri, Long tempMin, Long tempMax) {
        this.dt = dt;
        this.description = description;
        this.iconUri = iconUri;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public DailyForecastDB() {
    }

    @Override
    public String toString() {
        return "DailyForecastDB: { " +
                "id: " + getId() +
                ", dt: " + getDt() +
                ", description: " + getDescription() +
                ", iconUri: " + getIconUri() +
                ", tempMin: " + getTempMin() +
                ", tempMax: " + getTempMax() +
                " }";
    }
}
