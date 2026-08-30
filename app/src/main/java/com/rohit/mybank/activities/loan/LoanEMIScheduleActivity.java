package com.rohit.mybank.activities.loan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rohit.mybank.R;
import com.rohit.mybank.adapter.LoanEMIAdapter;
import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.loan.EMIPaymentResponse;
import com.rohit.mybank.model.loan.LoanEMIResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * LOAN EMI SCHEDULE ACTIVITY
 * =========================================================
 *
 * Displays the complete EMI schedule for a loan.
 *
 * Features:
 *
 * 1. Load EMI schedule
 * 2. Display EMI details
 * 3. Show paid/pending status
 * 4. Allow payment of next pending EMI
 * 5. Confirm EMI payment
 * 6. Verify Transaction PIN
 * 7. Process EMI payment
 * 8. Display payment success
 * 9. Refresh schedule after payment
 *
 */
public class LoanEMIScheduleActivity
        extends AppCompatActivity
        implements LoanEMIAdapter.OnPayEMIClickListener {


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

    private TextView tvEmpty;

    private RecyclerView recyclerViewEMI;


    // =========================================================
    // ADAPTER
    // =========================================================

    private LoanEMIAdapter adapter;


    // =========================================================
    // API
    // =========================================================

    private LoanApi loanApi;


    // =========================================================
    // LOAN NUMBER
    // =========================================================

    private String loanNumber;


    // =========================================================
    // PROGRESS DIALOG
    // =========================================================

    private ProgressDialog progressDialog;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_loan_emi_schedule
        );


        // -----------------------------------------------------
        // GET LOAN NUMBER
        // -----------------------------------------------------

        loanNumber =
                getIntent().getStringExtra(
                        EXTRA_LOAN_NUMBER
                );


        if (TextUtils.isEmpty(loanNumber)) {

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
        // INITIALIZE API
        // -----------------------------------------------------

        initializeApi();


        // -----------------------------------------------------
        // INITIALIZE RECYCLER VIEW
        // -----------------------------------------------------

        initializeRecyclerView();


        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        btnBack.setOnClickListener(
                v -> finish()
        );


        // -----------------------------------------------------
        // INITIAL STATE
        // -----------------------------------------------------

        recyclerViewEMI.setVisibility(
                View.GONE
        );

        tvEmpty.setVisibility(
                View.GONE
        );


        // -----------------------------------------------------
        // LOAD EMI SCHEDULE
        // -----------------------------------------------------

        loadEMISchedule();
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


        tvEmpty =
                findViewById(
                        R.id.tvEmpty
                );


        recyclerViewEMI =
                findViewById(
                        R.id.recyclerViewEMI
                );


        tvLoanNumber.setText(
                loanNumber
        );


        tvTitle.setText(
                "EMI Schedule"
        );
    }


    // =========================================================
    // INITIALIZE API
    // =========================================================

    private void initializeApi() {

        /*
         * IMPORTANT:
         *
         * Your project uses:
         *
         * RetrofitClient.getClient(this)
         *
         * NOT:
         *
         * RetrofitClient.getInstance()
         */

        loanApi =
                RetrofitClient
                        .getClient(this)
                        .create(
                                LoanApi.class
                        );
    }


    // =========================================================
    // INITIALIZE RECYCLER VIEW
    // =========================================================

    private void initializeRecyclerView() {

        adapter =
                new LoanEMIAdapter(
                        this
                );


        recyclerViewEMI.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        recyclerViewEMI.setHasFixedSize(
                false
        );


        recyclerViewEMI.setAdapter(
                adapter
        );
    }


    // =========================================================
    // LOAD EMI SCHEDULE
    // =========================================================

    private void loadEMISchedule() {

        showLoading(
                "Loading EMI schedule..."
        );


        Call<List<LoanEMIResponse>> call =
                loanApi.getEMISchedule(
                        loanNumber
                );


        call.enqueue(
                new Callback<List<LoanEMIResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<LoanEMIResponse>> call,
                            @NonNull Response<List<LoanEMIResponse>> response
                    ) {

                        hideLoading();


                        // -------------------------------------------------
                        // HTTP ERROR
                        // -------------------------------------------------

                        if (!response.isSuccessful()) {

                            recyclerViewEMI.setVisibility(
                                    View.GONE
                            );


                            tvEmpty.setVisibility(
                                    View.VISIBLE
                            );


                            tvEmpty.setText(
                                    "Unable to load EMI schedule.\n\nHTTP " +
                                            response.code()
                            );


                            return;
                        }


                        // -------------------------------------------------
                        // RESPONSE
                        // -------------------------------------------------

                        List<LoanEMIResponse> emis =
                                response.body();


                        // -------------------------------------------------
                        // EMPTY SCHEDULE
                        // -------------------------------------------------

                        if (emis == null ||
                                emis.isEmpty()) {

                            adapter.setEMIs(
                                    null
                            );


                            recyclerViewEMI.setVisibility(
                                    View.GONE
                            );


                            tvEmpty.setVisibility(
                                    View.VISIBLE
                            );


                            tvEmpty.setText(
                                    "No EMI schedule found."
                            );


                            tvSummary.setText(
                                    "No EMI schedule available."
                            );


                            return;
                        }


                        // -------------------------------------------------
                        // DISPLAY SCHEDULE
                        // -------------------------------------------------

                        recyclerViewEMI.setVisibility(
                                View.VISIBLE
                        );


                        tvEmpty.setVisibility(
                                View.GONE
                        );


                        adapter.setEMIs(
                                emis
                        );


                        // -------------------------------------------------
                        // UPDATE SUMMARY
                        // -------------------------------------------------

                        updateSummary(
                                emis
                        );
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<List<LoanEMIResponse>> call,
                            @NonNull Throwable t
                    ) {

                        hideLoading();


                        recyclerViewEMI.setVisibility(
                                View.GONE
                        );


                        tvEmpty.setVisibility(
                                View.VISIBLE
                        );


                        tvEmpty.setText(
                                "Unable to connect to server."
                        );


                        Toast.makeText(
                                LoanEMIScheduleActivity.this,
                                "Network error: " +
                                        t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // UPDATE SUMMARY
    // =========================================================

    private void updateSummary(
            List<LoanEMIResponse> emis
    ) {

        int total =
                emis.size();


        int paid =
                0;


        int pending =
                0;


        double totalAmount =
                0.0;


        for (LoanEMIResponse emi : emis) {

            if (emi == null) {
                continue;
            }


            // -------------------------------------------------
            // AMOUNT
            // -------------------------------------------------

            if (emi.getEmiAmount() != null) {

                totalAmount +=
                        emi.getEmiAmount();
            }


            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            if (emi.getStatus() != null) {

                String status =
                        emi.getStatus().name();


                if ("PAID".equalsIgnoreCase(status)) {

                    paid++;

                } else if (
                        "PENDING".equalsIgnoreCase(status)
                ) {

                    pending++;
                }
            }
        }


        tvSummary.setText(
                total +
                        " EMIs • " +
                        paid +
                        " Paid • " +
                        pending +
                        " Pending\n" +
                        "Total Scheduled: " +
                        formatAmount(
                                totalAmount
                        )
        );
    }


    // =========================================================
    // PAY EMI CLICK
    // =========================================================

    @Override
    public void onPayEMIClick(
            LoanEMIResponse emi
    ) {

        // -----------------------------------------------------
        // VALIDATE EMI
        // -----------------------------------------------------

        if (emi == null) {

            showError(
                    "Invalid EMI information."
            );

            return;
        }


        // -----------------------------------------------------
        // EMI NUMBER
        // -----------------------------------------------------

        if (emi.getEmiNumber() == null) {

            showError(
                    "EMI number is missing."
            );

            return;
        }


        // -----------------------------------------------------
        // LOAN NUMBER
        // -----------------------------------------------------

        if (TextUtils.isEmpty(
                emi.getLoanNumber()
        )) {

            showError(
                    "Loan number is missing."
            );

            return;
        }


        // -----------------------------------------------------
        // ALREADY PAID
        // -----------------------------------------------------

        if (emi.getStatus() != null &&
                "PAID".equalsIgnoreCase(
                        emi.getStatus().name()
                )) {

            Toast.makeText(
                    this,
                    "This EMI has already been paid.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // ONLY PENDING EMI CAN BE PAID
        // -----------------------------------------------------

        if (emi.getStatus() == null ||
                !"PENDING".equalsIgnoreCase(
                        emi.getStatus().name()
                )) {

            showError(
                    "This EMI is not available for payment."
            );

            return;
        }


        // -----------------------------------------------------
        // CONFIRM PAYMENT
        // -----------------------------------------------------

        showPaymentConfirmation(
                emi
        );
    }


    // =========================================================
    // PAYMENT CONFIRMATION
    // =========================================================

    private void showPaymentConfirmation(
            LoanEMIResponse emi
    ) {

        String amount =
                formatAmount(
                        emi.getEmiAmount()
                );


        String emiNumber =
                safeInteger(
                        emi.getEmiNumber()
                );


        String dueDate =
                safeString(
                        emi.getDueDate()
                );


        String message =
                "EMI Number: #" +
                        emiNumber +
                        "\n\n" +

                        "Amount: " +
                        amount +
                        "\n\n" +

                        "Due Date: " +
                        dueDate +
                        "\n\n" +

                        "The EMI amount will be debited from your linked bank account." +
                        "\n\n" +

                        "Do you want to continue?";


        new MaterialAlertDialogBuilder(
                this
        )

                .setTitle(
                        "Confirm EMI Payment"
                )

                .setMessage(
                        message
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Pay EMI",
                        (dialog, which) -> {

                            // =================================================
                            // IMPORTANT
                            // =================================================
                            //
                            // DO NOT DIRECTLY PROCESS THE PAYMENT.
                            //
                            // First verify the Transaction PIN.
                            // =================================================

                            showEMIPinVerification(
                                    emi
                            );
                        }
                )

                .show();
    }


    // =========================================================
    // TRANSACTION PIN VERIFICATION
    // =========================================================

    private void showEMIPinVerification(
            LoanEMIResponse emi
    ) {

        // -----------------------------------------------------
        // VALIDATE EMI
        // -----------------------------------------------------

        if (emi == null) {

            showError(
                    "Invalid EMI information."
            );

            return;
        }


        // -----------------------------------------------------
        // VALIDATE EMI NUMBER
        // -----------------------------------------------------

        if (emi.getEmiNumber() == null) {

            showError(
                    "EMI number is missing."
            );

            return;
        }


        // -----------------------------------------------------
        // VALIDATE LOAN NUMBER
        // -----------------------------------------------------

        if (TextUtils.isEmpty(
                emi.getLoanNumber()
        )) {

            showError(
                    "Loan number is missing."
            );

            return;
        }


        // =====================================================
        // SHOW TRANSACTION PIN DIALOG
        // =====================================================

        PinVerificationDialog.showForTransactionPin(

                LoanEMIScheduleActivity.this,

                new PinVerificationDialog
                        .OnPinVerifiedWithPinListener() {

                    @Override
                    public void onSuccess(
                            String transactionPin
                    ) {

                        // =================================================
                        // PIN VERIFIED
                        // =================================================

                        if (TextUtils.isEmpty(
                                transactionPin
                        )) {

                            showError(
                                    "Transaction PIN verification failed."
                            );

                            return;
                        }


                        // =================================================
                        // PIN VERIFIED SUCCESSFULLY
                        // =================================================
                        //
                        // The PIN dialog has already verified the PIN
                        // against the backend.
                        //
                        // Only after successful verification do we
                        // continue with the EMI payment.
                        //
                        // We intentionally do not log or display
                        // the transaction PIN.
                        // =================================================

                        processEMIPayment(
                                emi
                        );
                    }


                    @Override
                    public void onFailure() {

                        // =================================================
                        // PIN VERIFICATION FAILED
                        // =================================================

                        Toast.makeText(
                                LoanEMIScheduleActivity.this,
                                "Transaction PIN verification failed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // PROCESS EMI PAYMENT
    // =========================================================

    private void processEMIPayment(
            LoanEMIResponse emi
    ) {

        // -----------------------------------------------------
        // FINAL VALIDATION
        // -----------------------------------------------------

        if (emi == null) {

            showError(
                    "Invalid EMI information."
            );

            return;
        }


        if (TextUtils.isEmpty(
                emi.getLoanNumber()
        )) {

            showError(
                    "Loan number is missing."
            );

            return;
        }


        if (emi.getEmiNumber() == null) {

            showError(
                    "EMI number is missing."
            );

            return;
        }


        // =====================================================
        // SHOW LOADING
        // =====================================================

        showLoading(
                "Processing EMI payment..."
        );


        // =====================================================
        // API CALL
        // =====================================================

        Call<EMIPaymentResponse> call =
                loanApi.payEMI(
                        emi.getLoanNumber(),
                        emi.getEmiNumber()
                );


        call.enqueue(
                new Callback<EMIPaymentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<EMIPaymentResponse> call,
                            @NonNull Response<EMIPaymentResponse> response
                    ) {

                        hideLoading();


                        // -------------------------------------------------
                        // HTTP ERROR
                        // -------------------------------------------------

                        if (!response.isSuccessful()) {

                            showError(
                                    "EMI payment failed.\n\nHTTP " +
                                            response.code()
                            );

                            return;
                        }


                        // -------------------------------------------------
                        // RESPONSE
                        // -------------------------------------------------

                        EMIPaymentResponse result =
                                response.body();


                        if (result == null) {

                            showError(
                                    "Empty payment response received."
                            );

                            return;
                        }


                        // -------------------------------------------------
                        // BUSINESS ERROR
                        // -------------------------------------------------

                        if (!result.isSuccess()) {

                            showError(
                                    safeMessage(
                                            result.getMessage()
                                    )
                            );

                            return;
                        }


                        // -------------------------------------------------
                        // SUCCESS
                        // -------------------------------------------------

                        showPaymentSuccess(
                                result
                        );
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<EMIPaymentResponse> call,
                            @NonNull Throwable t
                    ) {

                        hideLoading();


                        showError(
                                "Payment request failed.\n\n" +
                                        "Please check your internet connection."
                        );
                    }
                }
        );
    }


    // =========================================================
    // PAYMENT SUCCESS
    // =========================================================

    private void showPaymentSuccess(
            EMIPaymentResponse result
    ) {

        if (result == null) {

            showError(
                    "Invalid payment response."
            );

            return;
        }


        String amountPaid =
                formatAmount(
                        result.getAmountPaid()
                );


        String principalPaid =
                formatAmount(
                        result.getPrincipalPaid()
                );


        String interestPaid =
                formatAmount(
                        result.getInterestPaid()
                );


        String remainingPrincipal =
                formatAmount(
                        result.getRemainingPrincipal()
                );


        String remainingInterest =
                formatAmount(
                        result.getRemainingInterest()
                );


        String remainingBalance =
                formatAmount(
                        result.getRemainingAccountBalance()
                );


        String paymentDate =
                safeString(
                        result.getPaymentDate()
                );


        String loanStatus =
                result.getLoanStatus() != null
                        ? result.getLoanStatus().name()
                        : "N/A";


        String message =
                "EMI #" +
                        safeInteger(
                                result.getEmiNumber()
                        ) +
                        " has been paid successfully." +
                        "\n\n" +

                        "Amount Paid: " +
                        amountPaid +
                        "\n\n" +

                        "Principal Paid: " +
                        principalPaid +
                        "\n\n" +

                        "Interest Paid: " +
                        interestPaid +
                        "\n\n" +

                        "Remaining Principal: " +
                        remainingPrincipal +
                        "\n\n" +

                        "Remaining Interest: " +
                        remainingInterest +
                        "\n\n" +

                        "Account Balance: " +
                        remainingBalance +
                        "\n\n" +

                        "Payment Date: " +
                        paymentDate +
                        "\n\n" +

                        "Loan Status: " +
                        loanStatus;


        new MaterialAlertDialogBuilder(
                this
        )

                .setTitle(
                        "Payment Successful"
                )

                .setMessage(
                        message
                )

                .setPositiveButton(
                        "OK",
                        (dialog, which) -> {

                            // -------------------------------------------------
                            // REFRESH EMI SCHEDULE
                            // -------------------------------------------------

                            loadEMISchedule();
                        }
                )

                .setCancelable(false)

                .show();
    }


    // =========================================================
    // SHOW LOADING
    // =========================================================

    private void showLoading(
            String message
    ) {

        if (isFinishing() ||
                isDestroyed()) {

            return;
        }


        if (progressDialog == null) {

            progressDialog =
                    new ProgressDialog(
                            this
                    );


            progressDialog.setCancelable(
                    false
            );
        }


        progressDialog.setMessage(
                message
        );


        if (!progressDialog.isShowing()) {

            progressDialog.show();
        }
    }


    // =========================================================
    // HIDE LOADING
    // =========================================================

    private void hideLoading() {

        if (progressDialog != null &&
                progressDialog.isShowing()) {

            progressDialog.dismiss();
        }
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // SAFE MESSAGE
    // =========================================================

    private String safeMessage(
            String message
    ) {

        if (message == null ||
                message.trim().isEmpty()) {

            return "Unable to process EMI payment.";
        }


        return message;
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "N/A";
        }


        return value;
    }


    // =========================================================
    // SAFE INTEGER
    // =========================================================

    private String safeInteger(
            Integer value
    ) {

        if (value == null) {

            return "N/A";
        }


        return String.valueOf(
                value
        );
    }


    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(
            Double amount
    ) {

        if (amount == null) {

            return "₹ 0.00";
        }


        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale(
                                "en",
                                "IN"
                        )
                );


        formatter.setMinimumFractionDigits(
                2
        );


        formatter.setMaximumFractionDigits(
                2
        );


        return "₹ " +
                formatter.format(
                        amount
                );
    }


    // =========================================================
    // ON DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        hideLoading();

        super.onDestroy();
    }
}