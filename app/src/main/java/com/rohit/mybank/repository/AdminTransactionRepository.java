package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.admin.AdminTransactionPageResponse;

import retrofit2.Call;
import retrofit2.Retrofit;

public class AdminTransactionRepository {

    private final ApiService apiService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdminTransactionRepository(
            Context context) {

        Retrofit retrofit =
                RetrofitClient.getClient(
                        context
                );

        apiService =
                retrofit.create(
                        ApiService.class
                );
    }


    // =====================================================
    // GET ADMIN TRANSACTIONS
    // =====================================================

    public Call<AdminTransactionPageResponse>
    getTransactions(
            int page,
            int size,
            String sort) {

        return apiService.getAdminTransactions(
                page,
                size,
                sort
        );
    }
}