package com.iguerra94.weathernow.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;

public class DialogRegisteringUser implements IGenericDialog {

    private static DialogRegisteringUser instance;
    private static Dialog dialog;

    private DialogRegisteringUser() {
    }

    static DialogRegisteringUser getInstance() {
        if (instance == null) {
            instance = new DialogRegisteringUser();
        }
        return instance;
    }

    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public Dialog create(Context context, int viewId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();

        builder.setView(inflater.inflate(viewId, null));

        dialog = builder.create();

        return dialog;
    }

}