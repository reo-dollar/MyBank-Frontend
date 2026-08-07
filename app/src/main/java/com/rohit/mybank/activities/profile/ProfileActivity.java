package com.rohit.mybank.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.databinding.ActivityProfileBinding;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.repository.ProfileRepository;
import com.rohit.mybank.session.SessionManager;
import com.rohit.mybank.utils.BiometricHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private ProfileRepository repository;

    // Prevents duplicate callbacks when switch state is changed programmatically
    private boolean isUpdatingFingerprintSwitch = false;

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
     * Load Profile
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
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

    /**
     * Button Click Listeners
     */
    private void initializeClickListeners() {

        // -----------------------------------------
        // Initialize Fingerprint UI
        // -----------------------------------------

        isUpdatingFingerprintSwitch = true;

        binding.switchFingerprint.setChecked(
                sessionManager.isFingerprintEnabled()
        );

        isUpdatingFingerprintSwitch = false;

        binding.tvFingerprintStatus.setText(
                sessionManager.isFingerprintEnabled()
                        ? "Enabled"
                        : "Disabled"
        );

        // -----------------------------------------
        // Edit Profile
        // -----------------------------------------

        binding.cardEditProfile.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                EditProfileActivity.class
                        )
                )
        );

        // -----------------------------------------
        // Change Password
        // -----------------------------------------

        binding.cardChangePassword.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                ChangePasswordActivity.class
                        )
                )
        );

        // -----------------------------------------
        // Notifications
        // -----------------------------------------

        binding.cardNotifications.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Coming Soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // -----------------------------------------
        // Dark Mode
        // -----------------------------------------

        binding.cardDarkMode.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Coming Soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // =========================================
        // Fingerprint Login
        // =========================================
        binding.switchFingerprint.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(
                            CompoundButton buttonView,
                            boolean isChecked) {

                        // Prevent callback loop
                        if (isUpdatingFingerprintSwitch) {
                            return;
                        }

                        // ===========================
                        // Enable Fingerprint
                        // ===========================

                        if (isChecked) {

                            if (!BiometricHelper.isBiometricAvailable(ProfileActivity.this)) {

                                Toast.makeText(
                                        ProfileActivity.this,
                                        BiometricHelper.getBiometricStatus(ProfileActivity.this),
                                        Toast.LENGTH_LONG
                                ).show();

                                isUpdatingFingerprintSwitch = true;

                                binding.switchFingerprint.setChecked(false);

                                isUpdatingFingerprintSwitch = false;

                                binding.tvFingerprintStatus.setText("Disabled");

                                return;
                            }

                            BiometricHelper.authenticate(
                                    ProfileActivity.this,
                                    new BiometricHelper.AuthenticationListener() {

                                        @Override
                                        public void onAuthenticationSuccess() {

                                            sessionManager.setFingerprintEnabled(true);

                                            binding.tvFingerprintStatus.setText("Enabled");

                                            Toast.makeText(
                                                    ProfileActivity.this,
                                                    "Fingerprint Login Enabled Successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                        }

                                        @Override
                                        public void onAuthenticationFailed(String message) {

                                            sessionManager.setFingerprintEnabled(false);

                                            isUpdatingFingerprintSwitch = true;

                                            binding.switchFingerprint.setChecked(false);

                                            isUpdatingFingerprintSwitch = false;

                                            binding.tvFingerprintStatus.setText("Disabled");

                                            Toast.makeText(
                                                    ProfileActivity.this,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                        }

                                    });

                        }

                        // ===========================
                        // Disable Fingerprint
                        // ===========================

                        else {

                            sessionManager.setFingerprintEnabled(false);

                            binding.tvFingerprintStatus.setText("Disabled");

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Fingerprint Login Disabled",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                });

        // ==========================================
        // Logout
        // ==========================================

        binding.btnLogout.setOnClickListener(v -> {

            // Disable fingerprint after logout
            sessionManager.setFingerprintEnabled(false);

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
