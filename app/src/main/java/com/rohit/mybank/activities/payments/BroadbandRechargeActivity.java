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
import androidx.appcompat.app.AppCompatActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.utils.PaymentSecurityHelper;
import com.rohit.mybank.databinding.ActivityBroadbandRechargeBinding;
import com.rohit.mybank.model.broadband.BroadbandRechargeRequest;
import com.rohit.mybank.model.broadband.BroadbandRechargeResponse;
import com.rohit.mybank.repository.BroadbandRechargeRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BroadbandRechargeActivity extends AppCompatActivity {

    private ActivityBroadbandRechargeBinding binding;

    private BroadbandRechargeRepository repository;

    // ===========================================
    // Plans
    // ===========================================

    private final Map<String, String[]> providerPlans = new HashMap<>();

    private final Map<String, Double> planAmount = new HashMap<>();

    // ===========================================
    // Variables
    // ===========================================

    private String customerId;
    private String provider;
    private String selectedPlan;

    private double amount;

    // ===========================================
    // Verify PIN Launcher
    // ===========================================

    private final ActivityResultLauncher<Intent> verifyPinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            rechargeBroadband();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

    // ===========================================
    // onCreate
    // ===========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityBroadbandRechargeBinding.inflate(
                        getLayoutInflater());

        setContentView(binding.getRoot());

        repository = new BroadbandRechargeRepository(this);

        binding.actProvider.setKeyListener(null);
        binding.actPlan.setKeyListener(null);

        setupProviders();

        setupListeners();

        updateSummary();
    }
    // =====================================================
// Setup Providers
// =====================================================

    private void setupProviders() {

        providerPlans.put(
                "JioFiber",
                new String[]{
                        "30 Mbps",
                        "100 Mbps",
                        "300 Mbps",
                        "1 Gbps"
                });

        providerPlans.put(
                "Airtel Xstream",
                new String[]{
                        "40 Mbps",
                        "100 Mbps",
                        "200 Mbps",
                        "1 Gbps"
                });

        providerPlans.put(
                "BSNL Bharat Fiber",
                new String[]{
                        "30 Mbps",
                        "60 Mbps",
                        "100 Mbps",
                        "300 Mbps"
                });

        providerPlans.put(
                "ACT Fibernet",
                new String[]{
                        "50 Mbps",
                        "150 Mbps",
                        "300 Mbps",
                        "1 Gbps"
                });

        providerPlans.put(
                "Tata Play Fiber",
                new String[]{
                        "50 Mbps",
                        "100 Mbps",
                        "300 Mbps",
                        "1 Gbps"
                });

        ArrayAdapter<String> providerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        providerPlans.keySet().toArray(new String[0])
                );

        binding.actProvider.setAdapter(providerAdapter);

        setupPlanAmount();

        setupProviderListener();

    }
    // =====================================================
// Broadband Plan Amount Mapping
// =====================================================

    private void setupPlanAmount() {

        planAmount.put("30 Mbps", 399.0);

        planAmount.put("40 Mbps", 499.0);

        planAmount.put("50 Mbps", 599.0);

        planAmount.put("60 Mbps", 699.0);

        planAmount.put("100 Mbps", 799.0);

        planAmount.put("150 Mbps", 999.0);

        planAmount.put("200 Mbps", 1199.0);

        planAmount.put("300 Mbps", 1499.0);

        planAmount.put("1 Gbps", 2999.0);

    }
    // =====================================================
