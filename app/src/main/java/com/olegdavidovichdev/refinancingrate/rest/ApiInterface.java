package com.olegdavidovichdev.refinancingrate.rest;

import com.olegdavidovichdev.refinancingrate.model.RefinancingRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("RefinancingRate")
    Call<List<RefinancingRate>> getRefinancingRateOnDay (@Query("onDate") String date);

    @GET("RefinancingRate")
    Call<List<RefinancingRate>> getAll();

}
