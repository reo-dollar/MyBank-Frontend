package com.rohit.mybank.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.admin.AdminDashboardActivity;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.security.AppLockManager;
import com.rohit.mybank.session.SessionManager;
import com.rohit.mybank.utils.BiometricHelper;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class AuthenticationActivity extends AppCompatActivity {

    // ==========================================================
    // Views
    // ==========================================================

    private TextView tvAuthenticationStatus;

    private Button btnBiometric;

    private Button btnDeviceLock;

    // ==========================================================
    // Session
    // ==========================================================

    private SessionManager sessionManager;

    // ==========================================================
    // Activity Lifecycle
    // ==========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_authentication
        );

        // ======================================================
        // Session
        // ======================================================

        sessionManager =
                new SessionManager(this);

        // ======================================================
        // Initialize Views
        // ======================================================

        initializeViews();

        // ======================================================
        // Check Login
        // ======================================================

        if (!sessionManager.isLoggedIn()) {

            goToLogin();

            return;
        }

        // ======================================================
        // Prevent Back
        // ======================================================

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        Toast.makeText(
                                AuthenticationActivity.this,
                                "Authentication is required to access MyBank.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        // ======================================================
        // Configure Authentication Options
        // ======================================================

        configureAuthenticationOptions();

        // ======================================================
        // Fingerprint / Face Button
        // ======================================================

        btnBiometric.setOnClickListener(
                v -> authenticateBiometric()
        );

        // ======================================================
        // Device PIN / Pattern / Password Button
        // ======================================================

        btnDeviceLock.setOnClickListener(
                v -> authenticateDeviceLock()
        );

        // ======================================================
        // Start Initial Authentication
        // ======================================================

        startInitialAuthentication();
    }

    // ==========================================================
    // Initialize Views
    // ==========================================================

    private void initializeViews() {

        tvAuthenticationStatus =
                findViewById(
                        R.id.tvAuthenticationStatus
                );

        btnBiometric =
                findViewById(
                        R.id.btnBiometric
                );

        btnDeviceLock =
                findViewById(
                        R.id.btnDeviceLock
                );
    }

    // ==========================================================
    // Configure Authentication Options
    // ==========================================================

    private void configureAuthenticationOptions() {

        boolean biometricAvailable =
                BiometricHelper.isBiometricAvailable(this);

        boolean deviceLockAvailable =
                BiometricHelper.isDeviceCredentialAvailable(this);

        // ======================================================
        // Fingerprint / Face Button
        // ======================================================

        btnBiometric.setVisibility(
                View.VISIBLE
        );

        btnBiometric.setEnabled(
                true
        );

        // ======================================================
        // Device Lock Button
        // ======================================================

        btnDeviceLock.setVisibility(
                View.VISIBLE
        );

        btnDeviceLock.setEnabled(
                true
        );

        // ======================================================
        // Button Text
        // ======================================================

        if (biometricAvailable) {

            btnBiometric.setText(
                    "🔐  Use Fingerprint / Face"
            );

        } else {

            btnBiometric.setText(
                    "🔐  Fingerprint / Face Unavailable"
            );
        }

        if (deviceLockAvailable) {

            btnDeviceLock.setText(
                    "🔢  Use Device PIN / Pattern / Password"
            );

        } else {

            btnDeviceLock.setText(
                    "🔒  Set Up Device Lock"
            );
        }
    }

    // ==========================================================
    // INITIAL AUTHENTICATION
    // ==========================================================

    private void startInitialAuthentication() {

        // ======================================================
        // Check Login
        // ======================================================

        if (!sessionManager.isLoggedIn()) {

            goToLogin();

            return;
        }

        // ======================================================
        // Prefer Biometric
        // ======================================================

        if (BiometricHelper.isBiometricAvailable(this)) {

            tvAuthenticationStatus.setText(
                    "Verify using your fingerprint or face"
            );

            authenticateBiometric();

            return;
        }

        // ======================================================
        // Device Credential Fallback
        // ======================================================

        if (BiometricHelper.isDeviceCredentialAvailable(this)) {

            tvAuthenticationStatus.setText(
                    "Verify using your device lock"
            );

            authenticateDeviceLock();

            return;
        }

        // ======================================================
        // Nothing Available
        // ======================================================

        enableAuthenticationButtons();

        tvAuthenticationStatus.setText(
                "Please set a device PIN, pattern, or password to continue."
        );

        Toast.makeText(
                this,
                "A secure device lock is required to access MyBank.",
                Toast.LENGTH_LONG
        ).show();
    }

    // ==========================================================
    // BIOMETRIC AUTHENTICATION
    // ==========================================================

    private void authenticateBiometric() {

        // ======================================================
        // Verify Availability
        // ======================================================

        if (!BiometricHelper.isBiometricAvailable(this)) {

            Toast.makeText(
                    this,
                    "Fingerprint / Face authentication is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            // ==================================================
            // Automatically redirect to Device Lock
            // ==================================================

            if (BiometricHelper.isDeviceCredentialAvailable(this)) {

                authenticateDeviceLock();

            } else {

                enableAuthenticationButtons();

                tvAuthenticationStatus.setText(
                        "Please set a device PIN, pattern, or password."
                );
            }

            return;
        }

        // ======================================================
        // Disable Buttons Only While System Prompt Is Open
        // ======================================================

        disableAuthenticationButtons();

        tvAuthenticationStatus.setText(
                "Verify using your fingerprint or face"
        );

        // ======================================================
        // Launch Biometric Authentication
        // ======================================================

        BiometricHelper.authenticateBiometric(
                this,
                new BiometricHelper.AuthenticationListener() {

                    // ==========================================
                    // SUCCESS
                    // ==========================================

                    @Override
                    public void onAuthenticationSuccess() {

                        runOnUiThread(() -> {

                            tvAuthenticationStatus.setText(
                                    "Authentication successful"
                            );

                            AppLockManager
                                    .getInstance(getApplication())
                                    .authenticationCompleted();

                            // ==================================
                            // ROLE-BASED ROUTING
                            // ==================================

                            goToDashboard();
                        });
                    }

                    // ==========================================
                    // CANCEL / ERROR
                    // ==========================================

                    @Override
                    public void onAuthenticationFailed(
                            String message
                    ) {

                        runOnUiThread(() -> {

                            /*
                             * Biometric authentication has ended.
                             *
                             * Buttons MUST become usable again.
                             */

                            enableAuthenticationButtons();

                            // ==================================
                            // FALLBACK TO DEVICE LOCK
                            // ==================================

                            if (BiometricHelper
                                    .isDeviceCredentialAvailable(
                                            AuthenticationActivity.this
                                    )) {

                                tvAuthenticationStatus.setText(
                                        "Use your device PIN, pattern, or password"
                                );

                                Toast.makeText(
                                        AuthenticationActivity.this,
                                        "Biometric authentication cancelled. You can use your device lock.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                /*
                                 * Small delay gives Android time to
                                 * completely close the biometric dialog.
                                 */

                                btnDeviceLock.postDelayed(
                                        () -> authenticateDeviceLock(),
                                        250
                                );

                            } else {

                                tvAuthenticationStatus.setText(
                                        "Choose an authentication method"
                                );

                                Toast.makeText(
                                        AuthenticationActivity.this,
                                        "Biometric authentication cancelled.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }
                }
        );
    }

    // ==========================================================
    // DEVICE LOCK AUTHENTICATION
    // ==========================================================

    private void authenticateDeviceLock() {

        // ======================================================
        // Device Lock Not Configured
        // ======================================================

        if (!BiometricHelper.isDeviceCredentialAvailable(this)) {

            enableAuthenticationButtons();

            tvAuthenticationStatus.setText(
                    "Please set a device PIN, pattern, or password."
            );

            Toast.makeText(
                    this,
                    "Please set a device PIN, pattern, or password first.",
                    Toast.LENGTH_LONG
            ).show();

            BiometricHelper.openDeviceSecuritySettings(
                    this
            );

            return;
        }

        // ======================================================
        // Disable Buttons While System Prompt Is Open
        // ======================================================

        disableAuthenticationButtons();

        tvAuthenticationStatus.setText(
                "Verify using your device PIN, pattern, or password"
        );

        // ======================================================
        // Launch Device Credential Authentication
        // ======================================================

        BiometricHelper.authenticateDeviceCredential(
                this,
                new BiometricHelper.AuthenticationListener() {

                    // ==========================================
                    // SUCCESS
                    // ==========================================

                    @Override
                    public void onAuthenticationSuccess() {

                        runOnUiThread(() -> {

                            tvAuthenticationStatus.setText(
                                    "Authentication successful"
                            );

                            AppLockManager
                                    .getInstance(getApplication())
                                    .authenticationCompleted();

                            // ==================================
                            // ROLE-BASED ROUTING
                            // ==================================

                            goToDashboard();
                        });
                    }

                    // ==========================================
                    // CANCEL / ERROR
                    // ==========================================

                    @Override
                    public void onAuthenticationFailed(
                            String message
                    ) {

                        runOnUiThread(() -> {

                            /*
                             * IMPORTANT:
                             *
                             * User did NOT authenticate.
                             *
                             * MyBank remains locked.
                             *
                             * Return to authentication screen.
                             */

                            enableAuthenticationButtons();

                            tvAuthenticationStatus.setText(
                                    "Choose how you want to authenticate"
                            );

                            Toast.makeText(
                                    AuthenticationActivity.this,
                                    "Authentication cancelled. MyBank remains locked.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }
                }
        );
    }

    // ==========================================================
    // Disable Authentication Buttons
    // ==========================================================

    private void disableAuthenticationButtons() {

        if (btnBiometric != null) {

            btnBiometric.setEnabled(
                    false
            );
        }

        if (btnDeviceLock != null) {

            btnDeviceLock.setEnabled(
                    false
            );
        }
    }

    // ==========================================================
    // Enable Authentication Buttons
    // ==========================================================

    private void enableAuthenticationButtons() {

        if (btnBiometric != null) {

            btnBiometric.setVisibility(
                    View.VISIBLE
            );

            btnBiometric.setEnabled(
                    true
            );
        }

        if (btnDeviceLock != null) {

            btnDeviceLock.setVisibility(
                    View.VISIBLE
            );

            btnDeviceLock.setEnabled(
                    true
            );
        }
    }

    // ==========================================================
    // Android 10 LEGACY DEVICE CREDENTIAL RESULT
    // ==========================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == 1001) {

            if (resultCode == RESULT_OK) {

                // ==============================================
                // Authentication successful
                // ==============================================

                goToDashboard();

            } else {

                enableAuthenticationButtons();

                tvAuthenticationStatus.setText(
                        "Choose how you want to authenticate"
                );

                Toast.makeText(
                        this,
                        "Authentication cancelled. MyBank remains locked.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // ==========================================================
    // GO TO DASHBOARD
    // ==========================================================
    //
    // USER  → DashboardActivity
    // ADMIN → AdminDashboardActivity
    //
    // The backend remains the actual security boundary.
    // This method is only responsible for Android UI routing.
    // ==========================================================

    private void goToDashboard() {

        // ======================================================
        // Get User Role From JWT
        // ======================================================

        String role = getRoleFromToken();

        Intent intent;

        // ======================================================
        // ADMIN
        // ======================================================

        if ("ADMIN".equalsIgnoreCase(role)) {

            intent = new Intent(
                    AuthenticationActivity.this,
                    AdminDashboardActivity.class
            );

        } else {

            // ==================================================
            // NORMAL USER
            // ==================================================

            intent = new Intent(
                    AuthenticationActivity.this,
                    DashboardActivity.class
            );
        }

        // ======================================================
        // Remove AuthenticationActivity From Back Stack
        // ======================================================

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // ==========================================================
    // GET ROLE FROM JWT ACCESS TOKEN
    // ==========================================================
    //
    // JWT format:
    //
    // HEADER.PAYLOAD.SIGNATURE
    //
    // We read only the payload to determine which Android
    // dashboard should be displayed.
    //
    // IMPORTANT:
    // This is NOT backend authorization.
    // Backend /admin/** endpoints remain protected by
    // Spring Security.
    // ==========================================================

    private String getRoleFromToken() {

        try {

            // ==================================================
            // Get Access Token
            // ==================================================

            String token =
                    sessionManager.getToken();

            if (token == null
                    || token.trim().isEmpty()) {

                return "USER";
            }

            // ==================================================
            // Split JWT
            // ==================================================

            String[] parts =
                    token.split("\\.");

            if (parts.length != 3) {

                return "USER";
            }

            // ==================================================
            // JWT Payload
            // ==================================================

            String payload =
                    parts[1];

            // ==================================================
            // Decode Base64URL Payload
            // ==================================================

            byte[] decodedBytes =
                    Base64.decode(
                            payload,
                            Base64.URL_SAFE
                                    | Base64.NO_WRAP
                    );

            String decodedPayload =
                    new String(
                            decodedBytes,
                            StandardCharsets.UTF_8
                    );

            // ==================================================
            // Convert Payload To JSON
            // ==================================================

            JSONObject jsonObject =
                    new JSONObject(
                            decodedPayload
                    );

            // ==================================================
            // Extract Role
            // ==================================================

            String role =
                    jsonObject.optString(
                            "role",
                            "USER"
                    );

            // ==================================================
            // Normalize Role
            // ==================================================

            if (role == null
                    || role.trim().isEmpty()) {

                return "USER";
            }

            return role.trim();

        } catch (Exception e) {

            e.printStackTrace();

            /*
             * Security-safe fallback.
             *
             * If the role cannot be read, never assume ADMIN.
             * Treat the session as a normal USER session.
             */

            return "USER";
        }
    }

    // ==========================================================
    // GO TO LOGIN
    // ==========================================================

    private void goToLogin() {

        Intent intent =
                new Intent(
                        AuthenticationActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(
                intent
        );

        finish();
    }
}