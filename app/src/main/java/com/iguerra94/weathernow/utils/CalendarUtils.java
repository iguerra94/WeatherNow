package com.iguerra94.weathernow.utils;

import java.util.Calendar;

public class CalendarUtils {

    private static final long ONE_MINUTE_IN_MILLIS = 60000;

    public static String getCurrentTimeMinusTwoMinutesFormattedToString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - 2 * ONE_MINUTE_IN_MILLIS);

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return String.format("%02d:%02d", hourOfDay, minutes);
    }

    public static String getCurrentTimeFormattedToString() {
        Calendar calendar = Calendar.getInstance();

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return String.format("%02d:%02d", hourOfDay, minutes);
    }
}
