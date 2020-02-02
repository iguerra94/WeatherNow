package com.iguerra94.weathernow.views.signup_screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.views.login_screens.LoginActivity;
import com.iguerra94.weathernow.views.splash_screen.SplashActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class UserRegisteredActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registered);

        setupToolbar();

        String userFirstName = getIntent().getStringExtra("USER_FIRST_NAME");

        TextView userRegisteredRuccessTextView = findViewById(R.id.user_registered_success_text_view);
        userRegisteredRuccessTextView.setText("¡Exito! Bienvenido " + userFirstName + " a WeatherNow.");

        Button btnSigninAfterRegister = findViewById(R.id.btnSigninAfterRegister);
        btnSigninAfterRegister.setOnClickListener(this);
    }

    private void setupToolbar() {
        SimpleToolbar userRegisteredToolbar = findViewById(R.id.user_registered_toolbar);
        userRegisteredToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        userRegisteredToolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));

        setSupportActionBar(userRegisteredToolbar);

        getSupportActionBar().setTitle("Volver");
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
