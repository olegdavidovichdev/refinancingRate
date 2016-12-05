package com.example.refinancingrate.rest;

import com.example.refinancingrate.model.RefinancingRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Oleg on 22.11.2016.
 */

public interface ApiInterface {

    @GET("RefinancingRate")
    Call<List<RefinancingRate>> getRefinancingRateOnDay (@Query("onDate") String date);

    @GET("RefinancingRate")
    Call<List<RefinancingRate>> getAll (@Query("") String empty);

}
