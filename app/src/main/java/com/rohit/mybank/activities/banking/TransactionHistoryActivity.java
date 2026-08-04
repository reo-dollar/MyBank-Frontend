package com.rohit.mybank.activities.banking;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.rohit.mybank.R;
import com.rohit.mybank.adapter.TransactionAdapter;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.transaction.Transaction;
import com.rohit.mybank.model.transaction.TransactionPageResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private RecyclerView recyclerTransactions;

    private ProgressBar progressBar;

    private DashboardRepository dashboardRepository;
    private TransactionRepository transactionRepository;

    private TransactionAdapter adapter;

    private final List<Transaction> transactionList = new ArrayList<>();

    private String accountNumber = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        initializeViews();

        dashboardRepository = new DashboardRepository(this);
        transactionRepository = new TransactionRepository(this);

        adapter = new TransactionAdapter(transactionList);

        recyclerTransactions.setLayoutManager(
                new LinearLayoutManager(this));

        recyclerTransactions.setAdapter(adapter);

        toolbar.setNavigationOnClickListener(v -> finish());

        loadDashboard();
    }

    private void initializeViews() {

        toolbar = findViewById(R.id.toolbar);

        recyclerTransactions =
                findViewById(R.id.recyclerTransactions);

        progressBar =
                findViewById(R.id.progressBar);
    }

    private void loadDashboard() {

        progressBar.setVisibility(View.VISIBLE);

        dashboardRepository.getMyAccount()
                .enqueue(new Callback<DashboardResponse>() {

                    @Override
                    public void onResponse(
                            Call<DashboardResponse> call,
                            Response<DashboardResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            accountNumber =
                                    response.body().getAccNo();

                            loadTransactions();

                        } else {

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(
                                    TransactionHistoryActivity.this,
                                    "Unable to load account",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<DashboardResponse> call,
                            Throwable t) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                TransactionHistoryActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
    private void loadTransactions() {

        transactionRepository.getTransactions(
                accountNumber,
                0,
                20,
                "desc"
        ).enqueue(new Callback<TransactionPageResponse>() {

            @Override
            public void onResponse(
                    Call<TransactionPageResponse> call,
                    Response<TransactionPageResponse> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()
                        && response.body() != null) {

                    transactionList.clear();

                    if (response.body().getContent() != null) {

                        transactionList.addAll(
                                response.body().getContent()
                        );

                        adapter.notifyDataSetChanged();

                    } else {

                        Toast.makeText(
                                TransactionHistoryActivity.this,
                                "No transactions found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    Toast.makeText(
                            TransactionHistoryActivity.this,
                            "Unable to load transactions",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<TransactionPageResponse> call,
                    Throwable t) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(
                        TransactionHistoryActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

}