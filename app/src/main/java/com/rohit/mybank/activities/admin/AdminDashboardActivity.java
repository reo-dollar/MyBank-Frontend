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
 * Admin dashboard showing system-wide statistics, transaction
 * distribution, financial-volume comparison and administration links.
 *
 * Analytics are calculated from the dashboard totals currently returned
 * by the existing API. They are not historical or time-series statistics.
 */
public class AdminDashboardActivity extends AppCompatActivity {

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

    private TextView tvAnalyticsDepositsPercent;
    private TextView tvAnalyticsWithdrawalsPercent;
    private TextView tvAnalyticsTransfersPercent;
    private TextView tvAnalyticsPaymentsPercent;

    private ProgressBar progressBar;
    private ProgressBar progressAnalyticsDeposits;
    private ProgressBar progressAnalyticsWithdrawals;
    private ProgressBar progressAnalyticsTransfers;
    private ProgressBar progressAnalyticsPayments;

    private AdminAnalyticsChartView chartTransactionDistribution;
    private AdminAnalyticsChartView chartFinancialVolume;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Button btnUserManagement;
    private Button btnCustomerManagement;
    private Button btnAccountManagement;
    private Button btnTransactionManagement;
    private Button btnLoanManagement;
    private Button btnAdminLogout;

    private DashboardRepository dashboardRepository;
    private SessionManager sessionManager;
    private boolean firstResume = true;
    private Call<AdminDashboardResponse> dashboardCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initializeViews();
        dashboardRepository = new DashboardRepository(this);
        sessionManager = new SessionManager(this);

