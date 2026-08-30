package com.rohit.mybank.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rohit.mybank.R;

import com.rohit.mybank.activities.banking.DepositActivity;
import com.rohit.mybank.activities.banking.TransactionHistoryActivity;
import com.rohit.mybank.activities.banking.TransferActivity;
import com.rohit.mybank.activities.banking.WithdrawActivity;


import com.rohit.mybank.activities.payments.PaymentsActivity;
import com.rohit.mybank.activities.pin.SetTransactionPinActivity;
import com.rohit.mybank.activities.profile.ProfileActivity;
import com.rohit.mybank.activities.qr.QRScannerActivity;

import com.rohit.mybank.adapter.TransactionAdapter;

import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.transaction.Transaction;
import com.rohit.mybank.model.transaction.TransactionPageResponse;

import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.ProfileRepository;
import com.rohit.mybank.repository.TransactionRepository;

import com.rohit.mybank.session.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardActivity extends AppCompatActivity {

    // ==================================================
    // Header Views
    // ==================================================

    private TextView tvGreeting;
    private TextView tvGreetingMessage;
    private TextView tvWelcome;


    // ==================================================
    // Account Information
    // ==================================================

    private TextView tvAccountNumber;
    private TextView tvBalance;
    private TextView tvAccountType;
    private TextView tvBranch;
    private TextView tvIfsc;
    private TextView tvStatus;


    // ==================================================
    // Visibility Icons
    // ==================================================

    private ImageView imgToggleBalance;
    private ImageView imgToggleAccount;


    // ==================================================
    // Buttons
    // ==================================================

    private ImageButton btnProfile;


    // ==================================================
    // Quick Action Cards
    // ==================================================

    private CardView cardDeposit;
    private CardView cardWithdraw;
    private CardView cardTransfer;
    private CardView cardHistory;


    // ==================================================
    // Recent Transactions
    // ==================================================

    private RecyclerView rvRecentTransactions;

    private TransactionAdapter transactionAdapter;

    private final List<Transaction> recentTransactions =
            new ArrayList<>();


    // ==================================================
    // Bottom Navigation
    // ==================================================

    private BottomNavigationView bottomNavigation;


    // ==================================================
    // Repositories
    // ==================================================

    private DashboardRepository dashboardRepository;

    private TransactionRepository transactionRepository;

    private ProfileRepository profileRepository;


    // ==================================================
    // Session
    // ==================================================

    private SessionManager sessionManager;


    // ==================================================
    // Visibility State
    // ==================================================

    private boolean balanceVisible = false;

    private boolean accountVisible = false;


    // ==================================================
    // Actual Values
    // ==================================================

    private String actualAccountNumber = "";

    private double actualBalance = 0.0;


    // ==================================================
    // Misc
    // ==================================================

    private TextView tvViewAll;


    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_dashboard
        );


        // ==================================================
        // Initialize Views
        // ==================================================

        initializeViews();


        // ==================================================
        // Session
        // ==================================================

        sessionManager =
                new SessionManager(this);


        // ==================================================
        // Repositories
        // ==================================================

        dashboardRepository =
                new DashboardRepository(this);

        transactionRepository =
                new TransactionRepository(this);

        profileRepository =
                new ProfileRepository(this);


        // ==================================================
        // RecyclerView
        // ==================================================

        transactionAdapter =
                new TransactionAdapter(
                        recentTransactions
                );


        rvRecentTransactions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvRecentTransactions.setHasFixedSize(
                true
        );

        rvRecentTransactions.setAdapter(
                transactionAdapter
        );


        // ==================================================
        // Default Welcome
        // ==================================================

        tvWelcome.setText(
                "Welcome"
        );


        // ==================================================
        // Greeting
        // ==================================================

        setGreeting();


        // ==================================================
        // Visibility Buttons
        // ==================================================

        setupVisibilityButtons();


        // ==================================================
        // Bottom Navigation
        // ==================================================

        setupBottomNavigation();


        // ==================================================
        // Load Dashboard
        // ==================================================

        loadDashboard();


        // ==================================================
        // Deposit
        // ==================================================

        cardDeposit.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            DepositActivity.class
                    )
            );

        });


        // ==================================================
        // Withdraw
        // ==================================================

        cardWithdraw.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            WithdrawActivity.class
                    )
            );

        });


        // ==================================================
        // Transfer
        // ==================================================

        cardTransfer.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            TransferActivity.class
                    )
            );

        });


        // ==================================================
        // Transaction History
        // ==================================================

        cardHistory.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            TransactionHistoryActivity.class
                    )
            );

        });


        // ==================================================
        // View All
        // ==================================================

        tvViewAll.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            TransactionHistoryActivity.class
                    )
            );

        });


        // ==================================================
        // Profile
        // ==================================================

        btnProfile.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            ProfileActivity.class
                    )
            );

        });
    }


    // ==================================================
    // ON RESUME
    // ==================================================

    @Override
    protected void onResume() {

        super.onResume();

        setGreeting();

        if (dashboardRepository != null) {

            loadDashboard();
        }
    }


    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        bottomNavigation.setOnItemSelectedListener(
                item -> {

                    int id =
                            item.getItemId();


                    // ==================================================
                    // HOME
                    // ==================================================

                    if (id == R.id.nav_home) {

                        return true;
                    }


                    // ==================================================
                    // PAYMENTS
                    // ==================================================

                    if (id == R.id.nav_payments) {

                        startActivity(
                                new Intent(
                                        DashboardActivity.this,
                                        PaymentsActivity.class
                                )
                        );

                        return true;
                    }


                    // ==================================================
                    // SCAN
                    // ==================================================

                    if (id == R.id.nav_scan) {

                        startActivity(
                                new Intent(
                                        DashboardActivity.this,
                                        QRScannerActivity.class
                                )
                        );

                        return true;
                    }



                    // ==================================================
                    // MENU
                    // ==================================================

                    if (id == R.id.nav_menu) {

                        startActivity(
                                new Intent(
                                        DashboardActivity.this,
                                        ProfileActivity.class
                                )
                        );

                        return true;
                    }


                    return false;
                }
        );


        // ==================================================
        // HOME SELECTED BY DEFAULT
        // ==================================================

        bottomNavigation.setSelectedItemId(
                R.id.nav_home
        );
    }


    // ==================================================
    // DYNAMIC GREETING
    // ==================================================

    private void setGreeting() {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(
                        Calendar.HOUR_OF_DAY
                );


        if (hour >= 5 && hour < 12) {

            tvGreeting.setText(
                    "☀️ Good Morning 👋"
            );

            tvGreetingMessage.setText(
                    "Have a great day ahead!"
            );


        } else if (hour >= 12 && hour < 17) {

            tvGreeting.setText(
                    "🌤 Good Afternoon ☀️"
            );

            tvGreetingMessage.setText(
                    "Hope your day is going well!"
            );


        } else if (hour >= 17 && hour < 21) {

            tvGreeting.setText(
                    "🌇 Good Evening 🌆"
            );

            tvGreetingMessage.setText(
                    "Relax and manage your finances."
            );


        } else {

            tvGreeting.setText(
                    "🌙 Good Night 🌙"
            );

            tvGreetingMessage.setText(
                    "Take care and have a peaceful night."
            );
        }
    }


    // ==================================================
    // LOAD DASHBOARD
    // ==================================================

    private void loadDashboard() {

        dashboardRepository
                .getMyAccount()
                .enqueue(
                        new Callback<DashboardResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<DashboardResponse> call,
                                    @NonNull Response<DashboardResponse> response
                            ) {

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                ) {

                                    DashboardResponse account =
                                            response.body();


                                    actualAccountNumber =
                                            account.getAccNo();

                                    actualBalance =
                                            account.getBalance();


                                    updateAccountVisibility();

                                    updateBalanceVisibility();


                                    tvAccountType.setText(
                                            account.getAccountType()
                                    );

                                    tvBranch.setText(
                                            account.getBranchName()
                                    );

                                    tvIfsc.setText(
                                            account.getIfscCode()
                                    );

                                    tvStatus.setText(
                                            account.getStatus()
                                    );


                                    loadRecentTransactions(
                                            actualAccountNumber
                                    );


                                    checkTransactionPin();


                                } else {

                                    Toast.makeText(
                                            DashboardActivity.this,
                                            "Unable to load account details",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<DashboardResponse> call,
                                    @NonNull Throwable t
                            ) {

                                Toast.makeText(
                                        DashboardActivity.this,
                                        "Connection Failed : "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // ==================================================
    // CURRENCY FORMATTER
    // ==================================================

    private String formatCurrency(
            double amount
    ) {

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale(
                                "en",
                                "IN"
                        )
                );

        return formatter.format(
                amount
        );
    }


    // ==================================================
    // MASK ACCOUNT NUMBER
    // ==================================================

    private String maskAccountNumber(
            String accountNumber
    ) {

        if (
                accountNumber == null
                        || accountNumber.trim().isEmpty()
        ) {

            return "XXXX XXXX XXXX";
        }


        if (
                accountNumber.length() <= 4
        ) {

            return accountNumber;
        }


        String lastFour =
                accountNumber.substring(
                        accountNumber.length() - 4
                );


        return "XXXX XXXX " + lastFour;
    }


    // ==================================================
    // CHECK TRANSACTION PIN
    // ==================================================

    private void checkTransactionPin() {

        profileRepository
                .getProfile()
                .enqueue(
                        new Callback<ProfileResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<ProfileResponse> call,
                                    @NonNull Response<ProfileResponse> response
                            ) {

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                ) {

                                    ProfileResponse profile =
                                            response.body();


                                    // ==================================================
                                    // Full Name
                                    // ==================================================

                                    if (
                                            profile.getFullName() != null
                                                    && !profile.getFullName()
                                                    .trim()
                                                    .isEmpty()
                                    ) {

                                        tvWelcome.setText(
                                                "Welcome\n"
                                                        + profile.getFullName()
                                        );

                                    } else {

                                        tvWelcome.setText(
                                                "Welcome"
                                        );
                                    }


                                    // ==================================================
                                    // Transaction PIN
                                    // ==================================================

                                    if (
                                            !profile.isTransactionPinSet()
                                    ) {

                                        startActivity(
                                                new Intent(
                                                        DashboardActivity.this,
                                                        SetTransactionPinActivity.class
                                                )
                                        );
                                    }


                                } else {

                                    tvWelcome.setText(
                                            "Welcome"
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<ProfileResponse> call,
                                    @NonNull Throwable t
                            ) {

                                // Dashboard continues normally.
                            }
                        }
                );
    }


    // ==================================================
    // RECENT TRANSACTIONS
    // ==================================================

    private void loadRecentTransactions(
            String accountNumber
    ) {

        transactionRepository
                .getTransactions(
                        accountNumber,
                        0,
                        2,
                        "desc"
                )
                .enqueue(
                        new Callback<TransactionPageResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<TransactionPageResponse> call,
                                    @NonNull Response<TransactionPageResponse> response
                            ) {

                                recentTransactions.clear();


                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                                && response.body().getContent() != null
                                ) {

                                    recentTransactions.addAll(
                                            response.body().getContent()
                                    );
                                }


                                transactionAdapter
                                        .notifyDataSetChanged();
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<TransactionPageResponse> call,
                                    @NonNull Throwable t
                            ) {

                                recentTransactions.clear();

                                transactionAdapter
                                        .notifyDataSetChanged();


                                Toast.makeText(
                                        DashboardActivity.this,
                                        "Unable to load recent transactions",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // ==================================================
    // REFRESH DASHBOARD
    // ==================================================

    private void refreshDashboard() {

        loadDashboard();


        if (
                actualAccountNumber != null
                        && !actualAccountNumber.isEmpty()
        ) {

            loadRecentTransactions(
                    actualAccountNumber
            );
        }
    }


    // ==================================================
    // CLEAR DASHBOARD
    // ==================================================

    private void clearDashboard() {

        actualAccountNumber = "";

        actualBalance = 0.0;


        tvWelcome.setText(
                "Welcome"
        );

        tvAccountType.setText(
                "--"
        );

        tvBranch.setText(
                "--"
        );

        tvIfsc.setText(
                "--"
        );

        tvStatus.setText(
                "--"
        );


        recentTransactions.clear();

        transactionAdapter
                .notifyDataSetChanged();


        updateBalanceVisibility();

        updateAccountVisibility();
    }


    // ==================================================
    // VISIBILITY BUTTONS
    // ==================================================

    private void setupVisibilityButtons() {

        imgToggleBalance.setOnClickListener(v -> {

            balanceVisible =
                    !balanceVisible;

            updateBalanceVisibility();
        });


        imgToggleAccount.setOnClickListener(v -> {

            accountVisible =
                    !accountVisible;

            updateAccountVisibility();
        });


        updateBalanceVisibility();

        updateAccountVisibility();
    }


    // ==================================================
    // BALANCE VISIBILITY
    // ==================================================

    private void updateBalanceVisibility() {

        if (balanceVisible) {

            tvBalance.setText(
                    formatCurrency(
                            actualBalance
                    )
            );

            imgToggleBalance.setImageResource(
                    R.drawable.ic_visibility_off_24
            );


        } else {

            tvBalance.setText(
                    "₹ •••••••"
            );

            imgToggleBalance.setImageResource(
                    R.drawable.ic_visibility_24
            );
        }
    }


    // ==================================================
    // ACCOUNT NUMBER VISIBILITY
    // ==================================================

    private void updateAccountVisibility() {

        if (accountVisible) {

            tvAccountNumber.setText(
                    actualAccountNumber
            );

            imgToggleAccount.setImageResource(
                    R.drawable.ic_visibility_off_24
            );


        } else {

            tvAccountNumber.setText(
                    maskAccountNumber(
                            actualAccountNumber
                    )
            );

            imgToggleAccount.setImageResource(
                    R.drawable.ic_visibility_24
            );
        }
    }


    // ==================================================
    // UTILITY
    // ==================================================

    private void showToast(
            String message
    ) {

        Toast.makeText(
                DashboardActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }


    // ==================================================
    // INITIALIZE VIEWS
    // ==================================================

    private void initializeViews() {

        // ==================================================
        // Header
        // ==================================================

        tvGreeting =
                findViewById(
                        R.id.tvGreeting
                );

        tvGreetingMessage =
                findViewById(
                        R.id.tvGreetingMessage
                );

        tvWelcome =
                findViewById(
                        R.id.tvWelcome
                );


        // ==================================================
        // Account Details
        // ==================================================

        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );

        tvBalance =
                findViewById(
                        R.id.tvBalance
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

        tvStatus =
                findViewById(
                        R.id.tvStatus
                );


        // ==================================================
        // Visibility Icons
        // ==================================================

        imgToggleBalance =
                findViewById(
                        R.id.imgToggleBalance
                );

        imgToggleAccount =
                findViewById(
                        R.id.imgToggleAccount
                );


        // ==================================================
        // Profile
        // ==================================================

        btnProfile =
                findViewById(
                        R.id.btnProfile
                );


        // ==================================================
        // View All
        // ==================================================

        tvViewAll =
                findViewById(
                        R.id.tvViewAll
                );


        // ==================================================
        // Quick Actions
        // ==================================================

        cardDeposit =
                findViewById(
                        R.id.cardDeposit
                );

        cardWithdraw =
                findViewById(
                        R.id.cardWithdraw
                );

        cardTransfer =
                findViewById(
                        R.id.cardTransfer
                );

        cardHistory =
                findViewById(
                        R.id.cardHistory
                );


        // ==================================================
        // Recent Transactions
        // ==================================================

        rvRecentTransactions =
                findViewById(
                        R.id.rvRecentTransactions
                );


        // ==================================================
        // Bottom Navigation
        // ==================================================

        bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
                );
    }
}