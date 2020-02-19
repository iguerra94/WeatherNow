package com.iguerra94.weathernow.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

public class DateUtils {
    public static String getLastUpdatedDateFormattedToString(long time) {
        // format of the date
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

        return formatter.format(time);
    }
}
