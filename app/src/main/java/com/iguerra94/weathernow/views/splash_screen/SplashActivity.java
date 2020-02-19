package com.iguerra94.weathernow.views.splash_screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.main_screens.MainScreenActivity;
import com.iguerra94.weathernow.views.startup_screen.StartupActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPrefsManager.getInstance(this).readBoolean(SharedPrefsKeys.IS_USER_LOGGED)) {
            goToMainActivity();
        } else {
            goToStartupActivity();
        }
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        finish();
    }

    private void goToStartupActivity() {
        Intent i = new Intent(this, StartupActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        finish();
    }

}