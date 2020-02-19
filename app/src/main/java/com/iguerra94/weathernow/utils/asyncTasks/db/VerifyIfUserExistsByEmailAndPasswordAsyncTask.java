package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.content.Context;
import android.os.AsyncTask;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfUserExistsByEmailAndPasswordTaskResponse;

import java.lang.ref.WeakReference;

import lombok.SneakyThrows;

public class VerifyIfUserExistsByEmailAndPasswordAsyncTask extends AsyncTask<Void, Void, User> {

    private WeakReference<Context> context;

    private User user;
    private WeakReference<IVerifyIfUserExistsByEmailAndPasswordTaskResponse> mCallback;

    public VerifyIfUserExistsByEmailAndPasswordAsyncTask(Context context, User user, IVerifyIfUserExistsByEmailAndPasswordTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.user = user;
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected User doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        UserDao userDao = db.userDao();

        return userDao.findByEmailAndPassword(user.getEmail(), user.getPassword());
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(User userFound) {
        final IVerifyIfUserExistsByEmailAndPasswordTaskResponse callback = mCallback.get();

        if(callback != null) {
            callback.onVerifyIfUserExistsByEmailAndPasswordTaskDone(userFound);
        }
    }
}