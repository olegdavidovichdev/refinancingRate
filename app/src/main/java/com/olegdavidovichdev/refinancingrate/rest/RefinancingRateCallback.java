package com.olegdavidovichdev.refinancingrate.rest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.olegdavidovichdev.refinancingrate.R;
import com.olegdavidovichdev.refinancingrate.model.RefinancingRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RefinancingRateCallback implements Callback<List<RefinancingRate>> {

    private RequestListener listener;
    private Context context;

    public RefinancingRateCallback(RequestListener listener) {
        this.listener = listener;

        if (listener instanceof AppCompatActivity) {
            context = (AppCompatActivity) listener;
        }
    }

    @Override
    public void onResponse(Call<List<RefinancingRate>> call, Response<List<RefinancingRate>> response) {
        if (response.isSuccessful()) {
            if (!response.body().isEmpty()) {
                listener.onSuccess(response.body());
            } else {
                listener.onFailure(context.getResources().getString(R.string.rates_list_is_empty));
            }
        } else {
            listener.onFailure(context.getResources().getString(R.string.server_error));
        }
    }

    @Override
    public void onFailure(Call<List<RefinancingRate>> call, Throwable t) {
        listener.onFailure(t.getMessage());
    }

}
