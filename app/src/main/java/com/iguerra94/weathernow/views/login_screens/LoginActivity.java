package com.iguerra94.weathernow.views.login_screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.splash_screen.SplashActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginOptionsFragment loginOptionsFragment = new LoginOptionsFragment();
        FragmentUtils.setFragment(this, R.id.loginContainer, loginOptionsFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();

        String loginEmail = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.LOGIN_EMAIL);

        if (!loginEmail.isEmpty()) {
            SharedPrefsManager.getInstance(this).saveString(SharedPrefsKeys.LOGIN_EMAIL, "");
        }

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}