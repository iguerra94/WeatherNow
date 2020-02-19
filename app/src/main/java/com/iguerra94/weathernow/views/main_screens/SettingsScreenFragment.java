package com.iguerra94.weathernow.views.main_screens;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.FragmentSettingsScreenBinding;
import com.iguerra94.weathernow.utils.CalendarUtils;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver;
import com.iguerra94.weathernow.utils.network.NetworkUtils;
import com.iguerra94.weathernow.utils.services.WeatherNowForegroundService;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

import static com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class SettingsScreenFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    private FragmentSettingsScreenBinding binding;

    private RadioGroup radioGroupSettingsLanguageLocale;
    private RadioGroup radioGroupSettingsTempUnit;
    private EditText editTextScheduleAppNotifications;
    private Button btnSaveSettings;

    private BroadcastReceiver receiver;

    public SettingsScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_screen, container, false);

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.mainToolbarAppBarLayout);
        appBarLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));

        SimpleToolbar settingsScreenToolbar = getActivity().findViewById(R.id.mainToolbar);

        settingsScreenToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        settingsScreenToolbar.setNavigationIcon(R.drawable.ic_sort);

        settingsScreenToolbar.setTitle(getActivity().getResources().getString(R.string.menu_settings));
        settingsScreenToolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.colorBlack));

        boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());
        NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isConnectedToInternet);

        if (!isConnectedToInternet) {
            showNetworkNotConnectedMessage();
        } else {
            SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE, false);
            SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.CURRENT_APP_LANGUAGE_LOCALE_WITHOUT_UPDATE, "");
            SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.CURRENT_APP_TEMPERATURE_UNIT_WITHOUT_UPDATE, "");
        }

        receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInternetConnectionInfo(intent);
            }
        };

        registerNetworkReceiver(receiver);

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterNetworkReceiver(receiver);
    }

    private void showNetworkNotConnectedMessage() {
        binding.settingsChangedNotConnectionMessageTextView.setVisibility(View.VISIBLE);
        binding.settingsScreenNetworkStatusViewTextView.setBackgroundColor(getResources().getColor(R.color.colorRedMD700));
        binding.settingsScreenNetworkStatusViewTextView.setText(getResources().getString(R.string.app_network_status_without_connection_textview));
        binding.settingsScreenNetworkStatusViewTextView.setVisibility(View.VISIBLE);
    }

    private void showNetworkRecoveredMessage() {
        binding.settingsScreenNetworkStatusViewTextView.setBackgroundColor(getResources().getColor(R.color.colorGreenMD700));
        binding.settingsScreenNetworkStatusViewTextView.setText(getResources().getString(R.string.app_network_status_recovered_connection_textview));
    }

    private void hideNetworkRecoveredMessage() {
        binding.settingsChangedNotConnectionMessageTextView.setVisibility(View.GONE);
        binding.settingsScreenNetworkStatusViewTextView.setVisibility(View.GONE);
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
            if (!isNetworkAvailable) {
                showNetworkNotConnectedMessage();
            } else {
                String alreadyConnected =  SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.IS_CONNECTED_TO_INTERNET);

                if (!alreadyConnected.equals(SharedPrefsValues.IS_CONNECTED_TO_INTERNET.CONNECTED)) {
                    showNetworkRecoveredMessage();

                    new Handler().postDelayed(this::hideNetworkRecoveredMessage, 1700);
                } else {
                    receiver = new BroadcastReceiver() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            updateInternetConnectionInfo(intent);
                        }
                    };
                }
            }

            NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isNetworkAvailable);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        radioGroupSettingsLanguageLocale = getActivity().findViewById(R.id.radioGroupSettingsLanguageLocale);
        radioGroupSettingsTempUnit = getActivity().findViewById(R.id.radioGroupSettingsTempUnit);

        String APP_LANGUAGE_LOCALE = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        int currentLocaleRadioButtonId = (APP_LANGUAGE_LOCALE.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? R.id.settings_locale_spanish : R.id.settings_locale_english;

        String APP_TEMPERATURE_UNIT = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_TEMPERATURE_UNIT, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);
        int currentTempUnitRadioButtonId = (APP_TEMPERATURE_UNIT.equals(SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS)) ? R.id.settings_weather_unit_celsius : R.id.settings_weather_unit_fahrenheit;

        radioGroupSettingsLanguageLocale.check(currentLocaleRadioButtonId);
        radioGroupSettingsTempUnit.check(currentTempUnitRadioButtonId);

        editTextScheduleAppNotifications = getActivity().findViewById(R.id.settings_screen_schedule_app_notifications_edit_text);
        editTextScheduleAppNotifications.setOnClickListener(this);
        editTextScheduleAppNotifications.setOnFocusChangeListener(this);

        Switch switchEnableAppNotifications = getActivity().findViewById(R.id.settings_screen_enable_app_notifications_switch_field);
        switchEnableAppNotifications.setOnCheckedChangeListener(this);

        String currentTimeMinusTwoMinutes = CalendarUtils.getCurrentTimeMinusTwoMinutesFormattedToString();
        String APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME, currentTimeMinusTwoMinutes);

        editTextScheduleAppNotifications.setText(APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME);
        editTextScheduleAppNotifications.setEnabled(switchEnableAppNotifications.isChecked());

        boolean APP_DAILY_NOTIFICATIONS_ENABLED = SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_ENABLED, false);
        switchEnableAppNotifications.setChecked(APP_DAILY_NOTIFICATIONS_ENABLED);

        btnSaveSettings = getActivity().findViewById(R.id.btnSaveSettings);
        btnSaveSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveSettings:
                int languageLocaleRadioButtonId = radioGroupSettingsLanguageLocale.getCheckedRadioButtonId();

                SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.CURRENT_APP_LANGUAGE_LOCALE_WITHOUT_UPDATE,
                        SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE));
                updateLanguageLocaleSettings(languageLocaleRadioButtonId);

                int tempUnitRadioButtonId = radioGroupSettingsTempUnit.getCheckedRadioButtonId();
                SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.CURRENT_APP_TEMPERATURE_UNIT_WITHOUT_UPDATE,
                        SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_TEMPERATURE_UNIT));
                updateTempUnitSettings(tempUnitRadioButtonId);

                boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());
                NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isConnectedToInternet);

                boolean appLanguageLocaleChanged = !SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_APP_LANGUAGE_LOCALE_WITHOUT_UPDATE).equals(SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE));
                boolean appTempUnitChanged = !SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_APP_TEMPERATURE_UNIT_WITHOUT_UPDATE).equals(SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_TEMPERATURE_UNIT));

                if (!isConnectedToInternet || appLanguageLocaleChanged || appTempUnitChanged) {
                    SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE, true);
                }

                restartAppWithSettingsUpdated();

                break;
            case R.id.settings_screen_schedule_app_notifications_edit_text:
                String currentTimeMinusTwoMinutes = CalendarUtils.getCurrentTimeMinusTwoMinutesFormattedToString();
                String APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME, currentTimeMinusTwoMinutes);

                int hourOfDay = Integer.parseInt(APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME.split(":")[0]);
                int minutes = Integer.parseInt(APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME.split(":")[1]);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, updatedHourOfDay, updatedMinutes) -> {
                    AlertDialog dialogUpdatingNotificationTime = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(getActivity(), R.layout.custom_loading_dialog, R.string.dialog_updating_notification_time_message_text);
                    dialogUpdatingNotificationTime.setCancelable(false);
                    dialogUpdatingNotificationTime.show();

                    Handler h1 = new Handler();
                    h1.postDelayed(() -> {
                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, false);
                        WeatherNowForegroundService.stopForegroundServiceIntent(getActivity());
                    }, 500);

                    Handler h2 = new Handler();
                    h2.postDelayed(() -> {
                        String updatedAppNotificationScheduleTime = String.format("%02d:%02d", updatedHourOfDay, updatedMinutes);
                        SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME, updatedAppNotificationScheduleTime);

                        editTextScheduleAppNotifications.setText(updatedAppNotificationScheduleTime);

                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, true);
                        WeatherNowForegroundService.startForegroundServiceIntent(getActivity());

                        editTextScheduleAppNotifications.clearFocus();
                        btnSaveSettings.requestFocus();

                        dialogUpdatingNotificationTime.dismiss();

                        Toast toast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, getActivity().getResources().getString(R.string.app_notifications_schedule_time_updated_message));
                        toast.show();
                    }, 2000);

                }, hourOfDay, minutes, true);

                timePickerDialog.show();

                break;
            default:
                break;
        }
    }

    private void updateTempUnitSettings(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.settings_weather_unit_celsius:
                //Change Application temperature unit
                SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.APP_TEMPERATURE_UNIT, SharedPrefsValues.APP_TEMPERATURE_UNIT.CELSIUS);
                break;
            case R.id.settings_weather_unit_fahrenheit:
                //Change Application temperature unit
                SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.APP_TEMPERATURE_UNIT, SharedPrefsValues.APP_TEMPERATURE_UNIT.FAHRENHEIT);
                break;
            default:
                break;
        }
    }

    private void updateLanguageLocaleSettings(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.settings_locale_spanish:
                //Change Application level locale
                LocaleHelper.setLocale(getActivity(), SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH);
                break;
            case R.id.settings_locale_english:
                //Change Application level locale
                LocaleHelper.setLocale(getActivity(), SharedPrefsValues.APP_LANGUAGE_LOCALE.ENGLISH);
               break;
            default:
                break;
        }
    }

    private void restartAppWithSettingsUpdated() {
        getActivity().finish();

        Intent i1 = new Intent(getActivity(), MainScreenActivity.class);
        i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(i1);
    }

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.settings_screen_enable_app_notifications_switch_field) {
            SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_ENABLED, isChecked);

            if (isChecked) {
                Log.d(TAG_FOREGROUND_SERVICE, "onChecked() true");

                if (!SharedPrefsManager.getInstance(getActivity()).readBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED)) {
                    AlertDialog dialogEnablingNotifications = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(getActivity(), R.layout.custom_loading_dialog, R.string.dialog_enabling_notifications_message_text);
                    dialogEnablingNotifications.setCancelable(false);
                    dialogEnablingNotifications.show();

                    Handler h1 = new Handler();
                    h1.postDelayed(() -> {
                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, false);
                        WeatherNowForegroundService.stopForegroundServiceIntent(getActivity());
                    }, 500);

                    Handler h2 = new Handler();
                    h2.postDelayed(() -> {
                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, true);
                        WeatherNowForegroundService.startForegroundServiceIntent(getActivity());

                        dialogEnablingNotifications.dismiss();

                        Toast toast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, getActivity().getResources().getString(R.string.app_notifications_enabled_message));
                        toast.show();
                    }, 2000);
                }
            } else {
                Log.d(TAG_FOREGROUND_SERVICE, "onChecked() false");

                AlertDialog dialogDisablingNotifications = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(getActivity(), R.layout.custom_loading_dialog, R.string.dialog_disabling_notifications_message_text);
                dialogDisablingNotifications.setCancelable(false);
                dialogDisablingNotifications.show();

                Handler h1 = new Handler();
                h1.postDelayed(() -> {
                    SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, false);
                    WeatherNowForegroundService.stopForegroundServiceIntent(getActivity());

                    dialogDisablingNotifications.dismiss();

                    Toast toast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, getActivity().getResources().getString(R.string.app_notifications_disabled_message));
                    toast.show();
                }, 2000);
            }

            if (editTextScheduleAppNotifications != null) editTextScheduleAppNotifications.setEnabled(isChecked);
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocused) {
        if (view.getId() == R.id.settings_screen_schedule_app_notifications_edit_text) {
            if (isFocused) {
                String currentTimeMinusTwoMinutes = CalendarUtils.getCurrentTimeMinusTwoMinutesFormattedToString();
                String APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME, currentTimeMinusTwoMinutes);

                int hourOfDay = Integer.parseInt(APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME.split(":")[0]);
                int minutes = Integer.parseInt(APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME.split(":")[1]);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, updatedHourOfDay, updatedMinutes) -> {
                    AlertDialog dialogUpdatingNotificationTime = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(getActivity(), R.layout.custom_loading_dialog, R.string.dialog_updating_notification_time_message_text);
                    dialogUpdatingNotificationTime.setCancelable(false);
                    dialogUpdatingNotificationTime.show();

                    Handler h1 = new Handler();
                    h1.postDelayed(() -> {
                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, false);
                        WeatherNowForegroundService.stopForegroundServiceIntent(getActivity());
                    }, 500);

                    Handler h2 = new Handler();
                    h2.postDelayed(() -> {
                        String updatedAppNotificationScheduleTime = String.format("%02d:%02d", updatedHourOfDay, updatedMinutes);
                        SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.APP_DAILY_NOTIFICATIONS_SCHEDULE_TIME, updatedAppNotificationScheduleTime);

                        editTextScheduleAppNotifications.setText(updatedAppNotificationScheduleTime);

                        SharedPrefsManager.getInstance(getActivity()).saveBoolean(SharedPrefsKeys.NOTIFICATIONS_SERVICE_STARTED, true);
                        WeatherNowForegroundService.startForegroundServiceIntent(getActivity());

                        editTextScheduleAppNotifications.clearFocus();
                        btnSaveSettings.requestFocus();

                        dialogUpdatingNotificationTime.dismiss();

                        Toast toast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, getActivity().getResources().getString(R.string.app_notifications_schedule_time_updated_message));
                        toast.show();
                    }, 2000);

                }, hourOfDay, minutes, true);

                timePickerDialog.show();
            }
        }
    }

}