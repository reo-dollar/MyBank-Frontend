package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.broadband.BroadbandRechargeRequest;
import com.rohit.mybank.model.broadband.BroadbandRechargeResponse;

import retrofit2.Call;

public class BroadbandRechargeRepository {

    private final ApiService apiService;

    public BroadbandRechargeRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<BroadbandRechargeResponse> recharge(
            BroadbandRechargeRequest request) {

        return apiService.rechargeBroadband(request);

    }

}