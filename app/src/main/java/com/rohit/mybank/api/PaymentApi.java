package com.rohit.mybank.api;

import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.profile.UpdateProfileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface PaymentApi {

    @GET("profile")
    Call<ProfileResponse> getProfile();

    @PUT("profile")
    Call<ProfileResponse> updateProfile(
            @Body UpdateProfileRequest request
    );
}