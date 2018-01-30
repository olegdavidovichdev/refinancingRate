package com.olegdavidovichdev.refinancingrate.rest;

import com.olegdavidovichdev.refinancingrate.model.RefinancingRate;


public interface RequestListener {

    void onSuccess(RefinancingRate rate);

    void onFailure(String message);

}