package com.iguerra94.weathernow.utils.db;

import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.StringUtils;
import com.iguerra94.weathernow.utils.api.ApiUtils;
import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.api.retrofit.model.DailyForecast;
import com.iguerra94.weathernow.utils.api.retrofit.model.DayForecast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    public static CurrentWeatherDB initCurrentWeatherDBObject(CurrentWeather currentWeather) {
        CurrentWeatherDB currentWeatherDB = new CurrentWeatherDB();

        currentWeatherDB.setCityName(currentWeather.getName());
        currentWeatherDB.setCountry(currentWeather.getSys().getCountry());

        String currentWeatherDescription = currentWeather.getWeather().get(0).getDescription().substring(0,1).toUpperCase() + currentWeather.getWeather().get(0).getDescription().substring(1).toLowerCase();

        currentWeatherDB.setDescription(currentWeatherDescription);

        String iconIdColoured = StringUtils.getIconIdColoured(currentWeather.getWeather().get(0).getIcon());
        String iconUri = ApiUtils.WEATHER_ICONS_BASE_URL + iconIdColoured + "@2x.png";

        currentWeatherDB.setIconUri(iconUri);

        currentWeatherDB.setCurrentTemp(Math.round(currentWeather.getMain().getTemp()));
        currentWeatherDB.setCurrentTempMax(Math.round(currentWeather.getMain().getTempMax()));
        currentWeatherDB.setCurrentTempMin(Math.round(currentWeather.getMain().getTempMin()));

        return currentWeatherDB;
    }

    public static List<DailyForecastDB> initDailyForecastDBListObject(DailyForecast dailyForecast) {
        List<DayForecast> dailyForecastShortened = new ArrayList<>();

        int initialOffset = 8;
        int currentOffset;
        int resultsDistance = 8;
        int resultSize = 4;

        for (int i = 0; i < resultSize; i++) {
            currentOffset = initialOffset + i * resultsDistance;
            dailyForecastShortened.add(dailyForecast.getDailyForecast().get(currentOffset));
        }

        List<DailyForecastDB> dailyForecastDBList = new ArrayList<>();

        for (DayForecast dayForecast : dailyForecastShortened) {
            DailyForecastDB dailyForecastDB = new DailyForecastDB();

            dailyForecastDB.setDt(dayForecast.getDt());

            String description = dayForecast.getWeather().get(0).getDescription().substring(0,1).toUpperCase() + dayForecast.getWeather().get(0).getDescription().substring(1).toLowerCase();
            dailyForecastDB.setDescription(description);

            String iconIdColoured = StringUtils.getIconIdColoured(dayForecast.getWeather().get(0).getIcon());
            String iconUri = ApiUtils.WEATHER_ICONS_BASE_URL + iconIdColoured + ".png";

            dailyForecastDB.setIconUri(iconUri);
            dailyForecastDB.setTempMax(Math.round(dayForecast.getMain().getTempMax()));
            dailyForecastDB.setTempMin(Math.round(dayForecast.getMain().getTempMin()));

            dailyForecastDBList.add(dailyForecastDB);
        }

        return dailyForecastDBList;
    }

}