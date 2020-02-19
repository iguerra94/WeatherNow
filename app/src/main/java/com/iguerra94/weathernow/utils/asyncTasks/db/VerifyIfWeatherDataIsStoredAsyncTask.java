package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.CurrentWeatherDao;
import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfWeatherDataIsStoredTaskResponse;

import java.lang.ref.WeakReference;

import lombok.SneakyThrows;

public class VerifyIfWeatherDataIsStoredAsyncTask extends AsyncTask<Void, Void, CurrentWeatherDB> {

    private WeakReference<Context> context;
    private WeakReference<IVerifyIfWeatherDataIsStoredTaskResponse> mCallback;

    public VerifyIfWeatherDataIsStoredAsyncTask(Context context, IVerifyIfWeatherDataIsStoredTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected CurrentWeatherDB doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        CurrentWeatherDao currentWeatherDao = db.currentWeatherDao();

        return currentWeatherDao.readCurrentWeather();
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(CurrentWeatherDB currentWeatherDB) {
        final IVerifyIfWeatherDataIsStoredTaskResponse callback = mCallback.get();

        if(callback != null) {
            callback.onVerifyIfWeatherDataIsStoredTaskDone(currentWeatherDB != null);
        }
    }
}