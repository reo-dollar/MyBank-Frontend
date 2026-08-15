package com.rohit.mybank.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.adapter.AdminAccountTransactionAdapter;
import com.rohit.mybank.model.transaction.Transaction;
import com.rohit.mybank.model.transaction.TransactionPageResponse;
import com.rohit.mybank.repository.AdminAccountTransactionRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccountTransactionActivity
        extends AppCompatActivity {

    // =========================================================
    // CONSTANT
    // =========================================================

    public static final String EXTRA_ACCOUNT_NUMBER =
            "admin_transaction_account_number";

    private static final int PAGE_SIZE = 5;

    private static final String SORT_DIRECTION = "desc";

    // =========================================================
    // VIEWS
    // =========================================================

    private TextView tvAccountNumber;
    private TextView tvTransactionCount;

    private RecyclerView rvTransactions;

    private ProgressBar progressBar;

    private Button btnLoadMore;

    // =========================================================
    // DATA
    // =========================================================

    private final List<Transaction> transactions =
            new ArrayList<>();

    private AdminAccountTransactionAdapter adapter;

    private AdminAccountTransactionRepository repository;

    private String accountNumber;

    private int currentPage = 0;

    private boolean lastPage = false;

    private boolean loading = false;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_account_transaction
        );

        // =====================================================
        // GET ACCOUNT NUMBER
        // =====================================================

        accountNumber =
                getIntent().getStringExtra(
                        EXTRA_ACCOUNT_NUMBER
                );

        if (accountNumber == null
                || accountNumber.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Account number not provided.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // =====================================================
        // INITIALIZE
        // =====================================================

        initializeViews();

        repository =
                new AdminAccountTransactionRepository(
                        this
                );

        setupRecyclerView();

        tvAccountNumber.setText(
                accountNumber
        );

        // =====================================================
        // LOAD FIRST PAGE
        // =====================================================

        loadTransactions(false);
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );

        tvTransactionCount =
                findViewById(
                        R.id.tvTransactionCount
                );

        rvTransactions =
                findViewById(
                        R.id.rvTransactions
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        btnLoadMore =
                findViewById(
                        R.id.btnLoadMore
                );

        btnLoadMore.setOnClickListener(
                v -> loadTransactions(true)
        );
    }

    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        adapter =
                new AdminAccountTransactionAdapter(
                        transactions
                );

        rvTransactions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvTransactions.setAdapter(
                adapter
        );
    }

    // =========================================================
    // LOAD TRANSACTIONS
    // =========================================================

    private void loadTransactions(
            boolean loadMore) {

        if (loading) {
            return;
        }

        if (loadMore && lastPage) {
            return;
        }

        loading = true;

        showLoading(true);

        int page =
                loadMore
                        ? currentPage + 1
                        : 0;

        repository
                .getTransactions(
                        accountNumber,
                        page,
                        PAGE_SIZE,
                        SORT_DIRECTION
                )
                .enqueue(
                        new Callback<TransactionPageResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<TransactionPageResponse> call,
                                    @NonNull Response<TransactionPageResponse> response) {

                                loading = false;

                                showLoading(false);

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    Toast.makeText(
                                            AdminAccountTransactionActivity.this,
                                            "Unable to load transactions. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                TransactionPageResponse pageResponse =
                                        response.body();

                                List<Transaction> pageTransactions =
                                        pageResponse.getContent();

                                // ---------------------------------
                                // FIRST PAGE
                                // ---------------------------------

                                if (!loadMore) {

                                    transactions.clear();
                                }

                                // ---------------------------------
                                // ADD TRANSACTIONS
                                // ---------------------------------

                                if (pageTransactions != null
                                        && !pageTransactions.isEmpty()) {

                                    transactions.addAll(
                                            pageTransactions
                                    );
                                }

                                // ---------------------------------
                                // PAGE INFORMATION
                                // ---------------------------------

                                currentPage =
                                        pageResponse.getNumber();

                                lastPage =
                                        pageResponse.isLast();

                                // ---------------------------------
                                // UPDATE UI
                                // ---------------------------------

                                adapter.notifyDataSetChanged();

                                updateTransactionCount(
                                        pageResponse
                                );

                                updateLoadMoreButton();

                                // ---------------------------------
                                // EMPTY RESULT
                                // ---------------------------------

                                if (transactions.isEmpty()) {

                                    Toast.makeText(
                                            AdminAccountTransactionActivity.this,
                                            "No transactions found.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<TransactionPageResponse> call,
                                    @NonNull Throwable t) {

                                loading = false;

                                showLoading(false);

                                Toast.makeText(
                                        AdminAccountTransactionActivity.this,
                                        "Network Error: "
                                                + safeMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // TRANSACTION COUNT
    // =========================================================

    private void updateTransactionCount(
            TransactionPageResponse response) {

        tvTransactionCount.setText(
                response.getTotalElements()
                        + " transactions"
        );
    }

    // =========================================================
    // LOAD MORE BUTTON
    // =========================================================

    private void updateLoadMoreButton() {

        if (lastPage) {

            btnLoadMore.setVisibility(
                    View.GONE
            );

        } else {

            btnLoadMore.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void showLoading(
            boolean show) {

        progressBar.setVisibility(
                show
                        ? View.VISIBLE
                        : View.GONE
        );

        if (show) {

            btnLoadMore.setEnabled(false);

        } else {

            btnLoadMore.setEnabled(!lastPage);
        }
    }

    // =========================================================
    // SAFE ERROR MESSAGE
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