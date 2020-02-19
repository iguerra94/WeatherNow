package com.iguerra94.weathernow.views.main_screens.search_screen;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.ActivitySearchBinding;
import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.api.ApiUtils;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGet5DayForecastTaskResponse;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGetCurrentWeatherTaskResponse;
import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.api.retrofit.model.DailyForecast;
import com.iguerra94.weathernow.utils.db.DatabaseUtils;
import com.iguerra94.weathernow.utils.network.NetworkUtils;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.transition.FadeInTransition;
import com.iguerra94.weathernow.views.transition.FadeOutTransition;
import com.iguerra94.weathernow.views.transition.SimpleTransitionListener;

import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements IRetrofitGet5DayForecastTaskResponse, IRetrofitGetCurrentWeatherTaskResponse {

    private ActivitySearchBinding binding;
    private AlertDialog dialogSearchingCity;

    private String cityName;
    private CurrentWeatherDB searchedCurrentWeatherDBObject;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setSupportActionBar(binding.searchToolbar);

        // make sure to check if this is the first time running the activity
        // we don't want to play the enter animation on configuration changes (i.e. orientation)
        if (isFirstTimeRunning(savedInstanceState)) {
            // Start with an empty looking Toolbar
            // We are going to fade its contents in, as long as the activity finishes rendering
            binding.searchToolbar.hideContent();

            ViewTreeObserver viewTreeObserver = binding.searchToolbar.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.searchToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // after the activity has finished drawing the initial layout, we are going to continue the animation
                    // that we left off from the MainActivity
                    showSearch();
                }

                private void showSearch() {
                    // use the TransitionManager to animate the changes of the Toolbar
                    TransitionManager.beginDelayedTransition(binding.searchToolbar, FadeInTransition.createTransition());
                    // here we are just changing all children to VISIBLE
                    binding.searchToolbar.showContent();
                }
            });
        }

        EditText editText = binding.getRoot().findViewById(R.id.toolbar_search_edittext);

        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d(SearchActivity.class.getSimpleName(), "TEXT: " + textView.getText());

                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                cityName = textView.getText().toString();

                if (cityName.length() >= 4) {
                    searchCity(cityName);
                } else {
                    Toast wrongSearchedCityLengthToast = ToastUtils.createCustomToast(this, Toast.LENGTH_SHORT, getResources().getString(R.string.wrong_searched_city_length_message));
                    wrongSearchedCityLengthToast.show();
                }

                return true;
            }

            return false;
        });
    }

    private void searchCity(String city_name) {
        showSearchingCityDialog();

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (networkIsAvailable()) {
                getCurrentWeatherByCityName(city_name);
            } else {
                dismissSearchingCityDialog();
                Toast noInternetConnectionToast = ToastUtils.createCustomToast(this, Toast.LENGTH_LONG, getResources().getString(R.string.retrieve_server_data_no_internet_connection_message));
                noInternetConnectionToast.show();

                Handler h1 = new Handler();
                h1.postDelayed(this::finish, 800);
            }
        }, 2000);
    }

    private boolean networkIsAvailable() {
        boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(this);
        NetworkUtils.updateConnectionInfoSharedPrefs(this, isConnectedToInternet);

        return isConnectedToInternet;
    }

    private void showSearchingCityDialog() {
        dialogSearchingCity = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(this, R.layout.custom_loading_dialog, R.string.dialog_searching_city_message_text);
        dialogSearchingCity.setCancelable(false);
        dialogSearchingCity.show();
    }

    private void dismissSearchingCityDialog() {
        dialogSearchingCity.dismiss();
    }

    private boolean isFirstTimeRunning(Bundle savedInstanceState) {
        return savedInstanceState == null;
    }

    @Override
    public void finish() {
        // at the same time, start the exit transition
        exitTransitionWithAction(() -> {
            // which finishes the activity (for real) when done
            SearchActivity.super.finish();

            // override the system pending transition as we are handling ourselves
            overridePendingTransition(0, 0);
        });
    }

    private void exitTransitionWithAction(final Runnable endingAction) {
        Transition transition = FadeOutTransition.withAction(new SimpleTransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                endingAction.run();
            }
        });

        TransitionManager.beginDelayedTransition(binding.searchToolbar, transition);
        binding.searchToolbar.hideContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            finish();
            return true;
        } else if (item.getItemId() == R.id.action_clear) {
            binding.searchToolbar.clearText();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCurrentWeatherByCityName(String cityName) {
        Map<String, Object> requestParamsByCityName = ApiUtils.initRequestParamsByCityName(this, cityName);
        ApiUtils.getCurrentWeatherByCityName(this, requestParamsByCityName, this);
    }

    private void get5DayForecastByCityName(String cityName) {
        Map<String, Object> requestParamsByCityName = ApiUtils.initRequestParamsByCityName(this, cityName);
        ApiUtils.get5DayForecastByCityName(this, requestParamsByCityName, this);
    }

    @Override
    public void onRetrofitGetCurrentWeatherTaskDone(CurrentWeather currentWeather) {
        Log.d(SearchActivity.class.getSimpleName(), "currentWeather: " + currentWeather);
        searchedCurrentWeatherDBObject = DatabaseUtils.initCurrentWeatherDBObject(currentWeather);
        get5DayForecastByCityName(cityName);
    }

    @Override
    public void onRetrofitGetCurrentWeatherTaskFailure(int errorCode) {
        if (errorCode == ApiUtils.RESPONSE_CODES.NOT_FOUND) {
            Log.d(SearchActivity.class.getSimpleName(), "errorCode: " + errorCode);

            dismissSearchingCityDialog();
            Toast noCityDataFoundToast = ToastUtils.createCustomToast(this, Toast.LENGTH_SHORT, getResources().getString(R.string.no_city_data_found_from_internet_message));
            noCityDataFoundToast.show();
        }
    }

    @Override
    public void onRetrofitGet5DayForecastTaskDone(DailyForecast dailyForecast) {
        List<DailyForecastDB> searchedDailyForecastDBListObject = DatabaseUtils.initDailyForecastDBListObject(dailyForecast);

        SharedPrefsManager.getInstance(this).saveSearchedCurrentWeatherDBObject(SharedPrefsKeys.SEARCHED_CURRENT_WEATHER_DB_OBJECT, searchedCurrentWeatherDBObject);
        SharedPrefsManager.getInstance(this).saveSearchedDailyForecastDBListObject(SharedPrefsKeys.SEARCHED_DAILY_FORECAST_DB_LIST_OBJECT, searchedDailyForecastDBListObject);

        dismissSearchingCityDialog();

        Handler h1 = new Handler();
        h1.postDelayed(this::finish, 800);
    }

}