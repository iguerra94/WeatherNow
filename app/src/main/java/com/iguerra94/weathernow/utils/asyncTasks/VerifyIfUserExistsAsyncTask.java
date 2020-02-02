package com.iguerra94.weathernow.utils.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.UserExistsException;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IAsyncTaskResponse;

import java.lang.ref.WeakReference;

import lombok.SneakyThrows;

public class VerifyIfUserExistsAsyncTask extends AsyncTask<Void, Void, User> {

    private WeakReference<Context> context;
    private User user;
    private WeakReference<IAsyncTaskResponse> mCallback;

    public VerifyIfUserExistsAsyncTask(Context context, User user, IAsyncTaskResponse callback) {
        this.context = new WeakReference<>(context);
        this.user = user;
        this.mCallback = new WeakReference<>(callback);
    }

    @Override
    protected User doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        UserDao userDao = db.userDao();

        return userDao.findByEmail(user.getEmail());
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(User userFound) {
        final IAsyncTaskResponse callback = mCallback.get();

        if(callback != null) {
            callback.onTaskDone(userFound != null);
        }
    }
}