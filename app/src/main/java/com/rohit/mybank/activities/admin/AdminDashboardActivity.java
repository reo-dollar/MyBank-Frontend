package com.rohit.mybank.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.model.admin.AdminDashboardResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.session.SessionManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * ADMIN DASHBOARD ACTIVITY
 * =========================================================
 *
 * Displays system-wide banking statistics and provides
 * navigation to:
 *
 * 1. Admin User Management
 * 2. Admin Customer Management
 * 3. Admin Account Management
 * 4. Admin Transaction Management
 * 5. Admin Loan Management
 *
 * Admin Loan Management will be used for:
 *
 * - Viewing pending loan applications
 * - Viewing loan details
 * - Approving loans
 * - Rejecting loans
 */
public class AdminDashboardActivity
        extends AppCompatActivity {


    // ==========================================================
    // VIEWS
    // ==========================================================

    private TextView tvAdminTitle;
    private TextView tvAdminStatus;

    private TextView tvTotalUsers;
    private TextView tvTotalCustomers;
    private TextView tvTotalAccounts;

    private TextView tvTotalTransactions;
    private TextView tvTotalDeposits;
    private TextView tvTotalWithdrawals;
    private TextView tvTotalTransfers;
    private TextView tvTotalPayments;

    private TextView tvTotalDepositAmount;
    private TextView tvTotalWithdrawalAmount;
    private TextView tvTotalTransferAmount;
    private TextView tvTotalPaymentAmount;

    private ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;


    // ==========================================================
    // ADMINISTRATION BUTTONS
    // ==========================================================

    private Button btnUserManagement;

    private Button btnCustomerManagement;

    private Button btnAccountManagement;

    private Button btnTransactionManagement;

    /**
     * Loan management button.
     *
     * Opens AdminLoanManagementActivity.
     */
    private Button btnLoanManagement;

    private Button btnAdminLogout;


    // ==========================================================
    // REPOSITORY
    // ==========================================================

    private DashboardRepository dashboardRepository;

    private SessionManager sessionManager;


    // ==========================================================
    // ACTIVITY STATE
    // ==========================================================

    private boolean firstResume = true;


    // ==========================================================
    // ON CREATE
    // ==========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_dashboard
        );


        // ------------------------------------------------------
        // INITIALIZE VIEWS
        // ------------------------------------------------------

        initializeViews();


        // ------------------------------------------------------
        // INITIALIZE REPOSITORIES
        // ------------------------------------------------------

        dashboardRepository =
                new DashboardRepository(this);

        sessionManager =
                new SessionManager(this);


        // ------------------------------------------------------
        // SETUP UI
        // ------------------------------------------------------

        setupRefresh();

        setupLogout();

        setupUserManagement();

        setupCustomerManagement();

        setupAccountManagement();

        setupTransactionManagement();

        setupLoanManagement();


        // ------------------------------------------------------
        // LOAD DASHBOARD
        // ------------------------------------------------------

        loadAdminDashboard();
    }


    // ==========================================================
    // INITIALIZE VIEWS
    // ==========================================================

    private void initializeViews() {

        // ------------------------------------------------------
        // TITLE
        // ------------------------------------------------------

        tvAdminTitle =
                findViewById(
                        R.id.tvAdminTitle
                );


        // ------------------------------------------------------
        // STATUS
        // ------------------------------------------------------

        tvAdminStatus =
                findViewById(
                        R.id.tvAdminStatus
                );


        // ------------------------------------------------------
        // SYSTEM OVERVIEW
        // ------------------------------------------------------

        tvTotalUsers =
                findViewById(
                        R.id.tvTotalUsers
                );


        tvTotalCustomers =
                findViewById(
                        R.id.tvTotalCustomers
                );


        tvTotalAccounts =
                findViewById(
                        R.id.tvTotalAccounts
                );


        tvTotalTransactions =
                findViewById(
                        R.id.tvTotalTransactions
                );


        // ------------------------------------------------------
        // TRANSACTION ACTIVITY
        // ------------------------------------------------------

        tvTotalDeposits =
                findViewById(
                        R.id.tvTotalDeposits
                );


        tvTotalWithdrawals =
                findViewById(
                        R.id.tvTotalWithdrawals
                );


        tvTotalTransfers =
                findViewById(
                        R.id.tvTotalTransfers
                );


        tvTotalPayments =
                findViewById(
                        R.id.tvTotalPayments
                );


        // ------------------------------------------------------
        // FINANCIAL SUMMARY
        // ------------------------------------------------------

        tvTotalDepositAmount =
                findViewById(
                        R.id.tvTotalDepositAmount
                );


        tvTotalWithdrawalAmount =
                findViewById(
                        R.id.tvTotalWithdrawalAmount
                );


        tvTotalTransferAmount =
                findViewById(
                        R.id.tvTotalTransferAmount
                );


        tvTotalPaymentAmount =
                findViewById(
                        R.id.tvTotalPaymentAmount
                );


        // ------------------------------------------------------
        // PROGRESS
        // ------------------------------------------------------

        progressBar =
                findViewById(
                        R.id.progressBar
                );


        // ------------------------------------------------------
        // SWIPE REFRESH
        // ------------------------------------------------------

        swipeRefreshLayout =
                findViewById(
                        R.id.swipeRefreshLayout
                );


        // ======================================================
        // USER MANAGEMENT
        // ======================================================

        btnUserManagement =
                findViewById(
                        R.id.btnUserManagement
                );


        // ======================================================
        // CUSTOMER MANAGEMENT
        // ======================================================

        btnCustomerManagement =
                findViewById(
                        R.id.btnCustomerManagement
                );


        // ======================================================
        // ACCOUNT MANAGEMENT
        // ======================================================

        btnAccountManagement =
                findViewById(
                        R.id.btnAccountManagement
                );


        // ======================================================
        // TRANSACTION MANAGEMENT
        // ======================================================

        btnTransactionManagement =
                findViewById(
                        R.id.btnTransactionManagement
                );


        // ======================================================
        // LOAN MANAGEMENT
        // ======================================================

        btnLoanManagement =
                findViewById(
                        R.id.btnLoanManagement
                );


        // ======================================================
        // LOGOUT
        // ======================================================

        btnAdminLogout =
                findViewById(
                        R.id.btnAdminLogout
                );
    }


    // ==========================================================
    // SETUP REFRESH
    // ==========================================================

    private void setupRefresh() {

        if (swipeRefreshLayout == null) {
            return;
        }


        swipeRefreshLayout.setOnRefreshListener(
                this::loadAdminDashboard
        );
    }


    // ==========================================================
    // SETUP USER MANAGEMENT
    // ==========================================================

    private void setupUserManagement() {

        if (btnUserManagement == null) {
            return;
        }


        btnUserManagement.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    AdminDashboardActivity.this,
                                    AdminUserManagementActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    // ==========================================================
    // SETUP CUSTOMER MANAGEMENT
    // ==========================================================

    private void setupCustomerManagement() {

        if (btnCustomerManagement == null) {
            return;
        }


        btnCustomerManagement.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    AdminDashboardActivity.this,
                                    AdminCustomerManagementActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    // ==========================================================
    // SETUP ACCOUNT MANAGEMENT
    // ==========================================================

    private void setupAccountManagement() {

        if (btnAccountManagement == null) {
            return;
        }


        btnAccountManagement.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    AdminDashboardActivity.this,
                                    AdminAccountManagementActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    // ==========================================================
    // SETUP TRANSACTION MANAGEMENT
    // ==========================================================

    private void setupTransactionManagement() {

        if (btnTransactionManagement == null) {
            return;
        }


        btnTransactionManagement.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    AdminDashboardActivity.this,
                                    AdminTransactionsActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    // ==========================================================
    // SETUP LOAN MANAGEMENT
    // ==========================================================

    private void setupLoanManagement() {

        if (btnLoanManagement == null) {
            return;
        }


        btnLoanManagement.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    AdminDashboardActivity.this,
                                    AdminLoanManagementActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    // ==========================================================
    // SETUP LOGOUT
    // ==========================================================

    private void setupLogout() {

        if (btnAdminLogout == null) {
            return;
        }


        btnAdminLogout.setOnClickListener(
                v -> logout()
        );
    }


    // ==========================================================
    // LOGOUT
    // ==========================================================

    private void logout() {

        sessionManager.logout();


        if (swipeRefreshLayout != null) {

            swipeRefreshLayout.setRefreshing(
                    false
            );
        }


        Toast.makeText(
                AdminDashboardActivity.this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();


        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
                        LoginActivity.class
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(intent);

        finish();
    }


    // ==========================================================
    // LOAD ADMIN DASHBOARD
    // ==========================================================

    private void loadAdminDashboard() {

        if (dashboardRepository == null) {
            return;
        }


        showLoading(true);


        dashboardRepository
                .getAdminDashboard()
                .enqueue(
                        new Callback<AdminDashboardResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminDashboardResponse> call,
                                    Response<AdminDashboardResponse> response) {

                                showLoading(false);

                                stopRefreshing();


                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    displayDashboard(
                                            response.body()
                                    );

                                } else {

                                    handleHttpError(
                                            response
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<AdminDashboardResponse> call,
                                    Throwable t) {

                                showLoading(false);

                                stopRefreshing();


                                t.printStackTrace();


                                String message =
                                        t.getMessage();


                                if (message == null
                                        || message.trim().isEmpty()) {

                                    message =
                                            "Unable to connect to server.";
                                }


                                Toast.makeText(
                                        AdminDashboardActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // ==========================================================
    // HTTP ERROR
    // ==========================================================

    private void handleHttpError(
            Response<AdminDashboardResponse> response) {

        int code =
                response.code();


        // ======================================================
        // UNAUTHORIZED
        // ======================================================

        if (code == 401) {

            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
            ).show();


            sessionManager.logout();


            Intent intent =
                    new Intent(
                            this,
                            LoginActivity.class
                    );


            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );


            startActivity(intent);

            finish();


            return;
        }


        // ======================================================
        // FORBIDDEN
        // ======================================================

        if (code == 403) {

            Toast.makeText(
                    this,
                    "Access denied. Administrator privileges required.",
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // ======================================================
        // OTHER ERRORS
        // ======================================================

        Toast.makeText(
                this,
                "Unable to load Admin Dashboard.\nHTTP "
                        + code,
                Toast.LENGTH_LONG
        ).show();
    }


    // ==========================================================
    // DISPLAY DASHBOARD
    // ==========================================================

    private void displayDashboard(
            AdminDashboardResponse dashboard) {


        // ======================================================
        // HEADER
        // ======================================================

        if (tvAdminTitle != null) {

            tvAdminTitle.setText(
                    "Admin Dashboard"
            );
        }


        if (tvAdminStatus != null) {

            tvAdminStatus.setText(
                    "Live banking statistics"
            );
        }


        // ======================================================
        // SYSTEM OVERVIEW
        // ======================================================

        if (tvTotalUsers != null) {

            tvTotalUsers.setText(
                    String.valueOf(
                            dashboard.getTotalUsers()
                    )
            );
        }


        if (tvTotalCustomers != null) {

            tvTotalCustomers.setText(
                    String.valueOf(
                            dashboard.getTotalCustomers()
                    )
            );
        }


        if (tvTotalAccounts != null) {

            tvTotalAccounts.setText(
                    String.valueOf(
                            dashboard.getTotalAccounts()
                    )
            );
        }


        if (tvTotalTransactions != null) {

            tvTotalTransactions.setText(
                    String.valueOf(
                            dashboard.getTotalTransactions()
                    )
            );
        }


        // ======================================================
        // TRANSACTION ACTIVITY
        // ======================================================

        if (tvTotalDeposits != null) {

            tvTotalDeposits.setText(
                    String.valueOf(
                            dashboard.getTotalDeposits()
                    )
            );
        }


        if (tvTotalWithdrawals != null) {

            tvTotalWithdrawals.setText(
                    String.valueOf(
                            dashboard.getTotalWithdrawals()
                    )
            );
        }


        if (tvTotalTransfers != null) {

            tvTotalTransfers.setText(
                    String.valueOf(
                            dashboard.getTotalTransfers()
                    )
            );
        }


        if (tvTotalPayments != null) {

            tvTotalPayments.setText(
                    String.valueOf(
                            dashboard.getTotalPayments()
                    )
            );
        }


        // ======================================================
        // FINANCIAL SUMMARY
        // ======================================================

        if (tvTotalDepositAmount != null) {

            tvTotalDepositAmount.setText(
                    formatAmount(
                            dashboard.getTotalDepositAmount()
                    )
            );
        }


        if (tvTotalWithdrawalAmount != null) {

            tvTotalWithdrawalAmount.setText(
                    formatAmount(
                            dashboard.getTotalWithdrawalAmount()
                    )
            );
        }


        if (tvTotalTransferAmount != null) {

            tvTotalTransferAmount.setText(
                    formatAmount(
                            dashboard.getTotalTransferAmount()
                    )
            );
        }


        if (tvTotalPaymentAmount != null) {

            tvTotalPaymentAmount.setText(
                    formatAmount(
                            dashboard.getTotalPaymentAmount()
                    )
            );
        }
    }


    // ==========================================================
    // FORMAT MONEY
    // ==========================================================

    private String formatAmount(
            double amount) {

        return "₹ "
                + String.format(
                Locale.getDefault(),
                "%,.2f",
                amount
        );
    }


    // ==========================================================
    // SHOW / HIDE LOADING
    // ==========================================================

    private void showLoading(
            boolean loading) {

        if (progressBar == null) {
            return;
        }


        progressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
    }


    // ==========================================================
    // STOP REFRESH ANIMATION
    // ==========================================================

    private void stopRefreshing() {

        if (swipeRefreshLayout != null) {

            swipeRefreshLayout.setRefreshing(
                    false
            );
        }
    }


    // ==========================================================
    // AUTO REFRESH WHEN RETURNING
    // ==========================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (firstResume) {

            firstResume = false;

            return;
        }


        loadAdminDashboard();
    }


    // ==========================================================
    // CLEANUP
    // ==========================================================

    @Override
    protected void onDestroy() {

        if (swipeRefreshLayout != null) {

            swipeRefreshLayout.setOnRefreshListener(
                    null
            );
        }


        super.onDestroy();
    }
}