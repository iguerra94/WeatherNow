package com.iguerra94.weathernow.utils;

public class StringUtils {
    public static String getIconIdColoured(String iconId) {
        return iconId.substring(0,iconId.length()-1) + "d";
    }
}