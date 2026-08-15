package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.transaction.TransactionPageResponse;

import retrofit2.Call;

/**
 * =========================================================
 * ADMIN ACCOUNT TRANSACTION REPOSITORY
 * =========================================================
 *
 * Handles transaction history for Admin Account Management.
 *
 * Activity
 *      ↓
 * Repository
 *      ↓
 * ApiService
 *      ↓
 * Retrofit
 *      ↓
 * Spring Boot
 */
public class AdminAccountTransactionRepository {

    private final ApiService apiService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminAccountTransactionRepository(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        apiService =
                RetrofitClient
                        .getClient(
                                context.getApplicationContext()
                        )
                        .create(ApiService.class);
    }

    // =========================================================
    // GET TRANSACTIONS
    // =========================================================

    public Call<TransactionPageResponse> getTransactions(
            String accNo,
            int page,
            int size,
            String sort) {

        return apiService.getTransactions(
                accNo,
                page,
                size,
                sort
        );
    }
}