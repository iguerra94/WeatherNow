package com.iguerra94.weathernow.views.main_screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SimpleToolbar mainToolbar;
    private DrawerLayout drawerLayout;
    private WeatherScreenFragment weatherScreenFragment;
    private ProfileScreenFragment profileScreenFragment;
    private SettingsScreenFragment settingsScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        setupToolbar();
        setupNavigationDrawer();

        weatherScreenFragment = new WeatherScreenFragment();
        profileScreenFragment = new ProfileScreenFragment();
        settingsScreenFragment = new SettingsScreenFragment();

        FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, weatherScreenFragment);
    }

    private void setupToolbar() {
        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.mainScreenDrawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(false);

        drawerToggle.setToolbarNavigationClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_sort);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_weather:
                new Thread(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    menuItem.setChecked(true);

                    profileScreenFragment = null;
                    settingsScreenFragment = null;
                }).start();

                weatherScreenFragment = new WeatherScreenFragment();
                FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, weatherScreenFragment);

                break;
            case R.id.nav_profile:
                new Thread(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    menuItem.setChecked(true);

                    weatherScreenFragment = null;
                    settingsScreenFragment = null;
                }).start();

                profileScreenFragment = new ProfileScreenFragment();
                FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, profileScreenFragment);

                break;
            case R.id.nav_settings:
                new Thread(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    menuItem.setChecked(true);

                    weatherScreenFragment = null;
                    profileScreenFragment = null;
                }).start();

                settingsScreenFragment = new SettingsScreenFragment();
                FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, settingsScreenFragment);

                break;
            case R.id.nav_query_developer:
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.setType("message/rfc822");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "iguerra696@alumnos.iua.edu.ar" });
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Consulta] Aplicacion Android WeatherNow");

                try {
                    startActivity(Intent.createChooser(mailIntent, "Elige un Cliente de Correo"));
                } catch (Exception e) {
                    Toast toast = ToastUtils.createCustomToast(this, e.getMessage());
                    toast.show();
                }

                drawerLayout.closeDrawer(GravityCompat.START, true);

                break;
            case R.id.nav_logout:
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}