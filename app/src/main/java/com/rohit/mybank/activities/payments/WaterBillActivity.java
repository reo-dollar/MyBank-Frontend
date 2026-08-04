package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.databinding.ActivityWaterBillBinding;
import com.rohit.mybank.model.water.WaterBillRequest;
import com.rohit.mybank.model.water.WaterBillResponse;
import com.rohit.mybank.repository.WaterBillRepository;
import android.text.Editable;
import android.text.TextWatcher;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaterBillActivity extends AppCompatActivity {

    private ActivityWaterBillBinding binding;
    private WaterBillRepository repository;

    private String consumerNumber;
    private String board;
    private String state;
    private BigDecimal amount;

    /* State -> Water Board Mapping */
    private final Map<String, String[]> boardMap = new HashMap<>();

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            payWaterBill();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWaterBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new WaterBillRepository(this);

        setupStates();

        setupBoardMapping();

        setupStateListener();

        setupSummaryWatcher();

        binding.btnPayBill.setOnClickListener(v -> validateInput());

        updateSummary();
    }

    /**
     * Available States
     */
    private void setupStates() {

        String[] states = {
                "Maharashtra",
                "Delhi",
                "Karnataka",
                "Telangana",
                "Tamil Nadu",
                "Gujarat"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        states
                );

        binding.actState.setAdapter(adapter);
    }

    /**
     * State -> Board Mapping
     */
    private void setupBoardMapping() {

        boardMap.put(
                "Maharashtra",
                new String[]{
                        "Mumbai Water Supply",
                        "Pune Municipal Corporation",
                        "Nagpur Municipal Corporation"
                });

        boardMap.put(
                "Delhi",
                new String[]{
                        "Delhi Jal Board"
                });

        boardMap.put(
                "Karnataka",
                new String[]{
                        "Bangalore Water Supply"
                });

        boardMap.put(
                "Telangana",
                new String[]{
                        "Hyderabad Water Works"
                });

        boardMap.put(
                "Tamil Nadu",
                new String[]{
                        "Chennai Metro Water"
                });

        boardMap.put(
                "Gujarat",
                new String[]{
                        "Ahmedabad Municipal Corporation"
                });
    }

    /**
     * Filter Boards according to selected State
     */
    private void setupStateListener() {

        binding.actState.setOnItemClickListener((parent, view, position, id) -> {

            String selectedState =
                    binding.actState.getText().toString();

            String[] boards =
                    boardMap.get(selectedState);

            if (boards != null) {

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                boards
                        );

                binding.actBoard.setAdapter(adapter);

                binding.actBoard.setText("", false);
            }

            updateSummary();
        });

        binding.actBoard.setOnItemClickListener(
                (parent, view, position, id) -> updateSummary()
        );
    }
    /**
     * Live Summary Update
     */
    private void setupSummaryWatcher() {

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSummary();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.etConsumerNumber.addTextChangedListener(watcher);
        binding.etAmount.addTextChangedListener(watcher);
    }

    /**
     * Update Payment Summary Card
     */
    private void updateSummary() {

        String consumer =
                binding.etConsumerNumber.getText().toString().trim();

        String board =
                binding.actBoard.getText().toString().trim();

        String state =
                binding.actState.getText().toString().trim();

        String amount =
                binding.etAmount.getText().toString().trim();

        binding.tvConsumer.setText(
                consumer.isEmpty() ? "-" : consumer
        );

        binding.tvBoard.setText(
                board.isEmpty() ? "-" : board
        );

        binding.tvState.setText(
                state.isEmpty() ? "-" : state
        );

        if (amount.isEmpty()) {
            binding.tvAmount.setText("₹0");
        } else {
            binding.tvAmount.setText("₹" + amount);
        }
    }

    /**
     * Validate User Input
     */
    private void validateInput() {

        consumerNumber =
                binding.etConsumerNumber.getText().toString().trim();

        board =
                binding.actBoard.getText().toString().trim();

        state =
                binding.actState.getText().toString().trim();

        String amountText =
                binding.etAmount.getText().toString().trim();

        binding.layoutConsumerNumber.setError(null);
        binding.layoutBoard.setError(null);
        binding.layoutState.setError(null);
        binding.layoutAmount.setError(null);

        if (TextUtils.isEmpty(consumerNumber)) {

            binding.layoutConsumerNumber
                    .setError("Enter Consumer Number");

            return;
        }

        if (consumerNumber.length() < 8) {

            binding.layoutConsumerNumber
                    .setError("Consumer Number must be at least 8 digits");

            return;
        }

        if (TextUtils.isEmpty(state)) {

            binding.layoutState
                    .setError("Select State");

            return;
        }

        if (TextUtils.isEmpty(board)) {

            binding.layoutBoard
                    .setError("Select Water Board");

            return;
        }

        if (TextUtils.isEmpty(amountText)) {

            binding.layoutAmount
                    .setError("Enter Bill Amount");

            return;
        }

        try {

            amount = new BigDecimal(amountText);

        } catch (Exception e) {

            binding.layoutAmount
                    .setError("Invalid Amount");

            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            binding.layoutAmount
                    .setError("Amount should be greater than zero");

            return;
        }

        showConfirmationDialog();
    }

    /**
     * Confirmation Dialog
     */
    private void showConfirmationDialog() {

        String message =
                "Please verify the payment details.\n\n"
                        + "Consumer Number : " + consumerNumber
                        + "\nState : " + state
                        + "\nWater Board : " + board
                        + "\nAmount : ₹" + amount
                        + "\n\nDo you want to continue?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Water Bill Payment")
                .setMessage(message)
                .setPositiveButton("Proceed", (dialog, which) -> {

                    Intent intent =
                            new Intent(
                                    WaterBillActivity.this,
                                    VerifyTransactionPinActivity.class
                            );

                    pinLauncher.launch(intent);

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    /**
     * Call Water Bill Payment API
     */
    private void payWaterBill() {

        WaterBillRequest request = new WaterBillRequest(
                consumerNumber,
                board,
                state,
                amount
        );

        repository.payWaterBill(request)
                .enqueue(new Callback<WaterBillResponse>() {

                    @Override
                    public void onResponse(
                            Call<WaterBillResponse> call,
                            Response<WaterBillResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            showSuccessDialog(
                                    response.body().getPaymentId()
                            );

                        } else {

                            String message = "Payment Failed";

                            if (response.body() != null
                                    && response.body().getMessage() != null) {

                                message = response.body().getMessage();
                            }

                            Toast.makeText(
                                    WaterBillActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<WaterBillResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                WaterBillActivity.this,
                                "Network Error\n" + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    /**
     * Payment Success Dialog
     */
    private void showSuccessDialog(String paymentId) {

        String message =
                "Water Bill Paid Successfully.\n\n"
                        + "Consumer Number : " + consumerNumber
                        + "\nState : " + state
                        + "\nBoard : " + board
                        + "\nAmount : ₹" + amount
                        + "\n\nPayment ID : " + paymentId;

        new AlertDialog.Builder(this)
                .setTitle("Payment Successful")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Done", (dialog, which) -> {

                    dialog.dismiss();

                    finish();

                })
                .show();
    }

}