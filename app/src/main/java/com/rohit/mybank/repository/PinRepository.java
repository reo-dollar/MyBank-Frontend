package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.pin.ApiResponse;
import com.rohit.mybank.model.pin.SetPinRequest;
import com.rohit.mybank.model.pin.VerifyPinRequest;
import com.rohit.mybank.model.pin.VerifyPinResponse;

import retrofit2.Call;

public class PinRepository {

    private final ApiService apiService;

    public PinRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ==========================
    // Set Transaction PIN
    // ==========================

    public Call<ApiResponse> setTransactionPin(SetPinRequest request) {

        return apiService.setTransactionPin(request);
    }

    // ==========================
    // Verify Transaction PIN
    // ==========================

    public Call<VerifyPinResponse> verifyTransactionPin(
            VerifyPinRequest request) {

        return apiService.verifyTransactionPin(request);
    }
}