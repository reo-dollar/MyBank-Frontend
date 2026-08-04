package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.dashboard.DashboardResponse;

import retrofit2.Call;

public class DashboardRepository {

    private final ApiService apiService;

    public DashboardRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<DashboardResponse> getMyAccount() {

        return apiService.getMyAccount();

    }

}