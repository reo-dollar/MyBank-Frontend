package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.deposit.DepositRequest;
import com.rohit.mybank.model.deposit.DepositResponse;

import retrofit2.Call;

public class DepositRepository {

    private final ApiService apiService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DepositRepository(
            Context context
    ) {

        apiService =
                RetrofitClient
                        .getClient(context)
                        .create(ApiService.class);
    }


    // =========================================================
    // DEPOSIT
    // =========================================================

    public Call<DepositResponse> deposit(
            DepositRequest request
    ) {

        return apiService.deposit(
                request
        );
    }
}