// Provider Selection
// =====================================================

    private void setupProviderListener() {

        binding.actProvider.setOnItemClickListener(

                (parent, view, position, id) -> {

                    provider =
                            binding.actProvider
                                    .getText()
                                    .toString();

                    String[] plans =
                            providerPlans.get(provider);

                    if (plans == null) {

                        return;

                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(

                                    this,

                                    android.R.layout
                                            .simple_dropdown_item_1line,

                                    plans

                            );

                    binding.actPlan.setText("");

                    binding.actPlan.setAdapter(adapter);

                    selectedPlan = "";

                    amount = 0;

                    updateSummary();

                }

        );

    }
    // =====================================================
// Setup Listeners
// =====================================================

    private void setupListeners() {

        setupPlanListener();

        binding.etCustomerId.addTextChangedListener(

                new TextWatcher() {

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

                        customerId = s.toString().trim();

                        updateSummary();

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                }

        );

        binding.btnRecharge.setOnClickListener(v -> {

            if (validateInput()) {

                showConfirmationDialog();

            }

        });

    }
    // =====================================================
// Plan Selection
// =====================================================

    private void setupPlanListener() {

        binding.actPlan.setOnItemClickListener(

                (parent, view, position, id) -> {

                    selectedPlan =
                            binding.actPlan
                                    .getText()
                                    .toString();

                    Double value =
                            planAmount.get(selectedPlan);

                    if (value == null) {

                        amount = 0;

                    } else {

                        amount = value;

                    }

                    updateSummary();

                }

        );

    }
    // =====================================================
// Update Summary
// =====================================================

    private void updateSummary() {

        binding.tvCustomer.setText(

                TextUtils.isEmpty(customerId)
                        ? "-"
                        : customerId

        );

        binding.tvProvider.setText(

                TextUtils.isEmpty(provider)
                        ? "-"
                        : provider

        );

        binding.tvPlan.setText(

                TextUtils.isEmpty(selectedPlan)
                        ? "-"
                        : selectedPlan

        );

        binding.tvAmount.setText(

                String.format("₹%.2f", amount)

        );

    }
    // =====================================================
// Validate Input
// =====================================================

    private boolean validateInput() {

        binding.etCustomerId.setError(null);
        binding.actProvider.setError(null);
        binding.actPlan.setError(null);

        customerId = "";

        if (binding.etCustomerId.getText() != null) {

            customerId =
                    binding.etCustomerId
                            .getText()
                            .toString()
                            .trim();

        }

        provider =
                binding.actProvider
                        .getText()
                        .toString()
                        .trim();

        selectedPlan =
                binding.actPlan
                        .getText()
                        .toString()
                        .trim();

        // Customer ID

        if (TextUtils.isEmpty(customerId)) {

            binding.etCustomerId.setError(
                    "Enter Customer ID"
            );

            binding.etCustomerId.requestFocus();

            return false;

        }

        if (customerId.length() < 6) {

            binding.etCustomerId.setError(
                    "Invalid Customer ID"
            );

            binding.etCustomerId.requestFocus();

            return false;

        }

        // Provider

        if (TextUtils.isEmpty(provider)) {

            binding.actProvider.setError(
                    "Select Broadband Provider"
            );

            binding.actProvider.requestFocus();

            return false;

        }

        // Plan

        if (TextUtils.isEmpty(selectedPlan)) {

            binding.actPlan.setError(
                    "Select Recharge Plan"
            );

            binding.actPlan.requestFocus();

            return false;

        }

        if (amount <= 0) {

            Toast.makeText(
                    this,
                    "Invalid Recharge Amount",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        return true;

    }
    // =====================================================
// Confirmation Dialog
// =====================================================

    private void showConfirmationDialog() {

        String message =

                "Customer ID : " + customerId +

                        "\n\nProvider : " + provider +

                        "\n\nPlan : " + selectedPlan +

                        "\n\nRecharge Amount : ₹" + amount +

                        "\n\nProceed with recharge?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Broadband Recharge")

                .setMessage(message)

                .setNegativeButton("Cancel", null)

                .setPositiveButton(

                        "Continue",

                        (dialog, which) -> {

                            new PaymentSecurityHelper(

                                    BroadbandRechargeActivity.this,

                                    verifyPinLauncher,

                                    () -> rechargeBroadband()

                            ).verifyPayment();

                        }

                )

                .show();
    }
    // =====================================================
// Recharge Broadband
// =====================================================

    private void rechargeBroadband() {

        binding.btnRecharge.setEnabled(false);

        binding.btnRecharge.setText("Processing...");

        BroadbandRechargeRequest request =
                new BroadbandRechargeRequest();

        request.setCustomerId(
                customerId
        );

        request.setProvider(
                provider
        );

        request.setAmount(
                BigDecimal.valueOf(amount)
        );

        repository.recharge(request)

                .enqueue(

                        new Callback<BroadbandRechargeResponse>() {

                            @Override
                            public void onResponse(

                                    Call<BroadbandRechargeResponse> call,

                                    Response<BroadbandRechargeResponse> response) {

                                binding.btnRecharge.setEnabled(true);

                                binding.btnRecharge.setText(
                                        "RECHARGE BROADBAND"
                                );

                                if (!response.isSuccessful()) {

                                    Toast.makeText(

                                            BroadbandRechargeActivity.this,

                                            "HTTP Error : "
                                                    + response.code(),

                                            Toast.LENGTH_LONG

                                    ).show();

                                    return;

                                }

                                if (response.body() == null) {

                                    Toast.makeText(

                                            BroadbandRechargeActivity.this,

                                            "Empty server response.",

                                            Toast.LENGTH_LONG

                                    ).show();

                                    return;

                                }

                                BroadbandRechargeResponse rechargeResponse =
                                        response.body();

                                if (rechargeResponse.isSuccess()) {

                                    showSuccessDialog(
                                            rechargeResponse
                                    );

                                } else {

                                    Toast.makeText(

                                            BroadbandRechargeActivity.this,

                                            rechargeResponse.getMessage(),

                                            Toast.LENGTH_LONG

                                    ).show();

                                }

                            }

                            @Override
                            public void onFailure(

                                    Call<BroadbandRechargeResponse> call,

                                    Throwable t) {

                                binding.btnRecharge.setEnabled(true);

                                binding.btnRecharge.setText(
                                        "RECHARGE BROADBAND"
                                );

                                Toast.makeText(

                                        BroadbandRechargeActivity.this,

                                        "Network Error\n"
                                                + t.getMessage(),

                                        Toast.LENGTH_LONG

                                ).show();

                            }

                        }

                );

    }
    // =====================================================
// Recharge Success Dialog
// =====================================================

    private void showSuccessDialog(
            BroadbandRechargeResponse response
    ) {

        String paymentId = "";

        if (response.getPaymentId() != null) {

            paymentId = response.getPaymentId();

        }

        String message =

                "✅ Broadband Recharge Successful"

                        + "\n\nCustomer ID : "
                        + customerId

                        + "\n\nProvider : "
                        + provider

                        + "\n\nPlan : "
                        + selectedPlan

                        + "\n\nRecharge Amount : ₹"
                        + amount

                        + "\n\nPayment ID : "
                        + paymentId

                        + "\n\nStatus : SUCCESS";

        new AlertDialog.Builder(this)

                .setTitle("Recharge Successful")

                .setMessage(message)

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
// Clear Errors
// =====================================================

    private void clearErrors() {

        binding.etCustomerId.setError(null);

        binding.actProvider.setError(null);

        binding.actPlan.setError(null);

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