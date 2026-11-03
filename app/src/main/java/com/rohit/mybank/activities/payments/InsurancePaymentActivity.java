package com.rohit.mybank.activities.payments;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.databinding.ActivityInsurancePaymentBinding;
import com.rohit.mybank.model.insurance.InsurancePaymentRequest;
import com.rohit.mybank.model.insurance.InsurancePaymentResponse;
import com.rohit.mybank.repository.InsuranceRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsurancePaymentActivity extends AppCompatActivity {

    //====================================================
    // View Binding
    //====================================================

    private ActivityInsurancePaymentBinding binding;

    //====================================================
    // Repository
    //====================================================

    private InsuranceRepository repository;

    //====================================================
    // Selected Values
    //====================================================

    private String selectedCompany = "";

    private String selectedPolicyType = "";

    private String policyNumber = "";

    private double premiumAmount;

    //====================================================
    // Insurance Companies
    //====================================================

    private final List<String> companies = Arrays.asList(

            "LIC",

            "HDFC Life",

            "SBI Life",

            "ICICI Prudential",

            "Max Life",

            "Tata AIA",

            "Bajaj Allianz",

            "Aditya Birla Sun Life",

            "Kotak Life",

            "PNB MetLife"

    );

    //====================================================
    // Policy Types
    //====================================================

    private final List<String> policyTypes = Arrays.asList(

            "Life Insurance",

            "Health Insurance",

            "Motor Insurance",

            "Term Insurance",

            "Travel Insurance"

    );

    //====================================================
    // Transaction PIN Launcher
    //====================================================

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),

                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            payInsurance();

                        } else {

                            Toast.makeText(

                                    this,

                                    "Payment cancelled.",

                                    Toast.LENGTH_SHORT

                            ).show();

                        }

                    });

    //====================================================
    // onCreate
    //====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityInsurancePaymentBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        repository = new InsuranceRepository(this);

        setupCompanyDropdown();

        setupPolicyTypeDropdown();

        setupListeners();

        updateSummary();

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
    // =====================================================
// Company Dropdown
// =====================================================

    private void setupCompanyDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_dropdown_item_1line,

                        companies

                );

        binding.actCompany.setAdapter(adapter);

        binding.actCompany.setOnItemClickListener(

                (parent, view, position, id) -> {

                    selectedCompany = companies.get(position);

                    updateSummary();

                }

        );

    }
    // =====================================================
// Policy Type Dropdown
// =====================================================

    private void setupPolicyTypeDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_dropdown_item_1line,

                        policyTypes

                );

        binding.actPolicyType.setAdapter(adapter);

        binding.actPolicyType.setOnItemClickListener(

                (parent, view, position, id) -> {

                    selectedPolicyType = policyTypes.get(position);

                    updateSummary();

                }

        );

    }
    // =====================================================
// Setup Listeners
// =====================================================

    private void setupListeners() {

        binding.etPolicyNumber.addTextChangedListener(
                summaryWatcher
        );

        binding.etAmount.addTextChangedListener(
                summaryWatcher
        );

        binding.btnPayInsurance.setOnClickListener(v -> {

            if (validateInput()) {

                showConfirmationDialog();

            }

        });

    }
    // =====================================================
// Summary Watcher
// =====================================================

    private final TextWatcher summaryWatcher =
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

                    updateSummary();

                }

                @Override
                public void afterTextChanged(
                        Editable s
                ) {

                }

            };
    // =====================================================
// Update Summary
// =====================================================

    private void updateSummary() {

        policyNumber = "";

        if (binding.etPolicyNumber.getText() != null) {

            policyNumber =

                    binding.etPolicyNumber

                            .getText()

                            .toString()

                            .trim();

        }

        String amountText = "";

        if (binding.etAmount.getText() != null) {

            amountText =

                    binding.etAmount

                            .getText()

                            .toString()

                            .trim();

        }

        binding.tvCompany.setText(

                "Company : " +

                        (TextUtils.isEmpty(selectedCompany)
                                ? "-"
                                : selectedCompany)

        );

        binding.tvPolicy.setText(

                "Policy No : " +

                        (TextUtils.isEmpty(policyNumber)
                                ? "-"
                                : policyNumber)

        );

        binding.tvType.setText(

                "Policy Type : " +

                        (TextUtils.isEmpty(selectedPolicyType)
                                ? "-"
                                : selectedPolicyType)

        );

        binding.tvAmount.setText(

                "Amount : ₹" +

                        (TextUtils.isEmpty(amountText)
                                ? "0"
                                : amountText)

        );

    }
    // =====================================================
