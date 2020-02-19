package com.iguerra94.weathernow.views.startup_screen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.PermissionUtils;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.login_screens.LoginActivity;
import com.iguerra94.weathernow.views.signup_screens.SignupActivity;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!PermissionUtils.arePermissionsEnabled(this, PermissionUtils.appPermissions)){
                    PermissionUtils.requestMultiplePermissions(this, PermissionUtils.appPermissions);
                }
            }
        }, 800);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (SharedPrefsManager.getInstance(this).readBoolean(SharedPrefsKeys.IS_USER_LOGGED)) {
            finish();
        }
    }

    public void goToLoginActivity(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void goToSignupActivity(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101){
            for(int i = 0; i < grantResults.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        new AlertDialog.Builder(this)
                                .setMessage("The permissions are not granted!")
                                .setPositiveButton("Allow", (dialog, which) -> PermissionUtils.requestMultiplePermissions(this, permissions))
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    }
                    return;
                }
            }
        }
    }
}