package com.jevon.smarthome.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.jevon.smarthome.R;


public class ProgressDialog {

    private AlertDialog dialog;
    private AlertDialog.Builder mBuilder;

    public ProgressDialog(Context context) {
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(R.layout.layout_loadingdialog);

    }

    public void show() {
        dialog = mBuilder.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
