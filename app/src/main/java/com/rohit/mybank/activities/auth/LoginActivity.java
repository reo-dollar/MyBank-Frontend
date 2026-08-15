package com.rohit.mybank.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.kyc.PersonalDetailsActivity;
import com.rohit.mybank.model.auth.LoginRequest;
import com.rohit.mybank.model.auth.LoginResponse;
import com.rohit.mybank.repository.AuthRepository;
import com.rohit.mybank.session.SessionManager;

import java.io.IOException;

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

    // ==========================================================
    // ACTIVITY LIFECYCLE
    // ==========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        repository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // ======================================================
        // EXISTING SESSION
        // ======================================================

        if (sessionManager.isLoggedIn()) {

            goToAuthentication();

            return;
        }

        // ======================================================
        // LOGIN
        // ======================================================

        btnLogin.setOnClickListener(v -> login());

        // ======================================================
        // REGISTER
        // ======================================================

        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    PersonalDetailsActivity.class
            );

            startActivity(intent);
        });

        // ======================================================
        // FORGOT PASSWORD
        // ======================================================

        tvForgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });
    }

    // ==========================================================
    // INITIALIZE VIEWS
    // ==========================================================

    private void initializeViews() {

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvRegister = findViewById(R.id.tvRegister);

        tvForgotPassword = findViewById(
                R.id.tvForgotPassword
        );
    }

    // ==========================================================
    // LOGIN
    // ==========================================================

    private void login() {

        String username = etUsername
                .getText()
                .toString()
                .trim();

        String password = etPassword
                .getText()
                .toString()
                .trim();

        // ======================================================
        // VALIDATE USERNAME
        // ======================================================

        if (username.isEmpty()) {

            etUsername.setError(
                    "Username is required"
            );

            etUsername.requestFocus();

            return;
        }

        // ======================================================
        // VALIDATE PASSWORD
        // ======================================================

        if (password.isEmpty()) {

            etPassword.setError(
                    "Password is required"
            );

            etPassword.requestFocus();

            return;
        }

        // ======================================================
        // DISABLE BUTTON
        // ======================================================

        btnLogin.setEnabled(false);

        // ======================================================
        // CREATE LOGIN REQUEST
        // ======================================================

        LoginRequest request = new LoginRequest(
                username,
                password,
                "ANDROID"
        );

        // ======================================================
        // DEBUG
        // ======================================================

        System.out.println("========================================");
        System.out.println("ANDROID LOGIN REQUEST");
        System.out.println("Username : " + username);
        System.out.println("DeviceId : ANDROID");
        System.out.println("========================================");

        // ======================================================
        // CALL BACKEND
        // ======================================================

        repository.login(request).enqueue(
                new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response
                    ) {

                        btnLogin.setEnabled(true);

                        System.out.println(
                                "LOGIN HTTP CODE : "
                                        + response.code()
                        );

                        // ==================================================
                        // SUCCESS
                        // ==================================================

                        if (response.isSuccessful()
                                && response.body() != null) {

                            LoginResponse loginResponse =
                                    response.body();

                            System.out.println(
                                    "LOGIN SUCCESS"
                            );

                            // ==============================================
                            // SAVE ACCESS TOKEN
                            // ==============================================

                            sessionManager.saveToken(
                                    loginResponse.getAccessToken()
                            );

                            // ==============================================
                            // SAVE REFRESH TOKEN
                            // ==============================================

                            sessionManager.saveRefreshToken(
                                    loginResponse.getRefreshToken()
                            );

                            // ==============================================
                            // SAVE USERNAME
                            // ==============================================

                            sessionManager.saveUsername(
                                    username
                            );

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // ==============================================
                            // AUTHENTICATION
                            // ==============================================

                            goToAuthentication();

                        } else {

                            // ==================================================
                            // LOGIN FAILED
                            // ==================================================

                            String errorMessage =
                                    getErrorMessage(response);

                            System.out.println(
                                    "LOGIN FAILED"
                            );

                            System.out.println(
                                    "HTTP CODE : "
                                            + response.code()
                            );

                            System.out.println(
                                    "ERROR     : "
                                            + errorMessage
                            );

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login failed (" +
                                            response.code() +
                                            "):\n" +
                                            errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponse> call,
                            Throwable t
                    ) {

                        btnLogin.setEnabled(true);

                        t.printStackTrace();

                        System.out.println(
                                "LOGIN NETWORK ERROR : "
                                        + t.getMessage()
                        );

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

    // ==========================================================
    // EXTRACT BACKEND ERROR
    // ==========================================================

    private String getErrorMessage(
            Response<LoginResponse> response
    ) {

        try {

            if (response.errorBody() != null) {

                String errorBody =
                        response.errorBody().string();

                if (!errorBody.trim().isEmpty()) {
                    return errorBody;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return "Server rejected the login request.";
    }

    // ==========================================================
    // GO TO AUTHENTICATION
    // ==========================================================

    private void goToAuthentication() {

        Intent intent = new Intent(
                LoginActivity.this,
                AuthenticationActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}