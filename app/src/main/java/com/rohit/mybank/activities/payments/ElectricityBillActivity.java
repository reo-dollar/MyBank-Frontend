package com.rohit.mybank.activities.payments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.model.electricity.ElectricityBillRequest;
import com.rohit.mybank.model.electricity.ElectricityBillResponse;
import com.rohit.mybank.repository.ElectricityBillRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

public class ElectricityBillActivity extends AppCompatActivity {

    // =====================================================
    // Layouts
    // =====================================================

    private TextInputLayout layoutConsumerNumber;
    private TextInputLayout layoutBoard;
    private TextInputLayout layoutState;
    private TextInputLayout layoutAmount;

    // =====================================================
    // Inputs
    // =====================================================

    private TextInputEditText etConsumerNumber;
    private TextInputEditText etAmount;

    private AutoCompleteTextView actBoard;
    private AutoCompleteTextView actState;

    // =====================================================
    // Summary
    // =====================================================

    private TextView tvSummaryConsumer;
    private TextView tvSummaryBoard;
    private TextView tvSummaryState;
    private TextView tvSummaryAmount;

    // =====================================================
    // Button
    // =====================================================

    private MaterialButton btnPayBill;

    // =====================================================
    // Repository
    // =====================================================

    private ElectricityBillRepository repository;

    // =====================================================
    // Data
    // =====================================================

    private String consumerNumber = "";
    private String board = "";
    private String state = "";
    private double billAmount = 0;

    // =====================================================
    // Transaction PIN Launcher
    // =====================================================

    private final ActivityResultLauncher<Intent> pinVerificationLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() != RESULT_OK) {

                            Toast.makeText(
                                    this,
                                    "Transaction cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        if (result.getData() == null) {
                            return;
                        }

                        boolean verified =
                                result.getData().getBooleanExtra(
                                        VerifyTransactionPinActivity.EXTRA_PIN_VERIFIED,
                                        false
                                );

                        if (verified) {

                            performPayment();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    });

    // =====================================================
    // onCreate
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_electricity_bill);

        initializeViews();

        repository = new ElectricityBillRepository(this);

        loadDropdowns();

        setupSummaryListeners();

        updateSummary();

        btnPayBill.setOnClickListener(v -> validateInputs());

    }

    // =====================================================
    // Initialize Views
    // =====================================================

    private void initializeViews() {

        layoutConsumerNumber = findViewById(R.id.layoutConsumerNumber);
        layoutBoard = findViewById(R.id.layoutBoard);
        layoutState = findViewById(R.id.layoutState);
        layoutAmount = findViewById(R.id.layoutAmount);

        etConsumerNumber = findViewById(R.id.etConsumerNumber);
        etAmount = findViewById(R.id.etAmount);

        actBoard = findViewById(R.id.actBoard);
        actState = findViewById(R.id.actState);

        tvSummaryConsumer = findViewById(R.id.tvSummaryConsumer);
        tvSummaryBoard = findViewById(R.id.tvSummaryBoard);
        tvSummaryState = findViewById(R.id.tvSummaryState);
        tvSummaryAmount = findViewById(R.id.tvSummaryAmount);

        btnPayBill = findViewById(R.id.btnPayBill);
        getOnBackPressedDispatcher().addCallback(
                this,
                new androidx.activity.OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        finish();

                    }

                }
        );

    }

    // =====================================================
// Load Dropdown Data
// =====================================================

    private void loadDropdowns() {

        String[] boards = {

                "MSEDCL",
                "Adani Electricity",
                "BEST",
                "Tata Power",
                "Torrent Power",
                "BSES Rajdhani",
                "BSES Yamuna",
                "UHBVN",
                "DHBVN",
                "PSPCL",
                "KSEB",
                "TANGEDCO"

        };

        String[] states = {

                "Maharashtra",
                "Delhi",
                "Gujarat",
                "Haryana",
                "Punjab",
                "Kerala",
                "Tamil Nadu",
                "Karnataka",
                "Rajasthan",
                "Uttar Pradesh",
                "Madhya Pradesh"

        };

        ArrayAdapter<String> boardAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        boards
                );

        ArrayAdapter<String> stateAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        states
                );

        actBoard.setAdapter(boardAdapter);
        actState.setAdapter(stateAdapter);

    }

// =====================================================
// Summary Listeners
// =====================================================

    private void setupSummaryListeners() {

        etConsumerNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                updateSummary();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        etAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                updateSummary();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        actBoard.setOnItemClickListener(
                (parent, view, position, id) ->
                        updateSummary()
        );

        actState.setOnItemClickListener(
                (parent, view, position, id) ->
                        updateSummary()
        );

    }

// =====================================================
// Update Summary Card
// =====================================================

    private void updateSummary() {

        String consumer = "";

        if (etConsumerNumber.getText() != null) {
            consumer = etConsumerNumber
                    .getText()
                    .toString()
                    .trim();
        }

        String amount = "";

        if (etAmount.getText() != null) {
            amount = etAmount
                    .getText()
                    .toString()
                    .trim();
        }

        String selectedBoard = "";

        if (actBoard.getText() != null) {
            selectedBoard = actBoard
                    .getText()
                    .toString()
                    .trim();
        }

        String selectedState = "";

        if (actState.getText() != null) {
            selectedState = actState
                    .getText()
                    .toString()
                    .trim();
        }

        tvSummaryConsumer.setText(
                "Consumer No : " +
                        (consumer.isEmpty() ? "-" : consumer)
        );

        tvSummaryBoard.setText(
                "Board : " +
                        (selectedBoard.isEmpty() ? "-" : selectedBoard)
        );

        tvSummaryState.setText(
                "State : " +
                        (selectedState.isEmpty() ? "-" : selectedState)
        );

        tvSummaryAmount.setText(
                "Amount : ₹" +
                        (amount.isEmpty() ? "0.00" : amount)
        );

    }
    // =====================================================
