package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.electricity.ElectricityBillRequest;
import com.rohit.mybank.model.electricity.ElectricityBillResponse;

import retrofit2.Call;

public class ElectricityBillRepository {

    private final ApiService apiService;

    public ElectricityBillRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<ElectricityBillResponse> payBill(
            ElectricityBillRequest request
    ) {

        return apiService.payElectricityBill(request);

    }

}