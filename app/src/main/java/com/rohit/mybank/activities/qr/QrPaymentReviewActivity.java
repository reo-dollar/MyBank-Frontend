package com.rohit.mybank.activities.qr;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.qr.QrPaymentRequest;
import com.rohit.mybank.model.qr.QrPaymentResponse;
import com.rohit.mybank.activities.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * QR PAYMENT REVIEW ACTIVITY
 * =========================================================
 *
 * Final review screen for MyBank QR payment.
 *
 * Flow:
 *
 * Scan QR
 *      ↓
 * Resolve recipient
 *      ↓
 * Enter amount
 *      ↓
 * Review payment
 *      ↓
 * Transaction PIN
 *      ↓
 * POST /api/qr/pay
 *      ↓
 * Payment success screen
 *
 * IMPORTANT:
 *
 * The authenticated JWT determines the sender.
 *
 * This activity sends only:
 *
 * - paymentId
 * - amount
 * - transactionPin
 *
 * The backend resolves the recipient from paymentId.
 *
 * =========================================================
 */
public class QrPaymentReviewActivity
        extends AppCompatActivity {


    // =========================================================
    // INTENT EXTRAS
    // =========================================================

    public static final String EXTRA_PAYMENT_ID =
            "extra_payment_id";

    public static final String EXTRA_CUSTOMER_NAME =
            "extra_customer_name";

    public static final String EXTRA_MASKED_ACCOUNT =
            "extra_masked_account";

    public static final String EXTRA_QR_PAYLOAD =
            "extra_qr_payload";

    public static final String EXTRA_AMOUNT =
            "extra_amount";


    // =========================================================
    // UI
    // =========================================================

    private ImageButton btnBack;

    private TextView tvRecipientInitials;

    private TextView tvRecipientName;

    private TextView tvMaskedAccount;

    private TextView tvAmount;

    private TextView tvPaymentId;

    private Button btnContinue;


    // =========================================================
    // DATA
    // =========================================================

    private String paymentId;

    private String customerName;

    private String maskedAccountNumber;

    private String qrPayload;

    private String amount;


    // =========================================================
    // API
    // =========================================================

    private ApiService apiService;


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

        super.onCreate(savedInstanceState);


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
                R.layout.activity_qr_payment_review
        );


        // =====================================================
        // SYSTEM BARS
        // =====================================================

        Window window =
                getWindow();


        window.setStatusBarColor(
                Color.rgb(
                        21,
                        101,
                        192
                )
        );


        window.setNavigationBarColor(
                Color.BLACK
        );


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();


        // =====================================================
        // API
        // =====================================================

        apiService =
                RetrofitClient
                        .getClient(this)
                        .create(ApiService.class);


        // =====================================================
        // READ INTENT DATA
        // =====================================================

        readIntentData();


        // =====================================================
        // VALIDATE
        // =====================================================

        if (!validatePaymentData()) {
            return;
        }


        // =====================================================
        // DISPLAY
        // =====================================================

        displayPayment();


        // =====================================================
        // LISTENERS
        // =====================================================

        setupListeners();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        btnBack =
                findViewById(
                        R.id.btnBack
                );


        tvRecipientInitials =
                findViewById(
                        R.id.tvRecipientInitials
                );


        tvRecipientName =
                findViewById(
                        R.id.tvRecipientName
                );


        tvMaskedAccount =
                findViewById(
                        R.id.tvMaskedAccount
                );


        tvAmount =
                findViewById(
                        R.id.tvAmount
                );


        tvPaymentId =
                findViewById(
                        R.id.tvPaymentId
                );


        btnContinue =
                findViewById(
                        R.id.btnContinue
                );
    }


    // =========================================================
    // READ INTENT DATA
    // =========================================================

    private void readIntentData() {

        paymentId =
                getIntent().getStringExtra(
                        EXTRA_PAYMENT_ID
                );


        customerName =
                getIntent().getStringExtra(
                        EXTRA_CUSTOMER_NAME
                );


        maskedAccountNumber =
                getIntent().getStringExtra(
                        EXTRA_MASKED_ACCOUNT
                );


        qrPayload =
                getIntent().getStringExtra(
                        EXTRA_QR_PAYLOAD
                );


        amount =
                getIntent().getStringExtra(
                        EXTRA_AMOUNT
                );
    }


    // =========================================================
    // VALIDATE PAYMENT DATA
    // =========================================================

    private boolean validatePaymentData() {


        // -----------------------------------------------------
        // PAYMENT ID
        // -----------------------------------------------------

        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            showErrorAndFinish(
                    "Payment ID is missing."
            );

            return false;
        }


        paymentId =
                paymentId.trim();


        // -----------------------------------------------------
        // PAYMENT ID FORMAT
        // -----------------------------------------------------

        if (
                !paymentId.matches(
                        "^PAY_[A-Fa-f0-9]{36}$"
                )
        ) {

            showErrorAndFinish(
                    "Invalid QR payment ID."
            );

            return false;
        }


        // -----------------------------------------------------
        // CUSTOMER NAME
        // -----------------------------------------------------

        if (
                customerName == null
                        || customerName.trim().isEmpty()
        ) {

            customerName =
                    "MyBank User";

        } else {

            customerName =
                    customerName.trim();
        }


        // -----------------------------------------------------
        // AMOUNT
        // -----------------------------------------------------

        if (
                amount == null
                        || amount.trim().isEmpty()
        ) {

            showErrorAndFinish(
                    "Payment amount is missing."
            );

            return false;
        }


        amount =
                amount.trim();


        BigDecimal decimalAmount;

        try {

            decimalAmount =
                    new BigDecimal(
                            amount
                    );

        } catch (NumberFormatException e) {

            showErrorAndFinish(
                    "Invalid payment amount."
            );

            return false;
        }


        // -----------------------------------------------------
        // POSITIVE
        // -----------------------------------------------------

        if (
                decimalAmount.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {

            showErrorAndFinish(
                    "Payment amount must be greater than zero."
            );

            return false;
        }


        // -----------------------------------------------------
        // DECIMAL PLACES
        // -----------------------------------------------------

        if (
                decimalAmount.scale() > 2
        ) {

            showErrorAndFinish(
                    "Payment amount is invalid."
            );

            return false;
        }


        // -----------------------------------------------------
        // NORMALIZE
        // -----------------------------------------------------

        decimalAmount =
                decimalAmount.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        amount =
                decimalAmount.toPlainString();


        return true;
    }


    // =========================================================
    // DISPLAY PAYMENT
    // =========================================================

    private void displayPayment() {


        // -----------------------------------------------------
        // RECIPIENT NAME
        // -----------------------------------------------------

        if (tvRecipientName != null) {

            tvRecipientName.setText(
                    customerName
            );
        }


        // -----------------------------------------------------
        // INITIALS
        // -----------------------------------------------------

        if (tvRecipientInitials != null) {

            tvRecipientInitials.setText(
                    createInitials(
                            customerName
                    )
            );
        }


        // -----------------------------------------------------
        // ACCOUNT
        // -----------------------------------------------------

        if (tvMaskedAccount != null) {

            if (
                    maskedAccountNumber != null
                            && !maskedAccountNumber
                            .trim()
                            .isEmpty()
            ) {

                tvMaskedAccount.setText(
                        maskedAccountNumber
                );

            } else {

                tvMaskedAccount.setText(
                        "Account verified"
                );
            }
        }


        // -----------------------------------------------------
        // AMOUNT
        // -----------------------------------------------------

        if (tvAmount != null) {

            tvAmount.setText(
                    "₹" + amount
            );
        }


        // -----------------------------------------------------
        // PAYMENT ID
        // -----------------------------------------------------

        if (tvPaymentId != null) {

            tvPaymentId.setText(
                    paymentId
            );
        }
    }


    // =========================================================
    // CREATE INITIALS
    // =========================================================

    private String createInitials(
            String name
    ) {

        if (
                name == null
                        || name.trim().isEmpty()
        ) {

            return "MB";
        }


        String[] parts =
                name.trim()
                        .split("\\s+");


        if (parts.length == 1) {

            String value =
                    parts[0];


            if (value.length() == 1) {

                return value.toUpperCase();
            }


            return value
                    .substring(
                            0,
                            2
                    )
                    .toUpperCase();
        }


        String first =
                parts[0];


        String last =
                parts[parts.length - 1];


        return (
                first.substring(
                        0,
                        1
                )
                        + last.substring(
                        0,
                        1
                )
        ).toUpperCase();
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    private void setupListeners() {


        // -----------------------------------------------------
        // BACK
        // -----------------------------------------------------

        if (btnBack != null) {

            btnBack.setOnClickListener(
                    view -> {

                        if (!paymentInProgress) {

                            finish();
                        }
                    }
            );
        }


        // -----------------------------------------------------
        // CONTINUE
        // -----------------------------------------------------

        if (btnContinue != null) {

            btnContinue.setOnClickListener(
                    view -> continueToPin()
            );
        }
    }


    // =========================================================
    // CONTINUE TO TRANSACTION PIN
    // =========================================================

    private void continueToPin() {


        if (paymentInProgress) {
            return;
        }


        // =====================================================
        // FINAL VALIDATION
        // =====================================================

        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Payment ID is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (
                amount == null
                        || amount.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Payment amount is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // DISABLE REVIEW BUTTON
        // =====================================================

        if (btnContinue != null) {

            btnContinue.setEnabled(false);
        }


        // =====================================================
        // SHOW TRANSACTION PIN DIALOG
        // =====================================================

        PinVerificationDialog
                .showForTransactionPin(
                        QrPaymentReviewActivity.this,

                        new PinVerificationDialog
                                .OnPinVerifiedWithPinListener() {

                            @Override
                            public void onSuccess(
                                    String transactionPin
                            ) {

                                if (
                                        transactionPin == null
                                                || transactionPin
                                                .trim()
                                                .isEmpty()
                                ) {

                                    if (btnContinue != null) {

                                        btnContinue.setEnabled(true);
                                    }


                                    Toast.makeText(
                                            QrPaymentReviewActivity.this,
                                            "Transaction PIN is missing.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }


                                // =============================================
                                // PIN VERIFIED
                                // NOW EXECUTE QR PAYMENT
                                // =============================================

                                executeQrPayment(
                                        transactionPin.trim()
                                );
                            }


                            @Override
                            public void onFailure() {

                                if (btnContinue != null) {

                                    btnContinue.setEnabled(true);
                                }
                            }
                        }
                );
    }


    // =========================================================
    // EXECUTE QR PAYMENT
    // =========================================================

    private void executeQrPayment(
            String transactionPin
    ) {


        if (paymentInProgress) {
            return;
        }


        // =====================================================
        // VALIDATE API
        // =====================================================

        if (apiService == null) {

            if (btnContinue != null) {

                btnContinue.setEnabled(true);
            }


            Toast.makeText(
                    this,
                    "Payment service is unavailable.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // PARSE AMOUNT
        // =====================================================

        BigDecimal decimalAmount;

        try {

            decimalAmount =
                    new BigDecimal(
                            amount
                    );

        } catch (NumberFormatException e) {

            if (btnContinue != null) {

                btnContinue.setEnabled(true);
            }


            Toast.makeText(
                    this,
                    "Invalid payment amount.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // FINITE DOUBLE CHECK
        // =====================================================

        double numericAmount =
                decimalAmount.doubleValue();


        if (
                Double.isNaN(numericAmount)
                        || Double.isInfinite(numericAmount)
                        || numericAmount <= 0
        ) {

            if (btnContinue != null) {

                btnContinue.setEnabled(true);
            }


            Toast.makeText(
                    this,
                    "Invalid payment amount.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // CREATE REQUEST
        // =====================================================

        QrPaymentRequest request =
                new QrPaymentRequest();


        request.setPaymentId(
                paymentId
        );


        request.setAmount(
                numericAmount
        );


        request.setTransactionPin(
                transactionPin
        );


        // =====================================================
        // UPDATE STATE
        // =====================================================

        paymentInProgress = true;


        if (btnContinue != null) {

            btnContinue.setEnabled(false);

            btnContinue.setText(
                    "Processing..."
            );
        }


        // =====================================================
        // API CALL
        // =====================================================

        apiService
                .payUsingQr(
                        request
                )
                .enqueue(
                        new Callback<QrPaymentResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<QrPaymentResponse> call,
                                    @NonNull Response<QrPaymentResponse> response
                            ) {

                                paymentInProgress = false;


                                // =============================================
                                // SUCCESS
                                // =============================================

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                                && response.body().isSuccess()
                                ) {

                                    QrPaymentResponse result =
                                            response.body();


                                    showPaymentSuccess(
                                            result
                                    );

                                    return;
                                }


                                // =============================================
                                // ERROR
                                // =============================================

                                restoreContinueButton();


                                Toast.makeText(
                                        QrPaymentReviewActivity.this,
                                        getHttpErrorMessage(
                                                response
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<QrPaymentResponse> call,
                                    @NonNull Throwable throwable
                            ) {

                                paymentInProgress = false;


                                restoreContinueButton();


                                Toast.makeText(
                                        QrPaymentReviewActivity.this,
                                        "Payment failed.\n"
                                                + getNetworkErrorMessage(
                                                throwable
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // PAYMENT SUCCESS
    // =========================================================

    private void showPaymentSuccess(
            QrPaymentResponse result
    ) {


        // =====================================================
        // SAFETY CHECK
        // =====================================================

        if (result == null) {

            Toast.makeText(
                    this,
                    "Payment completed successfully.",
                    Toast.LENGTH_LONG
            ).show();


            Intent dashboardIntent =
                    new Intent(
                            QrPaymentReviewActivity.this,
                            DashboardActivity.class
                    );


            dashboardIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );


            startActivity(
                    dashboardIntent
            );


            finish();

            return;
        }


        // =====================================================
        // PAYMENT ID
        // =====================================================

        String resultPaymentId =
                result.getPaymentId();


        if (
                resultPaymentId == null
                        || resultPaymentId.trim().isEmpty()
        ) {

            resultPaymentId =
                    paymentId;
        }


        // =====================================================
        // RECIPIENT NAME
        // =====================================================

        String recipient =
                result.getRecipientName();


        if (
                recipient == null
                        || recipient.trim().isEmpty()
        ) {

            recipient =
                    customerName;
        }


        if (
                recipient == null
                        || recipient.trim().isEmpty()
        ) {

            recipient =
                    "MyBank User";
        }


        recipient =
                recipient.trim();


        // =====================================================
        // MASKED ACCOUNT
        // =====================================================

        String resultMaskedAccount =
                result.getMaskedRecipientAccount();


        if (
                resultMaskedAccount == null
                        || resultMaskedAccount.trim().isEmpty()
        ) {

            resultMaskedAccount =
                    maskedAccountNumber;
        }


        if (
                resultMaskedAccount == null
                        || resultMaskedAccount.trim().isEmpty()
        ) {

            resultMaskedAccount =
                    "Account verified";
        }


        resultMaskedAccount =
                resultMaskedAccount.trim();


        // =====================================================
        // AMOUNT
        // =====================================================

        String resultAmount =
                formatAmount(
                        result.getAmount()
                );


        // =====================================================
        // OPEN SUCCESS SCREEN
        // =====================================================

        Intent intent =
                new Intent(
                        QrPaymentReviewActivity.this,
                        QrPaymentSuccessActivity.class
                );


        intent.putExtra(
                QrPaymentSuccessActivity.EXTRA_PAYMENT_ID,
                resultPaymentId
        );


        intent.putExtra(
                QrPaymentSuccessActivity.EXTRA_AMOUNT,
                resultAmount
        );


        intent.putExtra(
                QrPaymentSuccessActivity.EXTRA_RECIPIENT_NAME,
                recipient
        );


        intent.putExtra(
                QrPaymentSuccessActivity.EXTRA_MASKED_ACCOUNT,
                resultMaskedAccount
        );


        startActivity(
                intent
        );


        finish();
    }


    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(
            double value
    ) {

        try {

            return new BigDecimal(
                    String.valueOf(value)
            )
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
                    .toPlainString();

        } catch (Exception e) {

            return String.format(
                    java.util.Locale.US,
                    "%.2f",
                    value
            );
        }
    }


    // =========================================================
    // RESTORE CONTINUE BUTTON
    // =========================================================

    private void restoreContinueButton() {

        if (btnContinue != null) {

            btnContinue.setEnabled(true);

            btnContinue.setText(
                    "Continue"
            );
        }
    }


    // =========================================================
    // HTTP ERROR MESSAGE
    // =========================================================

    private String getHttpErrorMessage(
            Response<?> response
    ) {

        if (response == null) {

            return "Payment failed.";
        }


        // =====================================================
        // TRY BACKEND JSON MESSAGE
        // =====================================================

        try {

            if (response.errorBody() != null) {

                String errorJson =
                        response.errorBody()
                                .string();


                if (
                        errorJson != null
                                && !errorJson.trim().isEmpty()
                ) {

                    JSONObject object =
                            new JSONObject(
                                    errorJson
                            );


                    String message =
                            object.optString(
                                    "message",
                                    ""
                            );


                    if (
                            message != null
                                    && !message.trim().isEmpty()
                    ) {

                        return message;
                    }
                }
            }

        } catch (Exception ignored) {

            // Fall through to generic message.
        }


        // =====================================================
        // STATUS-BASED FALLBACK
        // =====================================================

        switch (response.code()) {

            case 400:

                return "Payment request was rejected.";


            case 401:

                return "Your session has expired. Please log in again.";


            case 403:

                return "You are not authorized to make this payment.";


            case 404:

                return "Payment recipient could not be found.";


            case 409:

                return "Payment could not be completed.";


            case 500:

                return "Bank server error. Please try again.";


            default:

                return "Payment failed. Please try again.";
        }
    }


    // =========================================================
    // NETWORK ERROR MESSAGE
    // =========================================================

    private String getNetworkErrorMessage(
            Throwable throwable
    ) {

        if (
                throwable == null
                        || throwable.getMessage() == null
        ) {

            return "Unable to connect to the bank server.";
        }


        String message =
                throwable.getMessage()
                        .trim();


        if (message.isEmpty()) {

            return "Unable to connect to the bank server.";
        }


        return message;
    }


    // =========================================================
    // ERROR AND FINISH
    // =========================================================

    private void showErrorAndFinish(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();


        finish();
    }
}