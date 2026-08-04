package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.databinding.ActivityDthRechargeBinding;
import com.rohit.mybank.model.dth.DthRechargeRequest;
import com.rohit.mybank.model.dth.DthRechargeResponse;
import com.rohit.mybank.repository.DthRechargeRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DthRechargeActivity extends AppCompatActivity {

    private ActivityDthRechargeBinding binding;

    private DthRechargeRepository repository;

    private final Map<String, Double> operatorAmount = new HashMap<>();

    private String subscriberId;
    private String operator;
    private double amount;

    private final ActivityResultLauncher<Intent> verifyPinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            rechargeDth();

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

        binding = ActivityDthRechargeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new DthRechargeRepository(this);

        setupOperators();

        setupListeners();

        updateSummary();
    }

    private void setupOperators() {

        operatorAmount.put("Tata Play", 350.0);
        operatorAmount.put("Airtel Digital TV", 320.0);
        operatorAmount.put("Dish TV", 300.0);
        operatorAmount.put("Sun Direct", 280.0);
        operatorAmount.put("d2h", 330.0);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        operatorAmount.keySet().toArray(new String[0]));

        binding.actOperator.setAdapter(adapter);

        binding.actOperator.setOnItemClickListener((parent, view, position, id) -> {

            String selected =
                    binding.actOperator.getText().toString();

            Double value =
                    operatorAmount.get(selected);

            if (value != null) {

                binding.etAmount.setText(
                        String.valueOf(value.intValue())
                );

            }

            updateSummary();

        });

    }
    private void setupListeners() {

        binding.etSubscriberId.addTextChangedListener(textWatcher);

        binding.etAmount.addTextChangedListener(textWatcher);

        binding.actOperator.addTextChangedListener(textWatcher);

        binding.btnRecharge.setOnClickListener(v -> {

            if (validateInput()) {

                showConfirmationDialog();

            }

        });

    }

    private final TextWatcher textWatcher = new TextWatcher() {

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

    private void updateSummary() {

        binding.tvSubscriber.setText(
                binding.etSubscriberId.getText().toString().trim()
        );

        binding.tvOperator.setText(
                binding.actOperator.getText().toString().trim()
        );

        String amountText =
                binding.etAmount.getText().toString().trim();

        if (amountText.isEmpty()) {

            binding.tvAmount.setText("₹0");

        } else {

            binding.tvAmount.setText("₹" + amountText);

        }

    }

    private boolean validateInput() {

        subscriberId =
                binding.etSubscriberId.getText()
                        .toString()
                        .trim();

        operator =
                binding.actOperator.getText()
                        .toString()
                        .trim();

        String amountText =
                binding.etAmount.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(subscriberId)) {

            binding.etSubscriberId.setError(
                    "Enter Subscriber ID"
            );

            return false;
        }

        if (TextUtils.isEmpty(operator)) {

            binding.actOperator.setError(
                    "Select Operator"
            );

            return false;
        }

        if (TextUtils.isEmpty(amountText)) {

            binding.etAmount.setError(
                    "Enter Recharge Amount"
            );

            return false;
        }

        try {

            amount = Double.parseDouble(amountText);

        } catch (Exception e) {

            binding.etAmount.setError(
                    "Invalid Amount"
            );

            return false;
        }

        if (amount < 100) {

            binding.etAmount.setError(
                    "Minimum Recharge ₹100"
            );

            return false;
        }

        return true;
    }

    private void showConfirmationDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Confirm Recharge")

                .setMessage(
                        "Operator : "
                                + operator
                                + "\n\nSubscriber : "
                                + subscriberId
                                + "\n\nAmount : ₹"
                                + amount
                )

                .setPositiveButton(
                        "Continue",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            this,
                                            VerifyTransactionPinActivity.class
                                    );

                            verifyPinLauncher.launch(intent);

                        })

                .setNegativeButton(
                        "Cancel",
                        null)

                .show();

    }
    // ===========================================
    // Recharge DTH
    // ===========================================

    private void rechargeDth() {

        binding.btnRecharge.setEnabled(false);
        binding.btnRecharge.setText("Processing...");

        DthRechargeRequest request =
                new DthRechargeRequest(
                        subscriberId,
                        operator,
                        BigDecimal.valueOf(amount)
                );

        repository.recharge(request)
                .enqueue(new Callback<DthRechargeResponse>() {

                    @Override
                    public void onResponse(
                            Call<DthRechargeResponse> call,
                            Response<DthRechargeResponse> response) {

                        binding.btnRecharge.setEnabled(true);
                        binding.btnRecharge.setText("RECHARGE DTH");

                        if (response.isSuccessful()
                                && response.body() != null) {

                            DthRechargeResponse rechargeResponse =
                                    response.body();

                            if (rechargeResponse.isSuccess()) {

                                showSuccessDialog(
                                        rechargeResponse.getPaymentId(),
                                        rechargeResponse.getMessage()
                                );

                            } else {

                                Toast.makeText(
                                        DthRechargeActivity.this,
                                        rechargeResponse.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            }

                        } else {

                            Toast.makeText(
                                    DthRechargeActivity.this,
                                    "Recharge failed.",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<DthRechargeResponse> call,
                            Throwable t) {

                        binding.btnRecharge.setEnabled(true);
                        binding.btnRecharge.setText("RECHARGE DTH");

                        Toast.makeText(
                                DthRechargeActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    // ===========================================
    // Success Dialog
    // ===========================================

    private void showSuccessDialog(
            String paymentId,
            String message) {

        String successMessage =
                "DTH Recharge Successful"
                        + "\n\nPayment ID : "
                        + paymentId
                        + "\n\n"
                        + message;

        new AlertDialog.Builder(this)
                .setTitle("Recharge Successful")
                .setMessage(successMessage)
                .setCancelable(false)

                .setPositiveButton(
                        "OK",
                        (dialog, which) -> {

                            dialog.dismiss();
                            finish();

                        })

                .show();

    }

}