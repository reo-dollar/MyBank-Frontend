package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.RDResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * PAY RD INSTALLMENT ACTIVITY
 * =========================================================
 *
 * Flow:
 *
 * Pay Installment
 *       ↓
 * Validate RD Number
 *       ↓
 * Transaction PIN Dialog
 *       ↓
 * Verify PIN
 *       ↓
 * Send RD Number + Transaction PIN
 *       ↓
 * Backend
 *       ↓
 * Payment successful
 *
 * IMPORTANT:
 *
 * The payment API is NEVER called before PIN verification.
 *
 * =========================================================
 */
public class PayRDInstallmentActivity
        extends AppCompatActivity {


    // =========================================================
    // VIEWS
    // =========================================================

    private TextInputEditText etRDNumber;

    private MaterialButton btnPayInstallment;


    // =========================================================
    // REPOSITORY
    // =========================================================

    private RecurringDepositRepository repository;


    // =========================================================
    // STATE
    // =========================================================

    private boolean paymentInProgress = false;


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


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
                R.layout.activity_pay_rd_installment
        );


        // =====================================================
        // REPOSITORY
        // =====================================================

        repository =
                new RecurringDepositRepository(
                        this
                );


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();


        // =====================================================
        // LOAD INTENT DATA
        // =====================================================

        loadIntentData();


        // =====================================================
        // CLICK LISTENERS
        // =====================================================

        setupListeners();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        etRDNumber =
                findViewById(
                        R.id.etRDNumber
                );


        btnPayInstallment =
                findViewById(
                        R.id.btnPayInstallment
                );
    }


    // =========================================================
    // LOAD INTENT DATA
    // =========================================================

    private void loadIntentData() {

        if (getIntent() == null) {
            return;
        }


        String rdNumber =
                getIntent().getStringExtra(
                        "RD_NUMBER"
                );


        if (!TextUtils.isEmpty(
                rdNumber
        )) {

            etRDNumber.setText(
                    rdNumber
            );


            /*
             * RD number came from the RD screen.
             *
             * User should not modify it.
             */
            etRDNumber.setEnabled(
                    false
            );
        }
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    private void setupListeners() {

        btnPayInstallment.setOnClickListener(
                v -> validateAndStartPayment()
        );
    }


    // =========================================================
    // VALIDATE AND START PAYMENT
    // =========================================================
    //
    // IMPORTANT:
    //
    // DO NOT call the payment API from this method.
    //
    // This method only:
    //
    // 1. Validates RD number
    // 2. Opens Transaction PIN dialog
    //
    // =========================================================

    private void validateAndStartPayment() {


        // =====================================================
        // PREVENT DOUBLE CLICK
        // =====================================================

        if (paymentInProgress) {

            return;
        }


        // =====================================================
        // READ RD NUMBER
        // =====================================================

        String rdNumber = "";


        if (etRDNumber.getText() != null) {

            rdNumber =
                    etRDNumber.getText()
                            .toString()
                            .trim();
        }


        // =====================================================
        // RD NUMBER REQUIRED
        // =====================================================

        if (TextUtils.isEmpty(
                rdNumber
        )) {

            etRDNumber.setError(
                    "RD Number is required"
            );


            etRDNumber.requestFocus();


            return;
        }


        // =====================================================
        // START PIN VERIFICATION
        // =====================================================

        showTransactionPinDialog(
                rdNumber
        );
    }


    // =========================================================
    // SHOW TRANSACTION PIN DIALOG
    // =========================================================
    //
    // THIS IS THE CRITICAL FIX.
    //
    // The payment API is NOT called until:
    //
    // onSuccess(transactionPin)
    //
    // =========================================================

    private void showTransactionPinDialog(
            final String rdNumber
    ) {


        PinVerificationDialog.showForTransactionPin(

                PayRDInstallmentActivity.this,

                new PinVerificationDialog
                        .OnPinVerifiedWithPinListener() {


                    // =========================================
                    // PIN SUCCESS
                    // =========================================

                    @Override
                    public void onSuccess(
                            String transactionPin
                    ) {


                        // -------------------------------------
                        // SAFETY CHECK
                        // -------------------------------------

                        if (TextUtils.isEmpty(
                                transactionPin
                        )) {

                            Toast.makeText(
                                    PayRDInstallmentActivity.this,
                                    "Transaction PIN is required.",
                                    Toast.LENGTH_LONG
                            ).show();


                            return;
                        }


                        // -------------------------------------
                        // PIN VERIFIED
                        // -------------------------------------
                        //
                        // NOW and ONLY NOW call backend.
                        //
                        // -------------------------------------

                        performPayment(
                                rdNumber,
                                transactionPin
                        );
                    }


                    // =========================================
                    // PIN FAILURE
                    // =========================================

                    @Override
                    public void onFailure() {

                        /*
                         * PinVerificationDialog already handles
                         * the verification failure message.
                         *
                         * No payment API call is made.
                         */

                        Toast.makeText(
                                PayRDInstallmentActivity.this,
                                "Transaction PIN verification failed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // PERFORM PAYMENT
    // =========================================================
    //
    // This method can ONLY be reached after PIN verification.
    //
    // =========================================================

    private void performPayment(
            String rdNumber,
            String transactionPin
    ) {


        // =====================================================
        // VALIDATE RD NUMBER
        // =====================================================

        if (TextUtils.isEmpty(
                rdNumber
        )) {

            Toast.makeText(
                    PayRDInstallmentActivity.this,
                    "RD Number is required.",
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
                    PayRDInstallmentActivity.this,
                    "Transaction PIN is required.",
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // =====================================================
        // PREVENT DUPLICATE PAYMENT
        // =====================================================

        if (paymentInProgress) {

            return;
        }


        paymentInProgress =
                true;


        // =====================================================
        // DISABLE BUTTON
        // =====================================================

        btnPayInstallment.setEnabled(
                false
        );


        btnPayInstallment.setText(
                "Verifying Payment..."
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
                rdNumber
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
        //
        // At this point the request contains:
        //
        // {
        //     "rdNumber": "...",
        //     "transactionPin": "******"
        // }
        //
        // =====================================================

        repository
                .payRecurringDepositInstallment(
                        request
                )
                .enqueue(
                        new Callback<RDResponse>() {


                            // =================================
                            // RESPONSE
                            // =================================

                            @Override
                            public void onResponse(
                                    Call<RDResponse> call,
                                    Response<RDResponse> response
                            ) {


                                // -----------------------------
                                // RESTORE STATE
                                // -----------------------------

                                paymentInProgress =
                                        false;


                                btnPayInstallment
                                        .setEnabled(
                                                true
                                        );


                                btnPayInstallment
                                        .setText(
                                                "Pay Installment"
                                        );


                                // =================================
                                // SUCCESS
                                // =================================

                                if (
                                        response.isSuccessful()
                                                &&
                                                response.body() != null
                                ) {


                                    RDResponse rd =
                                            response.body();


                                    // -----------------------------
                                    // SUCCESS MESSAGE
                                    // -----------------------------

                                    String message =
                                            "RD installment paid successfully."
                                                    + "\n\n"
                                                    + "Paid Installments: "
                                                    + safeValue(
                                                    rd.getPaidInstallments()
                                            )
                                                    + "\n"
                                                    + "Remaining Installments: "
                                                    + safeValue(
                                                    rd.getRemainingInstallments()
                                            );


                                    Toast.makeText(
                                            PayRDInstallmentActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();


                                    // -----------------------------
                                    // RETURN SUCCESS
                                    // -----------------------------

                                    setResult(
                                            RESULT_OK
                                    );


                                    finish();


                                    return;
                                }


                                // =================================
                                // SERVER ERROR
                                // =================================

                                showServerError(
                                        response
                                );
                            }


                            // =================================
                            // NETWORK FAILURE
                            // =================================

                            @Override
                            public void onFailure(
                                    Call<RDResponse> call,
                                    Throwable t
                            ) {


                                paymentInProgress =
                                        false;


                                btnPayInstallment
                                        .setEnabled(
                                                true
                                        );


                                btnPayInstallment
                                        .setText(
                                                "Pay Installment"
                                        );


                                String message;


                                if (t != null
                                        && !TextUtils.isEmpty(
                                        t.getMessage()
                                )) {

                                    message =
                                            t.getMessage();

                                } else {

                                    message =
                                            "Unknown network error.";
                                }


                                Toast.makeText(
                                        PayRDInstallmentActivity.this,
                                        "Network Error\n\n"
                                                + message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // SERVER ERROR
    // =========================================================

    private void showServerError(
            Response<RDResponse> response
    ) {


        String errorMessage =
                "Unable to pay RD installment.";


        try {

            if (response.errorBody()
                    != null) {

                String serverError =
                        response.errorBody()
                                .string();


                if (!TextUtils.isEmpty(
                        serverError
                )) {

                    errorMessage =
                            serverError;
                }
            }

        } catch (Exception e) {

            errorMessage =
                    "Unable to read server error.";
        }


        // =====================================================
        // STATUS-SPECIFIC MESSAGE
        // =====================================================

        String statusMessage;


        switch (
                response.code()
        ) {

            case 400:

                statusMessage =
                        "Invalid payment request.";

                break;


            case 401:

                statusMessage =
                        "Session expired. Please login again.";

                break;


            case 403:

                statusMessage =
                        "You are not authorized to make this payment.";

                break;


            case 404:

                statusMessage =
                        "RD account was not found.";

                break;


            case 409:

                statusMessage =
                        "This installment cannot be paid right now.";

                break;


            case 500:

                statusMessage =
                        "Server error while processing the RD payment.";

                break;


            default:

                statusMessage =
                        "Payment request failed.";
        }


        Toast.makeText(
                PayRDInstallmentActivity.this,

                "HTTP "
                        + response.code()
                        + "\n\n"
                        + statusMessage
                        + "\n\n"
                        + errorMessage,

                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // SAFE VALUE
    // =========================================================

    private String safeValue(
            Object value
    ) {

        if (value == null) {

            return "0";
        }


        return String.valueOf(
                value
        );
    }
}