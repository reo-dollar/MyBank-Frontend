package com.rohit.mybank.activities.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.auth.ForgotPasswordRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendResetLink;
    private TextView tvBackToLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        initializeViews();

        // ============================================
        // Initialize Retrofit API
        // ============================================

        apiService = RetrofitClient
                .getClient(this)
                .create(ApiService.class);

        // ============================================
        // Send Reset Link
        // ============================================

        btnSendResetLink.setOnClickListener(
                v -> sendResetLink()
        );

        // ============================================
        // Back To Login
        // ============================================

        tvBackToLogin.setOnClickListener(
                v -> finish()
        );
    }

    /**
     * Initialize Views
     */
    private void initializeViews() {

        etEmail = findViewById(
                R.id.etEmail
        );

        btnSendResetLink = findViewById(
                R.id.btnSendResetLink
        );

        tvBackToLogin = findViewById(
                R.id.tvBackToLogin
        );
    }

    /**
     * Validate email and send
     * forgot-password request to backend.
     */
    private void sendResetLink() {

        String email = etEmail
                .getText()
                .toString()
                .trim();

        // ============================================
        // 1. Email Required
        // ============================================

        if (TextUtils.isEmpty(email)) {

            etEmail.setError(
                    "Email address is required"
            );

            etEmail.requestFocus();

            return;
        }

        // ============================================
        // 2. Email Format Validation
        // ============================================

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etEmail.setError(
                    "Enter a valid email address"
            );

            etEmail.requestFocus();

            return;
        }

        // ============================================
        // 3. Disable Button
        // ============================================

        btnSendResetLink.setEnabled(false);

        btnSendResetLink.setText(
                "SENDING..."
        );

        // ============================================
        // 4. Create Request
        // ============================================

        ForgotPasswordRequest request =
                new ForgotPasswordRequest(email);

        // ============================================
        // 5. Send Request To Backend
        // ============================================

        apiService
                .forgotPassword(request)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {

                        // Re-enable button
                        btnSendResetLink.setEnabled(true);

                        btnSendResetLink.setText(
                                "SEND RESET LINK"
                        );

                        // ====================================
                        // SUCCESS
                        // ====================================

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Password reset link sent to your email.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        // ====================================
                        // SERVER ERROR
                        // ====================================

                        String message;

                        switch (response.code()) {

                            case 400:
                                message =
                                        "Invalid forgot-password request.";
                                break;

                            case 404:
                                message =
                                        "Email address is not registered.";
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
                                        "Unable to send reset link. "
                                                + "Error code: "
                                                + response.code();
                                break;
                        }

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {

                        // ====================================
                        // Re-enable Button
                        // ====================================

                        btnSendResetLink.setEnabled(true);

                        btnSendResetLink.setText(
                                "SEND RESET LINK"
                        );

                        // ====================================
                        // Network Error
                        // ====================================

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Unable to connect to server.",
                                Toast.LENGTH_LONG
                        ).show();

                        // Print exact error in Logcat
                        t.printStackTrace();
                    }
                });
    }
}