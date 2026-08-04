package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.water.WaterBillRequest;
import com.rohit.mybank.model.water.WaterBillResponse;

import retrofit2.Call;

public class WaterBillRepository {

    private final ApiService apiService;

    public WaterBillRepository(Context context) {
        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    public Call<WaterBillResponse> payWaterBill(
            WaterBillRequest request) {

        return apiService.payWaterBill(request);
    }
}