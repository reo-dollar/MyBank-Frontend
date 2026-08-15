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
import com.rohit.mybank.adapter.AdminCustomerAdapter;
import com.rohit.mybank.model.admin.AdminCustomerResponse;
import com.rohit.mybank.repository.AdminCustomerRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ==========================================================
 * ADMIN CUSTOMER MANAGEMENT
 * ==========================================================
 *
 * Allows administrators to:
 *
 * 1. View all customers
 * 2. Search customers
 * 3. View customer details
 *
 * Flow:
 *
 * Admin Dashboard
 *       ↓
 * Customer Management
 *       ↓
 * AdminCustomerRepository
 *       ↓
 * ApiService
 *       ↓
 * Spring Boot Admin APIs
 */
public class AdminCustomerManagementActivity
        extends AppCompatActivity {

    // ==========================================================
    // VIEWS
    // ==========================================================

    private TextInputEditText etSearch;

    private TextView tvCustomerCount;

    private TextView tvEmpty;

    private ProgressBar progressBar;

    private RecyclerView rvCustomers;

    // ==========================================================
    // DATA
    // ==========================================================

    private final List<AdminCustomerResponse>
            customers =
            new ArrayList<>();

    // ==========================================================
    // ADAPTER
    // ==========================================================

    private AdminCustomerAdapter adapter;

    // ==========================================================
    // REPOSITORY
    // ==========================================================

    private AdminCustomerRepository repository;

    // ==========================================================
    // SEARCH
    // ==========================================================

    private String lastSearch = "";

    // ==========================================================
    // ON CREATE
    // ==========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // ======================================================
        // LAYOUT
        // ======================================================

        setContentView(
                R.layout.activity_admin_customer_management
        );

        // ======================================================
        // INITIALIZE VIEWS
        // ======================================================

        initializeViews();

        // ======================================================
        // INITIALIZE REPOSITORY
        // ======================================================
        /*
         * AdminCustomerRepository requires Context.
         *
         * Activity extends Context, therefore `this` is correct.
         */

        repository =
                new AdminCustomerRepository(this);

        // ======================================================
        // RECYCLER VIEW
        // ======================================================

        setupRecyclerView();

        // ======================================================
        // SEARCH
        // ======================================================

        setupSearch();

        // ======================================================
        // LOAD CUSTOMERS
        // ======================================================

        loadCustomers();
    }

    // ==========================================================
    // INITIALIZE VIEWS
    // ==========================================================

    private void initializeViews() {

        etSearch =
                findViewById(
                        R.id.etSearchCustomers
                );

        tvCustomerCount =
                findViewById(
                        R.id.tvCustomerCount
                );

        tvEmpty =
                findViewById(
                        R.id.tvEmptyCustomers
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        rvCustomers =
                findViewById(
                        R.id.rvCustomers
                );
    }

    // ==========================================================
    // RECYCLER VIEW
    // ==========================================================

    private void setupRecyclerView() {

        adapter =
                new AdminCustomerAdapter(
                        customers,
                        this::openCustomerDetails
                );

        rvCustomers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvCustomers.setHasFixedSize(true);

        rvCustomers.setAdapter(adapter);
    }

    // ==========================================================
    // SEARCH
    // ==========================================================

    private void setupSearch() {

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                        // Not required
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        String query =
                                s.toString().trim();

                        // ======================================
                        // PREVENT DUPLICATE SEARCH
                        // ======================================

                        if (query.equals(lastSearch)) {
                            return;
                        }

                        lastSearch = query;

                        // ======================================
                        // LOAD ALL CUSTOMERS
                        // ======================================

                        if (query.isEmpty()) {

                            loadCustomers();

                        } else {

                            // ==================================
                            // SEARCH CUSTOMERS
                            // ==================================

                            searchCustomers(query);
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                        // Not required
                    }
                }
        );
    }

    // ==========================================================
    // LOAD CUSTOMERS
    // ==========================================================

    private void loadCustomers() {

        showLoading(true);

        repository
                .getCustomers()
                .enqueue(
                        new Callback<
                                List<
                                        AdminCustomerResponse
                                        >>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            List<
                                                    AdminCustomerResponse
                                                    >
                                            > call,
                                    Response<
                                            List<
                                                    AdminCustomerResponse
                                                    >
                                            > response) {

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body()
                                        != null) {

                                    displayCustomers(
                                            response.body()
                                    );

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<
                                            List<
                                                    AdminCustomerResponse
                                                    >
                                            > call,
                                    Throwable t) {

                                showLoading(false);

                                showNetworkError(
                                        t,
                                        "Unable to load customers."
                                );
                            }
                        }
                );
    }

    // ==========================================================
    // SEARCH CUSTOMERS
    // ==========================================================

    private void searchCustomers(
            String query) {

        showLoading(true);

        repository
                .searchCustomers(query)
                .enqueue(
                        new Callback<
                                List<
                                        AdminCustomerResponse
                                        >>() {

                            @Override
                            public void onResponse(
                                    Call<
                                            List<
                                                    AdminCustomerResponse
                                                    >
                                            > call,
                                    Response<
                                            List<
                                                    AdminCustomerResponse
                                                    >
                                            > response) {

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body()
                                        != null) {

                                    displayCustomers(
                                            response.body()
                                    );

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<
                                            List<
                                                    AdminCustomerResponse
                                                    >
                                            > call,
                                    Throwable t) {

                                showLoading(false);

                                showNetworkError(
                                        t,
                                        "Search failed."
                                );
                            }
                        }
                );
    }

    // ==========================================================
    // DISPLAY CUSTOMERS
    // ==========================================================

    private void displayCustomers(
            List<AdminCustomerResponse> result) {

        customers.clear();

        if (result != null) {

            customers.addAll(result);
        }

        // ======================================================
        // UPDATE ADAPTER
        // ======================================================

        adapter.notifyDataSetChanged();

        // ======================================================
        // UPDATE COUNT
        // ======================================================

        tvCustomerCount.setText(
                customers.size()
                        + " customers"
        );

        // ======================================================
        // EMPTY STATE
        // ======================================================

        if (customers.isEmpty()) {

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            rvCustomers.setVisibility(
                    View.GONE
            );

        } else {

            tvEmpty.setVisibility(
                    View.GONE
            );

            rvCustomers.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // ==========================================================
    // OPEN CUSTOMER DETAILS
    // ==========================================================

    private void openCustomerDetails(
            AdminCustomerResponse customer) {

        if (customer == null) {

            Toast.makeText(
                    this,
                    "Invalid customer.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String customerId =
                customer.getCustomerId();

        if (customerId == null
                || customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer ID is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ======================================================
        // OPEN DETAILS ACTIVITY
        // ======================================================

        Intent intent =
                new Intent(
                        this,
                        AdminCustomerDetailsActivity.class
                );

        intent.putExtra(
                "customerId",
                customerId
        );

        startActivity(intent);
    }

    // ==========================================================
    // LOADING
    // ==========================================================

    private void showLoading(
            boolean loading) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    loading
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        // ======================================================
        // Disable RecyclerView during loading
        // ======================================================

        if (loading) {

            rvCustomers.setAlpha(0.5f);

        } else {

            rvCustomers.setAlpha(1.0f);
        }
    }

    // ==========================================================
    // NETWORK ERROR
    // ==========================================================

    private void showNetworkError(
            Throwable throwable,
            String prefix) {

        String message =
                throwable != null
                        ? throwable.getMessage()
                        : null;

        if (message == null
                || message.trim().isEmpty()) {

            message =
                    "Unable to connect to server.";
        }

        Toast.makeText(
                AdminCustomerManagementActivity.this,
                prefix
                        + "\n"
                        + message,
                Toast.LENGTH_LONG
        ).show();
    }

    // ==========================================================
    // HTTP ERROR
    // ==========================================================

    private void handleHttpError(
            int code) {

        // ======================================================
        // UNAUTHORIZED
        // ======================================================

        if (code == 401) {

            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

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

            finish();

            return;
        }

        // ======================================================
        // NOT FOUND
        // ======================================================

        if (code == 404) {

            Toast.makeText(
                    this,
                    "Customer service endpoint not found.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ======================================================
        // SERVER ERROR
        // ======================================================

        if (code >= 500) {

            Toast.makeText(
                    this,
                    "Server error. Please try again later.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ======================================================
        // OTHER HTTP ERROR
        // ======================================================

        Toast.makeText(
                this,
                "Unable to load customers.\nHTTP "
                        + code,
                Toast.LENGTH_LONG
        ).show();
    }
}