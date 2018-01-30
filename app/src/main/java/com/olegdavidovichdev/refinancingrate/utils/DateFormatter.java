package com.olegdavidovichdev.refinancingrate.utils;

import com.olegdavidovichdev.refinancingrate.entity.LocaleBY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateFormatter {

    public static String format(String pattern, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleBY.getLocaleBY());
        return sdf.format(new Date(time));
    }

    public static int formatToInt(String pattern, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleBY.getLocaleBY());
        return Integer.parseInt(sdf.format(new Date(time)));
    }


    public static String convert(String pattern, String date, ConvertErrorListener listener)  {
        SimpleDateFormat initSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", LocaleBY.getLocaleBY());
        Date d = null;
        try {
            d = initSDF.parse(date);
        } catch (ParseException e) {
            listener.onConvertError();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleBY.getLocaleBY());
        return sdf.format(d);
    }

    public static boolean compareTodayWithCurrent(long today, long current) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", LocaleBY.getLocaleBY());
        String s1 = sdf.format(new Date(today));
        String s2 = sdf.format(new Date(current));

        return s1.equals(s2);
    }
}
