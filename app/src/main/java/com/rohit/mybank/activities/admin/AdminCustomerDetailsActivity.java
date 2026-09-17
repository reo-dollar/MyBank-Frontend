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

public class AdminCustomerDetailsActivity extends AppCompatActivity {

    // =========================================================
    // CUSTOMER HEADER
    // =========================================================

    private TextView tvCustomerAvatar;
    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvCustomerId;

    // =========================================================
    // BASIC INFORMATION
    // =========================================================

    private TextView tvDateOfBirth;
    private TextView tvGender;

    // =========================================================
    // CONTACT INFORMATION
    // =========================================================

    private TextView tvMobile;
    private TextView tvEmail;

    // =========================================================
    // KYC INFORMATION
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
    // ADDITIONAL INFORMATION
    // =========================================================

    private TextView tvOccupation;
    private TextView tvCreatedAt;

    // =========================================================
    // ACCOUNT INFORMATION
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

        // -----------------------------------------------------
        // INITIALIZE VIEWS
        // -----------------------------------------------------

        initializeViews();

        // -----------------------------------------------------
        // INITIALIZE REPOSITORY
        // -----------------------------------------------------

        repository =
                new AdminCustomerRepository(this);

        // -----------------------------------------------------
        // GET CUSTOMER ID
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

            finish();
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

        // -----------------------------------------------------
        // CUSTOMER HEADER
        // -----------------------------------------------------

        tvCustomerAvatar =
                findViewById(
                        R.id.tvCustomerAvatar
                );

        tvFullName =
                findViewById(
                        R.id.tvFullName
                );

        tvUsername =
                findViewById(
                        R.id.tvUsername
                );

        tvCustomerId =
                findViewById(
                        R.id.tvCustomerId
                );

        // -----------------------------------------------------
        // BASIC INFORMATION
        // -----------------------------------------------------

        tvDateOfBirth =
                findViewById(
                        R.id.tvDateOfBirth
                );

        tvGender =
                findViewById(
                        R.id.tvGender
                );

        // -----------------------------------------------------
        // CONTACT
        // -----------------------------------------------------

        tvMobile =
                findViewById(
                        R.id.tvMobile
                );

        tvEmail =
                findViewById(
                        R.id.tvEmail
                );

        // -----------------------------------------------------
        // KYC
        // -----------------------------------------------------

        tvAadhaar =
                findViewById(
                        R.id.tvAadhaar
                );

        tvPan =
                findViewById(
                        R.id.tvPan
                );

        // -----------------------------------------------------
        // ADDRESS
        // -----------------------------------------------------

        tvAddress =
                findViewById(
                        R.id.tvAddress
                );

        tvCity =
                findViewById(
                        R.id.tvCity
                );

        tvState =
                findViewById(
                        R.id.tvState
                );

        tvPincode =
                findViewById(
                        R.id.tvPincode
                );

        // -----------------------------------------------------
        // ADDITIONAL INFORMATION
        // -----------------------------------------------------

        tvOccupation =
                findViewById(
                        R.id.tvOccupation
                );

        tvCreatedAt =
                findViewById(
                        R.id.tvCreatedAt
                );

        // -----------------------------------------------------
        // ACCOUNT INFORMATION
        // -----------------------------------------------------

        tvRole =
                findViewById(
                        R.id.tvRole
                );

        tvUserStatus =
                findViewById(
                        R.id.tvUserStatus
                );

        tvAccountStatus =
                findViewById(
                        R.id.tvAccountStatus
                );

        // -----------------------------------------------------
        // ACTION BUTTONS
        // -----------------------------------------------------

        btnEnableUser =
                findViewById(
                        R.id.btnEnableUser
                );

        btnDisableUser =
                findViewById(
                        R.id.btnDisableUser
                );

        btnLockAccount =
                findViewById(
                        R.id.btnLockAccount
                );

        btnUnlockAccount =
                findViewById(
                        R.id.btnUnlockAccount
                );

        // -----------------------------------------------------
        // PROGRESS
        // -----------------------------------------------------

