package com.example.refinancingrate.activity;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refinancingrate.DownloadDialog;
import com.example.refinancingrate.R;
import com.example.refinancingrate.model.RefinancingRate;
import com.example.refinancingrate.network.CheckNetwork;
import com.example.refinancingrate.rest.ApiClient;
import com.example.refinancingrate.rest.ApiInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";

    private static ApiInterface apiService;

    private static String currentDateString;
    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private TextView refinancingRate;
    private TextView attention;
    private TextView rateDescription;

    private DownloadDialog dialog;

    private double counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);


        refinancingRate = (TextView) findViewById(R.id.refinancingRate);
        attention = (TextView) findViewById(R.id.attention);
        rateDescription = (TextView) findViewById(R.id.rateDescription);

        attention.setVisibility(View.INVISIBLE);
        rateDescription.setVisibility(View.INVISIBLE);

        getCurrentDateString();

        setToolbarTitle(currentDateString);

        if (!CheckNetwork.isInternetAvailable(MainActivity.this))
            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_disable_internet), Toast.LENGTH_SHORT).show();
        else
            dialog = new DownloadDialog(this, R.style.ProgressDialogTheme);


        apiService = ApiClient.getClient().create(ApiInterface.class);


        Call<List<RefinancingRate>> call = apiService.getRefinancingRateOnDay(currentDateString);
        call.enqueue(new Callback<List<RefinancingRate>>() {
            @Override
            public void onResponse(Call<List<RefinancingRate>> call, Response<List<RefinancingRate>> response) {

                Log.d(TAG, "Сегодня: " + response.body().get(0).getDate()
                        + "; Значение ставки = " + response.body().get(0).getValue());

                RefinancingRate r = response.body().get(0);

                dialog.hide();
                attention.setVisibility(View.VISIBLE);
                rateDescription.setVisibility(View.VISIBLE);

                // rate
                setCounterValue(r.getValue());
                setAnimationRate();

                // attention
                String date = r.getDate().substring(0, 10);
                attention.setText(getResources().getString(R.string.attention) + " " + date);
                setAnimationAttention();
            }

            @Override
            public void onFailure(Call<List<RefinancingRate>> call, Throwable t) {

                if (CheckNetwork.isInternetAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing()) dialog.hide();
                }
            }
        });
    }


    private void setCounterValue(final double val) {

        double temp = 1500/(val*4);
        final long sleepTime = (long) temp;

        Thread counterThread = new Thread() {
            @Override
            public void run() {
                try {
                     for (counter = 0; counter < val; counter+=0.25) {
                         Thread.sleep(sleepTime);

                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             refinancingRate.setText(counter + "%");
                         }
                     });
                     }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        counterThread.start();

    }

    public static ApiInterface getApiService() {
        return apiService;
    }

    private void setAnimationRate() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.refinancing_rate_animation);
        refinancingRate.startAnimation(animation);
    }

    private void setAnimationAttention() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.attention_animation);
        attention.startAnimation(animation);
    }

    private String getCurrentDateString() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        currentDateString = dateFormat.format(date);

        return currentDateString;
    }

    private void setToolbarTitle(String current) {

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(current);
    }

    private void setCurrentYearMonthDay(String current) {
        currentYear = Integer.parseInt(current.substring(0, 4));
        currentMonth = Integer.parseInt(current.substring(5, 7)) - 1;
        currentDay = Integer.parseInt(current.substring(8, 10));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.calendar) {

            DatePickerDialog.OnDateSetListener myCallback = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    Log.d(TAG, year + " " + month + " " + dayOfMonth);
                    String calendarDate = null;

                    // Check Internet Connection
                    if (!CheckNetwork.isInternetAvailable(MainActivity.this))
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_disable_internet), Toast.LENGTH_SHORT).show();
                    else {
                        dialog = new DownloadDialog(MainActivity.this, R.style.ProgressDialogTheme);

                        // Check calendar dates
                        if (year <= currentYear && month <= currentMonth && dayOfMonth > currentDay) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_wrong_date),
                                    Toast.LENGTH_LONG).show();
                            calendarDate = currentYear + "-" + currentMonth + "-" + currentDay;
                        } else calendarDate = year + "-" + (month + 1) + "-" + dayOfMonth;


                        attention.setVisibility(View.VISIBLE);
                        rateDescription.setVisibility(View.VISIBLE);
                        rateDescription.setTextSize(16);
                    }


                    if (calendarDate != null) rateDescription.setText(getResources().getString(R.string.rate_description_calendar) + " " + calendarDate + "*:");

                    Call<List<RefinancingRate>> call = apiService.getRefinancingRateOnDay(calendarDate);
                    call.enqueue(new Callback<List<RefinancingRate>>() {
                        @Override
                        public void onResponse(Call<List<RefinancingRate>> call, Response<List<RefinancingRate>> response) {
                            dialog.hide();
                           // Log.d(TAG, "Установлена: " + response.body().get(0).getDate() + "; Value = " + response.body().get(0).getValue());

                            RefinancingRate r = response.body().get(0);

                            // rate
                            setCounterValue(r.getValue());
                            setAnimationRate();

                            // attention
                            String date = r.getDate().substring(0, 10);
                            attention.setText(getResources().getString(R.string.attention) + " " + date);
                            setAnimationAttention();

                        }

                        @Override
                        public void onFailure(Call<List<RefinancingRate>> call, Throwable t) {

                            if (CheckNetwork.isInternetAvailable(MainActivity.this)) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
                                if (dialog.isShowing()) dialog.hide();
                            }

                        }
                    });

                }
            };

            setCurrentYearMonthDay(currentDateString);

            DatePickerDialog dpd = new DatePickerDialog(this, R.style.DialogTheme, myCallback, currentYear,
                    currentMonth, currentDay);

            Calendar c = new GregorianCalendar(1991, Calendar.JULY, 1);

            dpd.getDatePicker().setMinDate(c.getTime().getTime());
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());

            dpd.show();

        }
        if (item.getItemId() == R.id.graphic) {
            Intent intent = new Intent(this, GraphicActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
