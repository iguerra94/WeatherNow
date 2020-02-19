package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.DailyForecastDao;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IRetrieveDailyForecastDataFromLocalDBTaskResponse;

import java.lang.ref.WeakReference;
import java.util.List;

import lombok.SneakyThrows;

public class RetrieveDailyForecastDataFromLocalDBAsyncTask extends AsyncTask<Void, Void, List<DailyForecastDB>> {

    private WeakReference<Context> context;
    private WeakReference<IRetrieveDailyForecastDataFromLocalDBTaskResponse> mCallback;

    public RetrieveDailyForecastDataFromLocalDBAsyncTask(Context context, IRetrieveDailyForecastDataFromLocalDBTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected List<DailyForecastDB> doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        DailyForecastDao dailyForecastDao = db.dailyForecastDao();

        return dailyForecastDao.findAll();
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(List<DailyForecastDB> dailyForecastDBList) {
        final IRetrieveDailyForecastDataFromLocalDBTaskResponse callback = mCallback.get();

        if (callback != null) {
            callback.onRetrieveDailyForecastDataFromLocalDBTaskDone(dailyForecastDBList);
        }
    }
}