package com.rohit.mybank.activities.payments.recurringdeposit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.RDResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RDDetailsActivity extends AppCompatActivity {

    // =========================================================
    // TEXT VIEWS
    // =========================================================

    private TextView tvRDNumber;
    private TextView tvAccountNumber;
    private TextView tvCustomerName;
    private TextView tvMonthlyInstallment;
    private TextView tvInterestRate;
    private TextView tvTenure;
    private TextView tvTotalDeposit;
    private TextView tvMaturityAmount;
    private TextView tvPaidInstallments;
    private TextView tvRemainingInstallments;
    private TextView tvNextInstallment;
    private TextView tvStatus;


    // =========================================================
    // BUTTONS
    // =========================================================

    private MaterialButton btnPayInstallment;
    private MaterialButton btnHistory;
    private MaterialButton btnPrematureClose;


    // =========================================================
    // REPOSITORY
    // =========================================================

    private RecurringDepositRepository repository;


    // =========================================================
    // RD DATA
    // =========================================================

    private String rdNumber;

    private RDResponse currentRD;


    // =========================================================
    // ACTIVITY
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_rd_details
        );


        // =====================================================
        // REPOSITORY
        // =====================================================

        repository =
                new RecurringDepositRepository(
                        this
                );


        // =====================================================
        // INITIALIZE UI
        // =====================================================

        initializeViews();


        // =====================================================
        // READ INTENT
        // =====================================================

        readIntent();


        // =====================================================
        // CLICK LISTENERS
        // =====================================================

        setupClickListeners();


        // =====================================================
        // LOAD RD
        // =====================================================

        if (!TextUtils.isEmpty(rdNumber)) {

            loadDetails();

        } else {

            Toast.makeText(
                    RDDetailsActivity.this,
                    "Invalid RD Number",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvRDNumber =
                findViewById(
                        R.id.tvRDNumber
                );


        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );


        tvCustomerName =
                findViewById(
                        R.id.tvCustomerName
                );


        tvMonthlyInstallment =
                findViewById(
                        R.id.tvMonthlyInstallment
                );


        tvInterestRate =
                findViewById(
                        R.id.tvInterestRate
                );


        tvTenure =
                findViewById(
                        R.id.tvTenure
                );


        tvTotalDeposit =
                findViewById(
                        R.id.tvTotalDeposit
                );


        tvMaturityAmount =
                findViewById(
                        R.id.tvMaturityAmount
                );


        tvPaidInstallments =
                findViewById(
                        R.id.tvPaidInstallments
                );


        tvRemainingInstallments =
                findViewById(
                        R.id.tvRemainingInstallments
                );


        tvNextInstallment =
                findViewById(
                        R.id.tvNextInstallment
                );


        tvStatus =
                findViewById(
                        R.id.tvStatus
                );


        btnPayInstallment =
                findViewById(
                        R.id.btnPayInstallment
                );


        btnHistory =
                findViewById(
                        R.id.btnHistory
                );


        btnPrematureClose =
                findViewById(
                        R.id.btnPrematureClose
                );
    }


    // =========================================================
    // READ INTENT
    // =========================================================

    private void readIntent() {

        Intent intent =
                getIntent();


        if (intent != null) {

            rdNumber =
                    intent.getStringExtra(
                            "RD_NUMBER"
                    );
        }
    }


    // =========================================================
    // CLICK LISTENERS
    // =========================================================

    private void setupClickListeners() {


        // =====================================================
        // PAY INSTALLMENT
        // =====================================================

        btnPayInstallment.setOnClickListener(
                v -> {

                    // ---------------------------------------------
                    // CHECK RD
                    // ---------------------------------------------

                    if (currentRD == null) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "Unable to load RD details.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    // ---------------------------------------------
                    // GET RD NUMBER
                    // ---------------------------------------------

                    String currentRDNumber =
                            currentRD.getRdNumber();


                    if (TextUtils.isEmpty(
                            currentRDNumber
                    )) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "Invalid RD information.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    // =================================================
                    // TRANSACTION PIN
                    // =================================================
                    //
                    // IMPORTANT:
                    //
                    // DO NOT call the payment API here.
                    //
                    // First verify Transaction PIN.
                    //
                    // Only after successful verification:
                    //
                    // performPayInstallment(transactionPin)
                    //
                    // =================================================

                    PinVerificationDialog.showForTransactionPin(

                            RDDetailsActivity.this,

                            new PinVerificationDialog
                                    .OnPinVerifiedWithPinListener() {

                                @Override
                                public void onSuccess(
                                        String transactionPin
                                ) {

                                    // ---------------------------------
                                    // VALIDATE RETURNED PIN
                                    // ---------------------------------

                                    if (TextUtils.isEmpty(
                                            transactionPin
                                    )) {

                                        Toast.makeText(
                                                RDDetailsActivity.this,
                                                "Transaction PIN verification failed.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }


                                    // ---------------------------------
                                    // PIN VERIFIED
                                    // ---------------------------------

                                    performPayInstallment(
                                            transactionPin
                                    );
                                }


                                @Override
                                public void onFailure() {

                                    Toast.makeText(
                                            RDDetailsActivity.this,
                                            "Invalid Transaction PIN.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }
        );


        // =====================================================
        // RD HISTORY
        // =====================================================

        btnHistory.setOnClickListener(
                v -> {

                    if (currentRD == null) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "RD details not loaded.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    String currentRDNumber =
                            currentRD.getRdNumber();


                    if (TextUtils.isEmpty(
                            currentRDNumber
                    )) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "Invalid RD information.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    Intent intent =
                            new Intent(
                                    RDDetailsActivity.this,
                                    RDHistoryActivity.class
                            );


                    intent.putExtra(
                            "RD_NUMBER",
                            currentRDNumber
                    );


                    startActivity(
                            intent
                    );
                }
        );


        // =====================================================
        // PREMATURE CLOSE
        // =====================================================

        btnPrematureClose.setOnClickListener(
                v -> {

                    if (currentRD == null) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "RD details not loaded.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    String currentRDNumber =
                            currentRD.getRdNumber();


                    if (TextUtils.isEmpty(
                            currentRDNumber
                    )) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "Invalid RD information.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    Intent intent =
                            new Intent(
                                    RDDetailsActivity.this,
                                    PrematureCloseRDActivity.class
                            );


                    intent.putExtra(
                            "RD_NUMBER",
                            currentRDNumber
                    );


                    startActivity(
                            intent
                    );
                }
        );
    }


    // =========================================================
    // PAY RD INSTALLMENT
    // =========================================================
    //
    // THIS METHOD CAN ONLY BE REACHED AFTER
    // TRANSACTION PIN VERIFICATION.
    //
    // =========================================================

    private void performPayInstallment(
            String transactionPin
    ) {


        // =====================================================
        // VALIDATE RD
        // =====================================================

        if (currentRD == null
                || TextUtils.isEmpty(
                currentRD.getRdNumber()
        )) {

            Toast.makeText(
                    RDDetailsActivity.this,
                    "Invalid RD information.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // VALIDATE PIN
        // =====================================================

        if (TextUtils.isEmpty(
                transactionPin
        )) {

            Toast.makeText(
                    RDDetailsActivity.this,
                    "Transaction PIN is required.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // DISABLE BUTTON
        // =====================================================

        btnPayInstallment.setEnabled(
                false
        );


        // =====================================================
        // CREATE REQUEST
        // =====================================================

        PayRecurringDepositInstallmentRequest request =
                new PayRecurringDepositInstallmentRequest();


        // =====================================================
        // RD NUMBER
        // =====================================================

        request.setRdNumber(
                currentRD.getRdNumber()
        );


        // =====================================================
        // TRANSACTION PIN
        // =====================================================

        request.setTransactionPin(
                transactionPin
        );


        // =====================================================
        // API CALL
        // =====================================================

        repository
                .payRecurringDepositInstallment(
                        request
                )
                .enqueue(
                        new Callback<RDResponse>() {

                            @Override
                            public void onResponse(
                                    Call<RDResponse> call,
                                    Response<RDResponse> response
                            ) {

                                // ---------------------------------
                                // ENABLE BUTTON
                                // ---------------------------------

                                btnPayInstallment.setEnabled(
                                        true
                                );


                                // =================================
                                // SUCCESS
                                // =================================

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    RDResponse updatedRD =
                                            response.body();


                                    // ---------------------------------
                                    // UPDATE CURRENT RD
                                    // ---------------------------------

                                    currentRD =
                                            updatedRD;


                                    // ---------------------------------
                                    // SUCCESS MESSAGE
                                    // ---------------------------------

                                    String message =
                                            "RD installment paid successfully."
                                                    + "\n\n"
                                                    + "Paid Installments: "
                                                    + safeInteger(
                                                    updatedRD
                                                            .getPaidInstallments()
                                            )
                                                    + "\n"
                                                    + "Remaining Installments: "
                                                    + safeInteger(
                                                    updatedRD
                                                            .getRemainingInstallments()
                                            );


                                    Toast.makeText(
                                            RDDetailsActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();


                                    // ---------------------------------
                                    // REFRESH UI
                                    // ---------------------------------

                                    populateData(
                                            updatedRD
                                    );

                                    return;
                                }


                                // =================================
                                // API ERROR
                                // =================================

                                showPaymentError(
                                        response
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<RDResponse> call,
                                    Throwable t
                            ) {

                                btnPayInstallment.setEnabled(
                                        true
                                );


                                Toast.makeText(
                                        RDDetailsActivity.this,
                                        "Network Error\n\n"
                                                + (
                                                t != null
                                                        ? t.getMessage()
                                                        : "Unknown error"
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // PAYMENT ERROR
    // =========================================================

    private void showPaymentError(
            Response<RDResponse> response
    ) {

        String error =
                "Unable to pay RD installment.";


        try {

            if (response.errorBody() != null) {

                String serverError =
                        response.errorBody()
                                .string();


                if (!TextUtils.isEmpty(
                        serverError
                )) {

                    error =
                            serverError;
                }
            }

        } catch (Exception ignored) {

            // Keep default error.
        }


        Toast.makeText(
                RDDetailsActivity.this,

                "Payment Failed\n\n"
                        + "HTTP "
                        + response.code()
                        + "\n\n"
                        + error,

                Toast.LENGTH_LONG

        ).show();
    }


    // =========================================================
    // LOAD RD DETAILS
    // =========================================================

    private void loadDetails() {

        repository
                .getRecurringDepositDetails(
                        rdNumber
                )
                .enqueue(
                        new Callback<RDResponse>() {

                            @Override
                            public void onResponse(
                                    Call<RDResponse> call,
                                    Response<RDResponse> response
                            ) {

                                // =============================
                                // SUCCESS
                                // =============================

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    populateData(
                                            response.body()
                                    );

                                    return;
                                }


                                // =============================
                                // ERROR
                                // =============================

                                String error =
                                        "Unable to load RD details.";


                                try {

                                    if (response.errorBody()
                                            != null) {

                                        String serverError =
                                                response.errorBody()
                                                        .string();


                                        if (!TextUtils.isEmpty(
                                                serverError
                                        )) {

                                            error =
                                                    serverError;
                                        }
                                    }

                                } catch (Exception ignored) {

                                    // Keep default error.
                                }


                                Toast.makeText(
                                        RDDetailsActivity.this,

                                        "Unable to load RD Details\n\n"
                                                + "HTTP "
                                                + response.code()
                                                + "\n\n"
                                                + error,

                                        Toast.LENGTH_LONG

                                ).show();
                            }


                            @Override
                            public void onFailure(
                                    Call<RDResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        RDDetailsActivity.this,

                                        "Network Error\n\n"
                                                + (
                                                t != null
                                                        ? t.getMessage()
                                                        : "Unknown error"
                                        ),

                                        Toast.LENGTH_LONG

                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // POPULATE RD DATA
    // =========================================================

    private void populateData(
            RDResponse rd
    ) {

        if (rd == null) {
            return;
        }


        // =====================================================
        // STORE CURRENT RD
        // =====================================================

        currentRD =
                rd;


        // =====================================================
        // BASIC INFORMATION
        // =====================================================

        tvRDNumber.setText(
                safeText(
                        rd.getRdNumber()
                )
        );


        tvAccountNumber.setText(
                safeText(
                        rd.getAccountNumber()
                )
        );


        tvCustomerName.setText(
                safeText(
                        rd.getCustomerName()
                )
        );


        // =====================================================
        // MONTHLY INSTALLMENT
        // =====================================================

        tvMonthlyInstallment.setText(
                "₹ "
                        + safeText(
                        rd.getMonthlyInstallment()
                )
        );


        // =====================================================
        // INTEREST RATE
        // =====================================================

        tvInterestRate.setText(
                safeText(
                        rd.getInterestRate()
                )
                        + " %"
        );


        // =====================================================
        // TENURE
        // =====================================================

        tvTenure.setText(
                safeText(
                        rd.getTenureMonths()
                )
                        + " Months"
        );


        // =====================================================
        // TOTAL DEPOSIT
        // =====================================================

        tvTotalDeposit.setText(
                "₹ "
                        + safeText(
                        rd.getTotalDeposit()
                )
        );


        // =====================================================
        // MATURITY AMOUNT
        // =====================================================

        tvMaturityAmount.setText(
                "₹ "
                        + safeText(
                        rd.getMaturityAmount()
                )
        );


        // =====================================================
        // PAID INSTALLMENTS
        // =====================================================

        tvPaidInstallments.setText(
                safeText(
                        rd.getPaidInstallments()
                )
        );


        // =====================================================
        // REMAINING INSTALLMENTS
        // =====================================================

        tvRemainingInstallments.setText(
                safeText(
                        rd.getRemainingInstallments()
                )
        );


        // =====================================================
        // NEXT INSTALLMENT
        // =====================================================

        Integer remaining =
                rd.getRemainingInstallments();


        if (remaining != null
                && remaining <= 0) {

            tvNextInstallment.setText(
                    "Completed"
            );

        } else if (
                TextUtils.isEmpty(
                        rd.getNextInstallmentDate()
                )
        ) {

            tvNextInstallment.setText(
                    "Not Available"
            );

        } else {

            tvNextInstallment.setText(
                    rd.getNextInstallmentDate()
            );
        }


        // =====================================================
        // STATUS
        // =====================================================

        tvStatus.setText(
                safeText(
                        rd.getStatus()
                )
        );


        // =====================================================
        // BUTTON STATE
        // =====================================================

        updateButtonState(
                rd
        );
    }


    // =========================================================
    // UPDATE BUTTON STATE
    // =========================================================

    private void updateButtonState(
            RDResponse rd
    ) {

        if (rd == null) {
            return;
        }


        Integer remaining =
                rd.getRemainingInstallments();


        String status =
                String.valueOf(
                        rd.getStatus()
                );


        // =====================================================
        // CHECK COMPLETED
        // =====================================================

        boolean completed =
                (remaining != null
                        && remaining <= 0)

                        || "MATURED"
                        .equalsIgnoreCase(
                                status
                        )

                        || "PREMATURE_CLOSED"
                        .equalsIgnoreCase(
                                status
                        )

                        || "CLOSED"
                        .equalsIgnoreCase(
                                status
                        );


        // =====================================================
        // PAY INSTALLMENT
        // =====================================================

        if (completed) {

            btnPayInstallment.setEnabled(
                    false
            );

            btnPayInstallment.setText(
                    "No Installment Due"
            );

        } else {

            btnPayInstallment.setEnabled(
                    true
            );

            btnPayInstallment.setText(
                    "Pay Installment"
            );
        }


        // =====================================================
        // PREMATURE CLOSE
        // =====================================================

        if (completed) {

            btnPrematureClose.setEnabled(
                    false
            );

            btnPrematureClose.setText(
                    "RD Closed"
            );

        } else {

            btnPrematureClose.setEnabled(
                    true
            );

            btnPrematureClose.setText(
                    "Premature Close"
            );
        }
    }


    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            Object value
    ) {

        if (value == null) {

            return "-";
        }


        return String.valueOf(
                value
        );
    }


    // =========================================================
    // SAFE INTEGER
    // =========================================================

    private String safeInteger(
            Integer value
    ) {

        if (value == null) {

            return "0";
        }


        return String.valueOf(
                value
        );
    }
}