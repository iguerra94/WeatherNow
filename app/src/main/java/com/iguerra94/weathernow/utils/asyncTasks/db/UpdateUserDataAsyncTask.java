package com.iguerra94.weathernow.utils.asyncTasks.db;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.db.AppDatabase;
import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.main_screens.MainScreenActivity;

import java.lang.ref.WeakReference;

public class UpdateUserDataAsyncTask extends AsyncTask<Void, Void, Integer> {

    private WeakReference<Context> context;
    private AlertDialog dialogUpdatingUserData;
    private User user;

    public UpdateUserDataAsyncTask(Context context, User user) {
        this.context = new WeakReference<>(context);
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        dialogUpdatingUserData = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(context.get(),
                R.layout.custom_loading_dialog, R.string.dialog_updating_user_data_message_text);

        dialogUpdatingUserData.setCancelable(false);
        dialogUpdatingUserData.show();

        Log.d(context.getClass().getSimpleName(), user.toString());
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDatabase(context.get());
        UserDao userDao = db.userDao();

        User userToUpdate = userDao.findByEmail(user.getEmail());
        user.setId(userToUpdate.getId());

        if (user.getPassword().isEmpty()) {
            user.setPassword(userToUpdate.getPassword());
        } else {
            //Hash password with SHA-256 Algorithm
            user.setPassword(UserUtils.hashPassword(user.getPassword()));
        }

        return userDao.update(user);
    }

    @Override
    protected void onPostExecute(Integer cantRows) {
        if (cantRows > 0) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                // acciones que se ejecutan tras los milisegundos
                SharedPrefsManager.getInstance(context.get()).saveUserDataInSharedPrefs(context.get(), user);

                Intent intent = new Intent(context.get(), MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.get().startActivity(intent);

                dialogUpdatingUserData.dismiss();
            }, 2000);

            handler.postDelayed(() -> {
                Toast toast = ToastUtils.createCustomToast((FragmentActivity) context.get(), Toast.LENGTH_LONG, context.get().getResources().getString(R.string.user_data_updated_successfully_message));
                toast.show();
            }, 2500);
        } else {
            Log.d(context.get().getClass().getSimpleName(), "Error de actualizacion de usuario.");
        }
    }
}