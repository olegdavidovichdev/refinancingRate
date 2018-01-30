package com.olegdavidovichdev.refinancingrate.utils;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.olegdavidovichdev.refinancingrate.entity.TimeRepository;
import com.olegdavidovichdev.refinancingrate.rest.LoadingListener;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class OnDateSetListenerImpl implements DatePickerDialog.OnDateSetListener {

    private TimeRepository timeRepository;
    private LoadingListener listener;

    public OnDateSetListenerImpl(TimeRepository timeRepository, LoadingListener listener) {
        this.timeRepository = timeRepository;
        this.listener = listener;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        updateTimeRepository(i, i1, i2);
        listener.onLoad();
    }

    private void updateTimeRepository(int i, int i1, int i2) {
        Calendar calendar = new GregorianCalendar(i, i1, i2);
        timeRepository.setCurrent(calendar.getTimeInMillis());
    }
}
