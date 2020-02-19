package com.iguerra94.weathernow.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.iguerra94.weathernow.R;

public class ToastUtils {

    public static Toast createCustomToast(FragmentActivity activity, int LENGTH, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, activity.findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.custom_toast_text_view);
        text.setText(message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(LENGTH);
        toast.setView(layout);

        return toast;
    }

}
