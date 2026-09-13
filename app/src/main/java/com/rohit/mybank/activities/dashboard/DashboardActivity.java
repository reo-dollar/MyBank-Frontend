package com.rohit.mybank.activities.dashboard;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
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

import com.rohit.mybank.activities.cards.CardsActivity;

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


/**
 * ============================================================
 * DASHBOARD ACTIVITY
 * ============================================================
 *
 * Main customer dashboard.
 *
 * Bottom Navigation:
 *
 * HOME
 * PAYMENTS
 * SCAN
 * CARDS
 * MENU
 *
 * Cards navigation:
 *
 * Dashboard
 *      ↓
 * Cards
 *      ↓
 * CardsActivity
 *      ↓
 * GET /api/debit-cards/my
 *
 * ============================================================
 */
public class DashboardActivity extends AppCompatActivity {


    // =========================================================
    // HEADER VIEWS
    // =========================================================

    private TextView tvGreeting;
    private TextView tvGreetingMessage;
    private TextView tvWelcome;


    // =========================================================
    // ACCOUNT INFORMATION
    // =========================================================

    private TextView tvAccountNumber;
    private TextView tvBalance;
    private TextView tvAccountType;
    private TextView tvBranch;
    private TextView tvIfsc;
    private TextView tvStatus;


    // =========================================================
    // VISIBILITY ICONS
    // =========================================================

    private ImageView imgToggleBalance;
    private ImageView imgToggleAccount;


    // =========================================================
    // PROFILE BUTTON
    // =========================================================

    private ImageButton btnProfile;


    // =========================================================
    // QUICK ACTION CARDS
    // =========================================================

    private CardView cardDeposit;
    private CardView cardWithdraw;
    private CardView cardTransfer;
    private CardView cardHistory;


    // =========================================================
    // RECENT TRANSACTIONS
    // =========================================================

    private RecyclerView rvRecentTransactions;

    private TransactionAdapter transactionAdapter;

    private final List<Transaction> recentTransactions =
            new ArrayList<>();


    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private BottomNavigationView bottomNavigation;


    // =========================================================
    // REPOSITORIES
    // =========================================================

    private DashboardRepository dashboardRepository;
    private TransactionRepository transactionRepository;
    private ProfileRepository profileRepository;


    // =========================================================
    // SESSION
    // =========================================================

    private SessionManager sessionManager;


    // =========================================================
    // VISIBILITY STATE
    // =========================================================

    private boolean balanceVisible = false;
    private boolean accountVisible = false;


    // =========================================================
    // ACTUAL VALUES
    // =========================================================

    private String actualAccountNumber = "";
    private double actualBalance = 0.0;


    // =========================================================
    // OTHER VIEWS
    // =========================================================

    private TextView tvViewAll;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();


        // =====================================================
        // SESSION
        // =====================================================

        sessionManager =
                new SessionManager(this);


        // =====================================================
        // REPOSITORIES
        // =====================================================

        dashboardRepository =
                new DashboardRepository(this);

        transactionRepository =
                new TransactionRepository(this);

        profileRepository =
                new ProfileRepository(this);


        // =====================================================
        // TRANSACTION ADAPTER
        // =====================================================

        transactionAdapter =
                new TransactionAdapter(
                        recentTransactions
                );


        rvRecentTransactions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvRecentTransactions.setHasFixedSize(true);

        rvRecentTransactions.setAdapter(
                transactionAdapter
        );


        // =====================================================
        // DEFAULT WELCOME
        // =====================================================

        tvWelcome.setText("Welcome");


        // =====================================================
        // GREETING
        // =====================================================

        setGreeting();


        // =====================================================
        // VISIBILITY BUTTONS
        // =====================================================

        setupVisibilityButtons();


        // =====================================================
        // LOAD DASHBOARD
        // =====================================================

        loadDashboard();


        // =====================================================
        // DEPOSIT
        // =====================================================

