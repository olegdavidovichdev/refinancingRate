package com.olegdavidovichdev.refinancingrate.rest;

import com.olegdavidovichdev.refinancingrate.model.RefinancingRate;

import java.util.List;


public interface RequestListener {

    void onSuccess(List<RefinancingRate> listOfRates);

    void onFailure(String message);

}