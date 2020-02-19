package com.iguerra94.weathernow.views.main_screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.services.WeatherNowForegroundService;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.splash_screen.SplashActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SimpleToolbar mainToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private WeatherScreenFragment weatherScreenFragment;
    private ProfileScreenFragment profileScreenFragment;
    private SettingsScreenFragment settingsScreenFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        setupToolbar();
        setupNavigationDrawer();

        fillNavViewHeaderWithUserData(navigationView);

        weatherScreenFragment = new WeatherScreenFragment();
        profileScreenFragment = new ProfileScreenFragment();
        settingsScreenFragment = new SettingsScreenFragment();

        FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, weatherScreenFragment);
    }

    private void setupToolbar() {
        mainToolbar = findViewById(R.id.mainToolbar);
        mainToolbar.setTitle("");
        setSupportActionBar(mainToolbar);
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.mainScreenDrawerLayout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(false);

        drawerToggle.setToolbarNavigationClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_sort);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void fillNavViewHeaderWithUserData(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);

        CircleImageView navUserImageImageView = headerView.findViewById(R.id.nav_user_image);
        TextView navUserFullNameTextView = headerView.findViewById(R.id.nav_user_full_name);

        String currentUserProfileImageUri = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.CURRENT_USER_PROFILE_IMAGE_URI);
        Glide.with(this)
                .load(currentUserProfileImageUri)
                .placeholder(R.mipmap.icon_placeholder)
                .error(R.mipmap.account_circle_white_bg)
                .into(navUserImageImageView);

        String currentUserGivenName = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME);
        String currentUserFamilyName = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.CURRENT_USER_FAMILY_NAME);

        navUserFullNameTextView.setText(String.format("%s %s", currentUserGivenName, currentUserFamilyName));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_weather:
                new Handler().postDelayed(() -> {
                    //put the statements which are need to be executed after closing drawer.
                    profileScreenFragment = null;
                    settingsScreenFragment = null;

                    weatherScreenFragment = new WeatherScreenFragment();
                    FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, weatherScreenFragment);
                }, 100);

                new Handler().postDelayed(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    menuItem.setChecked(true);
                }, 450);

                break;
            case R.id.nav_profile:
                new Handler().postDelayed(() -> {
                    //put the statements which are need to be executed after closing drawer.
                    weatherScreenFragment = null;
                    settingsScreenFragment = null;

                    profileScreenFragment = new ProfileScreenFragment();
                    FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, profileScreenFragment);
                }, 100);

                new Handler().postDelayed(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    menuItem.setChecked(true);
                }, 450);

                break;
            case R.id.nav_settings:
                new Handler().postDelayed(() -> {
                    //put the statements which are need to be executed after closing drawer.
                    weatherScreenFragment = null;
                    profileScreenFragment = null;

                    settingsScreenFragment = new SettingsScreenFragment();
                    FragmentUtils.setFragment(this, R.id.main_content_screen_fragment, settingsScreenFragment);
                }, 100);

                new Handler().postDelayed(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    menuItem.setChecked(true);
                }, 450);

                break;
            case R.id.nav_query_developer:
                new Handler().postDelayed(() -> {
                    //put the statements which are need to be executed after closing drawer.
                    weatherScreenFragment = null;
                    profileScreenFragment = null;
                    settingsScreenFragment = null;

                    createEmailIntent();
                }, 100);

                new Handler().postDelayed(() -> {
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                }, 450);

                break;
            case R.id.nav_logout:
                AlertDialog dialogLogoutUser = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(this, R.layout.custom_loading_dialog, R.string.dialog_logout_user_message_text);
                dialogLogoutUser.setCancelable(false);
                dialogLogoutUser.show();

                SharedPrefsManager.getInstance(this).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, false);
                WeatherNowForegroundService.stopForegroundServiceIntent(this);

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    SharedPrefsManager.getInstance(this).resetUserSharedPrefsData(this);

                    goToSplashScreenActivity();
                    dialogLogoutUser.dismiss();
                }, 2000);

                break;
            default:
                break;
        }

        return true;
    }

    private void goToSplashScreenActivity() {
        Intent i = new Intent(this, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        finish();
    }

    private void createEmailIntent() {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "iguerra696@alumnos.iua.edu.ar" });
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.consult_developer_email_extra_subject_text));
        mailIntent.setData(Uri.parse("mailto:"));
        mailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(mailIntent, getResources().getString(R.string.consult_developer_email_intent_chooser_title)));
        } catch (Exception e) {
            Toast toast = ToastUtils.createCustomToast(this, Toast.LENGTH_LONG, e.getMessage());
            toast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}