package com.iguerra94.weathernow.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;

@Dao
public interface CurrentWeatherDao {
    @Insert
    Long insert(CurrentWeatherDB currentWeatherDB);

    @Query("SELECT * FROM current_weather")
    CurrentWeatherDB readCurrentWeather();

    @Query("DELETE FROM current_weather")
    void delete();
}