// Validate Input
// =====================================================

    private boolean validateInput() {

        clearErrors();

        policyNumber = "";

        if (binding.etPolicyNumber.getText() != null) {

            policyNumber =
                    binding.etPolicyNumber
                            .getText()
                            .toString()
                            .trim();

        }

        String amountText = "";

        if (binding.etAmount.getText() != null) {

            amountText =
                    binding.etAmount
                            .getText()
                            .toString()
                            .trim();

        }

        // Policy Number

        if (TextUtils.isEmpty(policyNumber)) {

            binding.etPolicyNumber.setError(
                    "Enter Policy Number"
            );

            binding.etPolicyNumber.requestFocus();

            return false;

        }

        if (policyNumber.length() < 6) {

            binding.etPolicyNumber.setError(
                    "Invalid Policy Number"
            );

            binding.etPolicyNumber.requestFocus();

            return false;

        }

        // Company

        if (TextUtils.isEmpty(selectedCompany)) {

            binding.actCompany.setError(
                    "Select Insurance Company"
            );

            binding.actCompany.requestFocus();

            return false;

        }

        // Policy Type

        if (TextUtils.isEmpty(selectedPolicyType)) {

            binding.actPolicyType.setError(
                    "Select Policy Type"
            );

            binding.actPolicyType.requestFocus();

            return false;

        }

        // Amount

        if (TextUtils.isEmpty(amountText)) {

            binding.etAmount.setError(
                    "Enter Premium Amount"
            );

            binding.etAmount.requestFocus();

            return false;

        }

        try {

            premiumAmount = Double.parseDouble(amountText);

        } catch (Exception e) {

            binding.etAmount.setError(
                    "Invalid Premium Amount"
            );

            binding.etAmount.requestFocus();

            return false;

        }

        if (premiumAmount <= 0) {

            binding.etAmount.setError(
                    "Amount must be greater than zero"
            );

            binding.etAmount.requestFocus();

            return false;

        }

        return true;

    }
    // =====================================================
// Confirmation Dialog
// =====================================================

    private void showConfirmationDialog() {

        String message =

                "Insurance Company : " + selectedCompany +

                        "\n\nPolicy Number : " + policyNumber +

                        "\n\nPolicy Type : " + selectedPolicyType +

                        "\n\nPremium Amount : ₹" + premiumAmount +

                        "\n\nProceed with payment?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Insurance Payment")

                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(

                        "Continue",

                        (dialog, which) ->

                                new PaymentSecurityHelper(

                                        InsurancePaymentActivity.this,

                                        pinLauncher,

                                        new PaymentCallback() {

                                            @Override
                                            public void onSuccess() {

                                                payInsurance();

                                            }

                                        }

                                ).verifyPayment()

                )

                .show();

    }
    // =====================================================
// Clear Errors
// =====================================================

    private void clearErrors() {

        binding.etPolicyNumber.setError(null);

        binding.etAmount.setError(null);

        binding.actCompany.setError(null);

        binding.actPolicyType.setError(null);

    }
    // =====================================================
