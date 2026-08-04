package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.recharge.MobileRechargeRequest;
import com.rohit.mybank.model.recharge.MobileRechargeResponse;

import retrofit2.Call;

public class MobileRechargeRepository {

    private final ApiService apiService;

    public MobileRechargeRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    public Call<MobileRechargeResponse> mobileRecharge(
            MobileRechargeRequest request
    ) {

        return apiService.mobileRecharge(request);

    }

}