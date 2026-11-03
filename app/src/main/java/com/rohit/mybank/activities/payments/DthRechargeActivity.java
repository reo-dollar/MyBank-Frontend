package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.databinding.ActivityDthRechargeBinding;
import com.rohit.mybank.model.dth.DthRechargeRequest;
import com.rohit.mybank.model.dth.DthRechargeResponse;
import com.rohit.mybank.repository.DthRechargeRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DthRechargeActivity extends AppCompatActivity {

    // -------------------------------------------------------
    // View Binding
    // -------------------------------------------------------

    private ActivityDthRechargeBinding binding;

    // -------------------------------------------------------
    // Repository
    // -------------------------------------------------------

    private DthRechargeRepository repository;

    // -------------------------------------------------------
    // Recharge Data
    // -------------------------------------------------------

    private final Map<String, Double> operatorAmount = new HashMap<>();

    private String subscriberId = "";
    private String operator = "";
    private double amount = 0.0;

    // -------------------------------------------------------
    // Transaction PIN Launcher
    // -------------------------------------------------------

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() != RESULT_OK) {
                            Toast.makeText(
                                    DthRechargeActivity.this,
                                    "Recharge cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if (result.getData() == null) {
                            return;
                        }

                        boolean verified = result.getData().getBooleanExtra(
                                VerifyTransactionPinActivity.EXTRA_PIN_VERIFIED,
                                false
                        );

                        if (verified) {
                            performRecharge();
                        } else {
                            Toast.makeText(
                                    DthRechargeActivity.this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
    // -------------------------------------------------------
    // Activity Lifecycle
    // -------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityDthRechargeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Repository
        repository = new DthRechargeRepository(this);

        // Load DTH Operators
        setupOperators();

        // Register Listeners
        setupListeners();

        // Display Initial Summary
        updateSummary();

        // Handle Back Button
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        finish();
                    }
                }
        );
    }
    // -------------------------------------------------------
    // Initialize DTH Operators
    // -------------------------------------------------------

    private void setupOperators() {

        operatorAmount.clear();

        operatorAmount.put("Tata Play", 350.00);
        operatorAmount.put("Airtel Digital TV", 320.00);
        operatorAmount.put("Dish TV", 300.00);
        operatorAmount.put("Sun Direct", 280.00);
        operatorAmount.put("d2h", 330.00);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                operatorAmount.keySet().toArray(new String[0])
        );

        binding.actOperator.setAdapter(adapter);

        binding.actOperator.setThreshold(1);

        binding.actOperator.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selectedOperator = binding.actOperator
                            .getText()
                            .toString()
                            .trim();

                    Double defaultAmount =
                            operatorAmount.get(selectedOperator);

                    if (defaultAmount != null) {

                        binding.etAmount.setText(
                                String.valueOf(defaultAmount.intValue())
                        );

                        binding.etAmount.setSelection(
                                binding.etAmount.getText().length()
                        );
                    }

                    updateSummary();
                }
        );
    }
    // -------------------------------------------------------
    // Register UI Listeners
    // -------------------------------------------------------

    private void setupListeners() {

        // Subscriber ID
        binding.etSubscriberId.addTextChangedListener(summaryWatcher);

        // Recharge Amount
        binding.etAmount.addTextChangedListener(summaryWatcher);

        // DTH Operator
        binding.actOperator.addTextChangedListener(summaryWatcher);

        // Recharge Button
        binding.btnRecharge.setOnClickListener(v -> validateInput());
    }

    // -------------------------------------------------------
    // Summary TextWatcher
    // -------------------------------------------------------

    private final TextWatcher summaryWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s,
                                      int start,
                                      int count,
                                      int after) {
            // No action required
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
            // No action required
        }
    };
    // -------------------------------------------------------
    // Update Recharge Summary
    // -------------------------------------------------------

    private void updateSummary() {

        // Subscriber ID
        String subscriber = "";

        if (binding.etSubscriberId.getText() != null) {
            subscriber = binding.etSubscriberId
                    .getText()
                    .toString()
                    .trim();
        }

        // Operator
        String selectedOperator = "";

        if (binding.actOperator.getText() != null) {
            selectedOperator = binding.actOperator
                    .getText()
                    .toString()
                    .trim();
        }

        // Recharge Amount
        String amountText = "";

        if (binding.etAmount.getText() != null) {
            amountText = binding.etAmount
                    .getText()
                    .toString()
                    .trim();
        }

        // Subscriber Summary
        if (subscriber.isEmpty()) {
            binding.tvSubscriber.setText("-");
        } else {
            binding.tvSubscriber.setText(subscriber);
        }

        // Operator Summary
        if (selectedOperator.isEmpty()) {
            binding.tvOperator.setText("-");
        } else {
            binding.tvOperator.setText(selectedOperator);
        }

        // Amount Summary
        if (amountText.isEmpty()) {
            binding.tvAmount.setText("₹0.00");
        } else {
            binding.tvAmount.setText("₹" + amountText);
        }

        // Enable Recharge Button only when all fields have values
        boolean enableRecharge =
                !subscriber.isEmpty()
                        && !selectedOperator.isEmpty()
                        && !amountText.isEmpty();

        binding.btnRecharge.setEnabled(enableRecharge);
    }
    // -------------------------------------------------------
    // Validate User Input
    // -------------------------------------------------------

    private void validateInput() {

        // Clear previous errors
        binding.etSubscriberId.setError(null);
        binding.actOperator.setError(null);
        binding.etAmount.setError(null);

        // -----------------------------
        // Subscriber ID
        // -----------------------------

        subscriberId = binding.etSubscriberId.getText() == null
                ? ""
                : binding.etSubscriberId.getText().toString().trim();

        if (TextUtils.isEmpty(subscriberId)) {
            binding.etSubscriberId.setError("Enter Subscriber ID");
            binding.etSubscriberId.requestFocus();
            return;
        }

        if (subscriberId.length() < 6) {
            binding.etSubscriberId.setError("Subscriber ID must be at least 6 characters");
            binding.etSubscriberId.requestFocus();
            return;
        }

        // -----------------------------
        // Operator
        // -----------------------------

        operator = binding.actOperator.getText() == null
                ? ""
                : binding.actOperator.getText().toString().trim();

        if (TextUtils.isEmpty(operator)) {
            binding.actOperator.setError("Select a DTH operator");
            binding.actOperator.requestFocus();
            return;
        }

        if (!operatorAmount.containsKey(operator)) {
            binding.actOperator.setError("Please select a valid operator");
            binding.actOperator.requestFocus();
            return;
        }

        // -----------------------------
        // Amount
        // -----------------------------

        String amountText = binding.etAmount.getText() == null
                ? ""
                : binding.etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {
            binding.etAmount.setError("Enter Recharge Amount");
            binding.etAmount.requestFocus();
            return;
        }

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            binding.etAmount.setError("Invalid Recharge Amount");
            binding.etAmount.requestFocus();
            return;
        }

        if (amount < 100) {
            binding.etAmount.setError("Minimum recharge amount is ₹100");
            binding.etAmount.requestFocus();
            return;
        }

        if (amount > 10000) {
            binding.etAmount.setError("Maximum recharge amount is ₹10,000");
            binding.etAmount.requestFocus();
            return;
        }

        // Proceed to confirmation
        showConfirmationDialog();
    }
    // -------------------------------------------------------
    // Show Recharge Confirmation Dialog
    // -------------------------------------------------------

    private void showConfirmationDialog() {

        StringBuilder message = new StringBuilder();

        message.append("Please verify the recharge details before proceeding.\n\n");
        message.append("Subscriber ID : ").append(subscriberId).append("\n");
        message.append("Operator      : ").append(operator).append("\n");
        message.append("Amount        : ₹").append(String.format("%.2f", amount)).append("\n\n");
        message.append("Do you want to continue?");

        new AlertDialog.Builder(this)
                .setTitle("Confirm DTH Recharge")
                .setMessage(message.toString())
                .setCancelable(false)

                .setNegativeButton("Cancel",
                        (dialog, which) -> dialog.dismiss())

                .setPositiveButton("Continue",
                        (dialog, which) -> {

                            PaymentSecurityHelper securityHelper =
                                    new PaymentSecurityHelper(
                                            DthRechargeActivity.this,
                                            pinLauncher,
                                            new PaymentCallback() {

                                                @Override
                                                public void onSuccess() {
                                                    performRecharge();
                                                }
                                            });

                            securityHelper.verifyPayment();
                        })

                .show();
    }
    // -------------------------------------------------------
    // Perform DTH Recharge
    // -------------------------------------------------------

    private void performRecharge() {

        // Prevent multiple clicks
        binding.btnRecharge.setEnabled(false);
        binding.btnRecharge.setText("Processing...");

        // Prepare Request
        DthRechargeRequest request = new DthRechargeRequest();
        request.setSubscriberId(subscriberId);
        request.setOperator(operator);
        request.setAmount(BigDecimal.valueOf(amount));

        repository.recharge(request).enqueue(new Callback<DthRechargeResponse>() {

            @Override
            public void onResponse(Call<DthRechargeResponse> call,
                                   Response<DthRechargeResponse> response) {

                restoreRechargeButton();

                if (!response.isSuccessful()) {
                    showPaymentErrorDialog(extractRechargeError(response));
                    return;
                }

                DthRechargeResponse rechargeResponse = response.body();

                if (rechargeResponse == null) {
                    showPaymentErrorDialog(
                            "Unable to process DTH recharge. Please try again."
                    );
                    return;
                }

                if (rechargeResponse.isSuccess()) {

                    showSuccessDialog(rechargeResponse);

                } else {

                    String message = rechargeResponse.getMessage();

                    if (TextUtils.isEmpty(message)) {
                        message = "Recharge failed.";
                    }

                    showPaymentErrorDialog(normalizePaymentError(message));
                }
            }

            @Override
            public void onFailure(Call<DthRechargeResponse> call,
                                  Throwable throwable) {

                restoreRechargeButton();

                if (call.isCanceled()) {
                    return;
                }

                showPaymentErrorDialog(
                        "Unable to connect to the server. Please try again."
                );
            }
        });
    }


    // -------------------------------------------------------
    // Extract error message from unsuccessful HTTP response
    // -------------------------------------------------------

    private String extractRechargeError(Response<DthRechargeResponse> response) {

        String message = null;

        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                message = extractMessageFromBody(body);
            }
        } catch (java.io.IOException ignored) {
            // Use a safe fallback if the error body cannot be read.
        }

        String normalized = normalizePaymentError(message);

        if (!"Unable to process DTH recharge. Please try again."
                .equals(normalized)) {
            return normalized;
        }

        if (response.code() == 401 || response.code() == 403) {
            return "Your session may have expired. Please log in again.";
        }

        return "Unable to process DTH recharge. Please try again.";
    }

    // -------------------------------------------------------
    // Read JSON or plain-text backend error
    // -------------------------------------------------------

    private String extractMessageFromBody(String body) {

        if (TextUtils.isEmpty(body)) {
            return null;
        }

        String trimmed = body.trim();

        if (!trimmed.startsWith("{")) {
            return trimmed;
        }

        try {
            org.json.JSONObject json = new org.json.JSONObject(trimmed);

            String[] keys = {"message", "error", "detail", "description"};

            for (String key : keys) {
                if (json.has(key) && !json.isNull(key)) {
                    String value = json.optString(key, "").trim();
                    if (!value.isEmpty()) {
                        return value;
                    }
                }
            }
        } catch (org.json.JSONException ignored) {
            // Fall back to a generic safe message.
        }

        return null;
    }

    // -------------------------------------------------------
    // Normalize business errors for the user
    // -------------------------------------------------------

    private String normalizePaymentError(String message) {

        if (TextUtils.isEmpty(message)) {
            return "Unable to process DTH recharge. Please try again.";
        }

        String normalized = message.trim().toLowerCase(java.util.Locale.ROOT);

        if (normalized.contains("insufficient balance")
                || normalized.contains("insufficient funds")
                || normalized.contains("not enough balance")
                || normalized.contains("not enough funds")
                || normalized.contains("low balance")) {
            return "Insufficient balance";
        }

        if (normalized.contains("internal server error")
                || normalized.contains("server error")
                || normalized.contains("stacktrace")
                || normalized.contains("exception")
                || normalized.matches(".*\\b5\\d\\d\\b.*")) {
            return "Unable to process DTH recharge. Please try again.";
        }

        return message.trim();
    }

    // -------------------------------------------------------
    // Persistent payment error dialog
    // -------------------------------------------------------

    private void showPaymentErrorDialog(String message) {

        if (isFinishing() || isDestroyed()) {
            return;
        }

        String safeMessage = normalizePaymentError(message);

        new AlertDialog.Builder(this)
                .setTitle("Recharge Failed")
                .setMessage(safeMessage)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // -------------------------------------------------------
    // Restore Recharge Button
    // -------------------------------------------------------

    private void restoreRechargeButton() {

        binding.btnRecharge.setEnabled(true);
        binding.btnRecharge.setText("RECHARGE DTH");
    }
    // -------------------------------------------------------
    // Show Recharge Success Dialog
    // -------------------------------------------------------

    private void showSuccessDialog(DthRechargeResponse response) {

        String paymentId = response.getPaymentId();

        if (paymentId == null || paymentId.trim().isEmpty()) {
            paymentId = "N/A";
        }

        StringBuilder message = new StringBuilder();

        message.append("✅ Recharge Successful\n\n");
        message.append("Subscriber ID : ")
                .append(subscriberId)
                .append("\n\n");

        message.append("Operator : ")
                .append(operator)
                .append("\n\n");

        message.append("Recharge Amount : ₹")
                .append(String.format("%.2f", amount))
                .append("\n\n");

        message.append("Payment ID : ")
                .append(paymentId)
                .append("\n\n");

        message.append("Status : SUCCESS");

        new AlertDialog.Builder(this)
                .setTitle("Recharge Successful")
                .setMessage(message.toString())
                .setCancelable(false)

                .setPositiveButton("Done", (dialog, which) -> {

                    dialog.dismiss();

                    setResult(RESULT_OK);

                    finish();
                })

                .show();
    }
}
