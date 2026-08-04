package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.gas.GasBillRequest;
import com.rohit.mybank.model.gas.GasBillResponse;

import retrofit2.Call;

public class GasBillRepository {

    private final ApiService apiService;

    public GasBillRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    public Call<GasBillResponse> bookCylinder(GasBillRequest request) {

        return apiService.bookGasCylinder(request);
    }
}