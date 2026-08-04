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

    private AuthRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

        repository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // Auto Login
        if (sessionManager.isLoggedIn()) {

            startActivity(new Intent(
                    LoginActivity.this,
                    DashboardActivity.class
            ));

            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> login());

        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    PersonalDetailsActivity.class
            );

            startActivity(intent);

        });
    }

    private void initializeViews() {

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void login() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {

            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(username, password);

        repository.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse loginResponse = response.body();

                    sessionManager.saveToken(
                            loginResponse.getAccessToken()
                    );

                    sessionManager.saveUsername(username);

                    Toast.makeText(
                            LoginActivity.this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                    ).show();

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
            public void onFailure(Call<LoginResponse> call,
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
        });
    }
}