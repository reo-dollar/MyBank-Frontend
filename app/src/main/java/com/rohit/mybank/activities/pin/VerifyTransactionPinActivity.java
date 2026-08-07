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

    public static final String EXTRA_PIN_VERIFIED =
            "pin_verified";

    private TextInputLayout layoutPin;

    private TextInputEditText etPin;

    private MaterialButton btnVerifyPin;

    private ProgressBar progressBar;

    private PinRepository pinRepository;

    private String paymentType;

    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_verify_transaction_pin
        );

        initializeViews();

        pinRepository =
                new PinRepository(this);

        paymentType =
                getIntent().getStringExtra(
                        "paymentType"
                );

        amount =
                getIntent().getDoubleExtra(
                        "amount",
                        0
                );

        btnVerifyPin.setOnClickListener(
                v -> verifyPin()
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        setResult(RESULT_CANCELED);

                        finish();

                    }

                }

        );

    }

    private void initializeViews() {

        layoutPin =
                findViewById(R.id.layoutPin);

        etPin =
                findViewById(R.id.etPin);

        btnVerifyPin =
                findViewById(R.id.btnVerifyPin);

        progressBar =
                findViewById(R.id.progressBar);

    }
    private void verifyPin() {

        layoutPin.setError(null);

        String pin = "";

        if (etPin.getText() != null) {

            pin = etPin.getText()
                    .toString()
                    .trim();

        }

        // Validate PIN
        if (TextUtils.isEmpty(pin)) {

            layoutPin.setError("Enter Transaction PIN");

            etPin.requestFocus();

            return;

        }

        if (!pin.matches("\\d{6}")) {

            layoutPin.setError("PIN must be exactly 6 digits");

            etPin.requestFocus();

            return;

        }

        VerifyPinRequest request =
                new VerifyPinRequest(pin);

        progressBar.setVisibility(View.VISIBLE);

        btnVerifyPin.setEnabled(false);

        pinRepository.verifyTransactionPin(request)

                .enqueue(new Callback<VerifyPinResponse>() {

                    @Override
                    public void onResponse(
                            Call<VerifyPinResponse> call,
                            Response<VerifyPinResponse> response) {

                        progressBar.setVisibility(View.GONE);

                        btnVerifyPin.setEnabled(true);

                        // HTTP Error
                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    VerifyTransactionPinActivity.this,
                                    "Server Error : " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        // Empty Body
                        if (response.body() == null) {

                            Toast.makeText(
                                    VerifyTransactionPinActivity.this,
                                    "Empty server response.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        VerifyPinResponse verifyResponse =
                                response.body();

                        // PIN Correct
                        if (verifyResponse.isSuccess()) {

                            Toast.makeText(
                                    VerifyTransactionPinActivity.this,
                                    "Transaction PIN Verified",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent result =
                                    new Intent();

                            result.putExtra(
                                    EXTRA_PIN_VERIFIED,
                                    true
                            );

                            result.putExtra(
                                    "paymentType",
                                    paymentType
                            );

                            result.putExtra(
                                    "amount",
                                    amount
                            );

                            setResult(
                                    RESULT_OK,
                                    result
                            );

                            finish();

                            return;

                        }

                        // Invalid PIN
                        String message =
                                verifyResponse.getMessage();

                        if (TextUtils.isEmpty(message)) {

                            message =
                                    "Invalid Transaction PIN";

                        }

                        layoutPin.setError(message);

                        etPin.requestFocus();

                    }

                    @Override
                    public void onFailure(
                            Call<VerifyPinResponse> call,
                            Throwable t) {

                        progressBar.setVisibility(View.GONE);

                        btnVerifyPin.setEnabled(true);

                        Toast.makeText(
                                VerifyTransactionPinActivity.this,
                                "Network Error\n\n"
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }
    // =====================================================
// Cancel Authentication
// =====================================================

    private void cancelAuthentication() {

        setResult(RESULT_CANCELED);

        finish();

    }

// =====================================================
// Toolbar Back Button
// =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        cancelAuthentication();

        return true;

    }

// =====================================================
// Activity Destroy
// =====================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (progressBar != null) {

            progressBar.setVisibility(View.GONE);

        }

        if (btnVerifyPin != null) {

            btnVerifyPin.setEnabled(true);

        }

    }

}