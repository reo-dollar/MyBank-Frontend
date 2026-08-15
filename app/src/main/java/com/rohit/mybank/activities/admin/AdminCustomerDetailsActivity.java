package com.rohit.mybank.activities.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.admin.AdminCustomerResponse;
import com.rohit.mybank.model.admin.AdminUserResponse;
import com.rohit.mybank.repository.AdminCustomerRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCustomerDetailsActivity
        extends AppCompatActivity {

    // =========================================================
    // CUSTOMER INFORMATION
    // =========================================================

    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvCustomerId;

    private TextView tvDateOfBirth;
    private TextView tvGender;

    // =========================================================
    // CONTACT
    // =========================================================

    private TextView tvMobile;
    private TextView tvEmail;

    // =========================================================
    // KYC
    // =========================================================

    private TextView tvAadhaar;
    private TextView tvPan;

    // =========================================================
    // ADDRESS
    // =========================================================

    private TextView tvAddress;
    private TextView tvCity;
    private TextView tvState;
    private TextView tvPincode;

    // =========================================================
    // OTHER
    // =========================================================

    private TextView tvOccupation;
    private TextView tvCreatedAt;

    // =========================================================
    // USER STATUS
    // =========================================================

    private TextView tvRole;
    private TextView tvUserStatus;
    private TextView tvAccountStatus;

    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    private Button btnEnableUser;
    private Button btnDisableUser;
    private Button btnLockAccount;
    private Button btnUnlockAccount;

    // =========================================================
    // PROGRESS
    // =========================================================

    private ProgressBar progressBar;

    // =========================================================
    // REPOSITORY
    // =========================================================

    private AdminCustomerRepository repository;

    // =========================================================
    // CURRENT CUSTOMER
    // =========================================================

    private AdminCustomerResponse currentCustomer;

    // =========================================================
    // CUSTOMER ID
    // =========================================================

    private String customerId;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_customer_details
        );

        initializeViews();

        repository =
                new AdminCustomerRepository(this);

        // -----------------------------------------------------
        // READ CUSTOMER ID FROM INTENT
        // -----------------------------------------------------

        customerId =
                getIntent().getStringExtra("customerId");

        if (customerId == null
                || customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer ID is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        customerId = customerId.trim();

        // -----------------------------------------------------
        // SETUP ACTION BUTTONS
        // -----------------------------------------------------

        setupActionButtons();

        // -----------------------------------------------------
        // LOAD CUSTOMER
        // -----------------------------------------------------

        loadCustomer(customerId);
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvFullName =
                findViewById(R.id.tvFullName);

        tvUsername =
                findViewById(R.id.tvUsername);

        tvCustomerId =
                findViewById(R.id.tvCustomerId);

        tvDateOfBirth =
                findViewById(R.id.tvDateOfBirth);

        tvGender =
                findViewById(R.id.tvGender);

        tvMobile =
                findViewById(R.id.tvMobile);

        tvEmail =
                findViewById(R.id.tvEmail);

        tvAadhaar =
                findViewById(R.id.tvAadhaar);

        tvPan =
                findViewById(R.id.tvPan);

        tvAddress =
                findViewById(R.id.tvAddress);

        tvCity =
                findViewById(R.id.tvCity);

        tvState =
                findViewById(R.id.tvState);

        tvPincode =
                findViewById(R.id.tvPincode);

        tvOccupation =
                findViewById(R.id.tvOccupation);

        tvCreatedAt =
                findViewById(R.id.tvCreatedAt);

        tvRole =
                findViewById(R.id.tvRole);

        tvUserStatus =
                findViewById(R.id.tvUserStatus);

        tvAccountStatus =
                findViewById(R.id.tvAccountStatus);

        btnEnableUser =
                findViewById(R.id.btnEnableUser);

        btnDisableUser =
                findViewById(R.id.btnDisableUser);

        btnLockAccount =
                findViewById(R.id.btnLockAccount);

        btnUnlockAccount =
                findViewById(R.id.btnUnlockAccount);

        progressBar =
                findViewById(R.id.progressBar);
    }

    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    private void setupActionButtons() {

        if (btnEnableUser != null) {

            btnEnableUser.setOnClickListener(
                    v -> confirmAction(
                            "Enable User",
                            "Are you sure you want to enable this user?",
                            this::enableUser
                    )
            );
        }

        if (btnDisableUser != null) {

            btnDisableUser.setOnClickListener(
                    v -> confirmAction(
                            "Disable User",
                            "Are you sure you want to disable this user?",
                            this::disableUser
                    )
            );
        }

        if (btnLockAccount != null) {

            btnLockAccount.setOnClickListener(
                    v -> confirmAction(
                            "Lock Account",
                            "Are you sure you want to lock this account?",
                            this::lockAccount
                    )
            );
        }

        if (btnUnlockAccount != null) {

            btnUnlockAccount.setOnClickListener(
                    v -> confirmAction(
                            "Unlock Account",
                            "Are you sure you want to unlock this account?",
                            this::unlockAccount
                    )
            );
        }
    }

    // =========================================================
    // CONFIRMATION DIALOG
    // =========================================================

    private void confirmAction(
            String title,
            String message,
            Runnable action) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Confirm",
                        (dialog, which) -> action.run()
                )
                .show();
    }

    // =========================================================
    // LOAD CUSTOMER
    // =========================================================

    private void loadCustomer(String customerId) {

        showLoading(true);

        repository
                .getCustomer(customerId)
                .enqueue(
                        new Callback<AdminCustomerResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminCustomerResponse> call,
                                    Response<AdminCustomerResponse> response) {

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    currentCustomer =
                                            response.body();

                                    displayCustomer(
                                            currentCustomer
                                    );

                                    return;
                                }

                                handleHttpError(
                                        response.code()
                                );
                            }

                            @Override
                            public void onFailure(
                                    Call<AdminCustomerResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                showError(
                                        "Unable to load customer.",
                                        t
                                );
                            }
                        }
                );
    }

    // =========================================================
    // DISPLAY CUSTOMER
    // =========================================================

    private void displayCustomer(
            AdminCustomerResponse customer) {

        tvFullName.setText(
                safe(customer.getFullName())
        );

        tvUsername.setText(
                "@" + safe(customer.getUsername())
        );

        tvCustomerId.setText(
                safe(customer.getCustomerId())
        );

        tvDateOfBirth.setText(
                safe(customer.getDateOfBirth())
        );

        tvGender.setText(
                safe(customer.getGender())
        );

        tvMobile.setText(
                safe(customer.getMobile())
        );

        tvEmail.setText(
                safe(customer.getEmail())
        );

        tvAadhaar.setText(
                safe(
                        customer.getMaskedAadhaarNumber()
                )
        );

        tvPan.setText(
                safe(
                        customer.getMaskedPanNumber()
                )
        );

        tvAddress.setText(
                safe(customer.getAddress())
        );

        tvCity.setText(
                safe(customer.getCity())
        );

        tvState.setText(
                safe(customer.getState())
        );

        tvPincode.setText(
                safe(customer.getPincode())
        );

        tvOccupation.setText(
                safe(customer.getOccupation())
        );

        tvCreatedAt.setText(
                safe(customer.getCreatedAt())
        );

        tvRole.setText(
                safe(customer.getUserRole())
        );

        updateStatus(customer);
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    private void updateStatus(
            AdminCustomerResponse customer) {

        boolean enabled =
                Boolean.TRUE.equals(
                        customer.getUserEnabled()
                );

        boolean locked =
                Boolean.TRUE.equals(
                        customer.getAccountLocked()
                );

        // -----------------------------------------------------
        // USER STATUS
        // -----------------------------------------------------

        if (locked) {

            tvUserStatus.setText("LOCKED");

        } else if (enabled) {

            tvUserStatus.setText("ACTIVE");

        } else {

            tvUserStatus.setText("DISABLED");
        }

        // -----------------------------------------------------
        // ACCOUNT STATUS
        // -----------------------------------------------------

        if (locked) {

            tvAccountStatus.setText(
                    "Account Locked"
            );

        } else if (enabled) {

            tvAccountStatus.setText(
                    "Account Enabled"
            );

        } else {

            tvAccountStatus.setText(
                    "Account Disabled"
            );
        }

        // -----------------------------------------------------
        // BUTTON VISIBILITY
        // -----------------------------------------------------

        if (btnEnableUser != null) {

            btnEnableUser.setVisibility(
                    enabled
                            ? View.GONE
                            : View.VISIBLE
            );
        }

        if (btnDisableUser != null) {

            btnDisableUser.setVisibility(
                    enabled
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (btnLockAccount != null) {

            btnLockAccount.setVisibility(
                    locked
                            ? View.GONE
                            : View.VISIBLE
            );
        }

        if (btnUnlockAccount != null) {

            btnUnlockAccount.setVisibility(
                    locked
                            ? View.VISIBLE
                            : View.GONE
            );
        }
    }

    // =========================================================
    // ENABLE USER
    // =========================================================

    private void enableUser() {

        if (!hasUsername()) {
            return;
        }

        showLoading(true);

        repository
                .enableUser(
                        currentCustomer.getUsername()
                )
                .enqueue(
                        new Callback<AdminUserResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminUserResponse> call,
                                    Response<AdminUserResponse> response) {

                                showLoading(false);

                                if (response.isSuccessful()) {

                                    Toast.makeText(
                                            AdminCustomerDetailsActivity.this,
                                            "User enabled successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    reloadCustomer();

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<AdminUserResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                showError(
                                        "Unable to enable user.",
                                        t
                                );
                            }
                        }
                );
    }

    // =========================================================
    // DISABLE USER
    // =========================================================

    private void disableUser() {

        if (!hasUsername()) {
            return;
        }

        showLoading(true);

        repository
                .disableUser(
                        currentCustomer.getUsername()
                )
                .enqueue(
                        new Callback<AdminUserResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminUserResponse> call,
                                    Response<AdminUserResponse> response) {

                                showLoading(false);

                                if (response.isSuccessful()) {

                                    Toast.makeText(
                                            AdminCustomerDetailsActivity.this,
                                            "User disabled successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    reloadCustomer();

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<AdminUserResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                showError(
                                        "Unable to disable user.",
                                        t
                                );
                            }
                        }
                );
    }

    // =========================================================
    // LOCK ACCOUNT
    // =========================================================

    private void lockAccount() {

        if (!hasUsername()) {
            return;
        }

        showLoading(true);

        repository
                .lockAccount(
                        currentCustomer.getUsername()
                )
                .enqueue(
                        new Callback<AdminUserResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminUserResponse> call,
                                    Response<AdminUserResponse> response) {

                                showLoading(false);

                                if (response.isSuccessful()) {

                                    Toast.makeText(
                                            AdminCustomerDetailsActivity.this,
                                            "Account locked successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    reloadCustomer();

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<AdminUserResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                showError(
                                        "Unable to lock account.",
                                        t
                                );
                            }
                        }
                );
    }

    // =========================================================
    // UNLOCK ACCOUNT
    // =========================================================

    private void unlockAccount() {

        if (!hasUsername()) {
            return;
        }

        showLoading(true);

        repository
                .unlockAccount(
                        currentCustomer.getUsername()
                )
                .enqueue(
                        new Callback<AdminUserResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminUserResponse> call,
                                    Response<AdminUserResponse> response) {

                                showLoading(false);

                                if (response.isSuccessful()) {

                                    Toast.makeText(
                                            AdminCustomerDetailsActivity.this,
                                            "Account unlocked successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    reloadCustomer();

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<AdminUserResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                showError(
                                        "Unable to unlock account.",
                                        t
                                );
                            }
                        }
                );
    }

    // =========================================================
    // RELOAD CUSTOMER
    // =========================================================

    private void reloadCustomer() {

        if (customerId == null
                || customerId.trim().isEmpty()) {

            return;
        }

        loadCustomer(customerId);
    }

    // =========================================================
    // USERNAME VALIDATION
    // =========================================================

    private boolean hasUsername() {

        if (currentCustomer == null) {

            Toast.makeText(
                    this,
                    "Customer information is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        String username =
                currentCustomer.getUsername();

        if (username == null
                || username.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Linked username is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void showLoading(boolean loading) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    loading
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (btnEnableUser != null) {
            btnEnableUser.setEnabled(!loading);
        }

        if (btnDisableUser != null) {
            btnDisableUser.setEnabled(!loading);
        }

        if (btnLockAccount != null) {
            btnLockAccount.setEnabled(!loading);
        }

        if (btnUnlockAccount != null) {
            btnUnlockAccount.setEnabled(!loading);
        }
    }

    // =========================================================
    // HTTP ERROR
    // =========================================================

    private void handleHttpError(int code) {

        if (code == 401) {

            /*
             * IMPORTANT:
             *
             * Do NOT finish this Activity here.
             *
             * Previously this method called finish(), which
             * could make the app appear to jump back to the
             * previous screen.
             */

            Toast.makeText(
                    this,
                    "Session expired or authentication failed.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (code == 403) {

            Toast.makeText(
                    this,
                    "Access denied. Administrator privileges required.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (code == 404) {

            Toast.makeText(
                    this,
                    "Customer not found.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (code >= 500) {

            Toast.makeText(
                    this,
                    "Server error. Please try again later.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Request failed. HTTP " + code,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // NETWORK ERROR
    // =========================================================

    private void showError(
            String prefix,
            Throwable throwable) {

        String message =
                throwable != null
                        ? throwable.getMessage()
                        : null;

        if (message == null
                || message.trim().isEmpty()) {

            message =
                    "Unable to connect to server.";
        }

        Toast.makeText(
                this,
                prefix + "\n" + message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }
}