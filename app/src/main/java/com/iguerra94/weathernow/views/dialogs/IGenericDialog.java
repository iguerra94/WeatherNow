package com.iguerra94.weathernow.views.dialogs;

import android.app.Dialog;
import android.content.Context;

public interface IGenericDialog {
    Dialog create(Context context, int viewId);
    Dialog getDialog();
}