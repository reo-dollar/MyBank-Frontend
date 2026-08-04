package com.rohit.mybank.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.model.profile.ChangePasswordRequest;
import com.rohit.mybank.repository.ProfileRepository;
import com.rohit.mybank.session.SessionManager;

import androidx.appcompat.app.AlertDialog;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;

    private MaterialButton btnChangePassword;
    private ProgressBar progressBar;

    private ProfileRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initializeViews();

        repository = new ProfileRepository(this);
        sessionManager = new SessionManager(this);

        btnChangePassword.setOnClickListener(v -> validateAndChangePassword());
    }

    private void initializeViews() {

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void validateAndChangePassword() {

        String currentPassword =
                etCurrentPassword.getText().toString().trim();

        String newPassword =
                etNewPassword.getText().toString().trim();

        String confirmPassword =
                etConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {

            etCurrentPassword.setError("Current password required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {

            etNewPassword.setError("New password required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 8) {

            etNewPassword.setError("Minimum 8 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.setError("Confirm password required");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnChangePassword.setEnabled(false);

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        currentPassword,
                        newPassword,
                        confirmPassword
                );

        repository.changePassword(request).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                progressBar.setVisibility(View.GONE);
                btnChangePassword.setEnabled(true);

                if (response.isSuccessful()) {

                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setTitle("Password Changed")
                            .setMessage("Your password has been changed successfully.\n\nPlease login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, which) -> {

                                        sessionManager.logout();

                                        Intent intent = new Intent(
                                                ChangePasswordActivity.this,
                                                LoginActivity.class
                                        );

                                        intent.setFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        );

                                        startActivity(intent);
                                        finish();
                                    })
                            .show();

                } else {

                    Toast.makeText(
                            ChangePasswordActivity.this,
                            "Unable to change password.",
                            Toast.LENGTH_LONG
                    ).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call,
                                  Throwable t) {

                progressBar.setVisibility(View.GONE);
                btnChangePassword.setEnabled(true);

                Toast.makeText(
                        ChangePasswordActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                t.printStackTrace();
            }
        });
    }
}