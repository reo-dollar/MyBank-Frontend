package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.dth.DthRechargeRequest;
import com.rohit.mybank.model.dth.DthRechargeResponse;

import retrofit2.Call;

public class DthRechargeRepository {

    private final ApiService apiService;

    public DthRechargeRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<DthRechargeResponse> recharge(
            DthRechargeRequest request) {

        return apiService.rechargeDth(request);

    }

}