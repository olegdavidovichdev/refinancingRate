package com.olegdavidovichdev.refinancingrate.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.olegdavidovichdev.refinancingrate.R;
import com.olegdavidovichdev.refinancingrate.RefRateHandler;
import com.olegdavidovichdev.refinancingrate.UpdateViewListener;
import com.olegdavidovichdev.refinancingrate.entity.TimeRepository;
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
import com.olegdavidovichdev.refinancingrate.utils.OnDateSetListenerImpl;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements LoadingListener,
        RequestListener, ConvertErrorListener, UpdateViewListener {

    private static ApiInterface apiService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.refinancingRate) TextView refinancingRate;
    @BindView(R.id.attention) TextView attention;
    @BindView(R.id.rateDescription) TextView rateDescription;

    private DownloadDialog dialog;

    private TimeRepository timeRepository;
    private RefinancingRateCallback refinancingRateCallback;
    private Handler refRateHandler;
    private DecimalFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        ButterKnife.bind(this);
        timeRepository = new TimeRepository();

        attention.setVisibility(View.INVISIBLE);
        rateDescription.setVisibility(View.INVISIBLE);

        String currentDate = DateFormatter.format("dd-MM-yyyy", timeRepository.getToday());

        setToolbarTitle(currentDate);

        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(this,
                    getResources().getString(R.string.disable_internet), Toast.LENGTH_SHORT).show();
        } else {
            dialog = new DownloadDialog(this, R.style.ProgressDialogTheme);
            onLoad();
        }
    }

    private void setToolbarTitle(String current) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(current);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar:

                DatePickerDialog dpd = new DatePickerDialog(this, R.style.DialogTheme,
                        new OnDateSetListenerImpl(timeRepository, this),
                        DateFormatter.formatToInt("yyyy", timeRepository.getCurrent()),
                        DateFormatter.formatToInt("MM", timeRepository.getCurrent()) - 1,
                        DateFormatter.formatToInt("dd", timeRepository.getCurrent()));

                dpd.getDatePicker().setMinDate(TimeRepository.getMinDateLong());
                dpd.getDatePicker().setMaxDate(timeRepository.getToday());

                dpd.show();
                break;
            case R.id.graphic:
                Intent intent = new Intent(this, GraphicActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCounterValue(double val) {
        if (refRateHandler == null) {
            refRateHandler = new RefRateHandler(this);
        }

        if (df == null) {
            df = new DecimalFormat(".00");
        }

        double temp = 6000 / (val * 4);
        long sleepTime = (long) temp;

        for (double counter = 0; counter <= val; counter += 0.25) {
            Message message = refRateHandler.obtainMessage(0, df.format(counter));
            refRateHandler.sendMessageDelayed(message, (long) (sleepTime * counter));
        }
    }

    private void setAnimationRate() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.refinancing_rate_animation);
        refinancingRate.startAnimation(animation);
    }

    private void setAnimationAttention() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.attention_animation);
        attention.startAnimation(animation);
    }

    @Override
    public void onLoad() {
        if (apiService == null) {
            apiService = ApiClient.getApiService();
        }

        Call<List<RefinancingRate>> call =
                apiService.getRefinancingRateOnDay(DateFormatter.format("yyyy-MM-dd", timeRepository.getCurrent()));

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

        attention.setVisibility(View.VISIBLE);
        rateDescription.setVisibility(View.VISIBLE);

        if (DateFormatter.compareTodayWithCurrent(timeRepository.getToday(), timeRepository.getCurrent())) {
            rateDescription.setText(getResources().getString(R.string.rate_description));
        } else {
            rateDescription.setText(getResources().getString(R.string.rate_description_calendar,
                    DateFormatter.format("dd-MM-yyyy", timeRepository.getCurrent())));
        }


        // rate
        setCounterValue(listOfRates.get(0).getValue());
        setAnimationRate();

        // attention
        attention.setText(getResources().getString(R.string.attention,
                DateFormatter.convertToString("dd-MM-yyyy", listOfRates.get(0).getDate(), this)));
        setAnimationAttention();
    }

    @Override
    public void onFailure(String message) {
        if (dialog != null) {
            dialog.hide();
        }

        if (CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (dialog.isShowing()) dialog.hide();
        }
    }


    @Override
    public void onConvertError() {
        Toast.makeText(this, getString(R.string.convert_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdateView(String value) {
        if (refinancingRate != null) {
            refinancingRate.setText(value + "%");
        }
    }

}
