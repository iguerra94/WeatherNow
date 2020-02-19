package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.DailyForecastDao;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IRetrieveDailyForecastDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IStoreDailyForecastDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.views.main_screens.WeatherScreenFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class StoreDailyForecastDataAsyncTask extends AsyncTask<Void, Void, Integer> {

    private WeakReference<Context> context;
    private List<DailyForecastDB> dailyForecastDBList;
    private WeakReference<IStoreDailyForecastDataFromLocalDBTaskResponse> mCallback;

    public StoreDailyForecastDataAsyncTask(Context context, List<DailyForecastDB> dailyForecastDBList, IStoreDailyForecastDataFromLocalDBTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.dailyForecastDBList = dailyForecastDBList;
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        DailyForecastDao dailyForecastDao = db.dailyForecastDao();

        for (DailyForecastDB dailyForecastDB : dailyForecastDBList) {
            dailyForecastDao.insert(dailyForecastDB);
        }

        return dailyForecastDBList.size();
    }

    @Override
    protected void onPostExecute(Integer dailyForecastRowsStored) {
        Log.d(WeatherScreenFragment.class.getSimpleName(), "ROWS STORED: " + dailyForecastRowsStored);

        final IStoreDailyForecastDataFromLocalDBTaskResponse callback = mCallback.get();

        int expectedRowsStored = 4;

        if (callback != null) {
            callback.onStoreDailyForecastDataFromLocalDBTaskDone(dailyForecastRowsStored == expectedRowsStored);
        }
    }
}