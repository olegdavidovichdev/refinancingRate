package com.olegdavidovichdev.refinancingrate.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final String BASE_URL = "http://www.nbrb.by/API/";
    private static Retrofit retrofit = null;
    private static ApiInterface apiService = null;

    private static void getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            if (apiService == null) {
                apiService = retrofit.create(ApiInterface.class);
            }
        }
    }

    public static ApiInterface getApiService() {
        if (retrofit == null) {
            getClient();
        }
        return apiService;
    }

}
