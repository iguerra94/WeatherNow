package com.iguerra94.weathernow.views.main_screens.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.db.entities.DailyForecastDB;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder> {

    private final Context mContext;
    private List<DailyForecastDB> dailyForecastDBList;

    public DailyForecastAdapter(Context context, List<DailyForecastDB> dailyForecastDBList) {
        this.mContext = context;
        this.dailyForecastDBList = dailyForecastDBList;
    }

    @NonNull
    @Override
    public DailyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_forecast_item, parent, false);
        return new DailyForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DailyForecastViewHolder holder, final int position) {
        holder.setDayName(dailyForecastDBList.get(position).getDt());
        holder.setDate(dailyForecastDBList.get(position).getDt());
        holder.setIcon(dailyForecastDBList.get(position).getIconUri());
        holder.setDescription(dailyForecastDBList.get(position).getDescription());
        holder.setTempMin(dailyForecastDBList.get(position).getTempMin());
        holder.setTempMax(dailyForecastDBList.get(position).getTempMax());
    }

    @Override
    public int getItemCount() {
        return dailyForecastDBList.size();
    }

    class DailyForecastViewHolder extends RecyclerView.ViewHolder {
        TextView mDayNameTextView;
        TextView mDateTextView;
        ImageView mIconImageView;
        TextView mDescriptionTextView;
        TextView mTempMinTextView;
        TextView mTempMaxTextView;

        DailyForecastViewHolder(View itemView) {
            super(itemView);

            mDayNameTextView = itemView.findViewById(R.id.day_forecast_day_name);
            mDateTextView = itemView.findViewById(R.id.day_forecast_date);
            mIconImageView = itemView.findViewById(R.id.day_forecast_weather_icon);
            mDescriptionTextView = itemView.findViewById(R.id.day_forecast_weather_description);
            mTempMinTextView = itemView.findViewById(R.id.day_forecast_weather_temp_min);
            mTempMaxTextView = itemView.findViewById(R.id.day_forecast_weather_temp_max);
        }

        void setDayName(Long dateTimestamp) {
            Date date = new Date(dateTimestamp*1000L);

            boolean settingsChangedAndNeedUpdate = SharedPrefsManager.getInstance(mContext).readBoolean(SharedPrefsKeys.SETTINGS_CHANGED_AND_NEED_UPDATE);

            if (!settingsChangedAndNeedUpdate) {
                SharedPrefsManager.getInstance(mContext).saveString(SharedPrefsKeys.CURRENT_APP_LANGUAGE_LOCALE_WITHOUT_UPDATE, "");
            }

            String appLanguageLocaleWithoutUpdate = SharedPrefsManager.getInstance(mContext).readString(SharedPrefsKeys.CURRENT_APP_LANGUAGE_LOCALE_WITHOUT_UPDATE);
            String appLanguageLocale = SharedPrefsManager.getInstance(mContext).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);

            SimpleDateFormat dateFormatted;

            if (appLanguageLocaleWithoutUpdate.isEmpty()) {
                // format of the date
                dateFormatted = new SimpleDateFormat("EEE", appLanguageLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                        new Locale( "es" , "AR" ) : Locale.ENGLISH);
            } else {
                // format of the date
                dateFormatted = new SimpleDateFormat("EEE", appLanguageLocaleWithoutUpdate.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH) ?
                        new Locale( "es" , "AR" ) : Locale.ENGLISH);
            }

            String dayName = dateFormatted.format(date.getTime());
            dayName = dayName.substring(0,1).toUpperCase() + dayName.substring(1).toLowerCase();

            mDayNameTextView.setText(dayName);
        }

        void setDate(Long dateTimestamp) {
            Date date = new Date(dateTimestamp*1000L);

            // format of the date
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");

            String dateFormatted = formatter.format(date.getTime());
            mDateTextView.setText(dateFormatted);
        }

        void setIcon(String iconUri) {
            Glide.with(mContext).load(iconUri).placeholder(R.mipmap.icon_placeholder).into(mIconImageView);
        }

        void setDescription(String description) {
            mDescriptionTextView.setText(description);
        }

        void setTempMin(Long temp) {
            String tempMin = String.format("%s°", temp);
            mTempMinTextView.setText(tempMin);
        }

        void setTempMax(Long temp) {
            String tempMax = String.format("%s°", temp);
            mTempMaxTextView.setText(tempMax);
        }
    }
}