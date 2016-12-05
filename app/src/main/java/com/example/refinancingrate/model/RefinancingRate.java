package com.example.refinancingrate.model;

import com.google.gson.annotations.SerializedName;

public class RefinancingRate
{
    @SerializedName("Date")
    private String date;
    @SerializedName("Value")
    private Double value;


    public RefinancingRate(String date, Double value)
    {
        this.date = date;
        this.value = value;
    }


    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public Double getValue()
    {
        return value;
    }

    public void setValue(Double value)
    {
        this.value = value;
    }

}

