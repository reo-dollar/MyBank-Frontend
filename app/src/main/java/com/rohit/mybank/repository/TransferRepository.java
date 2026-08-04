package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.transfer.TransferRequest;
import com.rohit.mybank.model.transfer.TransferResponse;

import retrofit2.Call;

public class TransferRepository {

    private final ApiService apiService;

    public TransferRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);

    }

    public Call<TransferResponse> transfer(TransferRequest request) {

        return apiService.transfer(request);

    }
}