package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.withdraw.WithdrawRequest;
import com.rohit.mybank.model.withdraw.WithdrawResponse;

import retrofit2.Call;

public class WithdrawRepository {

    private final ApiService apiService;

    public WithdrawRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<WithdrawResponse> withdraw(WithdrawRequest request) {

        return apiService.withdraw(request);

    }
}