package com.iguerra94.weathernow.views.login_screens;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.FragmentUtils;

public class LoginActivity extends AppCompatActivity {

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

}