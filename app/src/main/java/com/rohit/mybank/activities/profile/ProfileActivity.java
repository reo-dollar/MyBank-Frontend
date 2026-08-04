package com.rohit.mybank.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.databinding.ActivityProfileBinding;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.repository.ProfileRepository;
import com.rohit.mybank.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private ProfileRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = new ProfileRepository(this);

        loadProfile();

        initializeClickListeners();
    }

    /**
     * Load profile from backend
     */
    private void loadProfile() {

        repository.getProfile().enqueue(new Callback<ProfileResponse>() {

            @Override
            public void onResponse(Call<ProfileResponse> call,
                                   Response<ProfileResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ProfileResponse profile = response.body();

                    binding.tvName.setText(profile.getFullName());
                    binding.tvFullName.setText(profile.getFullName());

                    binding.tvUsername.setText(sessionManager.getUsername());

                    binding.tvEmail.setText(profile.getEmail());
                    binding.tvPhone.setText(profile.getMobile());
                    binding.tvAddress.setText(profile.getAddress());

                    binding.tvCustomerId.setText(profile.getCustomerId());
                    binding.tvAccountNumber.setText(profile.getAccountNumber());
                    binding.tvAccountType.setText(profile.getAccountType());

                    binding.tvAccountTypeHeader.setText(profile.getAccountType());

                    binding.tvBranch.setText(profile.getBranch());
                    binding.tvIfsc.setText(profile.getIfsc());
                    binding.tvKyc.setText(profile.getKycStatus());

                } else {

                    Toast.makeText(
                            ProfileActivity.this,
                            "Unable to load profile.",
                            Toast.LENGTH_SHORT
                    ).show();

                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        ProfileActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }
        });
    }

    /**
     * Button Clicks
     */
    private void initializeClickListeners() {

        binding.cardEditProfile.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                EditProfileActivity.class
                        )
                )
        );

        binding.cardChangePassword.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                ChangePasswordActivity.class
                        )
                )
        );

        binding.cardNotifications.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Coming Soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        binding.cardDarkMode.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Coming Soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        binding.cardFingerprint.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Coming Soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        binding.btnLogout.setOnClickListener(v -> {

            sessionManager.clearSession();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();

        });
    }
}