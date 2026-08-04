package com.rohit.mybank.activities.profile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.databinding.ActivityEditProfileBinding;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.profile.UpdateProfileRequest;
import com.rohit.mybank.repository.ProfileRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private ProfileRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new ProfileRepository(this);

        loadProfile();

        binding.btnUpdate.setOnClickListener(v -> updateProfile());
    }

    /**
     * Load existing profile
     */
    private void loadProfile() {

        repository.getProfile().enqueue(new Callback<ProfileResponse>() {

            @Override
            public void onResponse(Call<ProfileResponse> call,
                                   Response<ProfileResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ProfileResponse profile = response.body();

                    binding.etEmail.setText(profile.getEmail());
                    binding.etMobile.setText(profile.getMobile());
                    binding.etAddress.setText(profile.getAddress());
                    binding.etCity.setText(profile.getCity());
                    binding.etState.setText(profile.getState());
                    binding.etPincode.setText(profile.getPincode());
                    binding.etOccupation.setText(profile.getOccupation());

                } else {

                    Toast.makeText(
                            EditProfileActivity.this,
                            "Unable to load profile.",
                            Toast.LENGTH_SHORT
                    ).show();

                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        EditProfileActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }
        });

    }

    /**
     * Update profile
     */
    private void updateProfile() {

        String email = binding.etEmail.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String state = binding.etState.getText().toString().trim();
        String pincode = binding.etPincode.getText().toString().trim();
        String occupation = binding.etOccupation.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            return;
        }

        if (mobile.isEmpty()) {
            binding.etMobile.setError("Mobile is required");
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest();

        request.setEmail(email);
        request.setMobile(mobile);
        request.setAddress(address);
        request.setCity(city);
        request.setState(state);
        request.setPincode(pincode);
        request.setOccupation(occupation);

        repository.updateProfile(request)
                .enqueue(new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(Call<ProfileResponse> call,
                                           Response<ProfileResponse> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Profile Updated Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Update Failed",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call,
                                          Throwable t) {

                        Toast.makeText(
                                EditProfileActivity.this,
                                "Network Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}