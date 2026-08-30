package com.rohit.mybank.activities.loan;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.adapter.LoanEMIPaymentHistoryAdapter;
import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.loan.LoanEMIPaymentHistoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanEMIPaymentHistoryActivity
        extends AppCompatActivity {

    // =========================================================
    // CONSTANT
    // =========================================================

    public static final String EXTRA_LOAN_NUMBER =
            "loanNumber";


    // =========================================================
    // VIEWS
    // =========================================================

    private ImageButton btnBack;

    private TextView tvTitle;
    private TextView tvLoanNumber;
    private TextView tvSummary;

    private RecyclerView recyclerViewHistory;

    private View progressBar;
    private View emptyState;


    // =========================================================
    // API
    // =========================================================

    private LoanApi loanApi;


    // =========================================================
    // ADAPTER
    // =========================================================

    private LoanEMIPaymentHistoryAdapter adapter;


    // =========================================================
    // DATA
    // =========================================================

    private String loanNumber;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_loan_payment_history
        );


        // -----------------------------------------------------
        // GET LOAN NUMBER
        // -----------------------------------------------------

        if (getIntent() != null) {

            loanNumber =
                    getIntent().getStringExtra(
                            EXTRA_LOAN_NUMBER
                    );
        }


        // -----------------------------------------------------
        // VALIDATE LOAN NUMBER
        // -----------------------------------------------------

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Loan number is missing.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        loanNumber =
                loanNumber.trim();


        // -----------------------------------------------------
        // INITIALIZE VIEWS
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // SHOW LOAN NUMBER
        // -----------------------------------------------------

        tvLoanNumber.setText(
                loanNumber
        );


        // -----------------------------------------------------
        // INITIALIZE RECYCLER VIEW
        // -----------------------------------------------------

        initializeRecyclerView();


        // -----------------------------------------------------
        // INITIALIZE API
        // -----------------------------------------------------

        initializeApi();


        // -----------------------------------------------------
        // INITIAL STATE
        // -----------------------------------------------------

        recyclerViewHistory.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.GONE
        );


        // -----------------------------------------------------
        // LOAD PAYMENT HISTORY
        // -----------------------------------------------------

        loadPaymentHistory();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        tvTitle =
                findViewById(
                        R.id.tvTitle
                );

        tvLoanNumber =
                findViewById(
                        R.id.tvLoanNumber
                );

        tvSummary =
                findViewById(
                        R.id.tvSummary
                );

        recyclerViewHistory =
                findViewById(
                        R.id.recyclerViewHistory
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        emptyState =
                findViewById(
                        R.id.emptyState
                );


        // -----------------------------------------------------
        // SAFETY CHECK
        // -----------------------------------------------------

        if (btnBack == null) {

            throw new IllegalStateException(
                    "btnBack is missing from activity_loan_payment_history.xml"
            );
        }

        if (tvTitle == null) {

            throw new IllegalStateException(
                    "tvTitle is missing from activity_loan_payment_history.xml"
            );
        }

        if (tvLoanNumber == null) {

            throw new IllegalStateException(
                    "tvLoanNumber is missing from activity_loan_payment_history.xml"
            );
        }

        if (tvSummary == null) {

            throw new IllegalStateException(
                    "tvSummary is missing from activity_loan_payment_history.xml"
            );
        }

        if (recyclerViewHistory == null) {

            throw new IllegalStateException(
                    "recyclerViewHistory is missing from activity_loan_payment_history.xml"
            );
        }

        if (progressBar == null) {

            throw new IllegalStateException(
                    "progressBar is missing from activity_loan_payment_history.xml"
            );
        }

        if (emptyState == null) {

            throw new IllegalStateException(
                    "emptyState is missing from activity_loan_payment_history.xml"
            );
        }


        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        tvTitle.setText(
                "EMI Payment History"
        );


        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        btnBack.setOnClickListener(
                v -> finish()
        );
    }


    // =========================================================
    // INITIALIZE RECYCLER VIEW
    // =========================================================

    private void initializeRecyclerView() {

        adapter =
                new LoanEMIPaymentHistoryAdapter();


        recyclerViewHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );


        recyclerViewHistory.setHasFixedSize(
                false
        );


        recyclerViewHistory.setAdapter(
                adapter
        );
    }


    // =========================================================
    // INITIALIZE API
    // =========================================================

    private void initializeApi() {

        loanApi =
                RetrofitClient
                        .getClient(this)
                        .create(
                                LoanApi.class
                        );
    }


    // =========================================================
    // LOAD PAYMENT HISTORY
    // =========================================================

    private void loadPaymentHistory() {

        showLoading();


        if (loanApi == null) {

            showError(
                    "Loan API is not initialized."
            );

            return;
        }


        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            showError(
                    "Loan number is missing."
            );

            return;
        }


        Call<List<LoanEMIPaymentHistoryResponse>> call =
                loanApi.getLoanPaymentHistory(
                        loanNumber
                );


        call.enqueue(
                new Callback<List<LoanEMIPaymentHistoryResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<LoanEMIPaymentHistoryResponse>> call,
                            @NonNull Response<List<LoanEMIPaymentHistoryResponse>> response
                    ) {

                        if (response.isSuccessful()) {

                            List<LoanEMIPaymentHistoryResponse> history =
                                    response.body();


                            if (history == null ||
                                    history.isEmpty()) {

                                showEmptyState();

                            } else {

                                showHistory(
                                        history
                                );
                            }

                            return;
                        }


                        // -------------------------------------------------
                        // HTTP ERROR
                        // -------------------------------------------------

                        String message;


                        switch (response.code()) {

                            case 401:

                                message =
                                        "Session expired. Please login again.";

                                break;


                            case 403:

                                message =
                                        "You are not authorized to view this history.";

                                break;


                            case 404:

                                message =
                                        "Payment history not found.";

                                break;


                            case 500:

                                message =
                                        "Server error. Please try again.";

                                break;


                            default:

                                message =
                                        "Unable to load payment history.";
                        }


                        showError(
                                message +
                                        "\nHTTP " +
                                        response.code()
                        );
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<List<LoanEMIPaymentHistoryResponse>> call,
                            @NonNull Throwable t
                    ) {

                        showError(
                                "Unable to connect to server.\n\n" +
                                        getErrorMessage(t)
                        );
                    }
                }
        );
    }


    // =========================================================
    // SHOW LOADING
    // =========================================================

    private void showLoading() {

        progressBar.setVisibility(
                View.VISIBLE
        );

        recyclerViewHistory.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.GONE
        );

        tvSummary.setText(
                "Loading payment history..."
        );
    }


    // =========================================================
    // SHOW HISTORY
    // =========================================================

    private void showHistory(
            List<LoanEMIPaymentHistoryResponse> history
    ) {

        progressBar.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.GONE
        );

        recyclerViewHistory.setVisibility(
                View.VISIBLE
        );


        adapter.setHistory(
                history
        );


        int count =
                history.size();


        if (count == 1) {

            tvSummary.setText(
                    "1 EMI payment found."
            );

        } else {

            tvSummary.setText(
                    count +
                            " EMI payments found."
            );
        }
    }


    // =========================================================
    // SHOW EMPTY STATE
    // =========================================================

    private void showEmptyState() {

        progressBar.setVisibility(
                View.GONE
        );

        recyclerViewHistory.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.VISIBLE
        );


        tvSummary.setText(
                "No EMI payments have been made yet."
        );
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        progressBar.setVisibility(
                View.GONE
        );

        recyclerViewHistory.setVisibility(
                View.GONE
        );

        emptyState.setVisibility(
                View.VISIBLE
        );


        tvSummary.setText(
                "Unable to load payment history."
        );


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String getErrorMessage(
            Throwable throwable
    ) {

        if (throwable == null) {

            return "Unknown network error.";
        }


        String message =
                throwable.getMessage();


        if (message == null ||
                message.trim().isEmpty()) {

            return "Network error.";
        }


        return message;
    }
}