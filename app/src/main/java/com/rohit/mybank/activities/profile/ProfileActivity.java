package com.rohit.mybank.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.databinding.ActivityProfileBinding;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.repository.ProfileRepository;
import com.rohit.mybank.session.SessionManager;
import com.rohit.mybank.utils.BiometricHelper;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * ============================================================
 * Profile Activity
 * ============================================================
 *
 * Handles:
 *
 * 1. Profile information
 * 2. Profile photo
 * 3. Edit profile
 * 4. Change password
 * 5. Notifications
 * 6. Dark Mode
 * 7. Fingerprint login
 * 8. Logout
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    private SessionManager sessionManager;

    private ProfileRepository repository;


    /*
     * ============================================================
     * Fingerprint Switch Protection
     * ============================================================
     *
     * Prevents the fingerprint listener from executing when
     * the switch is changed programmatically.
     */
    private boolean isUpdatingFingerprintSwitch = false;


    /*
     * ============================================================
     * Preferences
     * ============================================================
     */

    private static final String PREFS_NAME =
            "MyBankPreferences";

    private static final String KEY_DARK_MODE =
            "dark_mode";

    private static final String KEY_NOTIFICATIONS_ENABLED =
            "notifications_enabled";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        /*
         * ========================================================
         * Initialize ViewBinding
         * ========================================================
         */

        binding = ActivityProfileBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());


        /*
         * ========================================================
         * Initialize Session / Repository
         * ========================================================
         */

        sessionManager = new SessionManager(this);

        repository = new ProfileRepository(this);


        /*
         * ========================================================
         * Load Profile
         * ========================================================
         */

        loadProfile();


        /*
         * ========================================================
         * Load Profile Photo
         * ========================================================
         */

        loadProfilePhoto();


        /*
         * ========================================================
         * Initialize Click Listeners
         * ========================================================
         */

        initializeClickListeners();
    }


    /**
     * ============================================================
     * Reload profile when activity becomes visible.
     * ============================================================
     *
     * This ensures changes made in EditProfileActivity are
     * reflected when the user returns.
     */
    @Override
    protected void onResume() {

        super.onResume();


        /*
         * Don't duplicate work before repository is initialized.
         */
        if (repository != null) {

            loadProfile();

            loadProfilePhoto();
        }
    }


    /**
     * ============================================================
     * Load Profile
     * ============================================================
     */
    private void loadProfile() {

        repository.getProfile().enqueue(
                new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileResponse> call,
                            Response<ProfileResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ProfileResponse profile =
                                    response.body();


                            /*
                             * =================================================
                             * Name
                             * =================================================
                             */

                            binding.tvName.setText(
                                    safeValue(
                                            profile.getFullName()
                                    )
                            );

                            binding.tvFullName.setText(
                                    safeValue(
                                            profile.getFullName()
                                    )
                            );


                            /*
                             * =================================================
                             * Username
                             * =================================================
                             */

                            binding.tvUsername.setText(
                                    safeValue(
                                            sessionManager.getUsername()
                                    )
                            );


                            /*
                             * =================================================
                             * Contact Information
                             * =================================================
                             */

                            binding.tvEmail.setText(
                                    safeValue(
                                            profile.getEmail()
                                    )
                            );

                            binding.tvPhone.setText(
                                    safeValue(
                                            profile.getMobile()
                                    )
                            );

                            binding.tvAddress.setText(
                                    safeValue(
                                            profile.getAddress()
                                    )
                            );


                            /*
                             * =================================================
                             * Customer / Account Information
                             * =================================================
                             */

                            binding.tvCustomerId.setText(
                                    safeValue(
                                            profile.getCustomerId()
                                    )
                            );

                            binding.tvAccountNumber.setText(
                                    safeValue(
                                            profile.getAccountNumber()
                                    )
                            );

                            binding.tvAccountType.setText(
                                    safeValue(
                                            profile.getAccountType()
                                    )
                            );

                            binding.tvAccountTypeHeader.setText(
                                    safeValue(
                                            profile.getAccountType()
                                    )
                            );

                            binding.tvBranch.setText(
                                    safeValue(
                                            profile.getBranch()
                                    )
                            );

                            binding.tvIfsc.setText(
                                    safeValue(
                                            profile.getIfsc()
                                    )
                            );

                            binding.tvKyc.setText(
                                    safeValue(
                                            profile.getKycStatus()
                                    )
                            );

                        } else {

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Unable to load profile.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ProfileResponse> call,
                            Throwable t
                    ) {

                        if (call.isCanceled()) {
                            return;
                        }

                        Toast.makeText(
                                ProfileActivity.this,
                                "Network Error : "
                                        + getErrorMessage(t),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    /**
     * ============================================================
     * Load Persisted Profile Photo
     * ============================================================
     *
     * Backend:
     *
     * GET /profile/photo
     *
     * JWT is automatically attached by the existing
     * AuthInterceptor through RetrofitClient.
     */
    private void loadProfilePhoto() {

        repository.getProfilePhoto().enqueue(
                new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {

                        /*
                         * =================================================
                         * Photo exists.
                         * =================================================
                         */

                        if (response.isSuccessful()
                                && response.body() != null) {

                            try {

                                byte[] imageBytes =
                                        response.body().bytes();

                                Bitmap bitmap =
                                        BitmapFactory.decodeByteArray(
                                                imageBytes,
                                                0,
                                                imageBytes.length
                                        );

                                if (bitmap != null) {

                                    binding.imgProfile.setImageBitmap(
                                            bitmap
                                    );
                                }

                            } catch (IOException e) {

                                /*
                                 * Keep the default image if
                                 * decoding fails.
                                 */
                            }

                        } else if (response.code() == 404) {

                            /*
                             * No profile photo.
                             *
                             * Keep the default drawable from XML.
                             */

                        } else {

                            /*
                             * Photo loading failure should not
                             * prevent the profile screen from working.
                             */
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {

                        /*
                         * Don't show a separate photo network
                         * error because the profile itself can
                         * still be displayed.
                         */
                    }
                }
        );
    }


    /**
     * ============================================================
     * Initialize Click Listeners
     * ============================================================
     */
    private void initializeClickListeners() {


        /*
         * ========================================================
         * Fingerprint UI
         * ========================================================
         */

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


        /*
         * ========================================================
         * Notifications UI
         * ========================================================
         *
         * For now the notification preference is stored locally.
         *
         * Backend synchronization will be implemented in a
         * later step.
         */

        boolean notificationsEnabled =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                ).getBoolean(
                        KEY_NOTIFICATIONS_ENABLED,
                        true
                );


        /*
         * Restore notification switch state.
         *
         * Listener is attached after this so restoring the
         * state does not trigger the listener.
         */

        binding.switchNotifications.setChecked(
                notificationsEnabled
        );


        /*
         * ========================================================
         * Notifications Switch
         * ========================================================
         */

        binding.switchNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    /*
                     * Save notification preference locally.
                     *
                     * Backend persistence will be added later.
                     */

                    getSharedPreferences(
                            PREFS_NAME,
                            MODE_PRIVATE
                    )
                            .edit()
                            .putBoolean(
                                    KEY_NOTIFICATIONS_ENABLED,
                                    isChecked
                            )
                            .apply();


                    Toast.makeText(
                            ProfileActivity.this,
                            isChecked
                                    ? "Notifications enabled"
                                    : "Notifications disabled",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );


        /*
         * ========================================================
         * Notifications Card
         * ========================================================
         *
         * Tapping the card opens the dedicated notification
         * settings screen.
         */

        binding.cardNotifications.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                NotificationsActivity.class
                        )
                )
        );


        /*
         * ========================================================
         * Dark Mode UI
         * ========================================================
         */

        boolean darkModeEnabled =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                ).getBoolean(
                        KEY_DARK_MODE,
                        false
                );


        /*
         * Restore Dark Mode switch state.
         *
         * Listener is attached after restoring the state.
         */

        binding.switchDarkMode.setChecked(
                darkModeEnabled
        );


        /*
         * ========================================================
         * Dark Mode Switch
         * ========================================================
         */

        binding.switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    /*
                     * Save user's Dark Mode preference.
                     */

                    getSharedPreferences(
                            PREFS_NAME,
                            MODE_PRIVATE
                    )
                            .edit()
                            .putBoolean(
                                    KEY_DARK_MODE,
                                    isChecked
                            )
                            .apply();


                    /*
                     * Apply application theme.
                     */

                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );


        /*
         * ========================================================
         * Dark Mode Card
         * ========================================================
         *
         * Tapping anywhere on the Dark Mode card toggles the
         * switch.
         */

        binding.cardDarkMode.setOnClickListener(v ->
                binding.switchDarkMode.toggle()
        );


        /*
         * ========================================================
         * Edit Profile
         * ========================================================
         */

        binding.cardEditProfile.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                EditProfileActivity.class
                        )
                )
        );


        /*
         * ========================================================
         * Change Password
         * ========================================================
         */

        binding.cardChangePassword.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                ChangePasswordActivity.class
                        )
                )
        );


        /*
         * ========================================================
         * Fingerprint Login
         * ========================================================
         */

        binding.switchFingerprint.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(
                            CompoundButton buttonView,
                            boolean isChecked
                    ) {

                        /*
                         * Prevent callback when we change the
                         * switch programmatically.
                         */

                        if (isUpdatingFingerprintSwitch) {
                            return;
                        }


                        /*
                         * =================================================
                         * Enable Fingerprint
                         * =================================================
                         */

                        if (isChecked) {

                            if (!BiometricHelper.isBiometricAvailable(
                                    ProfileActivity.this
                            )) {

                                Toast.makeText(
                                        ProfileActivity.this,
                                        BiometricHelper.getBiometricStatus(
                                                ProfileActivity.this
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();


                                isUpdatingFingerprintSwitch = true;

                                binding.switchFingerprint.setChecked(
                                        false
                                );

                                isUpdatingFingerprintSwitch = false;


                                binding.tvFingerprintStatus.setText(
                                        "Disabled"
                                );

                                return;
                            }


                            /*
                             * Authenticate the user before enabling
                             * fingerprint login.
                             */

                            BiometricHelper.authenticate(
                                    ProfileActivity.this,
                                    new BiometricHelper.AuthenticationListener() {

                                        @Override
                                        public void onAuthenticationSuccess() {

                                            sessionManager
                                                    .setFingerprintEnabled(
                                                            true
                                                    );


                                            binding.tvFingerprintStatus
                                                    .setText("Enabled");


                                            Toast.makeText(
                                                    ProfileActivity.this,
                                                    "Fingerprint Login Enabled Successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }


                                        @Override
                                        public void onAuthenticationFailed(
                                                String message
                                        ) {

                                            sessionManager
                                                    .setFingerprintEnabled(
                                                            false
                                                    );


                                            isUpdatingFingerprintSwitch =
                                                    true;

                                            binding.switchFingerprint
                                                    .setChecked(false);

                                            isUpdatingFingerprintSwitch =
                                                    false;


                                            binding.tvFingerprintStatus
                                                    .setText("Disabled");


                                            Toast.makeText(
                                                    ProfileActivity.this,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                            );

                        }


                        /*
                         * =================================================
                         * Disable Fingerprint
                         * =================================================
                         */

                        else {

                            sessionManager.setFingerprintEnabled(
                                    false
                            );


                            binding.tvFingerprintStatus.setText(
                                    "Disabled"
                            );


                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Fingerprint Login Disabled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );


        /*
         * ========================================================
         * Logout
         * ========================================================
         */

        binding.btnLogout.setOnClickListener(v -> {

            /*
             * Disable fingerprint after logout.
             */

            sessionManager.setFingerprintEnabled(false);


            /*
             * Clear authentication/session data.
             */

            sessionManager.clearSession();


            /*
             * Navigate to Login.
             */

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


    /**
     * ============================================================
     * Safe Text Value
     * ============================================================
     */
    private String safeValue(String value) {

        if (value == null || value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }


    /**
     * ============================================================
     * Safe Network Error Message
     * ============================================================
     */
    private String getErrorMessage(Throwable throwable) {

        if (throwable == null) {

            return "Unknown error";
        }


        String message =
                throwable.getMessage();


        if (message == null
                || message.trim().isEmpty()) {

            return "Unknown error";
        }


        return message;
    }


    /**
     * ============================================================
     * Destroy Activity
     * ============================================================
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        binding = null;
    }
}