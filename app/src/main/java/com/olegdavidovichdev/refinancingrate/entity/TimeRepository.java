package com.olegdavidovichdev.refinancingrate.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimeRepository {

    private static final Calendar MIN_DATE = new GregorianCalendar(1991, Calendar.JULY, 1);

    private long today;
    private long current;

    public TimeRepository() {
        today = new Date().getTime();
        current = today;
    }

    public static long getMinDateLong() {
        return MIN_DATE.getTime().getTime();
    }

    public long getToday() {
        return today;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

}