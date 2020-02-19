package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.CurrentWeatherDao;
import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.views.main_screens.WeatherScreenFragment;

import java.lang.ref.WeakReference;

public class StoreCurrentWeatherDataAsyncTask extends AsyncTask<Void, Void, Long> {

    private WeakReference<Context> context;
    private CurrentWeatherDB currentWeatherDB;

    public StoreCurrentWeatherDataAsyncTask(Context context, CurrentWeatherDB currentWeatherDB) {
        this.context = new WeakReference<>(context);
        this.currentWeatherDB = currentWeatherDB;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        CurrentWeatherDao currentWeatherDao = db.currentWeatherDao();

        return currentWeatherDao.insert(currentWeatherDB);
    }

    @Override
    protected void onPostExecute(Long currenWeatherRowId) {
        Log.d(WeatherScreenFragment.class.getSimpleName(), "ID: " + currenWeatherRowId);
    }
}