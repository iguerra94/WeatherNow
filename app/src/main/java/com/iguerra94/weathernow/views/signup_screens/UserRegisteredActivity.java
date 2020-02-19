package com.iguerra94.weathernow.views.signup_screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.login_screens.LoginActivity;
import com.iguerra94.weathernow.views.splash_screen.SplashActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class UserRegisteredActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registered);

        setupToolbar();

        String userFirstName = getIntent().getStringExtra("USER_FIRST_NAME");

        TextView userRegisteredRSuccessTextView = findViewById(R.id.user_registered_success_text_view);

        String currentLocale = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);

        String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "¡Exito! Bienvenido " + userFirstName + " a WeatherNow." : "¡Success! Welcome " + userFirstName + " to WeatherNow.";

        userRegisteredRSuccessTextView.setText(message);

        Button btnSigninAfterRegister = findViewById(R.id.btnSigninAfterRegister);
        btnSigninAfterRegister.setOnClickListener(this);
    }

    private void setupToolbar() {
        SimpleToolbar userRegisteredToolbar = findViewById(R.id.user_registered_toolbar);
        userRegisteredToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        userRegisteredToolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));

        setSupportActionBar(userRegisteredToolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.action_back_splash));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSigninAfterRegister) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}
