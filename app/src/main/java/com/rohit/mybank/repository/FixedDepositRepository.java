package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositResponse;
import com.rohit.mybank.model.fixeddeposit.FixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.FixedDepositResponse;

import retrofit2.Call;

public class FixedDepositRepository {

    private final ApiService apiService;

    public FixedDepositRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    // =====================================
    // FD Calculator
    // =====================================

    public Call<FixedDepositResponse> calculateFixedDeposit(
            FixedDepositRequest request) {

        return apiService.calculateFixedDeposit(request);

    }

    // =====================================
    // Create Fixed Deposit
    // =====================================

    public Call<CreateFixedDepositResponse> createFixedDeposit(
            CreateFixedDepositRequest request) {

        return apiService.createFixedDeposit(request);

    }

}