package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.auth.LoginRequest;
import com.rohit.mybank.model.auth.LoginResponse;
import com.rohit.mybank.model.auth.RegisterRequest;
import com.rohit.mybank.model.auth.RegisterResponse;

import retrofit2.Call;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    // =====================================
    // Login
    // =====================================

    public Call<LoginResponse> login(LoginRequest request) {

        return apiService.login(request);

    }

    // =====================================
    // Refresh Token
    // =====================================

    public Call<LoginResponse> refreshToken(String refreshToken) {

        return apiService.refreshToken(refreshToken);

    }

    // =====================================
    // Register
    // =====================================

    public Call<RegisterResponse> register(RegisterRequest request) {

        return apiService.register(request);

    }

}