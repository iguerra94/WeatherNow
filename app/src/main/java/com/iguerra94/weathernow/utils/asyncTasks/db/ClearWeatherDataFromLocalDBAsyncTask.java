package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.CurrentWeatherDao;
import com.iguerra94.weathernow.db.daos.DailyForecastDao;
import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IClearWeatherDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.views.main_screens.WeatherScreenFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import lombok.SneakyThrows;

public class ClearWeatherDataFromLocalDBAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<Context> context;
    private WeakReference<IClearWeatherDataFromLocalDBTaskResponse> mCallback;

    public ClearWeatherDataFromLocalDBAsyncTask(Context context, IClearWeatherDataFromLocalDBTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        CurrentWeatherDao currentWeatherDao = db.currentWeatherDao();
        DailyForecastDao dailyForecastDao = db.dailyForecastDao();

        currentWeatherDao.delete();
        dailyForecastDao.deleteAll();

        CurrentWeatherDB currentWeatherDB = currentWeatherDao.readCurrentWeather();
        List<DailyForecastDB> dailyForecastDBList = dailyForecastDao.findAll();

        Log.d(WeatherScreenFragment.class.getSimpleName(), "CURRENT WEATHER CLEARED: " + (currentWeatherDB == null));
        Log.d(WeatherScreenFragment.class.getSimpleName(), "DAILY FORECAST LIST CLEARED: " + (dailyForecastDBList.size() == 0));

        return true;
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(Boolean weatherDataClearedSuccessfully) {
        Log.d(WeatherScreenFragment.class.getSimpleName(), "WEATHER DATA CLEARED SUCCESSFULLY: " + weatherDataClearedSuccessfully);

        final IClearWeatherDataFromLocalDBTaskResponse callback = mCallback.get();

        if (callback != null) {
            callback.onClearWeatherDataFromLocalDBTaskDone(weatherDataClearedSuccessfully);
        }
    }
}