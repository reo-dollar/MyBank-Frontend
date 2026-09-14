package com.rohit.mybank.activities.qr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * =========================================================
 * QR AMOUNT ACTIVITY
 * =========================================================
 *
 * Collects the amount that the authenticated user wants
 * to pay to the recipient resolved from the QR code.
 *
 * This screen DOES NOT:
 *
 * - debit the sender
 * - credit the recipient
 * - create a transaction
 * - verify transaction PIN
 *
 * It validates the amount and opens the payment review
 * screen with the recipient/payment information.
 *
 * =========================================================
 */
public class QrAmountActivity extends AppCompatActivity {

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

    private TextView tvPaymentId;

    private EditText etAmount;

    private TextView tvAmountError;

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
                R.layout.activity_qr_amount
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
        // INITIALIZE VIEWS
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // READ INTENT DATA
        // -----------------------------------------------------

        readIntentData();


        // -----------------------------------------------------
        // DISPLAY RECIPIENT
        // -----------------------------------------------------

        displayRecipient();


        // -----------------------------------------------------
        // SETUP AMOUNT INPUT
        // -----------------------------------------------------

        setupAmountInput();


        // -----------------------------------------------------
        // SETUP LISTENERS
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

        etAmount =
                findViewById(
                        R.id.etAmount
                );

        tvAmountError =
                findViewById(
                        R.id.tvAmountError
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


        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Payment information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
    }


    // =========================================================
    // DISPLAY RECIPIENT
    // =========================================================

    private void displayRecipient() {

        if (
                customerName == null
                        || customerName.trim().isEmpty()
        ) {

            customerName =
                    "MyBank User";
        }


        customerName =
                customerName.trim();


        // -----------------------------------------------------
        // NAME
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
    // SETUP AMOUNT INPUT
    // =========================================================

    private void setupAmountInput() {

        if (etAmount == null) {
            return;
        }


        etAmount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );


        /*
         * Maximum 2 decimal places.
         *
         * Accepted:
         *
         * 10
         * 10.5
         * 10.50
         *
         * Rejected:
         *
         * 10.555
         */

        etAmount.setFilters(
                new InputFilter[]{
                        new DecimalDigitsInputFilter(
                                2
                        )
                }
        );


        etAmount.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        clearAmountError();
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }


