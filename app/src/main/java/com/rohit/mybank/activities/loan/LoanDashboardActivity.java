package com.rohit.mybank.activities.loan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rohit.mybank.R;
import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.loan.LoanDashboardResponse;
import com.rohit.mybank.model.loan.LoanPrepaymentResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoanDashboardActivity extends AppCompatActivity {

    // =========================================================
    // API
    // =========================================================

    private LoanApi loanApi;


    // =========================================================
    // LOAN NUMBER
    // =========================================================

    private String loanNumber;


    // =========================================================
    // HEADER
    // =========================================================

    private ImageButton btnBack;


    // =========================================================
    // BASIC LOAN INFORMATION
    // =========================================================

    private TextView tvLoanNumber;
    private TextView tvLoanType;
    private TextView tvStatus;
    private TextView tvAccountNumber;


    // =========================================================
    // FINANCIAL INFORMATION
    // =========================================================

    private TextView tvPrincipal;
    private TextView tvInterestRate;
    private TextView tvTenure;
    private TextView tvEmi;
    private TextView tvTotalInterest;
    private TextView tvTotalPayable;


    // =========================================================
    // OUTSTANDING
    // =========================================================

    private TextView tvOutstandingPrincipal;
    private TextView tvOutstandingInterest;


    // =========================================================
    // EMI SUMMARY
    // =========================================================

    private TextView tvTotalEmis;
    private TextView tvPaidEmis;
    private TextView tvPendingEmis;


    // =========================================================
    // NEXT EMI
    // =========================================================

    private TextView tvNextEmiNumber;
    private TextView tvNextEmiDate;
    private TextView tvNextEmiAmount;


    // =========================================================
    // PURPOSE
    // =========================================================

    private TextView tvPurpose;


    // =========================================================
    // IMPORTANT DATES
    // =========================================================

    private TextView tvApplicationDate;
    private TextView tvApprovalDate;
    private TextView tvDisbursementDate;
    private TextView tvClosureDate;


    // =========================================================
    // STATE VIEWS
    // =========================================================

    private View progressBar;
    private View contentContainer;
    private View emptyState;


    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    private Button btnViewEmiSchedule;
    private Button btnViewPaymentHistory;
    private Button btnPrepayLoan;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_loan_dashboard
        );


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();


        // =====================================================
        // BACK BUTTON
        // =====================================================

        if (btnBack != null) {

            btnBack.setOnClickListener(
                    v -> finish()
            );
        }


        // =====================================================
        // GET LOAN NUMBER
        // =====================================================

        loanNumber =
                getIntent().getStringExtra(
                        "loanNumber"
                );


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


        // =====================================================
        // INITIALIZE API
        // =====================================================

        initializeApi();


        // =====================================================
        // INITIALIZE ACTIONS
        // =====================================================

        initializeActions();


        // =====================================================
        // LOAD DASHBOARD
        // =====================================================

        loadLoanDashboard();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        btnBack =
                findViewById(
                        R.id.btnBack
                );


        // -----------------------------------------------------
        // BASIC INFORMATION
        // -----------------------------------------------------

        tvLoanNumber =
                findViewById(
                        R.id.tvLoanNumber
                );

        tvLoanType =
                findViewById(
                        R.id.tvLoanType
                );

        tvStatus =
                findViewById(
                        R.id.tvStatus
                );

        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );


        // -----------------------------------------------------
        // FINANCIAL INFORMATION
        // -----------------------------------------------------

        tvPrincipal =
                findViewById(
                        R.id.tvPrincipal
                );

        tvInterestRate =
                findViewById(
                        R.id.tvInterestRate
                );

        tvTenure =
                findViewById(
                        R.id.tvTenure
                );

        tvEmi =
                findViewById(
                        R.id.tvEmi
                );

        tvTotalInterest =
                findViewById(
                        R.id.tvTotalInterest
                );

        tvTotalPayable =
                findViewById(
                        R.id.tvTotalPayable
                );


        // -----------------------------------------------------
        // OUTSTANDING
        // -----------------------------------------------------

        tvOutstandingPrincipal =
                findViewById(
                        R.id.tvOutstandingPrincipal
                );

        tvOutstandingInterest =
                findViewById(
                        R.id.tvOutstandingInterest
                );


        // -----------------------------------------------------
        // EMI SUMMARY
        // -----------------------------------------------------

        tvTotalEmis =
                findViewById(
                        R.id.tvTotalEmis
                );

        tvPaidEmis =
                findViewById(
                        R.id.tvPaidEmis
                );

        tvPendingEmis =
                findViewById(
                        R.id.tvPendingEmis
                );


        // -----------------------------------------------------
        // NEXT EMI
        // -----------------------------------------------------

        tvNextEmiNumber =
                findViewById(
                        R.id.tvNextEmiNumber
                );

        tvNextEmiDate =
                findViewById(
                        R.id.tvNextEmiDate
                );

        tvNextEmiAmount =
                findViewById(
                        R.id.tvNextEmiAmount
                );


        // -----------------------------------------------------
        // PURPOSE
        // -----------------------------------------------------

        tvPurpose =
                findViewById(
                        R.id.tvPurpose
                );


        // -----------------------------------------------------
        // IMPORTANT DATES
        // -----------------------------------------------------

        tvApplicationDate =
                findViewById(
                        R.id.tvApplicationDate
                );

        tvApprovalDate =
                findViewById(
                        R.id.tvApprovalDate
                );

        tvDisbursementDate =
                findViewById(
                        R.id.tvDisbursementDate
                );

        tvClosureDate =
                findViewById(
                        R.id.tvClosureDate
                );


        // -----------------------------------------------------
        // STATE
        // -----------------------------------------------------

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        contentContainer =
                findViewById(
                        R.id.contentContainer
                );

        emptyState =
                findViewById(
                        R.id.emptyState
                );


        // -----------------------------------------------------
        // ACTION BUTTONS
        // -----------------------------------------------------

        btnViewEmiSchedule =
                findViewById(
                        R.id.btnViewEmiSchedule
                );

        btnViewPaymentHistory =
                findViewById(
                        R.id.btnViewPaymentHistory
                );

        btnPrepayLoan =
                findViewById(
                        R.id.btnPrepayLoan
                );
    }


    // =========================================================
    // INITIALIZE ACTIONS
    // =========================================================

    private void initializeActions() {

        // =====================================================
        // VIEW EMI SCHEDULE
        // =====================================================

        if (btnViewEmiSchedule != null) {

            btnViewEmiSchedule.setOnClickListener(
                    v -> openEMISchedule()
            );
        }


        // =====================================================
        // VIEW PAYMENT HISTORY
        // =====================================================

        if (btnViewPaymentHistory != null) {

            btnViewPaymentHistory.setOnClickListener(
                    v -> openPaymentHistory()
            );
        }


        // =====================================================
        // CLOSE LOAN / PREPAY
        // =====================================================

        if (btnPrepayLoan != null) {

            btnPrepayLoan.setOnClickListener(
                    v -> showPrepaymentConfirmation()
            );
        }
    }


    // =========================================================
    // OPEN EMI SCHEDULE
    // =========================================================

    private void openEMISchedule() {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Loan number is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Intent intent =
                new Intent(
                        LoanDashboardActivity.this,
                        LoanEMIScheduleActivity.class
                );


        intent.putExtra(
                "loanNumber",
                loanNumber
        );


        startActivity(intent);
    }


    // =========================================================
    // OPEN PAYMENT HISTORY
    // =========================================================

    private void openPaymentHistory() {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Loan number is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Intent intent =
                new Intent(
                        LoanDashboardActivity.this,
                        LoanEMIPaymentHistoryActivity.class
                );


        intent.putExtra(
                "loanNumber",
                loanNumber
        );


        startActivity(intent);
    }


    // =========================================================
    // SHOW PREPAYMENT CONFIRMATION
    // =========================================================

    private void showPrepaymentConfirmation() {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Loan number is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // CHECK CURRENT STATUS
        // -----------------------------------------------------

        String status =
                tvStatus.getText() != null
                        ? tvStatus.getText().toString()
                        : "";


        if ("CLOSED".equalsIgnoreCase(status)) {

            Toast.makeText(
                    this,
                    "This loan is already closed.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // OUTSTANDING AMOUNTS
        // -----------------------------------------------------

        String principal =
                tvOutstandingPrincipal.getText().toString();

        String interest =
                tvOutstandingInterest.getText().toString();


        String message =
                "Are you sure you want to close this loan?\n\n" +

                        "Loan Number: " +
                        loanNumber +

                        "\n\nOutstanding Principal: " +
                        principal +

                        "\nOutstanding Interest: " +
                        interest +

                        "\n\nThe outstanding loan amount will be deducted from your account.";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Close Loan / Prepay")
                .setMessage(message)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Confirm",
                        (dialog, which) -> {

                            // =================================================
                            // TRANSACTION PIN VERIFICATION
                            // =================================================
                            //
                            // IMPORTANT:
                            // Do NOT call executeLoanPrepayment() directly.
                            // The loan must only be closed after the
                            // Transaction PIN has been successfully verified.
                            //
                            PinVerificationDialog.showForTransactionPin(
                                    LoanDashboardActivity.this,
                                    new PinVerificationDialog.OnPinVerifiedWithPinListener() {

                                        @Override
                                        public void onSuccess(
                                                String transactionPin
                                        ) {

                                            // PIN verified successfully.
                                            // Now execute the actual prepayment.
                                            executeLoanPrepayment();
                                        }

                                        @Override
                                        public void onFailure() {

                                            // Invalid PIN / network failure.
                                            // PinVerificationDialog keeps the
                                            // PIN dialog open when appropriate.
                                        }
                                    }
                            );
                        }
                )
                .show();
    }


    // =========================================================
    // EXECUTE PREPAYMENT
    // =========================================================

    private void executeLoanPrepayment() {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Loan number is unavailable.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // DISABLE BUTTON
        // -----------------------------------------------------

        if (btnPrepayLoan != null) {

            btnPrepayLoan.setEnabled(false);

            btnPrepayLoan.setText(
                    "Processing..."
            );
        }


        // -----------------------------------------------------
        // SHOW LOADING
        // -----------------------------------------------------

        showLoading();


        // -----------------------------------------------------
        // API CALL
        // -----------------------------------------------------

        Call<LoanPrepaymentResponse> call =
                loanApi.prepayLoan(
                        loanNumber
                );


        call.enqueue(
                new Callback<LoanPrepaymentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<LoanPrepaymentResponse> call,
                            @NonNull Response<LoanPrepaymentResponse> response
                    ) {

                        hideLoading();


                        // =================================================
                        // HTTP ERROR
                        // =================================================

                        if (!response.isSuccessful()) {

                            enablePrepayButton();

                            String serverMessage =
                                    getServerErrorMessage(response);

                            showPrepaymentError(
                                    "Unable to close the loan.\n\n" +
                                            serverMessage
                            );

                            return;
                        }


                        // =================================================
                        // RESPONSE BODY
                        // =================================================

                        LoanPrepaymentResponse prepayment =
                                response.body();


                        if (prepayment == null) {

                            enablePrepayButton();

                            showPrepaymentError(
                                    "Loan closure response is empty."
                            );

                            return;
                        }


                        // =================================================
                        // SUCCESS
                        // =================================================

                        showPrepaymentSuccess(
                                prepayment
                        );
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<LoanPrepaymentResponse> call,
                            @NonNull Throwable throwable
                    ) {

                        hideLoading();

                        enablePrepayButton();

                        showPrepaymentError(
                                getNetworkErrorMessage(
                                        throwable
                                )
                        );
                    }
                }
        );
    }


    // =========================================================
    // PREPAYMENT SUCCESS DIALOG
    // =========================================================

    private void showPrepaymentSuccess(
            LoanPrepaymentResponse response
    ) {

        String loan =
                valueOrNA(
                        response.getLoanNumber()
                );


        String account =
                valueOrNA(
                        response.getAccountNumber()
                );


        String prepaymentAmount =
                formatAmount(
                        response.getPrepaymentAmount()
                );


        String principalBefore =
                formatAmount(
                        response.getPrincipalBefore()
                );


        String interestBefore =
                formatAmount(
                        response.getInterestBefore()
                );


        String balanceAfter =
                formatAmount(
                        response.getAccountBalanceAfterPrepayment()
                );


        String outstandingPrincipal =
                formatAmount(
                        response.getOutstandingPrincipal()
                );


        String outstandingInterest =
                formatAmount(
                        response.getOutstandingInterest()
                );


        String status =
                response.getStatus() != null
                        ? response.getStatus().name()
                        : "CLOSED";


        String prepaymentDate =
                valueOrNA(
                        response.getPrepaymentDate()
                );


        String closureDate =
                valueOrNA(
                        response.getClosureDate()
                );


        String message =
                "Loan Number: " +
                        loan +

                        "\n\nAccount Number: " +
                        account +

                        "\n\nPrepayment Amount: " +
                        prepaymentAmount +

                        "\n\nPrincipal Before: " +
                        principalBefore +

                        "\nInterest Before: " +
                        interestBefore +

                        "\n\nOutstanding Principal: " +
                        outstandingPrincipal +

                        "\nOutstanding Interest: " +
                        outstandingInterest +

                        "\n\nAccount Balance After: " +
                        balanceAfter +

                        "\n\nPrepayment Date: " +
                        prepaymentDate +

                        "\nClosure Date: " +
                        closureDate +

                        "\n\nLoan Status: " +
                        status;


        new MaterialAlertDialogBuilder(this)
                .setTitle("Loan Closed Successfully")
                .setMessage(message)
                .setPositiveButton(
                        "OK",
                        (dialog, which) -> {

                            // -----------------------------------------
                            // LOAD UPDATED DASHBOARD
                            // -----------------------------------------

                            loadLoanDashboard();
                        }
                )
                .setCancelable(false)
                .show();
    }


    // =========================================================
    // READ SERVER ERROR
    // =========================================================

    /**
     * Extracts the actual error message returned by the backend.
     *
     * Spring Boot commonly returns JSON such as:
     *
     * {
     *     "timestamp": "...",
     *     "status": 400,
     *     "error": "Bad Request",
     *     "message": "Insufficient account balance for loan prepayment",
     *     "path": "/loans/..."
     * }
     *
     * The old implementation only displayed "Server returned: 400",
     * which hid the real business validation failure.
     */
    private String getServerErrorMessage(
            Response<?> response
    ) {

        if (response == null) {
            return "Unknown server error.";
        }

        if (response.errorBody() == null) {
            return "Server returned HTTP " +
                    response.code() +
                    ".";
        }

        try {

            String rawError =
                    response.errorBody().string();

            if (rawError == null ||
                    rawError.trim().isEmpty()) {

                return "Server returned HTTP " +
                        response.code() +
                        ".";
            }

            String trimmed =
                    rawError.trim();

            // -------------------------------------------------
            // JSON error response
            // -------------------------------------------------

            if (trimmed.startsWith("{")) {

                JSONObject json =
                        new JSONObject(trimmed);

                String message =
                        json.optString(
                                "message",
                                ""
                        );

                if (message != null &&
                        !message.trim().isEmpty() &&
                        !"null".equalsIgnoreCase(
                                message.trim()
                        )) {

                    return message.trim();
                }

                String detail =
                        json.optString(
                                "detail",
                                ""
                        );

                if (detail != null &&
                        !detail.trim().isEmpty() &&
                        !"null".equalsIgnoreCase(
                                detail.trim()
                        )) {

                    return detail.trim();
                }

                String error =
                        json.optString(
                                "error",
                                ""
                        );

                if (error != null &&
                        !error.trim().isEmpty() &&
                        !"null".equalsIgnoreCase(
                                error.trim()
                        )) {

                    return error.trim();
                }
            }

            // -------------------------------------------------
            // Non-JSON response
            // -------------------------------------------------

            return trimmed;

        } catch (Exception exception) {

            return "Server returned HTTP " +
                    response.code() +
                    ".";
        }
    }


    // =========================================================
    // PREPAYMENT ERROR
    // =========================================================

    private void showPrepaymentError(
            String message
    ) {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Loan Closure Failed")
                .setMessage(message)
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }


    // =========================================================
    // ENABLE PREPAY BUTTON
    // =========================================================

    private void enablePrepayButton() {

        if (btnPrepayLoan != null) {

            btnPrepayLoan.setEnabled(true);

            btnPrepayLoan.setText(
                    "Close Loan / Prepay"
            );
        }
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
    // LOAD DASHBOARD
    // =========================================================

    private void loadLoanDashboard() {

        showLoading();


        Call<LoanDashboardResponse> call =
                loanApi.getLoanDashboard(
                        loanNumber
                );


        call.enqueue(
                new Callback<LoanDashboardResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<LoanDashboardResponse> call,
                            @NonNull Response<LoanDashboardResponse> response
                    ) {

                        hideLoading();


                        if (!response.isSuccessful()) {

                            enablePrepayButton();

                            showError(
                                    "Unable to load loan dashboard.\n\n" +
                                            getServerErrorMessage(response)
                            );

                            return;
                        }


                        LoanDashboardResponse dashboard =
                                response.body();


                        if (dashboard == null) {

                            enablePrepayButton();

                            showError(
                                    "Loan dashboard data is empty."
                            );

                            return;
                        }


                        displayDashboard(
                                dashboard
                        );
                    }


                    @Override
                    public void onFailure(
                            @NonNull Call<LoanDashboardResponse> call,
                            @NonNull Throwable throwable
                    ) {

                        hideLoading();

                        enablePrepayButton();

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
    // DISPLAY DASHBOARD
    // =========================================================

    private void displayDashboard(
            LoanDashboardResponse dashboard
    ) {

        // -----------------------------------------------------
        // VISIBILITY
        // -----------------------------------------------------

        progressBar.setVisibility(
                View.GONE
        );


        contentContainer.setVisibility(
                View.VISIBLE
        );


        emptyState.setVisibility(
                View.GONE
        );


        // -----------------------------------------------------
        // BASIC INFORMATION
        // -----------------------------------------------------

        tvLoanNumber.setText(
                valueOrNA(
                        dashboard.getLoanNumber()
                )
        );


        tvAccountNumber.setText(
                valueOrNA(
                        dashboard.getAccountNumber()
                )
        );


        tvLoanType.setText(
                dashboard.getLoanType() != null
                        ? dashboard.getLoanType().name()
                        : "N/A"
        );


        String status =
                dashboard.getStatus() != null
                        ? dashboard.getStatus().name()
                        : "UNKNOWN";


        tvStatus.setText(
                status
        );


        tvPurpose.setText(
                valueOrNA(
                        dashboard.getPurpose()
                )
        );


        // -----------------------------------------------------
        // FINANCIAL INFORMATION
        // -----------------------------------------------------

        tvPrincipal.setText(
                formatAmount(
                        dashboard.getPrincipalAmount()
                )
        );


        tvInterestRate.setText(
                formatRate(
                        dashboard.getInterestRate()
                )
        );


        tvTenure.setText(
                dashboard.getTenureMonths() != null
                        ? dashboard.getTenureMonths()
                          + " months"
                        : "N/A"
        );


        tvEmi.setText(
                formatAmount(
                        dashboard.getEmiAmount()
                )
        );


        tvTotalInterest.setText(
                formatAmount(
                        dashboard.getTotalInterest()
                )
        );


        tvTotalPayable.setText(
                formatAmount(
                        dashboard.getTotalPayable()
                )
        );


        // -----------------------------------------------------
        // OUTSTANDING
        // -----------------------------------------------------

        tvOutstandingPrincipal.setText(
                formatAmount(
                        dashboard.getOutstandingPrincipal()
                )
        );


        tvOutstandingInterest.setText(
                formatAmount(
                        dashboard.getOutstandingInterest()
                )
        );


        // -----------------------------------------------------
        // EMI SUMMARY
        // -----------------------------------------------------

        tvTotalEmis.setText(
                String.valueOf(
                        dashboard.getTotalEMIs()
                )
        );


        tvPaidEmis.setText(
                String.valueOf(
                        dashboard.getPaidEMIs()
                )
        );


        tvPendingEmis.setText(
                String.valueOf(
                        dashboard.getPendingEMIs()
                )
        );


        // -----------------------------------------------------
        // NEXT EMI
        // -----------------------------------------------------

        if (dashboard.getNextEMINumber() != null) {

            tvNextEmiNumber.setText(
                    "EMI #" +
                            dashboard.getNextEMINumber()
            );

        } else {

            tvNextEmiNumber.setText(
                    "No pending EMI"
            );
        }


        tvNextEmiDate.setText(
                dashboard.getNextEMIDate() != null
                        ? dashboard.getNextEMIDate()
                        : "N/A"
        );


        tvNextEmiAmount.setText(
                formatAmount(
                        dashboard.getNextEMIAmount()
                )
        );


        // -----------------------------------------------------
        // IMPORTANT DATES
        // -----------------------------------------------------

        tvApplicationDate.setText(
                formatDate(
                        dashboard.getApplicationDate()
                )
        );


        tvApprovalDate.setText(
                formatDate(
                        dashboard.getApprovalDate()
                )
        );


        tvDisbursementDate.setText(
                formatDate(
                        dashboard.getDisbursementDate()
                )
        );


        tvClosureDate.setText(
                formatDate(
                        dashboard.getClosureDate()
                )
        );


        // -----------------------------------------------------
        // PREPAY BUTTON STATUS
        // -----------------------------------------------------

        updatePrepayButton(
                status
        );
    }


    // =========================================================
    // UPDATE PREPAY BUTTON
    // =========================================================

    private void updatePrepayButton(
            String status
    ) {

        if (btnPrepayLoan == null) {
            return;
        }


        if ("CLOSED".equalsIgnoreCase(status)) {

            btnPrepayLoan.setEnabled(false);

            btnPrepayLoan.setText(
                    "Loan Closed"
            );

        } else {

            btnPrepayLoan.setEnabled(true);

            btnPrepayLoan.setText(
                    "Close Loan / Prepay"
            );
        }
    }


    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(
            Number amount
    ) {

        if (amount == null) {

            return "₹ 0.00";
        }


        return String.format(
                Locale.US,
                "₹ %,.2f",
                amount.doubleValue()
        );
    }


    // =========================================================
    // FORMAT RATE
    // =========================================================

    private String formatRate(
            Number rate
    ) {

        if (rate == null) {

            return "N/A";
        }


        return String.format(
                Locale.US,
                "%.2f%%",
                rate.doubleValue()
        );
    }


    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String date
    ) {

        if (date == null ||
                date.trim().isEmpty()) {

            return "N/A";
        }


        return date;
    }


    // =========================================================
    // VALUE OR N/A
    // =========================================================

    private String valueOrNA(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "N/A";
        }


        return value;
    }


    // =========================================================
    // SHOW LOADING
    // =========================================================

    private void showLoading() {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.VISIBLE
            );
        }


        if (contentContainer != null) {

            contentContainer.setVisibility(
                    View.GONE
            );
        }


        if (emptyState != null) {

            emptyState.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // HIDE LOADING
    // =========================================================

    private void hideLoading() {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.GONE
            );
        }


        if (contentContainer != null) {

            contentContainer.setVisibility(
                    View.GONE
            );
        }


        if (emptyState != null) {

            emptyState.setVisibility(
                    View.VISIBLE
            );
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // NETWORK ERROR
    // =========================================================

    private String getNetworkErrorMessage(
            Throwable throwable
    ) {

        if (throwable == null) {

            return "Unable to connect to server.";
        }


        String message =
                throwable.getMessage();


        if (message == null ||
                message.trim().isEmpty()) {

            return "Unable to connect to server.";
        }


        return "Network error: " +
                message;
    }
}
