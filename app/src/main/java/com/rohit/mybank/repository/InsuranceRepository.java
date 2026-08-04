package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.insurance.InsurancePaymentRequest;
import com.rohit.mybank.model.insurance.InsurancePaymentResponse;

import retrofit2.Call;

public class InsuranceRepository {

    private final ApiService apiService;

    public InsuranceRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<InsurancePaymentResponse> payInsurance(
            InsurancePaymentRequest request) {

        return apiService.payInsurance(request);

    }
}