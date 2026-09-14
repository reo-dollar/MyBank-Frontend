package com.rohit.mybank.activities.qr;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.dashboard.DashboardActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;


/**
 * =========================================================
 * QR PAYMENT SUCCESS ACTIVITY
 * =========================================================
 *
 * Displays the final successful QR payment result.
 *
 * =========================================================
 */
public class QrPaymentSuccessActivity
        extends AppCompatActivity {


    // =========================================================
    // INTENT EXTRAS
    // =========================================================

    public static final String EXTRA_PAYMENT_ID =
            "extra_payment_id";

    public static final String EXTRA_AMOUNT =
            "extra_amount";

    public static final String EXTRA_RECIPIENT_NAME =
            "extra_recipient_name";

    public static final String EXTRA_MASKED_ACCOUNT =
            "extra_masked_account";


    // =========================================================
    // UI
    // =========================================================

    private TextView tvAmount;

    private TextView tvRecipientName;

    private TextView tvMaskedAccount;

    private TextView tvPaymentId;

    private Button btnDone;


    // =========================================================
    // DATA
    // =========================================================

    private String paymentId;

    private String recipientName;

    private String maskedAccount;

    private String amount;


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
                R.layout.activity_qr_payment_success
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
        // INITIALIZE
        // =====================================================

        initializeViews();


        // =====================================================
        // READ DATA
        // =====================================================

        readIntentData();


        // =====================================================
        // VALIDATE
        // =====================================================

        if (!validateData()) {

            finish();

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


        tvAmount =
                findViewById(
                        R.id.tvAmount
                );


        tvRecipientName =
                findViewById(
                        R.id.tvRecipientName
                );


        tvMaskedAccount =
                findViewById(
                        R.id.tvMaskedAccount
                );


        tvPaymentId =
                findViewById(
                        R.id.tvPaymentId
                );


        btnDone =
                findViewById(
                        R.id.btnDone
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


        amount =
                getIntent().getStringExtra(
                        EXTRA_AMOUNT
                );


        recipientName =
                getIntent().getStringExtra(
                        EXTRA_RECIPIENT_NAME
                );


        maskedAccount =
                getIntent().getStringExtra(
                        EXTRA_MASKED_ACCOUNT
                );
    }


    // =========================================================
    // VALIDATE DATA
    // =========================================================

    private boolean validateData() {


        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            return false;
        }


        paymentId =
                paymentId.trim();


        if (
                amount == null
                        || amount.trim().isEmpty()
        ) {

            return false;
        }


        amount =
                amount.trim();


        if (
                recipientName == null
                        || recipientName.trim().isEmpty()
        ) {

            recipientName =
                    "MyBank User";

        } else {

            recipientName =
                    recipientName.trim();
        }


        if (
                maskedAccount == null
                        || maskedAccount.trim().isEmpty()
        ) {

            maskedAccount =
                    "Account verified";

        } else {

            maskedAccount =
                    maskedAccount.trim();
        }


        return true;
    }


    // =========================================================
    // DISPLAY PAYMENT
    // =========================================================

    private void displayPayment() {


        // =====================================================
        // AMOUNT
        // =====================================================

        if (tvAmount != null) {

            tvAmount.setText(
                    "₹" + formatAmount(amount)
            );
        }


        // =====================================================
        // RECIPIENT
        // =====================================================

        if (tvRecipientName != null) {

            tvRecipientName.setText(
                    recipientName
            );
        }


        // =====================================================
        // MASKED ACCOUNT
        // =====================================================

        if (tvMaskedAccount != null) {

            tvMaskedAccount.setText(
                    maskedAccount
            );
        }


        // =====================================================
        // PAYMENT ID
        // =====================================================

        if (tvPaymentId != null) {

            tvPaymentId.setText(
                    paymentId
            );
        }
    }


    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(
            String value
    ) {

        try {

            BigDecimal decimalAmount =
                    new BigDecimal(
                            value
                    );


            return decimalAmount
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
                    .toPlainString();

        } catch (Exception ignored) {

            try {

                return String.format(
                        Locale.US,
                        "%.2f",
                        Double.parseDouble(value)
                );

            } catch (Exception ignoredAgain) {

                return value;
            }
        }
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    private void setupListeners() {


        if (btnDone != null) {

            btnDone.setOnClickListener(
                    view -> returnToDashboard()
            );
        }
    }


    // =========================================================
    // RETURN TO DASHBOARD
    // =========================================================

    private void returnToDashboard() {


        Intent intent =
                new Intent(
                        QrPaymentSuccessActivity.this,
                        DashboardActivity.class
                );


        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );


        startActivity(intent);


        finish();
    }


    // =========================================================
    // BACK BUTTON
    // =========================================================

    @Override
    public void onBackPressed() {

        returnToDashboard();
    }
}