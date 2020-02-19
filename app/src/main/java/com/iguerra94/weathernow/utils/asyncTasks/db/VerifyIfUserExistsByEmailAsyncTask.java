package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfUserExistsByEmailTaskResponse;

import java.lang.ref.WeakReference;

import lombok.SneakyThrows;

public class VerifyIfUserExistsByEmailAsyncTask extends AsyncTask<Void, Void, User> {

    private WeakReference<Context> context;
    private String email;
    private WeakReference<IVerifyIfUserExistsByEmailTaskResponse> mCallback;

    public VerifyIfUserExistsByEmailAsyncTask(Context context, String email, IVerifyIfUserExistsByEmailTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.email = email;
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected User doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        UserDao userDao = db.userDao();

        return userDao.findByEmail(email);
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(User userFound) {
        final IVerifyIfUserExistsByEmailTaskResponse callback = mCallback.get();

        if(callback != null) {
            callback.onVerifyIfUserExistsByEmailTaskDone(userFound != null);
        }
    }
}