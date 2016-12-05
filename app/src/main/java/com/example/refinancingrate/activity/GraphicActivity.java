package com.example.refinancingrate.activity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.example.refinancingrate.DownloadDialog;
import com.example.refinancingrate.R;
import com.example.refinancingrate.model.RefinancingRate;
import com.example.refinancingrate.network.CheckNetwork;
import com.example.refinancingrate.rest.ApiInterface;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * Created by Oleg on 23.11.2016.
 */

public class GraphicActivity extends AppCompatActivity {

    private static final String GRAPHIC_TAG = "tagGraphic";

    private CoordinatorLayout coordinatorLayout;
    private DownloadDialog dialog;
    private GraphView graph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_graphic);

        graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        if (!CheckNetwork.isInternetAvailable(GraphicActivity.this))
            Toast.makeText(GraphicActivity.this, getResources().getString(R.string.toast_disable_internet), Toast.LENGTH_SHORT).show();
        else dialog = new DownloadDialog(this, R.style.ProgressDialogTheme);

        ApiInterface apiSecondClient = MainActivity.getApiService();

        Call<List<RefinancingRate>> call = apiSecondClient.getAll("");
        call.enqueue(new Callback<List<RefinancingRate>>() {
            @Override
            public void onResponse(Call<List<RefinancingRate>> call, Response<List<RefinancingRate>> response) {
                dialog.hide();
                graph.setVisibility(View.VISIBLE);
                List<RefinancingRate> result = response.body();

                final DataPoint[] array = new DataPoint[response.body().size()];
                int i = 0;
                for (RefinancingRate r : result) {
                 //   Log.d(GRAPHIC_TAG, r.getDate() + " " + r.getValue());
                    String strDate = r.getDate();

                    Date date = parseStringToDate(strDate);
                    Log.d(GRAPHIC_TAG, "date.getTime()" + date.getTime());
                    double value = r.getValue();

                    DataPoint dataPoint = new DataPoint(date, value);
                    array[i] = dataPoint;
                    i++;
                }
              //  Log.d(GRAPHIC_TAG, Arrays.toString(array));



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

                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        double temp = dataPoint.getX();
                        long longo = (long) temp;
                        Date d = new Date(longo);
                        String s = new SimpleDateFormat("dd-MM-yyyy").format(d);

                        Log.d(GRAPHIC_TAG, dataPoint.toString() + " " + series);

                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Дата: " + s + "; Ставка рефинансирования: "
                                         + dataPoint.getY() + "%", Snackbar.LENGTH_LONG);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        });

                        View v = snackbar.getView();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            v.setBackground(getResources().getDrawable(R.drawable.gradient_background_snackbar, null));
                        } else v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_background));

                        snackbar.show();
                    }
                });

                configGridLabelFormatter();

                graph.getViewport().setMinX(array[0].getX());
                graph.getViewport().setMaxX(array[array.length - 1].getX());
                graph.addSeries(series);
            }

            @Override
            public void onFailure(Call<List<RefinancingRate>> call, Throwable t) {
                if (dialog != null) dialog.hide();
                if (CheckNetwork.isInternetAvailable(GraphicActivity.this)) {
                    Toast.makeText(GraphicActivity.this, getResources().getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configGridLabelFormatter() {

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


        graph.getViewport().setXAxisBoundsManual(false);
        graph.getViewport().setYAxisBoundsManual(false);

       // graph.getGridLabelRenderer().setHumanRounding(true);

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

    private Date parseStringToDate(String responseString) {

        int year = Integer.parseInt(responseString.substring(0, 4));
        int month = Integer.parseInt(responseString.substring(5, 7));
        int day = Integer.parseInt(responseString.substring(8, 10));
        Log.d(GRAPHIC_TAG, year + " " + month + " " + day);

        Calendar calendar = new GregorianCalendar(year, month - 1, day);

        return calendar.getTime();
    }
}
