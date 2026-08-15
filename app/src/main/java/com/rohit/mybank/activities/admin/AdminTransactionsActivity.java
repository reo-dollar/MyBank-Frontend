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
import com.rohit.mybank.adapter.AdminTransactionAdapter;
import com.rohit.mybank.model.admin.AdminTransactionPageResponse;
import com.rohit.mybank.model.admin.AdminTransactionResponse;
import com.rohit.mybank.repository.AdminTransactionRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTransactionsActivity
        extends AppCompatActivity {

    // =====================================================
    // CONSTANTS
    // =====================================================

    private static final int PAGE_SIZE = 10;

    private static final String SORT_DIRECTION = "desc";


    // =====================================================
    // VIEWS
    // =====================================================

    private TextView tvTitle;

    private TextView tvTransactionCount;

    private RecyclerView rvTransactions;

    private ProgressBar progressBar;

    private Button btnLoadMore;


    // =====================================================
    // DATA
    // =====================================================

    private final List<AdminTransactionResponse> transactions =
            new ArrayList<>();


    // =====================================================
    // ADAPTER
    // =====================================================

    private AdminTransactionAdapter adapter;


    // =====================================================
    // REPOSITORY
    // =====================================================

    private AdminTransactionRepository repository;


    // =====================================================
    // PAGINATION
    // =====================================================

    private int currentPage = 0;

    private boolean lastPage = false;

    private boolean loading = false;


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_transactions
        );

        initializeViews();

        repository =
                new AdminTransactionRepository(
                        this
                );

        setupRecyclerView();

        loadTransactions(false);
    }


    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        tvTitle =
                findViewById(
                        R.id.tvTitle
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


        // -------------------------------------------------
        // TITLE
        // -------------------------------------------------

        if (tvTitle != null) {

            tvTitle.setText(
                    "All Transactions"
            );
        }


        // -------------------------------------------------
        // LOAD MORE
        // -------------------------------------------------

        if (btnLoadMore != null) {

            btnLoadMore.setOnClickListener(
                    v -> loadTransactions(true)
            );
        }
    }


    // =====================================================
    // RECYCLER VIEW
    // =====================================================

    private void setupRecyclerView() {

        /*
         * IMPORTANT:
         *
         * The adapter and activity MUST use:
         *
         * List<AdminTransactionResponse>
         *
         * They must NOT use:
         *
         * List<Transaction>
         */

        adapter =
                new AdminTransactionAdapter(
                        transactions
                );


        rvTransactions.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        rvTransactions.setHasFixedSize(
                true
        );


        rvTransactions.setAdapter(
                adapter
        );
    }


    // =====================================================
    // LOAD TRANSACTIONS
    // =====================================================

    private void loadTransactions(
            boolean loadMore) {

        // -------------------------------------------------
        // PREVENT DUPLICATE REQUEST
        // -------------------------------------------------

        if (loading) {
            return;
        }


        // -------------------------------------------------
        // PREVENT LOAD AFTER LAST PAGE
        // -------------------------------------------------

        if (loadMore && lastPage) {
            return;
        }


        loading = true;

        showLoading(true);


        // -------------------------------------------------
        // DETERMINE PAGE
        // -------------------------------------------------

        int page =
                loadMore
                        ? currentPage + 1
                        : 0;


        // -------------------------------------------------
        // API REQUEST
        // -------------------------------------------------

        repository
                .getTransactions(
                        page,
                        PAGE_SIZE,
                        SORT_DIRECTION
                )
                .enqueue(
                        new Callback<
                                AdminTransactionPageResponse>() {

                            @Override
                            public void onResponse(

                                    @NonNull Call<
                                            AdminTransactionPageResponse>
                                            call,

                                    @NonNull Response<
                                            AdminTransactionPageResponse>
                                            response) {

                                loading = false;

                                showLoading(false);


                                // =================================
                                // HTTP ERROR
                                // =================================

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    handleHttpError(
                                            response.code()
                                    );

                                    return;
                                }


                                AdminTransactionPageResponse
                                        pageResponse =
                                        response.body();


                                // =================================
                                // FIRST PAGE
                                // =================================

                                if (!loadMore) {

                                    transactions.clear();

                                    currentPage = 0;

                                    lastPage = false;
                                }


                                // =================================
                                // CONTENT
                                // =================================

                                List<AdminTransactionResponse>
                                        pageTransactions =
                                        pageResponse.getContent();


                                if (pageTransactions != null
                                        && !pageTransactions.isEmpty()) {

                                    transactions.addAll(
                                            pageTransactions
                                    );
                                }


                                // =================================
                                // PAGINATION
                                // =================================

                                currentPage =
                                        pageResponse.getNumber();

                                lastPage =
                                        pageResponse.isLast();


                                // =================================
                                // UPDATE RECYCLER VIEW
                                // =================================

                                adapter.notifyDataSetChanged();


                                // =================================
                                // UPDATE COUNT
                                // =================================

                                tvTransactionCount.setText(
                                        pageResponse
                                                .getTotalElements()
                                                + " transactions"
                                );


                                // =================================
                                // LOAD MORE BUTTON
                                // =================================

                                updateLoadMoreButton();


                                // =================================
                                // EMPTY RESULT
                                // =================================

                                if (transactions.isEmpty()) {

                                    Toast.makeText(
                                            AdminTransactionsActivity.this,
                                            "No transactions found.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            // =============================================
                            // API FAILURE
                            // =============================================

                            @Override
                            public void onFailure(

                                    @NonNull Call<
                                            AdminTransactionPageResponse>
                                            call,

                                    @NonNull Throwable t) {

                                loading = false;

                                showLoading(false);


                                Toast.makeText(
                                        AdminTransactionsActivity.this,
                                        "Network Error: "
                                                + safeMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // HTTP ERROR
    // =====================================================

    private void handleHttpError(
            int code) {

        if (code == 401) {

            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
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
                    "Transaction service not found.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (code == 500) {

            Toast.makeText(
                    this,
                    "Server error while loading transactions.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        Toast.makeText(
                this,
                "Unable to load transactions.\nHTTP "
                        + code,
                Toast.LENGTH_LONG
        ).show();
    }


    // =====================================================
    // LOAD MORE BUTTON
    // =====================================================

    private void updateLoadMoreButton() {

        if (btnLoadMore == null) {
            return;
        }


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


    // =====================================================
    // LOADING
    // =====================================================

    private void showLoading(
            boolean show) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    show
                            ? View.VISIBLE
                            : View.GONE
            );
        }


        if (btnLoadMore != null) {

            btnLoadMore.setEnabled(
                    !show && !lastPage
            );
        }
    }


    // =====================================================
    // SAFE ERROR MESSAGE
    // =====================================================

    private String safeMessage(
            Throwable t) {

        if (t == null
                || t.getMessage() == null
                || t.getMessage()
                .trim()
                .isEmpty()) {

            return "Unable to connect to server.";
        }

        return t.getMessage();
    }
}