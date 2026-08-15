package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.admin.AdminCustomerResponse;
import com.rohit.mybank.model.admin.AdminUserResponse;

import java.util.List;

import retrofit2.Call;

/**
 * =========================================================
 * ADMIN CUSTOMER REPOSITORY
 * =========================================================
 *
 * Handles all API communication for:
 *
 * - Customer list
 * - Customer search
 * - Customer details
 * - Enable user
 * - Disable user
 * - Lock account
 * - Unlock account
 */
public class AdminCustomerRepository {

    // =========================================================
    // API SERVICE
    // =========================================================

    private final ApiService apiService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminCustomerRepository(Context context) {

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
    // GET ALL CUSTOMERS
    // =========================================================

    public Call<List<AdminCustomerResponse>>
    getCustomers() {

        return apiService.getAdminCustomers();
    }

    // =========================================================
    // SEARCH CUSTOMERS
    // =========================================================

    /**
     * Searches customers by:
     *
     * - Name
     * - Username
     * - Email
     * - Customer ID
     *
     * Backend:
     *
     * GET /admin/customers/search?query=value
     */
    public Call<List<AdminCustomerResponse>>
    searchCustomers(String query) {

        return apiService.searchAdminCustomers(query);
    }

    // =========================================================
    // GET CUSTOMER DETAILS
    // =========================================================

    /**
     * Backend:
     *
     * GET /admin/customers/{customerId}
     */
    public Call<AdminCustomerResponse>
    getCustomer(String customerId) {

        return apiService.getAdminCustomer(
                customerId
        );
    }

    // =========================================================
    // ENABLE USER
    // =========================================================

    public Call<AdminUserResponse>
    enableUser(String username) {

        return apiService.enableAdminUser(
                username
        );
    }

    // =========================================================
    // DISABLE USER
    // =========================================================

    public Call<AdminUserResponse>
    disableUser(String username) {

        return apiService.disableAdminUser(
                username
        );
    }

    // =========================================================
    // LOCK ACCOUNT
    // =========================================================

    public Call<AdminUserResponse>
    lockAccount(String username) {

        return apiService.lockAdminUser(
                username
        );
    }

    // =========================================================
    // UNLOCK ACCOUNT
    // =========================================================

    public Call<AdminUserResponse>
    unlockAccount(String username) {

        return apiService.unlockAdminUser(
                username
        );
    }

    // =========================================================
    // GET API SERVICE
    // =========================================================

    public ApiService getApiService() {

        return apiService;
    }
}