package com.rohit.mybank.activities.loan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.adapter.LoanAdapter;
import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.loan.LoanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyLoansActivity extends AppCompatActivity
        implements LoanAdapter.OnLoanClickListener {

    // =========================================================
    // VIEWS
    // =========================================================

    private RecyclerView recyclerLoans;

    private View progressBar;

    private View emptyState;

    // =========================================================
    // ADAPTER
    // =========================================================

    private LoanAdapter loanAdapter;

    // =========================================================
    // API
    // =========================================================

    private LoanApi loanApi;

    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_my_loans
        );

        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();

        // =====================================================
        // INITIALIZE RECYCLER VIEW
        // =====================================================

        initializeRecyclerView();

        // =====================================================
        // INITIALIZE API
        // =====================================================

        initializeApi();

        // =====================================================
        // LOAD MY LOANS
        // =====================================================

        loadMyLoans();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerLoans =
                findViewById(
                        R.id.recyclerLoans
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        emptyState =
                findViewById(
                        R.id.emptyState
                );
    }

    // =========================================================
    // INITIALIZE RECYCLER VIEW
    // =========================================================

    private void initializeRecyclerView() {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        recyclerLoans.setLayoutManager(
                layoutManager
        );

        recyclerLoans.setHasFixedSize(true);

        loanAdapter =
                new LoanAdapter(this);

        recyclerLoans.setAdapter(
                loanAdapter
        );
    }

    // =========================================================
    // INITIALIZE API
    // =========================================================

    private void initializeApi() {

        Retrofit retrofit =
                RetrofitClient.getClient(
                        getApplicationContext()
                );

        loanApi =
                retrofit.create(
                        LoanApi.class
                );
    }

    // =========================================================
    // LOAD MY LOANS
    // =========================================================

    private void loadMyLoans() {

        showLoading();

        Call<List<LoanResponse>> call =
                loanApi.getMyLoans();

        call.enqueue(
                new Callback<List<LoanResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<LoanResponse>> call,
                            @NonNull Response<List<LoanResponse>> response
                    ) {

                        hideLoading();

                        // -----------------------------------------
                        // SERVER ERROR
                        // -----------------------------------------

                        if (!response.isSuccessful()) {

                            showError(
                                    "Unable to load loans. " +
                                            "Server returned: " +
                                            response.code()
                            );

                            return;
                        }

                        // -----------------------------------------
                        // RESPONSE BODY
                        // -----------------------------------------

                        List<LoanResponse> loans =
                                response.body();

                        // -----------------------------------------
                        // EMPTY
                        // -----------------------------------------

                        if (
                                loans == null ||
                                        loans.isEmpty()
                        ) {

                            showEmptyState();

                            return;
                        }

                        // -----------------------------------------
                        // DISPLAY LOANS
                        // -----------------------------------------

                        hideEmptyState();

                        loanAdapter.setLoans(
                                loans
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<LoanResponse>> call,
                            @NonNull Throwable throwable
                    ) {

                        hideLoading();

                        showError(
                                getNetworkErrorMessage(
                                        throwable
                                )
                        );
                    }
                }
        );
    }

    // =========================================================
    // LOAN CLICK
    // =========================================================
    //
    // This is the important fix.
    //
    // View Loan Details
    //       ↓
    // LoanDashboardActivity
    //       ↓
    // Pass loanNumber
    //       ↓
    // Dashboard API
    //
    // =========================================================

    @Override
    public void onLoanClick(
            LoanResponse loan
    ) {

        // -----------------------------------------------------
        // CHECK LOAN
        // -----------------------------------------------------

        if (loan == null) {

            Toast.makeText(
                    this,
                    "Loan information is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // -----------------------------------------------------
        // GET LOAN NUMBER
        // -----------------------------------------------------

        String loanNumber =
                loan.getLoanNumber();

        // -----------------------------------------------------
        // VALIDATE LOAN NUMBER
        // -----------------------------------------------------

        if (
                loanNumber == null ||
                        loanNumber.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Loan number is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // -----------------------------------------------------
        // OPEN LOAN DASHBOARD
        // -----------------------------------------------------

        Intent intent =
                new Intent(
                        MyLoansActivity.this,
                        LoanDashboardActivity.class
                );

        // -----------------------------------------------------
        // PASS LOAN NUMBER
        // -----------------------------------------------------

        intent.putExtra(
                "loanNumber",
                loanNumber
        );

        // -----------------------------------------------------
        // START ACTIVITY
        // -----------------------------------------------------

        startActivity(intent);
    }

    // =========================================================
    // SHOW LOADING
    // =========================================================

    private void showLoading() {

        progressBar.setVisibility(
                View.VISIBLE
        );

        recyclerLoans.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.GONE
        );
    }

    // =========================================================
    // HIDE LOADING
    // =========================================================

    private void hideLoading() {

        progressBar.setVisibility(
                View.GONE
        );
    }

    // =========================================================
    // SHOW EMPTY STATE
    // =========================================================

    private void showEmptyState() {

        recyclerLoans.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.VISIBLE
        );
    }

    // =========================================================
    // HIDE EMPTY STATE
    // =========================================================

    private void hideEmptyState() {

        recyclerLoans.setVisibility(
                View.VISIBLE
        );

        emptyState.setVisibility(
                View.GONE
        );
    }

    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        recyclerLoans.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.VISIBLE
        );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // NETWORK ERROR MESSAGE
    // =========================================================

    private String getNetworkErrorMessage(
            Throwable throwable
    ) {

        if (throwable == null) {

            return "Unable to connect to server.";
        }

        String message =
                throwable.getMessage();

        if (
                message == null ||
                        message.trim().isEmpty()
        ) {

            return "Unable to connect to server.";
        }

        return "Network error: " + message;
    }
}