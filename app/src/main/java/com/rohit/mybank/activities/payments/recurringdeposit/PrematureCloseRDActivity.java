package com.rohit.mybank.activities.payments.recurringdeposit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.recurringdeposit.RDResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrematureCloseRDActivity extends AppCompatActivity {

    // ============================================================
    // Views
    // ============================================================

    private TextView tvRDNumber;
    private MaterialButton btnCloseRD;


    // ============================================================
    // Repository
    // ============================================================

    private RecurringDepositRepository repository;


    // ============================================================
    // Data
    // ============================================================

    private String rdNumber;


    // ============================================================
    // Lifecycle
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_premature_close_rd
        );

        initializeRepository();

        initializeViews();

        loadRDNumber();

        setupListeners();

        validateRDNumber();
    }


    // ============================================================
    // Initialize Repository
    // ============================================================

    private void initializeRepository() {

        repository =
                new RecurringDepositRepository(this);
    }


    // ============================================================
    // Initialize Views
    // ============================================================

    private void initializeViews() {

        tvRDNumber =
                findViewById(R.id.tvRDNumber);

        btnCloseRD =
                findViewById(R.id.btnCloseRD);
    }


    // ============================================================
    // Load RD Number From Intent
    // ============================================================

    private void loadRDNumber() {

        if (getIntent() == null) {
            return;
        }

        rdNumber =
                getIntent().getStringExtra(
                        "RD_NUMBER"
                );

        if (!TextUtils.isEmpty(rdNumber)) {

            rdNumber =
                    rdNumber.trim();

            tvRDNumber.setText(
                    rdNumber
            );
        }
    }


    // ============================================================
    // Validate RD Number
    // ============================================================

    private void validateRDNumber() {

        if (TextUtils.isEmpty(rdNumber)) {

            btnCloseRD.setEnabled(false);

            Toast.makeText(
                    this,
                    "RD Number not received.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
    }


    // ============================================================
    // Setup Listeners
    // ============================================================

    private void setupListeners() {

        btnCloseRD.setOnClickListener(
                v -> showConfirmationDialog()
        );
    }


    // ============================================================
    // Confirmation Dialog
    // ============================================================

    private void showConfirmationDialog() {

        if (TextUtils.isEmpty(rdNumber)) {

            Toast.makeText(
                    this,
                    "Invalid RD Number.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        new AlertDialog.Builder(this)

                .setTitle(
                        "Premature Close RD"
                )

                .setMessage(
                        "Are you sure you want to close RD "
                                + rdNumber
                                + " before maturity?"
                                + "\n\n"
                                + "The RD will be permanently "
                                + "closed prematurely."
                )

                // =================================================
                // IMPORTANT
                // =================================================
                // DO NOT directly call closeRecurringDeposit().
                //
                // First verify Transaction PIN.
                // =================================================

                .setPositiveButton(
                        "Yes, Continue",
                        (dialog, which) ->
                                showTransactionPinDialog()
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setCancelable(true)

                .show();
    }


    // ============================================================
    // TRANSACTION PIN VERIFICATION
    // ============================================================

    private void showTransactionPinDialog() {

        if (TextUtils.isEmpty(rdNumber)) {

            Toast.makeText(
                    this,
                    "Invalid RD Number.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        PinVerificationDialog.show(
                PrematureCloseRDActivity.this,

                new PinVerificationDialog.OnPinVerifiedListener() {

                    @Override
                    public void onSuccess() {

                        // =================================================
                        // PIN VERIFIED
                        // =================================================
                        //
                        // ONLY NOW perform the premature-close API call.
                        // =================================================

                        closeRecurringDeposit();
                    }


                    @Override
                    public void onFailure() {

                        // =================================================
                        // PIN VERIFICATION FAILED
                        // =================================================

                        Toast.makeText(
                                PrematureCloseRDActivity.this,
                                "Transaction PIN verification failed.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // ============================================================
    // Premature Close RD
    // ============================================================

    private void closeRecurringDeposit() {

        if (TextUtils.isEmpty(rdNumber)) {

            Toast.makeText(
                    this,
                    "Invalid RD Number.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // ============================================================
        // Prevent duplicate requests
        // ============================================================

        btnCloseRD.setEnabled(false);


        // ============================================================
        // API CALL
        // ============================================================

        repository
                .prematureCloseRecurringDeposit(
                        rdNumber
                )
                .enqueue(
                        new Callback<RDResponse>() {

                            @Override
                            public void onResponse(
                                    Call<RDResponse> call,
                                    Response<RDResponse> response
                            ) {

                                btnCloseRD.setEnabled(true);


                                // =================================================
                                // SUCCESS
                                // =================================================

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    RDResponse result =
                                            response.body();

                                    showSuccessDialog(
                                            result
                                    );

                                }

                                // =================================================
                                // SERVER ERROR
                                // =================================================

                                else {

                                    showServerError(
                                            response
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<RDResponse> call,
                                    Throwable t
                            ) {

                                btnCloseRD.setEnabled(true);

                                showNetworkError(t);
                            }
                        }
                );
    }


    // ============================================================
    // Success Dialog
    // ============================================================

    private void showSuccessDialog(
            RDResponse result) {

        StringBuilder message =
                new StringBuilder();


        message.append(
                "Recurring Deposit closed successfully."
        );


        message.append("\n\n");


        message.append(
                "RD Number : "
        );


        message.append(
                safeText(
                        result.getRdNumber()
                )
        );


        message.append("\n");


        message.append(
                "Account Number : "
        );


        message.append(
                safeText(
                        result.getAccountNumber()
                )
        );


        message.append("\n\n");


        message.append(
                "Total Deposit : ₹"
        );


        message.append(
                safeText(
                        result.getTotalDeposit()
                )
        );


        message.append("\n");


        message.append(
                "Maturity Amount : ₹"
        );


        message.append(
                safeText(
                        result.getMaturityAmount()
                )
        );


        message.append("\n\n");


        message.append(
                "Status : "
        );


        message.append(
                safeText(
                        result.getStatus()
                )
        );


        new AlertDialog.Builder(
                PrematureCloseRDActivity.this
        )

                .setTitle(
                        "RD Closed Successfully"
                )

                .setMessage(
                        message.toString()
                )

                .setPositiveButton(
                        "OK",
                        (dialog, which) -> {

                            setResult(
                                    RESULT_OK
                            );

                            finish();
                        }
                )

                .setCancelable(false)

                .show();
    }


    // ============================================================
    // Server Error
    // ============================================================

    private void showServerError(
            Response<RDResponse> response) {

        String errorMessage =
                "Unable to close RD.";

        int statusCode =
                response.code();


        try {

            if (response.errorBody() != null) {

                String serverError =
                        response.errorBody().string();

                if (!TextUtils.isEmpty(
                        serverError
                )) {

                    errorMessage =
                            serverError.trim();
                }
            }

        } catch (Exception e) {

            if (!TextUtils.isEmpty(
                    e.getMessage()
            )) {

                errorMessage =
                        e.getMessage();
            }
        }


        String finalMessage =
                "HTTP "
                        + statusCode
                        + "\n\n"
                        + errorMessage;


        Toast.makeText(
                PrematureCloseRDActivity.this,
                finalMessage,
                Toast.LENGTH_LONG
        ).show();
    }


    // ============================================================
    // Network Error
    // ============================================================

    private void showNetworkError(
            Throwable throwable) {

        String errorMessage =
                "Network error occurred.";


        if (throwable != null
                && !TextUtils.isEmpty(
                throwable.getMessage()
        )) {

            errorMessage =
                    throwable.getMessage();
        }


        Toast.makeText(
                PrematureCloseRDActivity.this,
                "Network Error\n\n"
                        + errorMessage,
                Toast.LENGTH_LONG
        ).show();
    }


    // ============================================================
    // Safe Text
    // ============================================================

    private String safeText(
            Object value) {

        if (value == null) {

            return "-";
        }


        return String.valueOf(value);
    }
}