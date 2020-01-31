package com.iguerra94.weathernow.utils.asyncTasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.room.Room;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.signup_screens.UserRegisteredActivity;

import java.lang.ref.WeakReference;

public class RegisterUserAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    private WeakReference<Context> context;
    private AlertDialog dialogRegisteringUser;
    private User user;

    public RegisterUserAsyncTask(Context context, User user) {
        this.context = new WeakReference<>(context);
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        dialogRegisteringUser = (AlertDialog) DialogFactory.getInstance().getRegisteringUserDialog().create(context.get(), R.layout.dialog_registering_user);
        dialogRegisteringUser.setCancelable(false);
        dialogRegisteringUser.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        AppDatabase db = Room.databaseBuilder(context.get(), AppDatabase.class, "WeatherNowDB").build();
        UserDao userDao = db.userDao();
        userDao.insert(user);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialogRegisteringUser.dismiss();
        Intent userRegisteredIntent = new Intent(context.get(), UserRegisteredActivity.class);
        userRegisteredIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        context.get().startActivity(userRegisteredIntent);
    }
}

