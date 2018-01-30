package com.olegdavidovichdev.refinancingrate.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.olegdavidovichdev.refinancingrate.R;


public class DownloadDialog extends ProgressDialog {

    public DownloadDialog(Context context, int theme) {
        super(context, theme);

        setMessage(context.getResources().getString(R.string.dialog_message));
        setIndeterminate(true);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);

        show();
    }
}
