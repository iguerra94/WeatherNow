package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.signup_screens.UserRegisteredActivity;

import java.lang.ref.WeakReference;

public class RegisterUserAsyncTask extends AsyncTask<Void, Void, Long> {

    private WeakReference<Context> context;
    private AlertDialog dialogRegisteringUser;
    private User user;

    public RegisterUserAsyncTask(Context context, User user) {
        this.context = new WeakReference<>(context);
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        dialogRegisteringUser = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(context.get(),
                R.layout.custom_loading_dialog, R.string.dialog_registering_user_message_text);

        dialogRegisteringUser.setCancelable(false);
        dialogRegisteringUser.show();

        Log.d(context.getClass().getSimpleName(), user.toString());
    }

    @Override
    protected Long doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        UserDao userDao = db.userDao();

        return userDao.insert(user);
    }

    @Override
    protected void onPostExecute(Long rowId) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // acciones que se ejecutan tras los milisegundos
            dialogRegisteringUser.dismiss();
            Intent userRegisteredIntent = new Intent(context.get(), UserRegisteredActivity.class);
            userRegisteredIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            userRegisteredIntent.putExtra("USER_FIRST_NAME", user.getFirstName());

            context.get().startActivity(userRegisteredIntent);
        }, 3000);
    }
}