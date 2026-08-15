package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.admin.AdminUserResponse;

import java.util.List;

import retrofit2.Call;

/**
 * =========================================================
 * ADMIN USER REPOSITORY
 * =========================================================
 *
 * Handles all Admin User Management API operations.
 */
public class AdminUserRepository {

    private final ApiService apiService;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdminUserRepository(Context context) {

        apiService =
                RetrofitClient
                        .getClient(context)
                        .create(ApiService.class);
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================

    public Call<List<AdminUserResponse>> getAllUsers() {

        return apiService.getAdminUsers();
    }

    // =====================================================
    // GET USER
    // =====================================================

    public Call<AdminUserResponse> getUser(
            String username) {

        return apiService.getAdminUser(username);
    }

    // =====================================================
    // ENABLE USER
    // =====================================================

    public Call<AdminUserResponse> enableUser(
            String username) {

        return apiService.enableAdminUser(username);
    }

    // =====================================================
    // DISABLE USER
    // =====================================================

    public Call<AdminUserResponse> disableUser(
            String username) {

        return apiService.disableAdminUser(username);
    }

    // =====================================================
    // LOCK USER
    // =====================================================

    public Call<AdminUserResponse> lockUser(
            String username) {

        return apiService.lockAdminUser(username);
    }

    // =====================================================
    // UNLOCK USER
    // =====================================================

    public Call<AdminUserResponse> unlockUser(
            String username) {

        return apiService.unlockAdminUser(username);
    }
}