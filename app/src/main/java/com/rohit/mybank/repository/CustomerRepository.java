package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.customer.KycRequest;
import com.rohit.mybank.model.customer.KycResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class CustomerRepository {

    private final ApiService apiService;

    public CustomerRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public void registerCustomer(
            KycRequest request,
            Callback<KycResponse> callback
    ) {

        Call<KycResponse> call =
                apiService.registerKyc(request);

        call.enqueue(callback);

    }

}