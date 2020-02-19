package com.iguerra94.weathernow.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.iguerra94.weathernow.db.entities.DailyForecastDB;

import java.util.List;

@Dao
public interface DailyForecastDao {
    @Insert
    Long insert(DailyForecastDB dailyForecastDB);

    @Query("SELECT * FROM daily_forecast")
    List<DailyForecastDB> findAll();

    @Query("DELETE FROM daily_forecast")
    void deleteAll();
}