        progressBar =
                findViewById(
                        R.id.progressBar
                );
    }

    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    private void setupActionButtons() {

        // -----------------------------------------------------
        // ENABLE USER
        // -----------------------------------------------------

        if (btnEnableUser != null) {

            btnEnableUser.setOnClickListener(
                    v -> confirmAction(
                            "Enable User",
                            "Are you sure you want to enable this user?",
                            this::enableUser
                    )
            );
        }

        // -----------------------------------------------------
        // DISABLE USER
        // -----------------------------------------------------

        if (btnDisableUser != null) {

            btnDisableUser.setOnClickListener(
                    v -> confirmAction(
                            "Disable User",
                            "Are you sure you want to disable this user?",
                            this::disableUser
                    )
            );
        }

        // -----------------------------------------------------
        // LOCK ACCOUNT
        // -----------------------------------------------------

        if (btnLockAccount != null) {

            btnLockAccount.setOnClickListener(
                    v -> confirmAction(
                            "Lock Account",
                            "Are you sure you want to lock this account?",
                            this::lockAccount
                    )
            );
        }

        // -----------------------------------------------------
        // UNLOCK ACCOUNT
        // -----------------------------------------------------

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
                        (dialog, which) -> {

                            if (!isFinishing()) {
                                action.run();
                            }
                        }
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

        if (customer == null) {
            return;
        }

        // -----------------------------------------------------
        // CUSTOMER AVATAR
        // -----------------------------------------------------

        setCustomerAvatar(
                customer.getFullName()
        );

        // -----------------------------------------------------
        // FULL NAME
        // -----------------------------------------------------

        tvFullName.setText(
                safe(
                        customer.getFullName()
                )
        );

        // -----------------------------------------------------
        // USERNAME
        // -----------------------------------------------------

        String username =
                safe(customer.getUsername());

        if (!username.equals("N/A")) {

            tvUsername.setText(
                    "@" + username
            );

        } else {

            tvUsername.setText(
                    "N/A"
            );
        }

        // -----------------------------------------------------
        // CUSTOMER ID
        // -----------------------------------------------------

        tvCustomerId.setText(
                safe(
                        customer.getCustomerId()
                )
        );

        // -----------------------------------------------------
        // DATE OF BIRTH
        // -----------------------------------------------------

        tvDateOfBirth.setText(
                safe(
                        customer.getDateOfBirth()
                )
        );

        // -----------------------------------------------------
        // GENDER
        // -----------------------------------------------------

        tvGender.setText(
                safe(
                        customer.getGender()
                )
        );

        // -----------------------------------------------------
        // MOBILE
        // -----------------------------------------------------

        tvMobile.setText(
                safe(
                        customer.getMobile()
                )
        );

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        tvEmail.setText(
                safe(
                        customer.getEmail()
                )
        );

        // -----------------------------------------------------
        // AADHAAR
        // -----------------------------------------------------

        tvAadhaar.setText(
                safe(
                        customer.getMaskedAadhaarNumber()
                )
        );

        // -----------------------------------------------------
        // PAN
        // -----------------------------------------------------

        tvPan.setText(
                safe(
                        customer.getMaskedPanNumber()
                )
        );

        // -----------------------------------------------------
        // ADDRESS
        // -----------------------------------------------------

        tvAddress.setText(
                safe(
                        customer.getAddress()
                )
        );

        // -----------------------------------------------------
        // CITY
        // -----------------------------------------------------

        tvCity.setText(
                safe(
                        customer.getCity()
                )
        );

        // -----------------------------------------------------
        // STATE
        // -----------------------------------------------------

        tvState.setText(
                safe(
                        customer.getState()
                )
        );

        // -----------------------------------------------------
        // PINCODE
        // -----------------------------------------------------

        tvPincode.setText(
                safe(
                        customer.getPincode()
                )
        );

        // -----------------------------------------------------
        // OCCUPATION
        // -----------------------------------------------------

        tvOccupation.setText(
                safe(
                        customer.getOccupation()
                )
        );

        // -----------------------------------------------------
        // CUSTOMER SINCE
        // -----------------------------------------------------

        tvCreatedAt.setText(
                safe(
                        customer.getCreatedAt()
                )
        );

        // -----------------------------------------------------
        // ROLE
        // -----------------------------------------------------

        tvRole.setText(
                safe(
                        customer.getUserRole()
                )
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        updateStatus(customer);
    }

    // =========================================================
    // CUSTOMER AVATAR
    // =========================================================

    private void setCustomerAvatar(String fullName) {

        if (tvCustomerAvatar == null) {
            return;
        }

        if (fullName == null
                || fullName.trim().isEmpty()) {

            tvCustomerAvatar.setText("C");
            return;
        }

        String trimmedName =
                fullName.trim();

        String initial =
                trimmedName
                        .substring(0, 1)
                        .toUpperCase();

        tvCustomerAvatar.setText(
                initial
        );
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

        // =====================================================
        // USER STATUS
        // =====================================================

        if (locked) {

            tvUserStatus.setText(
                    "LOCKED"
            );

        } else if (enabled) {

            tvUserStatus.setText(
                    "ACTIVE"
            );

        } else {

            tvUserStatus.setText(
                    "DISABLED"
            );
        }

        // =====================================================
        // ACCOUNT STATUS
        // =====================================================

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

        // =====================================================
        // ENABLE BUTTON
        // =====================================================

        if (btnEnableUser != null) {

            btnEnableUser.setVisibility(
                    enabled
                            ? View.GONE
                            : View.VISIBLE
            );
        }

        // =====================================================
        // DISABLE BUTTON
        // =====================================================

        if (btnDisableUser != null) {

            btnDisableUser.setVisibility(
                    enabled
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        // =====================================================
        // LOCK BUTTON
        // =====================================================

        if (btnLockAccount != null) {

            btnLockAccount.setVisibility(
                    locked
                            ? View.GONE
                            : View.VISIBLE
            );
        }

        // =====================================================
        // UNLOCK BUTTON
        // =====================================================

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

        loadCustomer(
                customerId
        );
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

        return value.trim();
    }
}