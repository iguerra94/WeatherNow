package com.iguerra94.weathernow.utils;

import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class PopUpMenuUtils {

    public static List<Object> createPopUpMenu(FragmentActivity activity, View view, int menuViewId) {
        MenuBuilder menuBuilder = new MenuBuilder(activity);

        MenuInflater inflater = new MenuInflater(activity);
        inflater.inflate(menuViewId, menuBuilder);

        MenuPopupHelper optionsMenu = new MenuPopupHelper(activity, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);

        List<Object> objects = new ArrayList<>();
        objects.add(menuBuilder);
        objects.add(optionsMenu);

        return objects;
    }
}
