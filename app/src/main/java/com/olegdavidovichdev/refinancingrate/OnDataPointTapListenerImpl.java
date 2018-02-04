package com.olegdavidovichdev.refinancingrate;

import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.olegdavidovichdev.refinancingrate.entity.LocaleBY;
import com.olegdavidovichdev.refinancingrate.utils.DateFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class OnDataPointTapListenerImpl implements OnDataPointTapListener {

    private SnackListener snackListener;

    public OnDataPointTapListenerImpl(SnackListener snackListener) {
        this.snackListener = snackListener;
    }

    @Override
    public void onTap(Series series, DataPointInterface dataPoint) {
        if (snackListener != null) {
            double temp = dataPoint.getX();
            String date = DateFormatter.format("dd-MM-yyyy", (long) temp);

            DecimalFormatSymbols dfs = new DecimalFormatSymbols(LocaleBY.getLocaleBY());
            dfs.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat(".##", dfs);
            String formatRate = df.format(dataPoint.getY());

            snackListener.onSnackbarShow(date, formatRate);
        }
    }

}