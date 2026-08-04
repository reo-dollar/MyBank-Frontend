package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.fastag.FastagRechargeRequest;
import com.rohit.mybank.model.fastag.FastagRechargeResponse;

import retrofit2.Call;

public class FastagRechargeRepository {

    private final ApiService apiService;

    public FastagRechargeRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<FastagRechargeResponse> recharge(
            FastagRechargeRequest request) {

        return apiService.rechargeFastag(request);

    }

}