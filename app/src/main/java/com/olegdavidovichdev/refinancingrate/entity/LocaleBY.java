package com.olegdavidovichdev.refinancingrate.entity;

import java.util.Locale;


public class LocaleBY {

    private static Locale localeRU;

    public static Locale getLocaleBY() {
        if (localeRU == null) {
            localeRU = new Locale("be");
            return localeRU;
        } else {
            return localeRU;
        }
    }
}
