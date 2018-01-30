package com.olegdavidovichdev.refinancingrate.model;

import com.google.gson.annotations.SerializedName;

public class RefinancingRate {

    @SerializedName("Date")
    private String date;
    @SerializedName("Value")
    private double value;


    public String getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }
}

