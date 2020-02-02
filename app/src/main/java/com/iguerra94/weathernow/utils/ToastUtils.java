package com.iguerra94.weathernow.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.iguerra94.weathernow.R;

public class ToastUtils {

    public static Toast createCustomToast(FragmentActivity activity, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, activity.findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.custom_toast_text_view);
        text.setText(message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        return toast;
    }

}
