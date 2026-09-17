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

import java.util.Locale;

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


        // =====================================================
        // GET ACCOUNT NUMBER
        // =====================================================

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
                    getString(
                            R.string.invalid_account_number
                    ),
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        accNo = accNo.trim();


        // =====================================================
        // VIEW TRANSACTIONS
        // =====================================================

        final String finalAccNo = accNo;

        if (btnViewTransactions != null) {

            btnViewTransactions.setOnClickListener(
                    v -> openTransactionHistory(finalAccNo)
            );
        }


        // =====================================================
        // LOAD ACCOUNT
        // =====================================================

        loadAccountDetails(finalAccNo);
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

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


        tvStatusBadge =
                findViewById(
                        R.id.tvStatusBadge
                );


        btnViewTransactions =
                findViewById(
                        R.id.btnViewTransactions
                );


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
                                            getString(
                                                    R.string.admin_account_load_failed,
                                                    response.code()
                                            ),
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
                                        getString(
                                                R.string.network_error
                                        )
                                                + ": "
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

        if (account == null) {
            return;
        }


        // -----------------------------------------------------
        // ACCOUNT INFORMATION
        // -----------------------------------------------------

        if (tvAccountNumber != null) {

            tvAccountNumber.setText(
                    safe(account.getAccNo())
            );
        }


        if (tvAccountType != null) {

            tvAccountType.setText(
                    safe(account.getAccountType())
            );
        }


        if (tvBranch != null) {

            tvBranch.setText(
                    safe(account.getBranchName())
            );
        }


        if (tvIfsc != null) {

            tvIfsc.setText(
                    safe(account.getIfscCode())
            );
        }


        if (tvBalance != null) {

            tvBalance.setText(
                    formatCurrency(
                            account.getBalance()
                    )
            );
        }


        if (tvStatus != null) {

            tvStatus.setText(
                    safe(account.getStatus())
            );
        }


        // -----------------------------------------------------
        // CUSTOMER INFORMATION
        // -----------------------------------------------------

        if (tvCustomerName != null) {

            tvCustomerName.setText(
                    safe(account.getCustomerName())
            );
        }


        if (tvCustomerId != null) {

            tvCustomerId.setText(
                    safe(account.getCustomerId())
            );
        }


        if (tvUsername != null) {

            String username =
                    safe(account.getUsername());

            if ("N/A".equals(username)) {

                tvUsername.setText(
                        getString(
                                R.string.not_available
                        )
                );

            } else {

                tvUsername.setText(
                        getString(
                                R.string.admin_username_format,
                                username
                        )
                );
            }
        }


        if (tvMobile != null) {

            tvMobile.setText(
                    safe(account.getMobile())
            );
        }


        if (tvEmail != null) {

            tvEmail.setText(
                    safe(account.getEmail())
            );
        }


        // -----------------------------------------------------
        // STATUS BADGE
        // -----------------------------------------------------

        updateStatusBadge(
                account.getStatus()
        );
    }


    // =========================================================
    // UPDATE STATUS BADGE
    // =========================================================

    private void updateStatusBadge(
            String status) {

        if (tvStatusBadge == null) {
            return;
        }


        String safeStatus =
                safe(status);


        if ("N/A".equals(safeStatus)) {

            tvStatusBadge.setText(
                    getString(
                            R.string.admin_status_unknown
                    )
            );

            return;
        }


        String formattedStatus =
                safeStatus.toUpperCase(
                        Locale.ROOT
                );


        tvStatusBadge.setText(
                getString(
                        R.string.admin_status_badge,
                        formattedStatus
                )
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
                    getString(
                            R.string.invalid_account_number
                    ),
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
                accNo.trim()
        );


        startActivity(intent);
    }


    // =========================================================
    // CURRENCY
    // =========================================================

    private String formatCurrency(
            double amount) {

        return String.format(
                Locale.getDefault(),
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

        return value.trim();
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


        if (btnViewTransactions != null) {

            btnViewTransactions.setEnabled(
                    !loading
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

            return getString(
                    R.string.unable_to_connect_server
            );
        }

        return t.getMessage();
    }
}