        setupRefresh();
        setupLogout();
        setupUserManagement();
        setupCustomerManagement();
        setupAccountManagement();
        setupTransactionManagement();
        setupLoanManagement();
        loadAdminDashboard();
    }

    private void initializeViews() {
        tvAdminTitle = findViewById(R.id.tvAdminTitle);
        tvAdminStatus = findViewById(R.id.tvAdminStatus);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        tvTotalAccounts = findViewById(R.id.tvTotalAccounts);

        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        tvTotalDeposits = findViewById(R.id.tvTotalDeposits);
        tvTotalWithdrawals = findViewById(R.id.tvTotalWithdrawals);
        tvTotalTransfers = findViewById(R.id.tvTotalTransfers);
        tvTotalPayments = findViewById(R.id.tvTotalPayments);

        tvTotalDepositAmount = findViewById(R.id.tvTotalDepositAmount);
        tvTotalWithdrawalAmount = findViewById(R.id.tvTotalWithdrawalAmount);
        tvTotalTransferAmount = findViewById(R.id.tvTotalTransferAmount);
        tvTotalPaymentAmount = findViewById(R.id.tvTotalPaymentAmount);

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        btnUserManagement = findViewById(R.id.btnUserManagement);
        btnCustomerManagement = findViewById(R.id.btnCustomerManagement);
        btnAccountManagement = findViewById(R.id.btnAccountManagement);
        btnTransactionManagement = findViewById(R.id.btnTransactionManagement);
        btnLoanManagement = findViewById(R.id.btnLoanManagement);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        tvAnalyticsDepositsPercent = findViewById(R.id.tvAnalyticsDepositsPercent);
        tvAnalyticsWithdrawalsPercent = findViewById(R.id.tvAnalyticsWithdrawalsPercent);
        tvAnalyticsTransfersPercent = findViewById(R.id.tvAnalyticsTransfersPercent);
        tvAnalyticsPaymentsPercent = findViewById(R.id.tvAnalyticsPaymentsPercent);

        progressAnalyticsDeposits = findViewById(R.id.progressAnalyticsDeposits);
        progressAnalyticsWithdrawals = findViewById(R.id.progressAnalyticsWithdrawals);
        progressAnalyticsTransfers = findViewById(R.id.progressAnalyticsTransfers);
        progressAnalyticsPayments = findViewById(R.id.progressAnalyticsPayments);

        chartTransactionDistribution = findViewById(R.id.chartTransactionDistribution);
        chartFinancialVolume = findViewById(R.id.chartFinancialVolume);
    }

    private void setupRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::loadAdminDashboard);
        }
    }

    private void setupUserManagement() {
        if (btnUserManagement != null) {
            btnUserManagement.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminUserManagementActivity.class)));
        }
    }

    private void setupCustomerManagement() {
        if (btnCustomerManagement != null) {
            btnCustomerManagement.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminCustomerManagementActivity.class)));
        }
    }

    private void setupAccountManagement() {
        if (btnAccountManagement != null) {
            btnAccountManagement.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminAccountManagementActivity.class)));
        }
    }

    private void setupTransactionManagement() {
        if (btnTransactionManagement != null) {
            btnTransactionManagement.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminTransactionsActivity.class)));
        }
    }

    private void setupLoanManagement() {
        if (btnLoanManagement != null) {
            btnLoanManagement.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminLoanManagementActivity.class)));
        }
    }

    private void setupLogout() {
        if (btnAdminLogout != null) {
            btnAdminLogout.setOnClickListener(v -> logout());
        }
    }

    private void logout() {
        if (dashboardCall != null) {
            dashboardCall.cancel();
        }
        sessionManager.logout();
        stopRefreshing();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadAdminDashboard() {
        if (dashboardRepository == null || isFinishing()) {
            stopRefreshing();
            return;
        }

        if (dashboardCall != null) {
            dashboardCall.cancel();
        }

        showLoading(true);
        dashboardCall = dashboardRepository.getAdminDashboard();

        dashboardCall.enqueue(new Callback<AdminDashboardResponse>() {
            @Override
            public void onResponse(Call<AdminDashboardResponse> call,
                                   Response<AdminDashboardResponse> response) {
                if (call.isCanceled() || isFinishing()) {
                    return;
                }

                showLoading(false);
                stopRefreshing();

                if (response.isSuccessful() && response.body() != null) {
                    displayDashboard(response.body());
                } else {
                    handleHttpError(response);
                }
            }

            @Override
            public void onFailure(Call<AdminDashboardResponse> call, Throwable t) {
                if (call.isCanceled() || isFinishing()) {
                    return;
                }

                showLoading(false);
                stopRefreshing();

                String message = t.getMessage();
                if (message == null || message.trim().isEmpty()) {
                    message = "Unable to connect to server.";
                }

                Toast.makeText(AdminDashboardActivity.this,
                        message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleHttpError(Response<AdminDashboardResponse> response) {
        int code = response.code();

        if (code == 401) {
            Toast.makeText(this,
                    "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            sessionManager.logout();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (code == 403) {
            Toast.makeText(this,
                    "Access denied. Administrator privileges required.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this,
                "Unable to load Admin Dashboard.\nHTTP " + code,
                Toast.LENGTH_LONG).show();
    }

    private void displayDashboard(AdminDashboardResponse dashboard) {
        tvAdminTitle.setText("Admin Dashboard");
        tvAdminStatus.setText("Live banking statistics");

        tvTotalUsers.setText(String.valueOf(dashboard.getTotalUsers()));
        tvTotalCustomers.setText(String.valueOf(dashboard.getTotalCustomers()));
        tvTotalAccounts.setText(String.valueOf(dashboard.getTotalAccounts()));
        tvTotalTransactions.setText(String.valueOf(dashboard.getTotalTransactions()));

        tvTotalDeposits.setText(String.valueOf(dashboard.getTotalDeposits()));
        tvTotalWithdrawals.setText(String.valueOf(dashboard.getTotalWithdrawals()));
        tvTotalTransfers.setText(String.valueOf(dashboard.getTotalTransfers()));
        tvTotalPayments.setText(String.valueOf(dashboard.getTotalPayments()));

        double depositAmount = nonNegative(dashboard.getTotalDepositAmount());
        double withdrawalAmount = nonNegative(dashboard.getTotalWithdrawalAmount());
        double transferAmount = nonNegative(dashboard.getTotalTransferAmount());
        double paymentAmount = nonNegative(dashboard.getTotalPaymentAmount());

        tvTotalDepositAmount.setText(formatAmount(depositAmount));
        tvTotalWithdrawalAmount.setText(formatAmount(withdrawalAmount));
        tvTotalTransferAmount.setText(formatAmount(transferAmount));
        tvTotalPaymentAmount.setText(formatAmount(paymentAmount));

        updateTransactionAnalytics(
                nonNegative(dashboard.getTotalDeposits()),
                nonNegative(dashboard.getTotalWithdrawals()),
                nonNegative(dashboard.getTotalTransfers()),
                nonNegative(dashboard.getTotalPayments())
        );

        updateAmountAnalytics(
                depositAmount, withdrawalAmount, transferAmount, paymentAmount
        );
    }

    private void updateTransactionAnalytics(double deposits,
                                            double withdrawals,
                                            double transfers,
                                            double payments) {
        double total = deposits + withdrawals + transfers + payments;

        setDistribution(progressAnalyticsDeposits, tvAnalyticsDepositsPercent, deposits, total);
        setDistribution(progressAnalyticsWithdrawals, tvAnalyticsWithdrawalsPercent, withdrawals, total);
        setDistribution(progressAnalyticsTransfers, tvAnalyticsTransfersPercent, transfers, total);
        setDistribution(progressAnalyticsPayments, tvAnalyticsPaymentsPercent, payments, total);

        if (chartTransactionDistribution != null) {
            chartTransactionDistribution.setTransactionData(deposits, withdrawals, transfers, payments);
        }
    }

    private void setDistribution(ProgressBar bar, TextView label,
                                 double value, double total) {
        int percent = total <= 0 ? 0 : (int) Math.round((value / total) * 100.0);
        percent = Math.max(0, Math.min(100, percent));

        if (bar != null) {
            bar.setMax(100);
            bar.setProgress(percent);
        }
        if (label != null) {
            label.setText(percent + "%");
        }
    }

    private void updateAmountAnalytics(double deposits,
                                       double withdrawals,
                                       double transfers,
                                       double payments) {
        if (chartFinancialVolume != null) {
            chartFinancialVolume.setFinancialData(deposits, withdrawals, transfers, payments);
        }
    }

    private double nonNegative(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, value);
    }

    private String formatAmount(double amount) {
        return "₹ " + String.format(Locale.getDefault(), "%,.2f", amount);
    }

    private void showLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstResume) {
            firstResume = false;
            return;
        }

        loadAdminDashboard();
    }

    @Override
    protected void onDestroy() {
        if (dashboardCall != null) {
            dashboardCall.cancel();
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null);
        }
        super.onDestroy();
    }
}
