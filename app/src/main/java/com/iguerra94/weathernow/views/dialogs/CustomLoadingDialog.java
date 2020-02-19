package com.iguerra94.weathernow.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.iguerra94.weathernow.R;

public class CustomLoadingDialog implements IGenericDialog {

    private static CustomLoadingDialog instance;
    private static Dialog dialog;

    private CustomLoadingDialog() {
    }

    static CustomLoadingDialog getInstance() {
        if (instance == null) {
            instance = new CustomLoadingDialog();
        }
        return instance;
    }

    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public Dialog create(Context context, int viewId, int stringResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();

        View v = inflater.inflate(viewId, null);

        TextView customLoadingDialogTextView = v.findViewById(R.id.custom_loading_dialog_text_view);
        customLoadingDialogTextView.setText(context.getResources().getString(stringResId));

        builder.setView(v);

        dialog = builder.create();

        return dialog;
    }

}