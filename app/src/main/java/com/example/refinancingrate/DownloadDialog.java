package com.example.refinancingrate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Oleg on 24.11.2016.
 */

public class DownloadDialog extends ProgressDialog {

    public DownloadDialog(Context context, int theme) {
        super(context, theme);

        setMessage(context.getResources().getString(R.string.dialog_message));
        setIndeterminate(true);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);

        show();
    }

    public DownloadDialog(Context context, Resources.Theme theme) {
        super(context);



    }

}
