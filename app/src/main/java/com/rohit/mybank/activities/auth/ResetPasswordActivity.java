package com.rohit.mybank.activities.auth;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.auth.ResetPasswordRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnResetPassword;

    private TextView tvBackToLogin;

    private ApiService apiService;

    private String resetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_reset_password
        );

        initializeViews();

        /*
         * Initialize Retrofit
         */
        apiService = RetrofitClient
                .getClient(this)
                .create(ApiService.class);

        /*
         * Get reset token
         */
        extractResetToken();

        /*
         * Reset password
         */
        btnResetPassword.setOnClickListener(
                v -> resetPassword()
        );

        /*
         * Back to Login
         */
        tvBackToLogin.setOnClickListener(
                v -> finish()
        );
    }

    /**
     * Initialize views.
     */
    private void initializeViews() {

        etNewPassword = findViewById(
                R.id.etNewPassword
        );

        etConfirmPassword = findViewById(
                R.id.etConfirmPassword
        );

        btnResetPassword = findViewById(
                R.id.btnResetPassword
        );

        tvBackToLogin = findViewById(
                R.id.tvBackToLogin
        );
    }

    /**
     * Extract token from incoming deep-link URI.
     */
    private void extractResetToken() {

        Uri uri = getIntent().getData();

        /*
         * First check URI.
         *
         * Example:
         *
         * mybank://reset-password?token=ABC123
         */
        if (uri != null) {

            resetToken = uri.getQueryParameter(
                    "token"
            );
        }

        /*
         * Also support token passed through Intent extra.
         *
         * This makes testing easier.
         */
        if (TextUtils.isEmpty(resetToken)) {

            resetToken = getIntent()
                    .getStringExtra("reset_token");
        }

        /*
         * Token validation
         */
        if (TextUtils.isEmpty(resetToken)) {

            btnResetPassword.setEnabled(false);

            Toast.makeText(
                    this,
                    "Invalid or missing reset link.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Validate and reset password.
     */
    private void resetPassword() {

        String newPassword =
                etNewPassword
                        .getText()
                        .toString()
                        .trim();

        String confirmPassword =
                etConfirmPassword
                        .getText()
                        .toString()
                        .trim();

        /*
         * New password required
         */
        if (TextUtils.isEmpty(newPassword)) {

            etNewPassword.setError(
                    "New password is required"
            );

            etNewPassword.requestFocus();

            return;
        }

        /*
         * Minimum password length
         */
        if (newPassword.length() < 8) {

            etNewPassword.setError(
                    "Password must contain at least 8 characters"
            );

            etNewPassword.requestFocus();

            return;
        }

        /*
         * Confirm password required
         */
        if (TextUtils.isEmpty(confirmPassword)) {

            etConfirmPassword.setError(
                    "Please confirm your password"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        /*
         * Password comparison
         */
        if (!newPassword.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        /*
         * Token validation
         */
        if (TextUtils.isEmpty(resetToken)) {

            Toast.makeText(
                    this,
                    "Invalid or expired reset link.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        /*
         * Disable button
         */
        btnResetPassword.setEnabled(false);

        btnResetPassword.setText(
                "RESETTING..."
        );

        /*
         * Create request
         */
        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        resetToken,
                        newPassword
                );

        /*
         * Send request
         */
        apiService
                .resetPassword(request)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {

                        btnResetPassword.setEnabled(true);

                        btnResetPassword.setText(
                                "RESET PASSWORD"
                        );

                        /*
                         * Success
                         */
                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    ResetPasswordActivity.this,
                                    "Password reset successfully.",
                                    Toast.LENGTH_LONG
                            ).show();

                            /*
                             * Return to Login
                             */
                            finish();

                            return;
                        }

                        /*
                         * Error
                         */
                        String message;

                        switch (response.code()) {

                            case 400:
                                message =
                                        "Invalid or expired reset link.";
                                break;

                            case 401:
                                message =
                                        "Reset link is invalid or expired.";
                                break;

                            case 404:
                                message =
                                        "Reset request not found.";
                                break;

                            case 429:
                                message =
                                        "Too many requests. Please try again later.";
                                break;

                            case 500:
                                message =
                                        "Server error. Please try again later.";
                                break;

                            default:
                                message =
                                        "Unable to reset password. "
                                                + "Error code: "
                                                + response.code();
                                break;
                        }

                        Toast.makeText(
                                ResetPasswordActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {

                        btnResetPassword.setEnabled(true);

                        btnResetPassword.setText(
                                "RESET PASSWORD"
                        );

                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "Unable to connect to server.",
                                Toast.LENGTH_LONG
                        ).show();

                        t.printStackTrace();
                    }
                });
    }
}