// Pay Insurance
// =====================================================

    private void payInsurance() {

        binding.btnPayInsurance.setEnabled(false);

        binding.btnPayInsurance.setText("Processing...");

        InsurancePaymentRequest request =
                new InsurancePaymentRequest();

        request.setPolicyNumber(policyNumber);

        request.setInsuranceCompany(selectedCompany);

        request.setPolicyType(selectedPolicyType);

        request.setAmount(
                BigDecimal.valueOf(premiumAmount)
        );

        repository.payInsurance(request)

                .enqueue(

                        new Callback<InsurancePaymentResponse>() {

                            @Override
                            public void onResponse(

                                    Call<InsurancePaymentResponse> call,

                                    Response<InsurancePaymentResponse> response) {

                                binding.btnPayInsurance.setEnabled(true);

                                binding.btnPayInsurance.setText(
                                        "PAY INSURANCE PREMIUM"
                                );

                                if (!response.isSuccessful()) {
                                    showPaymentErrorDialog(extractPaymentError(response));
                                    return;
                                }

                                if (response.body() == null) {
                                    showPaymentErrorDialog(
                                            "Unable to process insurance payment. Please try again."
                                    );
                                    return;
                                }

                                InsurancePaymentResponse insuranceResponse =
                                        response.body();

                                if (insuranceResponse.isSuccess()) {

                                    showSuccessDialog(
                                            insuranceResponse
                                    );

                                } else {

                                    showPaymentErrorDialog(
                                            normalizePaymentError(insuranceResponse.getMessage())
                                    );

                                }

                            }

                            @Override
                            public void onFailure(

                                    Call<InsurancePaymentResponse> call,

                                    Throwable t) {

                                binding.btnPayInsurance.setEnabled(true);

                                binding.btnPayInsurance.setText(
                                        "PAY INSURANCE PREMIUM"
                                );

                                if (call.isCanceled()) {
                                    return;
                                }

                                showPaymentErrorDialog(
                                        "Unable to connect to the server. Please try again."
                                );

                            }

                        }

                );

    }

    // =====================================================
    // Extract error message from unsuccessful HTTP response
    // =====================================================

    private String extractPaymentError(Response<InsurancePaymentResponse> response) {

        String message = null;

        try {
            if (response.errorBody() != null) {
                message = extractMessageFromBody(response.errorBody().string());
            }
        } catch (java.io.IOException ignored) {
            // Use safe fallback below.
        }

        String normalized = normalizePaymentError(message);

        if (!"Unable to process insurance payment. Please try again."
                .equals(normalized)) {
            return normalized;
        }

        if (response.code() == 401 || response.code() == 403) {
            return "Your session may have expired. Please log in again.";
        }

        return "Unable to process insurance payment. Please try again.";
    }

    // =====================================================
    // Extract JSON or plain-text error message
    // =====================================================

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
            // Use safe fallback below.
        }

        return null;
    }

    // =====================================================
    // Normalize payment errors
    // =====================================================

    private String normalizePaymentError(String message) {

        if (TextUtils.isEmpty(message)) {
            return "Unable to process insurance payment. Please try again.";
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
            return "Unable to process insurance payment. Please try again.";
        }

        return message.trim();
    }

    // =====================================================
    // Persistent payment error dialog
    // =====================================================

    private void showPaymentErrorDialog(String message) {

        if (isFinishing() || isDestroyed()) {
            return;
        }

        String safeMessage = normalizePaymentError(message);

        new AlertDialog.Builder(this)
                .setTitle("Payment Failed")
                .setMessage(safeMessage)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // =====================================================
// Payment Success
// =====================================================

    private void showSuccessDialog(
            InsurancePaymentResponse response
    ) {

        String paymentId = response.getPaymentId();

        if (paymentId == null) {

            paymentId = "N/A";

        }

        String receipt =

                "✅ Insurance Premium Paid Successfully"

                        + "\n\nInsurance Company : "
                        + selectedCompany

                        + "\n\nPolicy Number : "
                        + policyNumber

                        + "\n\nPolicy Type : "
                        + selectedPolicyType

                        + "\n\nPremium Amount : ₹"
                        + String.format("%.2f", premiumAmount)

                        + "\n\nPayment ID : "
                        + paymentId

                        + "\n\nStatus : SUCCESS";

        new AlertDialog.Builder(this)

                .setTitle("Payment Successful")

                .setMessage(receipt)

                .setCancelable(false)

                .setPositiveButton(

                        "Done",

                        (dialog, which) -> {

                            setResult(RESULT_OK);

                            finish();

                        }

                )

                .show();

    }
    // =====================================================
// Toolbar Back
// =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }
}