        cardDeposit.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            DepositActivity.class
                    )
            );

        });


        // =====================================================
        // WITHDRAW
        // =====================================================

        cardWithdraw.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            WithdrawActivity.class
                    )
            );

        });


        // =====================================================
        // TRANSFER
        // =====================================================

        cardTransfer.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            TransferActivity.class
                    )
            );

        });


        // =====================================================
        // TRANSACTION HISTORY
        // =====================================================

        cardHistory.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            TransactionHistoryActivity.class
                    )
            );

        });


        tvViewAll.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            TransactionHistoryActivity.class
                    )
            );

        });


        // =====================================================
        // PROFILE BUTTON
        // =====================================================

        btnProfile.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            DashboardActivity.this,
                            ProfileActivity.class
                    )
            );

        });


        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        setupBottomNavigation();
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        setGreeting();

        if (dashboardRepository != null) {

            loadDashboard();
        }
    }


    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private void setupBottomNavigation() {

        /*
         * IMPORTANT:
         *
         * The listener must be installed BEFORE selecting
         * the default item.
         */

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();


            // =================================================
            // HOME
            // =================================================

            if (id == R.id.nav_home) {

                return true;
            }


            // =================================================
            // PAYMENTS
            // =================================================

            if (id == R.id.nav_payments) {

                startActivity(
                        new Intent(
                                DashboardActivity.this,
                                PaymentsActivity.class
                        )
                );

                overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );

                return true;
            }


            // =================================================
            // SCAN
            // =================================================

            if (id == R.id.nav_scan) {

                startActivity(
                        new Intent(
                                DashboardActivity.this,
                                QRScannerActivity.class
                        )
                );

                overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );

                return true;
            }


            // =================================================
            // CARDS
            // =================================================
            //
            // THIS WAS THE MISSING PART.
            //
            // Dashboard Cards
            //        ↓
            // CardsActivity
            //
            // =================================================

            if (id == R.id.nav_cards) {

                startActivity(
                        new Intent(
                                DashboardActivity.this,
                                CardsActivity.class
                        )
                );

                overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );

                return true;
            }


            // =================================================
            // MENU
            // =================================================

            if (id == R.id.nav_menu) {

                startActivity(
                        new Intent(
                                DashboardActivity.this,
                                ProfileActivity.class
                        )
                );

                overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );

                return true;
            }


            // =================================================
            // UNKNOWN ITEM
            // =================================================

            return false;
        });


        // =====================================================
        // DEFAULT HOME
        // =====================================================

        bottomNavigation.setSelectedItemId(
                R.id.nav_home
        );
    }


    // =========================================================
    // GREETING
    // =========================================================

    private void setGreeting() {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(Calendar.HOUR_OF_DAY);


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


    // =========================================================
    // LOAD DASHBOARD
    // =========================================================

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


    // =========================================================
    // CURRENCY FORMATTER
    // =========================================================

    private String formatCurrency(
            double amount
    ) {

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("en", "IN")
                );

        return formatter.format(amount);
    }


    // =========================================================
    // MASK ACCOUNT NUMBER
    // =========================================================

    private String maskAccountNumber(
            String accountNumber
    ) {

        if (
                accountNumber == null
                        || accountNumber.trim().isEmpty()
        ) {

            return "XXXX XXXX XXXX";
        }


        if (accountNumber.length() <= 4) {

            return accountNumber;
        }


        String lastFour =
                accountNumber.substring(
                        accountNumber.length() - 4
                );


        return "XXXX XXXX " + lastFour;
    }


    // =========================================================
    // CHECK TRANSACTION PIN
    // =========================================================

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


                                    if (
                                            profile.getFullName() != null
                                                    && !profile
                                                    .getFullName()
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


                                    if (
                                            !profile.isTransactionPinSet()
                                    ) {

                                        showTransactionPinDialog();
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

                                /*
                                 * Dashboard should continue working
                                 * even if profile loading fails.
                                 */
                            }
                        }
                );
    }


    // =========================================================
    // LOAD RECENT TRANSACTIONS
    // =========================================================

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


    // =========================================================
    // REFRESH DASHBOARD
    // =========================================================

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


    // =========================================================
    // CLEAR DASHBOARD
    // =========================================================

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


    // =========================================================
    // VISIBILITY BUTTONS
    // =========================================================

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


    // =========================================================
    // BALANCE VISIBILITY
    // =========================================================

    private void updateBalanceVisibility() {

        if (balanceVisible) {

            tvBalance.setText(
                    formatCurrency(actualBalance)
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


    // =========================================================
    // ACCOUNT NUMBER VISIBILITY
    // =========================================================

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


    // =========================================================
    // TRANSACTION PIN DIALOG
    // =========================================================

    private void showTransactionPinDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Transaction PIN Required")
                .setMessage("Please set your transaction PIN before making transactions.")
                .setPositiveButton("Set PIN", (dialog, which) -> {
                    Intent intent = new Intent(
                            DashboardActivity.this,
                            SetTransactionPinActivity.class
                    );
                    startActivity(intent);
                })
                .setNegativeButton("Later", null)
                .show();
    }


    // =========================================================
    // TOAST
    // =========================================================

    private void showToast(
            String message
    ) {

        Toast.makeText(
                DashboardActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // =====================================================
        // HEADER
        // =====================================================

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


        // =====================================================
        // ACCOUNT
        // =====================================================

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


        // =====================================================
        // VISIBILITY ICONS
        // =====================================================

        imgToggleBalance =
                findViewById(
                        R.id.imgToggleBalance
                );

        imgToggleAccount =
                findViewById(
                        R.id.imgToggleAccount
                );


        // =====================================================
        // PROFILE
        // =====================================================

        btnProfile =
                findViewById(
                        R.id.btnProfile
                );


        // =====================================================
        // VIEW ALL
        // =====================================================

        tvViewAll =
                findViewById(
                        R.id.tvViewAll
                );


        // =====================================================
        // QUICK ACTIONS
        // =====================================================

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


        // =====================================================
        // RECENT TRANSACTIONS
        // =====================================================

        rvRecentTransactions =
                findViewById(
                        R.id.rvRecentTransactions
                );


        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
                );
    }
}
