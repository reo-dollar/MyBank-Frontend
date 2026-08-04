package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.profile.ChangePasswordRequest;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.profile.UpdateProfileRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProfileRepository {

    private final ApiService apiService;

    public ProfileRepository(Context context) {
        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    public Call<ProfileResponse> getProfile() {
        return apiService.getProfile();
    }

    public Call<ProfileResponse> updateProfile(UpdateProfileRequest request) {
        return apiService.updateProfile(request);
    }

    public Call<ResponseBody> changePassword(ChangePasswordRequest request) {
        return apiService.changePassword(request);
    }
}