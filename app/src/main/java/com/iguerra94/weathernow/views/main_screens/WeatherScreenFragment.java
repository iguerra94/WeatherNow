package com.iguerra94.weathernow.views.main_screens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.FragmentWeatherScreenBinding;
import com.iguerra94.weathernow.db.entities.CurrentWeatherDB;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.DateUtils;
import com.iguerra94.weathernow.utils.PermissionUtils;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.api.ApiUtils;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGet5DayForecastTaskResponse;
import com.iguerra94.weathernow.utils.api.callbacks.IRetrofitGetCurrentWeatherTaskResponse;
import com.iguerra94.weathernow.utils.api.retrofit.model.CurrentWeather;
import com.iguerra94.weathernow.utils.api.retrofit.model.DailyForecast;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IClearWeatherDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IRetrieveCurrentWeatherDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IRetrieveDailyForecastDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IStoreDailyForecastDataFromLocalDBTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfWeatherDataIsStoredTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.db.ClearWeatherDataFromLocalDBAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.db.RetrieveCurrentWeatherDataFromLocalDBAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.db.RetrieveDailyForecastDataFromLocalDBAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.db.StoreCurrentWeatherDataAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.db.StoreDailyForecastDataAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.db.VerifyIfWeatherDataIsStoredAsyncTask;
import com.iguerra94.weathernow.utils.db.DatabaseUtils;
import com.iguerra94.weathernow.utils.location.LocationUtils;
import com.iguerra94.weathernow.utils.location.callbacks.ILocationTaskResponse;
import com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver;
import com.iguerra94.weathernow.utils.network.NetworkUtils;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.main_screens.adapters.DailyForecastAdapter;
import com.iguerra94.weathernow.views.main_screens.search_screen.SearchActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;
import com.iguerra94.weathernow.views.transition.FadeOutTransition;
import com.iguerra94.weathernow.views.transition.SimpleTransitionListener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class WeatherScreenFragment extends Fragment implements View.OnClickListener, IVerifyIfWeatherDataIsStoredTaskResponse, ILocationTaskResponse, IRetrofitGetCurrentWeatherTaskResponse, IRetrofitGet5DayForecastTaskResponse, IStoreDailyForecastDataFromLocalDBTaskResponse, IRetrieveCurrentWeatherDataFromLocalDBTaskResponse, IRetrieveDailyForecastDataFromLocalDBTaskResponse, IClearWeatherDataFromLocalDBTaskResponse {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    private FragmentWeatherScreenBinding binding;

    private SimpleToolbar weatherScreenToolbar;

    private BroadcastReceiver receiver;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    public WeatherScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherScreenBinding.inflate(inflater, container, false);

        binding.btnRefreshServerData.setOnClickListener(this);
        binding.btnShowCurrentLocationData.setOnClickListener(this);

        long LAST_UPDATED_DATE_TIMESTAMP = SharedPrefsManager.getInstance(getActivity()).readLong(SharedPrefsKeys.LAST_UPDATED_DATE_TIMESTAMP);

        if (LAST_UPDATED_DATE_TIMESTAMP != 0) {
            Date date = new Date();
            date.setTime(LAST_UPDATED_DATE_TIMESTAMP);

            String dateFormatted = DateUtils.getLastUpdatedDateFormattedToString(date.getTime());
            binding.weatherLastUpdatedDateTextView.setText(dateFormatted);
        }

        receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInternetConnectionInfo(intent);
            }
        };

        return binding.getRoot();
    }

    private void showTopViewWeatherLastUpdatedDateWrapperLayout() {
        binding.weatherLastUpdatedDateWrapperLayout.setVisibility(View.VISIBLE);
        binding.returnToShowWeatherDataFromCurrentLocationWrapperLayout.setVisibility(View.GONE);
    }

    private void showTopViewReturnToShowWeatherDataFromCurrentLocationWrapperLayout() {
        binding.returnToShowWeatherDataFromCurrentLocationWrapperLayout.setVisibility(View.VISIBLE);
        binding.weatherLastUpdatedDateWrapperLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.mainToolbarAppBarLayout);
        appBarLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

        weatherScreenToolbar = getActivity().findViewById(R.id.mainToolbar);
        weatherScreenToolbar.setOnClickListener(this);
        weatherScreenToolbar.setNavigationIcon(R.drawable.ic_sort);

        weatherScreenToolbar.setTitle(getActivity().getResources().getString(R.string.search_hint));
        weatherScreenToolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.colorGrey));

        weatherScreenToolbar.setBackground(getActivity().getResources().getDrawable((R.drawable.border_rounded_white_bg)));

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());
        NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isConnectedToInternet);

        if (!isConnectedToInternet) {
            showNetworkNotConnectedMessage();
        } else {
            hideNetworkRecoveredMessage();
        }

        registerNetworkReceiver(receiver);

        boolean isDataAlreadyLoaded = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.IS_DATA_ALREADY_LOADED, false);
        boolean settingsChangedAndNeedUpdate = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE);

        if (SharedPrefsManager.getInstance(getActivity()).readSearchedCurrentWeatherDBObject(SharedPrefsKeys.SEARCHED_CURRENT_WEATHER_DB_OBJECT) != null) {
            showTopViewReturnToShowWeatherDataFromCurrentLocationWrapperLayout();

            showTopLoadingShimmerViews();
            showBottomLoadingShimmerViews();

            CurrentWeatherDB searchedCurrentWeatherDBObject = SharedPrefsManager.getInstance(getActivity()).readSearchedCurrentWeatherDBObject(SharedPrefsKeys.SEARCHED_CURRENT_WEATHER_DB_OBJECT);
            List<DailyForecastDB> searchedDailyForecastDBListObject = SharedPrefsManager.getInstance(getActivity()).readSearchedDailyForecastDBListObject(SharedPrefsKeys.SEARCHED_DAILY_FORECAST_DB_LIST_OBJECT);

            new Handler().postDelayed(() -> {
                fillTopContentViewsWithData(searchedCurrentWeatherDBObject);
            }, 500);

            new Handler().postDelayed(() -> {
                fillBottomContentViewsWithData(searchedDailyForecastDBListObject);
            }, 1000);
        } else {
            showTopViewWeatherLastUpdatedDateWrapperLayout();

            if (!isDataAlreadyLoaded) {
                showTopLoadingShimmerViews();
                showBottomLoadingShimmerViews();
                new Handler().postDelayed(() -> new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute(), 500);
            } else {
                showTopLoadingShimmerViews();
                showBottomLoadingShimmerViews();

                if (isConnectedToInternet && settingsChangedAndNeedUpdate) {
                    new Handler().postDelayed(() -> new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute(), 500);
                } else {
                    new Handler().postDelayed(() -> new RetrieveCurrentWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute(), 500);
                }
            }
        }

    }

    private void showNetworkNotConnectedMessage() {
        binding.weatherScreenNetworkStatusView.setVisibility(View.VISIBLE);
        binding.weatherScreenNetworkStatusViewTextView.setBackgroundColor(getResources().getColor(R.color.colorRedMD700));
        binding.weatherScreenNetworkStatusViewTextView.setText(getResources().getString(R.string.app_network_status_without_connection_textview));
    }

    private void registerNetworkReceiver(BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, intentFilter);
    }

    private void unregisterNetworkReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateInternetConnectionInfo(Intent intent) {
        boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);

        if (getActivity() != null) {
            showTopViewWeatherLastUpdatedDateWrapperLayout();

            if (!isNetworkAvailable) {
                showNetworkNotConnectedMessage();

                showTopLoadingShimmerViews();
                showBottomLoadingShimmerViews();
                new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute();
            } else {
                String alreadyConnected = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.IS_CONNECTED_TO_INTERNET);

                if (!alreadyConnected.equals(SharedPrefsValues.IS_CONNECTED_TO_INTERNET.CONNECTED)) {
                    showNetworkRecoveredMessage();
                    new Handler().postDelayed(this::hideNetworkRecoveredMessage, 1700);

                    showTopLoadingShimmerViews();
                    showBottomLoadingShimmerViews();
                    new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute();
                }
            }

            NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isNetworkAvailable);
        }
    }

    private void hideNetworkRecoveredMessage() {
        binding.weatherScreenNetworkStatusView.setVisibility(View.GONE);
    }

    private void showNetworkRecoveredMessage() {
        binding.weatherScreenNetworkStatusView.setVisibility(View.VISIBLE);
        binding.weatherScreenNetworkStatusViewTextView.setBackgroundColor(getResources().getColor(R.color.colorGreenMD700));
        binding.weatherScreenNetworkStatusViewTextView.setText(getResources().getString(R.string.app_network_status_recovered_connection_textview));
    }

    private void showNetworkNotConnectedView() {
        binding.weatherDataWrapperViewNetworkConnected.setVisibility(View.GONE);
        binding.weatherDataWrapperViewNetworkDisconnected.setVisibility(View.VISIBLE);
    }

    private void hideNetworkNotConnectedView() {
        if (binding.weatherDataWrapperViewNetworkDisconnected.getVisibility() == View.VISIBLE) {
            binding.weatherDataWrapperViewNetworkDisconnected.setVisibility(View.GONE);
            binding.weatherDataWrapperViewNetworkConnected.setVisibility(View.VISIBLE);
        }
    }

    private void showTopLoadingShimmerViews() {
        hideNetworkNotConnectedView();

        binding.shimmerViewContainerTopContent.setVisibility(View.VISIBLE);
        binding.fragmentWeatherScreenLoadedDataTopContent.getRoot().setVisibility(View.GONE);
        binding.shimmerViewContainerTopContent.startShimmer();
    }

    private void showBottomLoadingShimmerViews() {
        binding.shimmerViewContainerBottomContent.setVisibility(View.VISIBLE);
        binding.fragmentWeatherScreenLoadedDataBottomContent.getRoot().setVisibility(View.GONE);
        binding.shimmerViewContainerBottomContent.startShimmer();
    }

    private void hideTopLoadingShimmerViews() {
        binding.fragmentWeatherScreenLoadedDataTopContent.getRoot().setVisibility(View.VISIBLE);
        binding.shimmerViewContainerTopContent.setVisibility(View.GONE);
        binding.shimmerViewContainerTopContent.stopShimmer();

        hideNetworkNotConnectedView();
    }

    private void hideBottomLoadingShimmerViews() {
        binding.fragmentWeatherScreenLoadedDataBottomContent.getRoot().setVisibility(View.VISIBLE);
        binding.shimmerViewContainerBottomContent.setVisibility(View.GONE);
        binding.shimmerViewContainerBottomContent.stopShimmer();
    }

    private void transitionToSearch() {
        // create a transition that navigates to search when complete
        Transition transition = FadeOutTransition.withAction(navigateToSearchWhenDone());

        // let the TransitionManager do the heavy work for us!
        // all we have to do is change the attributes of the toolbar and the TransitionManager animates the changes
        TransitionManager.beginDelayedTransition(weatherScreenToolbar, transition);
        weatherScreenToolbar.hideContent();
    }

    private Transition.TransitionListener navigateToSearchWhenDone() {
        return new SimpleTransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);

                // we are handing the enter transitions ourselves
                // this line overrides that
                getActivity().overridePendingTransition(0, 0);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        // when you are back from the SearchActivity animate the 'shrinking' of the Toolbar and fade its contents back in
        fadeToolbarIn();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterNetworkReceiver(receiver);
    }

    private void fadeToolbarIn() {
        weatherScreenToolbar.showContent();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            transitionToSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mainToolbar) {
            transitionToSearch();
        }
        if (v.getId() == R.id.btnRefreshServerData) {
            showTopLoadingShimmerViews();
            showBottomLoadingShimmerViews();
            SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.DATA_IS_BEING_UPDATED, true);

            new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute();
        }
        if (v.getId() == R.id.btnShowCurrentLocationData) {
            boolean isDataAlreadyLoaded = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.IS_DATA_ALREADY_LOADED, false);
            boolean settingsChangedAndNeedUpdate = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE);

            boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());
            NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isConnectedToInternet);

            showTopViewWeatherLastUpdatedDateWrapperLayout();

            if (!isDataAlreadyLoaded) {
                showTopLoadingShimmerViews();
                showBottomLoadingShimmerViews();
                new Handler().postDelayed(() -> new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute(), 500);
            } else {
                showTopLoadingShimmerViews();
                showBottomLoadingShimmerViews();

                if (isConnectedToInternet && settingsChangedAndNeedUpdate) {
                    new Handler().postDelayed(() -> new VerifyIfWeatherDataIsStoredAsyncTask(getActivity(), this).execute(), 500);
                } else {
                    new Handler().postDelayed(() -> new RetrieveCurrentWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute(), 500);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    @Override
    public void onLocationTaskDone(Location currentLocation) {
        if (currentLocation != null) {
            mLastKnownLocation = currentLocation;

            new Handler().postDelayed(this::getCurrentWeatherByGeoCoords, 500);
        }
    }

    private void getCurrentWeatherByGeoCoords() {
        Map<String, Object> requestParamsByGeoCoords = ApiUtils.initRequestParamsByGeoCoords(getActivity(), mLastKnownLocation);
        ApiUtils.getCurrentWeatherByGeoCoords(getActivity(), requestParamsByGeoCoords, this);
    }

    private void get5DayForecastByGeoCoords() {
        Map<String, Object> requestParamsByGeoCoords = ApiUtils.initRequestParamsByGeoCoords(getActivity(), mLastKnownLocation);
        ApiUtils.get5DayForecastByGeoCoords(getActivity(), requestParamsByGeoCoords, this);
    }

    @Override
    public void onRetrofitGetCurrentWeatherTaskDone(CurrentWeather currentWeather) {
        CurrentWeatherDB currentWeatherDBObject = DatabaseUtils.initCurrentWeatherDBObject(currentWeather);
        new StoreCurrentWeatherDataAsyncTask(getActivity(), currentWeatherDBObject).execute();

        get5DayForecastByGeoCoords();
    }

    @Override
    public void onRetrofitGetCurrentWeatherTaskFailure(int errorCode) {}

    @Override
    public void onRetrofitGet5DayForecastTaskDone(DailyForecast dailyForecast) {
        List<DailyForecastDB> dailyForecastDBListObject = DatabaseUtils.initDailyForecastDBListObject(dailyForecast);
        new StoreDailyForecastDataAsyncTask(getActivity(), dailyForecastDBListObject, this).execute();
    }

    private void fillTopContentViewsWithData(CurrentWeatherDB currentWeatherDB) {
        boolean settingsChangedAndNeedUpdate = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE);

        if (!settingsChangedAndNeedUpdate) {
            SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.CURRENT_APP_TEMPERATURE_UNIT_WITHOUT_UPDATE, "");
        }

        String appTempUnitWithoutUpdate = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_APP_TEMPERATURE_UNIT_WITHOUT_UPDATE, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);
        String appTempUnit = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_TEMPERATURE_UNIT, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);

        String cityDetails = String.format("%s, %s", currentWeatherDB.getCityName(), currentWeatherDB.getCountry());
        binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherCityDetails.setText(cityDetails);
        binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherDescription.setText(currentWeatherDB.getDescription());

        Glide.with(binding.getRoot()).load(currentWeatherDB.getIconUri()).placeholder(R.mipmap.icon_placeholder).into(binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherIcon);

        String currentTemp = String.format("%s°", currentWeatherDB.getCurrentTemp());
        binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherTemp.setText(currentTemp);

        if (appTempUnitWithoutUpdate.isEmpty()) {
            binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherTempUnit.setText(appTempUnit.equals(SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS) ?
                    SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS : SharedPrefsValues.APP_TEMPERATURE_UNIT.FAHRENHEIT);
        } else {
            binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherTempUnit.setText(appTempUnitWithoutUpdate.equals(SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS) ?
                    SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS : SharedPrefsValues.APP_TEMPERATURE_UNIT.FAHRENHEIT);

            SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_APP_TEMPERATURE_UNIT_WITHOUT_UPDATE, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);
        }

        String currentTempMax = String.format("%s°", currentWeatherDB.getCurrentTempMax());
        binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherTempMax.setText(currentTempMax);
        String currentTempMin = String.format("%s°", currentWeatherDB.getCurrentTempMin());
        binding.fragmentWeatherScreenLoadedDataTopContent.currentWeatherTempMin.setText(currentTempMin);

        hideTopLoadingShimmerViews();

        if (SharedPrefsManager.getInstance(getActivity()).readSearchedCurrentWeatherDBObject(SharedPrefsKeys.SEARCHED_CURRENT_WEATHER_DB_OBJECT) != null) {
            SharedPrefsManager.getInstance(getActivity()).saveSearchedCurrentWeatherDBObject(SharedPrefsKeys.SEARCHED_CURRENT_WEATHER_DB_OBJECT, null);
        }
    }

    private void fillBottomContentViewsWithData(List<DailyForecastDB> dailyForecastDBList) {
        binding.fragmentWeatherScreenLoadedDataBottomContent.dailyForecastRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.fragmentWeatherScreenLoadedDataBottomContent.dailyForecastRecyclerView.setLayoutManager(layoutManager);

        DailyForecastAdapter adapter = new DailyForecastAdapter(getActivity(), dailyForecastDBList);
        binding.fragmentWeatherScreenLoadedDataBottomContent.dailyForecastRecyclerView.setAdapter(adapter);

        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.IS_DATA_ALREADY_LOADED, true);

        boolean dataIsBeingUpdated = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.DATA_IS_BEING_UPDATED, false);

        if (dataIsBeingUpdated) {
            SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.DATA_IS_BEING_UPDATED, false);
        }

        hideBottomLoadingShimmerViews();

        if (SharedPrefsManager.getInstance(getActivity()).readSearchedDailyForecastDBListObject(SharedPrefsKeys.SEARCHED_DAILY_FORECAST_DB_LIST_OBJECT) != null) {
            SharedPrefsManager.getInstance(getActivity()).saveSearchedDailyForecastDBListObject(SharedPrefsKeys.SEARCHED_DAILY_FORECAST_DB_LIST_OBJECT, null);
        }
    }

    private void fillLastUpdatedDateView() {
        Date date = new Date();

        String dateFormatted = DateUtils.getLastUpdatedDateFormattedToString(date.getTime());
        binding.weatherLastUpdatedDateTextView.setText(dateFormatted);

        saveLastUpdatedDateTimestampInSharedPrefs(date.getTime());
    }

    private void saveLastUpdatedDateTimestampInSharedPrefs(long time) {
        SharedPrefsManager.getInstance(getActivity()).saveLong(SharedPrefsKeys.LAST_UPDATED_DATE_TIMESTAMP, time);
    }

    @Override
    public void onVerifyIfWeatherDataIsStoredTaskDone(boolean isWeatherDataStored) {
        Log.d(this.getClass().getSimpleName(), "IS WEATHER DATA STORED? " + isWeatherDataStored);

        boolean dataIsBeingUpdated = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.DATA_IS_BEING_UPDATED, false);
        boolean settingsChangedAndNeedUpdate = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE, false);

        if (!isWeatherDataStored) {
            boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());

            if (!isConnectedToInternet) {
                if (dataIsBeingUpdated) {
                    new Handler().postDelayed(() -> {
                        Toast signupErrorToast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, getResources().getString(R.string.update_server_data_no_internet_connection_message));
                        signupErrorToast.show();

                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.DATA_IS_BEING_UPDATED, false);
                    }, 1000);
                }

                new Handler().postDelayed(() -> {
                    hideTopLoadingShimmerViews();
                    hideBottomLoadingShimmerViews();
                    showNetworkNotConnectedView();
                }, 1500);

                return;
            }

            SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.IS_DATA_ALREADY_LOADED, false);

            mLocationPermissionGranted = PermissionUtils.getLocationPermission(getActivity());
            LocationUtils.getDeviceLocation(getActivity(), mLocationPermissionGranted, this);
        } else {
            boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());

            if (!isConnectedToInternet) {
                if (dataIsBeingUpdated) {
                    new Handler().postDelayed(() -> {
                        Toast signupErrorToast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, getResources().getString(R.string.update_server_data_no_internet_connection_message));
                        signupErrorToast.show();

                        new RetrieveCurrentWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute();

                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.DATA_IS_BEING_UPDATED, false);
                    }, 1000);
                } else {
                    new Handler().postDelayed(() -> new RetrieveCurrentWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute(), 1000);
                }

                return;
            }

            if (dataIsBeingUpdated || settingsChangedAndNeedUpdate) {
                new ClearWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute();
            } else {
                new Handler().postDelayed(() -> new RetrieveCurrentWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute(), 1000);
            }
        }
    }

    @Override
    public void onStoreDailyForecastDataFromLocalDBTaskDone(boolean isDataStoredSuccessfully) {
        if (isDataStoredSuccessfully) {
            fillLastUpdatedDateView();

            new RetrieveCurrentWeatherDataFromLocalDBAsyncTask(getActivity(), this).execute();
        }
    }

    @Override
    public void onRetrieveCurrentWeatherDataFromLocalDBTaskDone(CurrentWeatherDB currentWeatherDB) {
        if (currentWeatherDB != null) {
            new RetrieveDailyForecastDataFromLocalDBAsyncTask(getActivity(), this).execute();
            fillTopContentViewsWithData(currentWeatherDB);
        }
    }

    @Override
    public void onRetrieveDailyForecastDataFromLocalDBTaskDone(List<DailyForecastDB> dailyForecastDBList) {
        if (dailyForecastDBList != null) {
            fillBottomContentViewsWithData(dailyForecastDBList);
        }
    }

    @Override
    public void onClearWeatherDataFromLocalDBTaskDone(boolean weatherDataClearedSuccessfully) {
        if (weatherDataClearedSuccessfully) {
            SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.IS_DATA_ALREADY_LOADED, false);

            boolean settingsChangedAndNeedUpdate = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE);

            if (settingsChangedAndNeedUpdate) {
                SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE, false);
            }

            mLocationPermissionGranted = PermissionUtils.getLocationPermission(getActivity());
            LocationUtils.getDeviceLocation(getActivity(), mLocationPermissionGranted, this);
        }
    }
}