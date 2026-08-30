package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.adapter.RDHistoryAdapter;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RDHistoryActivity extends AppCompatActivity {

    // =========================================================
    // VIEWS
    // =========================================================

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private TextView tvEmpty;

    // =========================================================
    // ADAPTER / DATA
    // =========================================================

    private RDHistoryAdapter adapter;

    private List<RDHistoryResponse> historyList;

    // =========================================================
    // REPOSITORY
    // =========================================================

    private RecurringDepositRepository repository;

    // =========================================================
    // RD NUMBER
    // =========================================================

    private String rdNumber;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_rd_history
        );

        initializeViews();

        initializeRepository();

        loadRDNumber();

        if (TextUtils.isEmpty(rdNumber)) {

            showError(
                    "RD Number not received."
            );

            return;
        }

        initializeRecyclerView();

        loadHistory();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerView =
                findViewById(
                        R.id.recyclerViewHistory
                );

        progressBar =
                findViewById(
                        R.id.progressBarHistory
                );

        tvEmpty =
                findViewById(
                        R.id.tvEmptyHistory
                );
    }

    // =========================================================
    // INITIALIZE REPOSITORY
    // =========================================================

    private void initializeRepository() {

        repository =
                new RecurringDepositRepository(
                        this
                );
    }

    // =========================================================
    // LOAD RD NUMBER
    // =========================================================

    private void loadRDNumber() {

        if (getIntent() == null) {
            return;
        }

        rdNumber =
                getIntent().getStringExtra(
                        "RD_NUMBER"
                );
    }

    // =========================================================
    // INITIALIZE RECYCLER VIEW
    // =========================================================

    private void initializeRecyclerView() {

        historyList =
                new ArrayList<>();

        /*
         * RDHistoryAdapter accepts only:
         *
         * new RDHistoryAdapter(historyList)
         *
         * Do NOT pass Context here.
         */

        adapter =
                new RDHistoryAdapter(
                        historyList
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        recyclerView.setAdapter(
                adapter
        );
    }

    // =========================================================
    // LOAD HISTORY
    // =========================================================

    private void loadHistory() {

        showLoading(true);

        repository
                .getRecurringDepositHistory(
                        rdNumber
                )
                .enqueue(
                        new Callback<List<RDHistoryResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<RDHistoryResponse>> call,
                                    Response<List<RDHistoryResponse>> response) {

                                showLoading(false);

                                if (response.isSuccessful()) {

                                    List<RDHistoryResponse> result =
                                            response.body();

                                    if (result == null
                                            || result.isEmpty()) {

                                        showEmptyState();

                                        return;
                                    }

                                    showHistory(
                                            result
                                    );

                                } else {

                                    showServerError(
                                            response
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<List<RDHistoryResponse>> call,
                                    Throwable t) {

                                showLoading(false);

                                String message =
                                        t.getMessage();

                                if (TextUtils.isEmpty(
                                        message
                                )) {

                                    message =
                                            "Unable to load RD history.";
                                }

                                Toast.makeText(
                                        RDHistoryActivity.this,
                                        "Network Error\n\n"
                                                + message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // SHOW HISTORY
    // =========================================================

    private void showHistory(
            List<RDHistoryResponse> result) {

        recyclerView.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setVisibility(
                View.GONE
        );

        historyList.clear();

        historyList.addAll(
                result
        );

        adapter.notifyDataSetChanged();
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private void showEmptyState() {

        recyclerView.setVisibility(
                View.GONE
        );

        tvEmpty.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setText(
                "No RD transaction history found."
        );
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void showLoading(
            boolean loading) {

        if (loading) {

            progressBar.setVisibility(
                    View.VISIBLE
            );

            recyclerView.setVisibility(
                    View.GONE
            );

            tvEmpty.setVisibility(
                    View.GONE
            );

        } else {

            progressBar.setVisibility(
                    View.GONE
            );
        }
    }

    // =========================================================
    // SERVER ERROR
    // =========================================================

    private void showServerError(
            Response<List<RDHistoryResponse>> response) {

        String message;

        switch (response.code()) {

            case 400:
                message =
                        "Invalid RD history request.";
                break;

            case 401:
                message =
                        "Session expired. Please login again.";
                break;

            case 403:
                message =
                        "You are not authorized to view this RD history.";
                break;

            case 404:
                message =
                        "RD history endpoint was not found.";
                break;

            case 500:
                message =
                        "Server error while loading RD history.";
                break;

            default:
                message =
                        "Unable to load RD history.";
                break;
        }

        Toast.makeText(
                RDHistoryActivity.this,
                "HTTP "
                        + response.code()
                        + "\n\n"
                        + message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // GENERIC ERROR
    // =========================================================

    private void showError(
            String message) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }
}