    // =========================================================
    // SETUP LISTENERS
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
                    view -> validateAndContinue()
            );
        }
    }


    // =========================================================
    // VALIDATE AMOUNT
    // =========================================================

    private void validateAndContinue() {

        if (etAmount == null) {
            return;
        }


        String amountText =
                etAmount.getText()
                        .toString()
                        .trim();


        // -----------------------------------------------------
        // EMPTY
        // -----------------------------------------------------

        if (amountText.isEmpty()) {

            showAmountError(
                    "Please enter an amount."
            );

            return;
        }


        // -----------------------------------------------------
        // INVALID FORMAT
        // -----------------------------------------------------

        BigDecimal amount;

        try {

            amount =
                    new BigDecimal(
                            amountText
                    );

        } catch (NumberFormatException e) {

            showAmountError(
                    "Please enter a valid amount."
            );

            return;
        }


        // -----------------------------------------------------
        // POSITIVE CHECK
        // -----------------------------------------------------

        if (
                amount.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {

            showAmountError(
                    "Amount must be greater than ₹0."
            );

            return;
        }


        // -----------------------------------------------------
        // DECIMAL CHECK
        // -----------------------------------------------------

        if (
                amount.scale() > 2
        ) {

            showAmountError(
                    "Amount can have a maximum of 2 decimal places."
            );

            return;
        }


        // -----------------------------------------------------
        // NORMALIZE
        // -----------------------------------------------------

        amount =
                amount.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // -----------------------------------------------------
        // OPEN REVIEW
        // -----------------------------------------------------

        openNextPaymentStep(
                amount.toPlainString()
        );
    }


    // =========================================================
    // OPEN PAYMENT REVIEW
    // =========================================================

    private void openNextPaymentStep(
            String amount
    ) {

        // -----------------------------------------------------
        // FINAL LOCAL VALIDATION
        // -----------------------------------------------------

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
                customerName == null
                        || customerName.trim().isEmpty()
        ) {

            customerName =
                    "MyBank User";
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


        // -----------------------------------------------------
        // CREATE REVIEW INTENT
        // -----------------------------------------------------

        Intent intent =
                new Intent(
                        QrAmountActivity.this,
                        QrPaymentReviewActivity.class
                );


        // -----------------------------------------------------
        // PAYMENT ID
        // -----------------------------------------------------

        intent.putExtra(
                QrPaymentReviewActivity.EXTRA_PAYMENT_ID,
                paymentId
        );


        // -----------------------------------------------------
        // CUSTOMER NAME
        // -----------------------------------------------------

        intent.putExtra(
                QrPaymentReviewActivity.EXTRA_CUSTOMER_NAME,
                customerName
        );


        // -----------------------------------------------------
        // MASKED ACCOUNT
        // -----------------------------------------------------

        if (
                maskedAccountNumber != null
                        && !maskedAccountNumber
                        .trim()
                        .isEmpty()
        ) {

            intent.putExtra(
                    QrPaymentReviewActivity.EXTRA_MASKED_ACCOUNT,
                    maskedAccountNumber
            );
        }


        // -----------------------------------------------------
        // QR PAYLOAD
        // -----------------------------------------------------

        if (
                qrPayload != null
                        && !qrPayload.trim().isEmpty()
        ) {

            intent.putExtra(
                    QrPaymentReviewActivity.EXTRA_QR_PAYLOAD,
                    qrPayload
            );
        }


        // -----------------------------------------------------
        // AMOUNT
        // -----------------------------------------------------

        intent.putExtra(
                QrPaymentReviewActivity.EXTRA_AMOUNT,
                amount
        );


        // -----------------------------------------------------
        // OPEN PAYMENT REVIEW SCREEN
        // -----------------------------------------------------

        startActivity(
                intent
        );
    }


    // =========================================================
    // SHOW AMOUNT ERROR
    // =========================================================

    private void showAmountError(
            String message
    ) {

        if (tvAmountError != null) {

            tvAmountError.setText(
                    message
            );

            tvAmountError.setVisibility(
                    View.VISIBLE
            );
        }


        if (etAmount != null) {

            etAmount.requestFocus();
        }
    }


    // =========================================================
    // CLEAR AMOUNT ERROR
    // =========================================================

    private void clearAmountError() {

        if (tvAmountError != null) {

            tvAmountError.setText(
                    ""
            );

            tvAmountError.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // DECIMAL INPUT FILTER
    // =========================================================

    private static class DecimalDigitsInputFilter
            implements InputFilter {

        private final int decimalDigits;


        DecimalDigitsInputFilter(
                int decimalDigits
        ) {

            this.decimalDigits =
                    decimalDigits;
        }


        @Override
        public CharSequence filter(
                CharSequence source,
                int start,
                int end,
                android.text.Spanned dest,
                int dstart,
                int dend
        ) {

            String current =
                    dest.toString();


            String proposed =
                    current.substring(
                            0,
                            dstart
                    )
                            + source.subSequence(
                            start,
                            end
                    )
                            + current.substring(
                            dend
                    );


            if (proposed.isEmpty()) {

                return null;
            }


            // -------------------------------------------------
            // NUMBERS + DECIMAL POINT ONLY
            // -------------------------------------------------

            if (
                    !proposed.matches(
                            "\\d*(\\.\\d*)?"
                    )
            ) {

                return "";
            }


            // -------------------------------------------------
            // ONE DECIMAL POINT + MAX 2 DIGITS
            // -------------------------------------------------

            int decimalIndex =
                    proposed.indexOf('.');


            if (decimalIndex >= 0) {

                int digitsAfterDecimal =
                        proposed.length()
                                - decimalIndex
                                - 1;


                if (
                        digitsAfterDecimal
                                > decimalDigits
                ) {

                    return "";
                }
            }


            return null;
        }
    }
}