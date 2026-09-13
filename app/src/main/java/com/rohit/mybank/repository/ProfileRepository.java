package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.profile.ChangePasswordRequest;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.profile.UpdateProfileRequest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProfileRepository {

    private final ApiService apiService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ProfileRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }


    // =========================================================
    // GET PROFILE
    // =========================================================

    public Call<ProfileResponse> getProfile() {

        return apiService.getProfile();
    }


    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    public Call<ProfileResponse> updateProfile(
            UpdateProfileRequest request
    ) {

        return apiService.updateProfile(request);
    }


    // =========================================================
    // UPLOAD PROFILE PHOTO
    // =========================================================

    /**
     * Uploads the selected profile photo.
     *
     * The MultipartBody.Part must use the field name:
     *
     * "photo"
     */
    public Call<ProfileResponse> uploadProfilePhoto(
            MultipartBody.Part photo
    ) {

        return apiService.uploadProfilePhoto(photo);
    }


    // =========================================================
    // GET PROFILE PHOTO
    // =========================================================

    /**
     * Downloads the authenticated user's profile photo.
     *
     * The backend returns the image as a binary response.
     */
    public Call<ResponseBody> getProfilePhoto() {

        return apiService.getProfilePhoto();
    }


    // =========================================================
    // DELETE PROFILE PHOTO
    // =========================================================

    /**
     * Deletes the authenticated user's profile photo.
     */
    public Call<ProfileResponse> deleteProfilePhoto() {

        return apiService.deleteProfilePhoto();
    }


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    public Call<ResponseBody> changePassword(
            ChangePasswordRequest request
    ) {

        return apiService.changePassword(request);
    }
}