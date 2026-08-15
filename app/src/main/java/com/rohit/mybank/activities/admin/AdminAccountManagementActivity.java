package com.rohit.mybank.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.adapter.AdminAccountAdapter;
import com.rohit.mybank.model.admin.AdminAccountResponse;
import com.rohit.mybank.repository.AdminAccountRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccountManagementActivity
        extends AppCompatActivity {

    // =========================================================
    // VIEWS
    // =========================================================

    private TextInputEditText etSearch;

    private RecyclerView recyclerViewAccounts;

    private ProgressBar progressBar;

    private TextView tvEmpty;

    // =========================================================
    // DATA
    // =========================================================

    private final List<AdminAccountResponse>
            accounts = new ArrayList<>();

    private AdminAccountAdapter adapter;

    private AdminAccountRepository repository;

    // =========================================================
    // LIFECYCLE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_account_management
        );

        initializeViews();

        repository =
                new AdminAccountRepository(this);

        setupRecyclerView();

        setupSearch();

        loadAccounts();
    }

    // =========================================================
    // INITIALIZE
    // =========================================================

    private void initializeViews() {

        etSearch =
                findViewById(
                        R.id.etSearch
                );

        recyclerViewAccounts =
                findViewById(
                        R.id.recyclerViewAccounts
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        tvEmpty =
                findViewById(
                        R.id.tvEmpty
                );
    }

    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        adapter =
                new AdminAccountAdapter(
                        accounts,
                        this::openAccountDetails
                );

        recyclerViewAccounts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerViewAccounts.setAdapter(
                adapter
        );
    }

    // =========================================================
    // LOAD ACCOUNTS
    // =========================================================

    private void loadAccounts() {

        showLoading(true);

        repository
                .getAccounts()
                .enqueue(
                        new Callback<
                                List<AdminAccountResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            List<AdminAccountResponse>
                                            > call,
                                    Response<
                                            List<AdminAccountResponse>
                                            > response) {

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    accounts.clear();

                                    accounts.addAll(
                                            response.body()
                                    );

                                    adapter.notifyDataSetChanged();

                                    updateEmptyState();

                                } else {

                                    Toast.makeText(
                                            AdminAccountManagementActivity.this,
                                            "Unable to load accounts. HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<
                                            List<AdminAccountResponse>
                                            > call,
                                    Throwable t) {

                                showLoading(false);

                                Toast.makeText(
                                        AdminAccountManagementActivity.this,
                                        "Network Error: "
                                                + safeMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void setupSearch() {

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        String query =
                                s.toString().trim();

                        if (query.isEmpty()) {

                            loadAccounts();

                        } else {

                            searchAccounts(query);
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    // =========================================================
    // SEARCH ACCOUNTS
    // =========================================================

    private void searchAccounts(
            String query) {

        repository
                .searchAccounts(query)
                .enqueue(
                        new Callback<
                                List<AdminAccountResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            List<AdminAccountResponse>
                                            > call,
                                    Response<
                                            List<AdminAccountResponse>
                                            > response) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    accounts.clear();

                                    accounts.addAll(
                                            response.body()
                                    );

                                    adapter.notifyDataSetChanged();

                                    updateEmptyState();

                                } else {

                                    accounts.clear();

                                    adapter.notifyDataSetChanged();

                                    updateEmptyState();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<
                                            List<AdminAccountResponse>
                                            > call,
                                    Throwable t) {

                                Toast.makeText(
                                        AdminAccountManagementActivity.this,
                                        "Search Error: "
                                                + safeMessage(t),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // OPEN ACCOUNT DETAILS
    // =========================================================

    private void openAccountDetails(
            AdminAccountResponse account) {

        if (account == null) {
            return;
        }

        String accNo =
                account.getAccNo();

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
                        this,
                        AdminAccountDetailsActivity.class
                );

        intent.putExtra(
                "accNo",
                accNo
        );

        startActivity(intent);
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private void updateEmptyState() {

        if (accounts.isEmpty()) {

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvEmpty.setVisibility(
                    View.GONE
            );
        }
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void showLoading(
            boolean loading) {

        progressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
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