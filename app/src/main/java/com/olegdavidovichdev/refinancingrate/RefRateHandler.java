package com.olegdavidovichdev.refinancingrate;

import android.os.Handler;
import android.os.Message;


public class RefRateHandler extends Handler {

    private UpdateViewListener updateViewListener;

    public RefRateHandler(UpdateViewListener updateViewListener) {
        this.updateViewListener = updateViewListener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (updateViewListener != null) {
            updateViewListener.onUpdateView((String) msg.obj);
        }
    }

}