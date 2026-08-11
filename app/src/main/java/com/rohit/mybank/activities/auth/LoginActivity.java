package com.rohit.mybank.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.activities.kyc.PersonalDetailsActivity;
import com.rohit.mybank.model.auth.LoginRequest;
import com.rohit.mybank.model.auth.LoginResponse;
import com.rohit.mybank.repository.AuthRepository;
import com.rohit.mybank.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;

    private AuthRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

        repository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // ==========================================
        // Auto Login
        // ==========================================

        if (sessionManager.isLoggedIn()) {

            startActivity(new Intent(
                    LoginActivity.this,
                    DashboardActivity.class
            ));

            finish();
            return;
        }

        // ==========================================
        // Login Button
        // ==========================================

        btnLogin.setOnClickListener(v -> login());

        // ==========================================
        // Register
        // ==========================================

        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    PersonalDetailsActivity.class
            );

            startActivity(intent);

        });

        // ==========================================
        // Forgot Password
        // ==========================================

        tvForgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);

        });
    }

    private void initializeViews() {

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

    }

    private void login() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // ==========================================
        // Username Validation
        // ==========================================

        if (username.isEmpty()) {

            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;

        }

        // ==========================================
        // Password Validation
        // ==========================================

        if (password.isEmpty()) {

            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;

        }

        // Disable button while request is running
        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(
                username,
                password
        );

        repository.login(request).enqueue(
                new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response) {

                        btnLogin.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            LoginResponse loginResponse =
                                    response.body();

                            // ==========================================
                            // Save Access Token
                            // ==========================================

                            sessionManager.saveToken(
                                    loginResponse.getAccessToken()
                            );

                            // ==========================================
                            // Save Refresh Token
                            // ==========================================

                            sessionManager.saveRefreshToken(
                                    loginResponse.getRefreshToken()
                            );

                            // ==========================================
                            // Save Username
                            // ==========================================

                            sessionManager.saveUsername(username);

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // ==========================================
                            // Open Dashboard
                            // ==========================================

                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    DashboardActivity.class
                            );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();

                        } else {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Invalid username or password.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponse> call,
                            Throwable t) {

                        btnLogin.setEnabled(true);

                        t.printStackTrace();

                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to connect to server.\n\n"
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }
                }
        );
    }
}