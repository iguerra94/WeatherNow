package com.iguerra94.weathernow.views.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.views.login_screens.LoginActivity;
import com.iguerra94.weathernow.views.signup_screens.SignupActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    public void goToLoginActivity(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void goToSignupActivity(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }
}