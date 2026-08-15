package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.admin.AdminAccountResponse;

import java.util.List;

import retrofit2.Call;

/**
 * =========================================================
 * ADMIN ACCOUNT REPOSITORY
 * =========================================================
 *
 * Handles API communication for Admin Account Management.
 *
 * Activity
 *    ↓
 * Repository
 *    ↓
 * ApiService
 *    ↓
 * Retrofit
 *    ↓
 * Spring Boot
 */
public class AdminAccountRepository {

    private final ApiService apiService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminAccountRepository(Context context) {

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
    // GET ALL ACCOUNTS
    // =========================================================

    public Call<List<AdminAccountResponse>>
    getAccounts() {

        return apiService.getAdminAccounts();
    }

    // =========================================================
    // SEARCH ACCOUNTS
    // =========================================================

    public Call<List<AdminAccountResponse>>
    searchAccounts(String query) {

        return apiService.searchAdminAccounts(
                query
        );
    }

    // =========================================================
    // GET ACCOUNT DETAILS
    // =========================================================

    public Call<AdminAccountResponse>
    getAccount(String accNo) {

        return apiService.getAdminAccount(
                accNo
        );
    }
}