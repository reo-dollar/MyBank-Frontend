package com.rohit.mybank.activities.qr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;

public class QrRecipientActivity extends AppCompatActivity {

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


    // =========================================================
    // UI
    // =========================================================

    private ImageButton btnBack;

    private TextView tvRecipientInitials;

    private TextView tvRecipientName;

    private TextView tvMaskedAccount;

    private TextView tvPaymentId;

    private Button btnContinue;


    // =========================================================
    // DATA
    // =========================================================

    private String paymentId;

    private String customerName;

    private String maskedAccountNumber;

    private String qrPayload;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_qr_recipient
        );


        // -----------------------------------------------------
        // SYSTEM BARS
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // INITIALIZE
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // READ DATA
        // -----------------------------------------------------

        readIntentData();


        // -----------------------------------------------------
        // DISPLAY DATA
        // -----------------------------------------------------

        displayRecipient();


        // -----------------------------------------------------
        // LISTENERS
        // -----------------------------------------------------

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
    }


    // =========================================================
    // DISPLAY RECIPIENT
    // =========================================================

    private void displayRecipient() {

        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Recipient information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
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
        }


        customerName =
                customerName.trim();


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
        // MASKED ACCOUNT
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
                    view -> finish()
            );
        }


        // -----------------------------------------------------
        // CONTINUE
        // -----------------------------------------------------

        if (btnContinue != null) {

            btnContinue.setOnClickListener(
                    view -> continueToAmount()
            );
        }
    }


    // =========================================================
    // CONTINUE TO AMOUNT SCREEN
    // =========================================================

    private void continueToAmount() {

        // -----------------------------------------------------
        // VALIDATE PAYMENT ID
        // -----------------------------------------------------

        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Payment information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // -----------------------------------------------------
        // CREATE INTENT
        // -----------------------------------------------------

        Intent intent =
                new Intent(
                        QrRecipientActivity.this,
                        QrAmountActivity.class
                );


        // -----------------------------------------------------
        // PAYMENT ID
        // -----------------------------------------------------

        intent.putExtra(
                QrAmountActivity.EXTRA_PAYMENT_ID,
                paymentId
        );


        // -----------------------------------------------------
        // CUSTOMER NAME
        // -----------------------------------------------------

        intent.putExtra(
                QrAmountActivity.EXTRA_CUSTOMER_NAME,
                customerName
        );


        // -----------------------------------------------------
        // MASKED ACCOUNT
        // -----------------------------------------------------

        intent.putExtra(
                QrAmountActivity.EXTRA_MASKED_ACCOUNT,
                maskedAccountNumber
        );


        // -----------------------------------------------------
        // QR PAYLOAD
        // -----------------------------------------------------

        if (
                qrPayload != null
                        && !qrPayload.trim().isEmpty()
        ) {

            intent.putExtra(
                    QrAmountActivity.EXTRA_QR_PAYLOAD,
                    qrPayload
            );
        }


        // -----------------------------------------------------
        // OPEN AMOUNT SCREEN
        // -----------------------------------------------------

        startActivity(
                intent
        );
    }
}