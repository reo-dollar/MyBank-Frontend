package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.transaction.TransactionPageResponse;

import retrofit2.Call;

public class TransactionRepository {

    private final ApiService apiService;

    public TransactionRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    public Call<TransactionPageResponse> getTransactions(
            String accNo,
            int page,
            int size,
            String sort
    ) {

        return apiService.getTransactions(
                accNo,
                page,
                size,
                sort
        );
    }
}