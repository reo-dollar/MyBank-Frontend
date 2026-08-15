package com.rohit.mybank.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.admin.AdminAccountResponse;
import com.rohit.mybank.repository.AdminAccountRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccountDetailsActivity extends AppCompatActivity {

    // =========================================================
    // CONSTANT
    // =========================================================

    public static final String EXTRA_ACCOUNT_NUMBER = "accNo";

    // =========================================================
    // ACCOUNT VIEWS
    // =========================================================

    private TextView tvAccountNumber;
    private TextView tvAccountType;
    private TextView tvBranch;
    private TextView tvIfsc;
    private TextView tvBalance;
    private TextView tvStatus;

    // =========================================================
    // CUSTOMER VIEWS
    // =========================================================

    private TextView tvCustomerName;
    private TextView tvCustomerId;
    private TextView tvUsername;
    private TextView tvMobile;
    private TextView tvEmail;

    // =========================================================
    // STATUS
    // =========================================================

    private TextView tvStatusBadge;

    // =========================================================
    // BUTTON
    // =========================================================

    private Button btnViewTransactions;

    // =========================================================
    // LOADING
    // =========================================================

    private ProgressBar progressBar;

    // =========================================================
    // REPOSITORY
    // =========================================================

    private AdminAccountRepository repository;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_account_details
        );

        initializeViews();

        repository =
                new AdminAccountRepository(this);

        String accNo =
                getIntent().getStringExtra(
                        EXTRA_ACCOUNT_NUMBER
                );

        // =====================================================
        // VALIDATE ACCOUNT NUMBER
        // =====================================================

        if (accNo == null
                || accNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid account number.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // =====================================================
        // VIEW TRANSACTIONS BUTTON
        // =====================================================

        final String finalAccNo = accNo;

        btnViewTransactions.setOnClickListener(
                v -> openTransactionHistory(finalAccNo)
        );

        // =====================================================
        // LOAD ACCOUNT
        // =====================================================

        loadAccountDetails(accNo);
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // -----------------------------------------------------
        // Account information
        // -----------------------------------------------------

        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );

        tvAccountType =
                findViewById(
                        R.id.tvAccountType
                );

        tvBranch =
                findViewById(
                        R.id.tvBranch
                );

        tvIfsc =
                findViewById(
                        R.id.tvIfsc
                );

        tvBalance =
                findViewById(
                        R.id.tvBalance
                );

        tvStatus =
                findViewById(
                        R.id.tvStatus
                );

        // -----------------------------------------------------
        // Customer information
        // -----------------------------------------------------

        tvCustomerName =
                findViewById(
                        R.id.tvCustomerName
                );

        tvCustomerId =
                findViewById(
                        R.id.tvCustomerId
                );

        tvUsername =
                findViewById(
                        R.id.tvUsername
                );

        tvMobile =
                findViewById(
                        R.id.tvMobile
                );

        tvEmail =
                findViewById(
                        R.id.tvEmail
                );

        // -----------------------------------------------------
        // Status
        // -----------------------------------------------------

        tvStatusBadge =
                findViewById(
                        R.id.tvStatusBadge
                );

        // -----------------------------------------------------
        // View Transactions
        // -----------------------------------------------------

        btnViewTransactions =
                findViewById(
                        R.id.btnViewTransactions
                );

        // -----------------------------------------------------
        // Loading
        // -----------------------------------------------------

        progressBar =
                findViewById(
                        R.id.progressBar
                );
    }

    // =========================================================
    // LOAD ACCOUNT DETAILS
    // =========================================================

    private void loadAccountDetails(
            String accNo) {

        showLoading(true);

        repository
                .getAccount(accNo)
                .enqueue(
                        new Callback<AdminAccountResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminAccountResponse> call,
                                    Response<AdminAccountResponse> response) {

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    displayAccountDetails(
                                            response.body()
                                    );

                                } else {

                                    Toast.makeText(
                                            AdminAccountDetailsActivity.this,
                                            "Unable to load account details. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<AdminAccountResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                Toast.makeText(
                                        AdminAccountDetailsActivity.this,
                                        "Network Error: "
                                                + safeMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // DISPLAY ACCOUNT DETAILS
    // =========================================================

    private void displayAccountDetails(
            AdminAccountResponse account) {

        // -----------------------------------------------------
        // ACCOUNT INFORMATION
        // -----------------------------------------------------

        tvAccountNumber.setText(
                safe(account.getAccNo())
        );

        tvAccountType.setText(
                safe(account.getAccountType())
        );

        tvBranch.setText(
                safe(account.getBranchName())
        );

        tvIfsc.setText(
                safe(account.getIfscCode())
        );

        tvBalance.setText(
                formatCurrency(
                        account.getBalance()
                )
        );

        tvStatus.setText(
                safe(account.getStatus())
        );

        // -----------------------------------------------------
        // CUSTOMER INFORMATION
        // -----------------------------------------------------

        tvCustomerName.setText(
                safe(account.getCustomerName())
        );

        tvCustomerId.setText(
                safe(account.getCustomerId())
        );

        tvUsername.setText(
                "@" + safe(account.getUsername())
        );

        tvMobile.setText(
                safe(account.getMobile())
        );

        tvEmail.setText(
                safe(account.getEmail())
        );

        // -----------------------------------------------------
        // STATUS BADGE
        // -----------------------------------------------------

        String status =
                safe(account.getStatus());

        tvStatusBadge.setText(
                "● " + status.toUpperCase()
        );
    }

    // =========================================================
    // OPEN TRANSACTION HISTORY
    // =========================================================

    private void openTransactionHistory(
            String accNo) {

        if (accNo == null
                || accNo.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid account number.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        AdminAccountDetailsActivity.this,
                        AdminAccountTransactionActivity.class
                );

        intent.putExtra(
                AdminAccountTransactionActivity.EXTRA_ACCOUNT_NUMBER,
                accNo
        );

        startActivity(intent);
    }

    // =========================================================
    // CURRENCY
    // =========================================================

    private String formatCurrency(
            double amount) {

        return String.format(
                java.util.Locale.getDefault(),
                "₹ %.2f",
                amount
        );
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void showLoading(
            boolean loading) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    loading
                            ? View.VISIBLE
                            : View.GONE
            );
        }
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String safeMessage(
            Throwable t) {

        if (t == null
                || t.getMessage() == null
                || t.getMessage().trim().isEmpty()) {

            return "Unable to connect to server.";
        }

        return t.getMessage();
    }
}