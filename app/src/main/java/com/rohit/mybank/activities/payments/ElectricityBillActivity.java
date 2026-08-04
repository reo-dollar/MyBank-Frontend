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
import com.rohit.mybank.model.electricity.ElectricityBillRequest;
import com.rohit.mybank.model.electricity.ElectricityBillResponse;
import com.rohit.mybank.repository.ElectricityBillRepository;

public class ElectricityBillActivity extends AppCompatActivity {

    //==================================================
    // Layouts
    //==================================================

    private TextInputLayout layoutConsumerNumber;
    private TextInputLayout layoutBoard;
    private TextInputLayout layoutState;
    private TextInputLayout layoutAmount;

    //==================================================
    // Inputs
    //==================================================

    private TextInputEditText etConsumerNumber;
    private TextInputEditText etAmount;

    private AutoCompleteTextView actBoard;
    private AutoCompleteTextView actState;

    //==================================================
    // Summary Views
    //==================================================

    private TextView tvSummaryConsumer;
    private TextView tvSummaryBoard;
    private TextView tvSummaryState;
    private TextView tvSummaryAmount;

    //==================================================
    // Button
    //==================================================

    private MaterialButton btnPayBill;

    //==================================================
    // Repository
    //==================================================

    private ElectricityBillRepository repository;

    //==================================================
    // Data
    //==================================================

    private String consumerNumber;
    private String board;
    private String state;
    private double billAmount;

    //==================================================
    // PIN Launcher
    //==================================================

    private final ActivityResultLauncher<Intent> pinVerificationLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            performPayment();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    });

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
    }

    private void loadDropdowns() {

        String[] boards = {
                "MSEDCL",
                "Adani Electricity",
                "Tata Power",
                "BEST",
                "Torrent Power"
        };

        String[] states = {
                "Maharashtra",
                "Gujarat",
                "Delhi",
                "Karnataka",
                "Tamil Nadu",
                "Punjab",
                "Rajasthan",
                "West Bengal"
        };

        ArrayAdapter<String> boardAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        boards);

        actBoard.setAdapter(boardAdapter);

        actBoard.setOnItemClickListener((parent, view, position, id) ->
                updateSummary());

        ArrayAdapter<String> stateAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        states);

        actState.setAdapter(stateAdapter);

        actState.setOnItemClickListener((parent, view, position, id) ->
                updateSummary());
    }

    private void setupSummaryListeners() {

        TextWatcher watcher = new TextWatcher() {

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
        };

        etConsumerNumber.addTextChangedListener(watcher);
        etAmount.addTextChangedListener(watcher);
    }

    private void updateSummary() {

        String consumer =
                etConsumerNumber.getText() == null
                        ? ""
                        : etConsumerNumber.getText().toString().trim();

        String boardValue =
                actBoard.getText().toString().trim();

        String stateValue =
                actState.getText().toString().trim();

        String amount =
                etAmount.getText() == null
                        ? ""
                        : etAmount.getText().toString().trim();

        tvSummaryConsumer.setText(
                "Consumer No : " +
                        (consumer.isEmpty() ? "-" : consumer));

        tvSummaryBoard.setText(
                "Board : " +
                        (boardValue.isEmpty() ? "-" : boardValue));

        tvSummaryState.setText(
                "State : " +
                        (stateValue.isEmpty() ? "-" : stateValue));

        tvSummaryAmount.setText(
                "Amount : ₹" +
                        (amount.isEmpty() ? "0" : amount));
    }
    //==================================================
    // Validate Inputs
    //==================================================

    private void validateInputs() {

        layoutConsumerNumber.setError(null);
        layoutBoard.setError(null);
        layoutState.setError(null);
        layoutAmount.setError(null);

        consumerNumber = etConsumerNumber.getText() == null
                ? ""
                : etConsumerNumber.getText().toString().trim();

        board = actBoard.getText().toString().trim();

        state = actState.getText().toString().trim();

        String amountText = etAmount.getText() == null
                ? ""
                : etAmount.getText().toString().trim();

        if (consumerNumber.isEmpty()) {
            layoutConsumerNumber.setError("Enter consumer number");
            return;
        }

        if (board.isEmpty()) {
            layoutBoard.setError("Select electricity board");
            return;
        }

        if (state.isEmpty()) {
            layoutState.setError("Select state");
            return;
        }

        if (amountText.isEmpty()) {
            layoutAmount.setError("Enter bill amount");
            return;
        }

        try {
            billAmount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            layoutAmount.setError("Invalid amount");
            return;
        }

        if (billAmount <= 0) {
            layoutAmount.setError("Amount must be greater than ₹0");
            return;
        }

        showConfirmation();
    }

    //==================================================
    // Confirmation Dialog
    //==================================================

    private void showConfirmation() {

        String message =
                "Consumer Number : " + consumerNumber +
                        "\n\nBoard : " + board +
                        "\nState : " + state +
                        "\nAmount : ₹" + billAmount +
                        "\n\nProceed with payment?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Electricity Bill")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Pay", (dialog, which) -> {

                    Intent intent = new Intent(
                            ElectricityBillActivity.this,
                            VerifyTransactionPinActivity.class
                    );

                    pinVerificationLauncher.launch(intent);

                })
                .show();
    }

    //==================================================
    // Perform Payment
    //==================================================

    private void performPayment() {

        ElectricityBillRequest request =
                new ElectricityBillRequest(
                        consumerNumber,
                        board,
                        state,
                        billAmount
                );

        repository.payElectricityBill(request)
                .enqueue(new retrofit2.Callback<ElectricityBillResponse>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<ElectricityBillResponse> call,
                            retrofit2.Response<ElectricityBillResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            showSuccessDialog(response.body());

                        } else {

                            try {

                                String error =
                                        response.errorBody() != null
                                                ? response.errorBody().string()
                                                : "Payment failed.";

                                new AlertDialog.Builder(
                                        ElectricityBillActivity.this)
                                        .setTitle("Payment Failed")
                                        .setMessage(error)
                                        .setPositiveButton("OK", null)
                                        .show();

                            } catch (Exception e) {

                                Toast.makeText(
                                        ElectricityBillActivity.this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            }
                        }

                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<ElectricityBillResponse> call,
                            Throwable t) {

                        new AlertDialog.Builder(
                                ElectricityBillActivity.this)
                                .setTitle("Network Error")
                                .setMessage(t.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                    }

                });
    }

    //==================================================
    // Success Dialog
    //==================================================

    private void showSuccessDialog(ElectricityBillResponse response) {

        String receipt =
                "✓ " + response.getMessage() +
                        "\n\nPayment ID : " + response.getPaymentId() +
                        "\n\nConsumer Number : " + consumerNumber +
                        "\nBoard : " + board +
                        "\nState : " + state +
                        "\nAmount : ₹" + billAmount;

        new AlertDialog.Builder(this)
                .setTitle("Payment Successful")
                .setCancelable(false)
                .setMessage(receipt)
                .setPositiveButton("Done", (dialog, which) -> finish())
                .show();
    }
}