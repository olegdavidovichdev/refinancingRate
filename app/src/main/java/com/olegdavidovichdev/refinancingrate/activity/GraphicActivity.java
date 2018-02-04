package com.olegdavidovichdev.refinancingrate.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.olegdavidovichdev.refinancingrate.OnDataPointTapListenerImpl;
import com.olegdavidovichdev.refinancingrate.R;
import com.olegdavidovichdev.refinancingrate.SnackListener;
import com.olegdavidovichdev.refinancingrate.model.RefinancingRate;
import com.olegdavidovichdev.refinancingrate.network.CheckNetwork;
import com.olegdavidovichdev.refinancingrate.rest.ApiClient;
import com.olegdavidovichdev.refinancingrate.rest.ApiInterface;
import com.olegdavidovichdev.refinancingrate.rest.LoadingListener;
import com.olegdavidovichdev.refinancingrate.rest.RefinancingRateCallback;
import com.olegdavidovichdev.refinancingrate.rest.RequestListener;
import com.olegdavidovichdev.refinancingrate.utils.ConvertErrorListener;
import com.olegdavidovichdev.refinancingrate.utils.DateFormatter;
import com.olegdavidovichdev.refinancingrate.utils.DownloadDialog;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class GraphicActivity extends AppCompatActivity implements LoadingListener, RequestListener,
        ConvertErrorListener, SnackListener, View.OnClickListener {

    private static ApiInterface apiService;

    @BindView(R.id.graph) GraphView graph;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;

    private DownloadDialog dialog;
    private RefinancingRateCallback refinancingRateCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_graphic);
        ButterKnife.bind(this);

        graph.setVisibility(View.INVISIBLE);

        if (!CheckNetwork.isInternetAvailable(GraphicActivity.this)) {
            Toast.makeText(GraphicActivity.this, getResources().getString(R.string.disable_internet), Toast.LENGTH_SHORT).show();
        } else {
            dialog = new DownloadDialog(this, R.style.ProgressDialogTheme);
            onLoad();
        }
    }

    @Override
    public void onLoad() {
        if (apiService == null) {
            apiService = ApiClient.getApiService();
        }

        Call<List<RefinancingRate>> call = apiService.getAll();

        if (refinancingRateCallback == null) {
            refinancingRateCallback = new RefinancingRateCallback(this);
        }

        dialog.show();
        call.enqueue(refinancingRateCallback);
    }

    @Override
    public void onSuccess(List<RefinancingRate> listOfRates) {
        if (dialog != null) {
            dialog.hide();
        }

        DataPoint[] array = new DataPoint[listOfRates.size()];
        for (int i = 0; i < listOfRates.size(); i++) {
            RefinancingRate r = listOfRates.get(i);

            Date date = DateFormatter.convertToDate(r.getDate(), this);

            double value = r.getValue();

            DataPoint dataPoint = new DataPoint(date, value);
            array[i] = dataPoint;
        }

        setUpGraph(array);
    }

    private void setUpGraph(DataPoint[] array) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(array);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphicActivity.this));

        series.setDataPointsRadius(8);
        series.setDrawDataPoints(true);
        series.setAnimated(true);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(50, 200, 200, 200));

        Paint paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        series.setCustomPaint(paint);

        series.setOnDataPointTapListener(new OnDataPointTapListenerImpl(this));

        configGridLabelRenderer();
        configViewport();

        graph.getViewport().setMinX(array[0].getX());
        graph.getViewport().setMaxX(array[array.length - 1].getX());
        graph.addSeries(series);
        graph.setVisibility(View.VISIBLE);
    }

    private void configGridLabelRenderer() {
        graph.getGridLabelRenderer().setNumVerticalLabels(6);
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);
        graph.getGridLabelRenderer().setTextSize(20);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(30);
        graph.getGridLabelRenderer().setLabelHorizontalHeight(15);
        graph.getGridLabelRenderer().setLabelVerticalWidth(45);
        graph.getGridLabelRenderer().setLabelsSpace(5);
        graph.getGridLabelRenderer().setPadding(15);
        graph.getGridLabelRenderer().setVerticalLabelsAlign(Paint.Align.LEFT);
        graph.getGridLabelRenderer().setGridColor(Color.LTGRAY);

        // label x and y color
        graph.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(GraphicActivity.this, R.color.colorAccent));
        graph.getGridLabelRenderer().setVerticalLabelsColor(ContextCompat.getColor(GraphicActivity.this, R.color.colorAccent));
    }

    private void configViewport() {
        graph.getViewport().setXAxisBoundsManual(false);
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setDrawBorder(true);
        graph.getViewport().setBorderColor(Color.YELLOW);

        // width viewport
        Paint viewPortPaint = new Paint();
        viewPortPaint.setStrokeWidth(3);
        viewPortPaint.setColor(Color.argb(255, 255, 255, 255));
        graph.getViewport().setBorderPaint(viewPortPaint);
    }

    @Override
    public void onFailure(String message) {
        if (dialog != null) dialog.hide();
        if (CheckNetwork.isInternetAvailable(GraphicActivity.this)) {
            Toast.makeText(GraphicActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConvertError() {
        Toast.makeText(this, getString(R.string.convert_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSnackbarShow(String date, String rate) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.snackbar, date, rate),
                Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", this);

        setCustomSnackbarBackground(snackbar);

        snackbar.show();
    }

    private void setCustomSnackbarBackground(Snackbar snackbar) {
        View snackbarBackground = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbarBackground.setBackground(getResources().getDrawable(R.drawable.gradient_background_snackbar, null));
        } else {
            snackbarBackground.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_background));
        }
    }

    @Override
    public void onClick(View view) {
        // ignored
    }
}
