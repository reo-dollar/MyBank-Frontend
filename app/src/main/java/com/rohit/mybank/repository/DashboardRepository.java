package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.admin.AdminDashboardResponse;
import com.rohit.mybank.model.dashboard.DashboardResponse;

import retrofit2.Call;

public class DashboardRepository {

    private final ApiService apiService;


    // ==========================================================
    // CONSTRUCTOR
    // ==========================================================

    public DashboardRepository(Context context) {

        apiService =
                RetrofitClient
                        .getClient(context)
                        .create(ApiService.class);
    }


    // ==========================================================
    // USER DASHBOARD / ACCOUNT
    // ==========================================================

    public Call<DashboardResponse> getMyAccount() {

        return apiService.getMyAccount();
    }


    // ==========================================================
    // ADMIN DASHBOARD
    // ==========================================================

    public Call<AdminDashboardResponse> getAdminDashboard() {

        return apiService.getAdminDashboard();
    }
}