// Validate Inputs
// =====================================================

    private void validateInputs() {

        layoutConsumerNumber.setError(null);
        layoutBoard.setError(null);
        layoutState.setError(null);
        layoutAmount.setError(null);

        consumerNumber = "";

        if (etConsumerNumber.getText() != null) {
            consumerNumber = etConsumerNumber.getText().toString().trim();
        }

        board = actBoard.getText().toString().trim();

        state = actState.getText().toString().trim();

        String amountText = "";

        if (etAmount.getText() != null) {
            amountText = etAmount.getText().toString().trim();
        }

        // Consumer Number

        if (consumerNumber.isEmpty()) {

            layoutConsumerNumber.setError("Enter Consumer Number");

            etConsumerNumber.requestFocus();

            return;

        }

        if (consumerNumber.length() < 8) {

            layoutConsumerNumber.setError(
                    "Invalid Consumer Number"
            );

            etConsumerNumber.requestFocus();

            return;

        }

        // Board

        if (board.isEmpty()) {

            layoutBoard.setError(
                    "Select Electricity Board"
            );

            actBoard.requestFocus();

            return;

        }

        // State

        if (state.isEmpty()) {

            layoutState.setError(
                    "Select State"
            );

            actState.requestFocus();

            return;

        }

        // Amount

        if (amountText.isEmpty()) {

            layoutAmount.setError(
                    "Enter Bill Amount"
            );

            etAmount.requestFocus();

            return;

        }

        try {

            billAmount = Double.parseDouble(amountText);

        } catch (Exception e) {

            layoutAmount.setError(
                    "Invalid Amount"
            );

            etAmount.requestFocus();

            return;

        }

        if (billAmount <= 0) {

            layoutAmount.setError(
                    "Amount must be greater than zero"
            );

            etAmount.requestFocus();

            return;

        }

        showConfirmationDialog();

    }

// =====================================================
// Confirmation Dialog
// =====================================================

    private void showConfirmationDialog() {

        String message =

                "Consumer Number : " + consumerNumber +

                        "\n\nBoard : " + board +

                        "\n\nState : " + state +

                        "\n\nBill Amount : ₹" + billAmount +

                        "\n\nProceed with payment?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Electricity Bill Payment")

                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Continue",
                        (dialog, which) ->

                                new PaymentSecurityHelper(

                                        ElectricityBillActivity.this,

                                        pinVerificationLauncher,

                                        new PaymentCallback() {

                                            @Override
                                            public void onSuccess() {

                                                performPayment();

                                            }

                                        }

                                ).verifyPayment()

                )

                .show();

    }
    // =====================================================
// Perform Electricity Bill Payment
// =====================================================

    private void performPayment() {

        btnPayBill.setEnabled(false);

        ElectricityBillRequest request =
                new ElectricityBillRequest();

        request.setConsumerNumber(consumerNumber);
        request.setBoard(board);
        request.setState(state);
        request.setAmount(billAmount);

        repository.payBill(request)

                .enqueue(new retrofit2.Callback<ElectricityBillResponse>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<ElectricityBillResponse> call,
                            retrofit2.Response<ElectricityBillResponse> response) {

                        btnPayBill.setEnabled(true);

                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    ElectricityBillActivity.this,
                                    "HTTP Error : " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        if (response.body() == null) {

                            Toast.makeText(
                                    ElectricityBillActivity.this,
                                    "Empty server response.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        ElectricityBillResponse billResponse =
                                response.body();

                        if (billResponse.isSuccess()) {

                            showSuccessDialog(
                                    billResponse
                            );

                        } else {

                            Toast.makeText(
                                    ElectricityBillActivity.this,
                                    billResponse.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<ElectricityBillResponse> call,
                            Throwable t) {

                        btnPayBill.setEnabled(true);

                        Toast.makeText(
                                ElectricityBillActivity.this,
                                "Network Error\n" + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }
    // =====================================================
// Payment Success Dialog
// =====================================================

    private void showSuccessDialog(
            ElectricityBillResponse response
    ) {

        String paymentId = "";

        if (response.getPaymentId() != null) {
            paymentId = response.getPaymentId();
        }

        String message =

                "✅ Electricity Bill Paid Successfully"

                        + "\n\nConsumer Number : "
                        + consumerNumber

                        + "\n\nElectricity Board : "
                        + board

                        + "\n\nState : "
                        + state

                        + "\n\nAmount Paid : ₹"
                        + String.format("%.2f", billAmount)

                        + "\n\nPayment ID : "
                        + paymentId

                        + "\n\nStatus : SUCCESS";

        new AlertDialog.Builder(this)

                .setTitle("Payment Successful")

                .setMessage(message)

                .setCancelable(false)

                .setPositiveButton(
                        "Done",
                        (dialog, which) -> {

                            setResult(RESULT_OK);

                            finish();

                        })

                .show();

    }

// =====================================================
// Clear Validation Errors
// =====================================================

    private void clearErrors() {

        layoutConsumerNumber.setError(null);
        layoutBoard.setError(null);
        layoutState.setError(null);
        layoutAmount.setError(null);

    }

// =====================================================
// Back Button
// =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }

}