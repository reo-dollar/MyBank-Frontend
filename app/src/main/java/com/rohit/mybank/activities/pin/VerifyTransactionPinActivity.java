package com.rohit.mybank.activities.pin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rohit.mybank.R;
import com.rohit.mybank.model.pin.VerifyPinRequest;
import com.rohit.mybank.model.pin.VerifyPinResponse;
import com.rohit.mybank.repository.PinRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyTransactionPinActivity
        extends AppCompatActivity {

    // =========================================================
    // RESULT CONSTANTS
    // =========================================================

    public static final String EXTRA_PIN_VERIFIED =
            "pin_verified";

    public static final String EXTRA_PAYMENT_TYPE =
            "paymentType";

    public static final String EXTRA_AMOUNT =
            "amount";

    // =========================================================
    // VIEW REFERENCES
    // =========================================================

    private TextInputLayout layoutPin;

    private TextInputEditText etPin;

    private MaterialButton btnVerifyPin;

    private ProgressBar progressBar;

    // =========================================================
    // REPOSITORY
    // =========================================================

    private PinRepository pinRepository;

    // =========================================================
    // PAYMENT CONTEXT
    // =========================================================

    private String paymentType;

    private double amount;

    // =========================================================
    // ACTIVITY CREATED
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_verify_transaction_pin
        );

        // -----------------------------------------------------
        // INITIALIZE VIEWS
        // -----------------------------------------------------

        initializeViews();

        // -----------------------------------------------------
        // INITIALIZE REPOSITORY
        // -----------------------------------------------------

        pinRepository =
                new PinRepository(this);

        // -----------------------------------------------------
        // GET PAYMENT CONTEXT
        // -----------------------------------------------------

        paymentType =
                getIntent().getStringExtra(
                        EXTRA_PAYMENT_TYPE
                );

        amount =
                getIntent().getDoubleExtra(
                        EXTRA_AMOUNT,
                        0.0
                );

        // -----------------------------------------------------
        // VERIFY BUTTON
        // -----------------------------------------------------

        btnVerifyPin.setOnClickListener(
                v -> verifyPin()
        );

        // -----------------------------------------------------
        // SYSTEM BACK
        // -----------------------------------------------------

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        cancelAuthentication();

                    }

                }
        );
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        layoutPin =
                findViewById(
                        R.id.layoutPin
                );

        etPin =
                findViewById(
                        R.id.etPin
                );

        btnVerifyPin =
                findViewById(
                        R.id.btnVerifyPin
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );
    }

    // =========================================================
    // VERIFY TRANSACTION PIN
    // =========================================================

    private void verifyPin() {

        // -----------------------------------------------------
        // CLEAR PREVIOUS ERROR
        // -----------------------------------------------------

        layoutPin.setError(null);

        // -----------------------------------------------------
        // READ PIN
        // -----------------------------------------------------

        String pin = "";

        if (etPin.getText() != null) {

            pin =
                    etPin.getText()
                            .toString()
                            .trim();
        }

        // -----------------------------------------------------
        // EMPTY PIN
        // -----------------------------------------------------

        if (TextUtils.isEmpty(pin)) {

            layoutPin.setError(
                    "Enter Transaction PIN"
            );

            etPin.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // PIN LENGTH
        // -----------------------------------------------------
        //
        // Your existing banking system uses a 6-digit
        // Transaction PIN.
        //

        if (!pin.matches("\\d{6}")) {

            layoutPin.setError(
                    "PIN must be exactly 6 digits"
            );

            etPin.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // CREATE REQUEST
        // -----------------------------------------------------

        VerifyPinRequest request =
                new VerifyPinRequest(pin);

        // -----------------------------------------------------
        // SHOW LOADING
        // -----------------------------------------------------

        progressBar.setVisibility(
                View.VISIBLE
        );

        btnVerifyPin.setEnabled(false);

        // -----------------------------------------------------
        // CALL BACKEND
        // -----------------------------------------------------

        pinRepository
                .verifyTransactionPin(request)
                .enqueue(
                        new Callback<VerifyPinResponse>() {

                            @Override
                            public void onResponse(
                                    Call<VerifyPinResponse> call,
                                    Response<VerifyPinResponse> response) {

                                // -----------------------------
                                // HIDE LOADING
                                // -----------------------------

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                btnVerifyPin.setEnabled(
                                        true
                                );

                                // -----------------------------
                                // HTTP ERROR
                                // -----------------------------

                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            VerifyTransactionPinActivity.this,
                                            "Server Error: "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // -----------------------------
                                // EMPTY RESPONSE
                                // -----------------------------

                                if (response.body() == null) {

                                    Toast.makeText(
                                            VerifyTransactionPinActivity.this,
                                            "Empty server response.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // -----------------------------
                                // RESPONSE
                                // -----------------------------

                                VerifyPinResponse verifyResponse =
                                        response.body();

                                // -----------------------------
                                // PIN VERIFIED
                                // -----------------------------

                                if (verifyResponse.isSuccess()) {

                                    Toast.makeText(
                                            VerifyTransactionPinActivity.this,
                                            "Transaction PIN Verified",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // -------------------------
                                    // PREPARE RESULT
                                    // -------------------------

                                    Intent resultIntent =
                                            new Intent();

                                    resultIntent.putExtra(
                                            EXTRA_PIN_VERIFIED,
                                            true
                                    );

                                    // -------------------------
                                    // RETURN PAYMENT TYPE
                                    // -------------------------

                                    if (paymentType != null) {

                                        resultIntent.putExtra(
                                                EXTRA_PAYMENT_TYPE,
                                                paymentType
                                        );
                                    }

                                    // -------------------------
                                    // RETURN AMOUNT
                                    // -------------------------

                                    resultIntent.putExtra(
                                            EXTRA_AMOUNT,
                                            amount
                                    );

                                    // -------------------------
                                    // RETURN SUCCESS
                                    // -------------------------

                                    setResult(
                                            RESULT_OK,
                                            resultIntent
                                    );

                                    finish();

                                    return;
                                }

                                // -------------------------------------------------
                                // INVALID PIN
                                // -------------------------------------------------

                                String message =
                                        verifyResponse.getMessage();

                                if (TextUtils.isEmpty(message)) {

                                    message =
                                            "Invalid Transaction PIN";
                                }

                                layoutPin.setError(
                                        message
                                );

                                etPin.requestFocus();
                            }

                            // =====================================================
                            // NETWORK FAILURE
                            // =====================================================

                            @Override
                            public void onFailure(
                                    Call<VerifyPinResponse> call,
                                    Throwable t) {

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                btnVerifyPin.setEnabled(
                                        true
                                );

                                String errorMessage =
                                        t.getMessage();

                                if (TextUtils.isEmpty(
                                        errorMessage
                                )) {

                                    errorMessage =
                                            "Unable to connect to the server.";
                                }

                                Toast.makeText(
                                        VerifyTransactionPinActivity.this,
                                        "Network Error\n\n"
                                                + errorMessage,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // CANCEL AUTHENTICATION
    // =========================================================

    private void cancelAuthentication() {

        setResult(
                RESULT_CANCELED
        );

        finish();
    }

    // =========================================================
    // TOOLBAR BACK
    // =========================================================

    @Override
    public boolean onSupportNavigateUp() {

        cancelAuthentication();

        return true;
    }

    // =========================================================
    // ACTIVITY DESTROYED
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.GONE
            );
        }

        if (btnVerifyPin != null) {

            btnVerifyPin.setEnabled(
                    true
            );
        }
    }
}