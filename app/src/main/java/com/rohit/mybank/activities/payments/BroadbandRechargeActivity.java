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

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.databinding.ActivityBroadbandRechargeBinding;
import com.rohit.mybank.model.broadband.BroadbandRechargeRequest;
import com.rohit.mybank.model.broadband.BroadbandRechargeResponse;
import com.rohit.mybank.repository.BroadbandRechargeRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

        setupProviders();

        setupListeners();

        updateSummary();
    }

    // ===========================================
    // Providers
    // ===========================================

    private void setupProviders() {

        providerPlans.put(
                "JioFiber",
                new String[]{
                        "₹399 - 30 Days",
                        "₹699 - 60 Days",
                        "₹999 - 90 Days"
                });

        providerPlans.put(
                "Airtel Xstream Fiber",
                new String[]{
                        "₹499 - 30 Days",
                        "₹799 - 60 Days",
                        "₹1099 - 90 Days"
                });

        providerPlans.put(
                "BSNL FTTH",
                new String[]{
                        "₹329 - 30 Days",
                        "₹599 - 60 Days",
                        "₹899 - 90 Days"
                });

        providerPlans.put(
                "ACT Fibernet",
                new String[]{
                        "₹549 - 30 Days",
                        "₹749 - 60 Days",
                        "₹1049 - 90 Days"
                });

        providerPlans.put(
                "Tata Play Fiber",
                new String[]{
                        "₹450 - 30 Days",
                        "₹850 - 60 Days",
                        "₹950 - 90 Days"
                });

        // =====================================
        // Plan Amount Mapping
        // =====================================

        planAmount.put("₹399 - 30 Days",399.0);
        planAmount.put("₹699 - 60 Days",699.0);
        planAmount.put("₹999 - 90 Days",999.0);

        planAmount.put("₹499 - 30 Days",499.0);
        planAmount.put("₹799 - 60 Days",799.0);
        planAmount.put("₹1099 - 90 Days",1099.0);

        planAmount.put("₹329 - 30 Days",329.0);
        planAmount.put("₹599 - 60 Days",599.0);
        planAmount.put("₹899 - 90 Days",899.0);

        planAmount.put("₹549 - 30 Days",549.0);
        planAmount.put("₹749 - 60 Days",749.0);
        planAmount.put("₹1049 - 60 Days",1049.0);


        planAmount.put("₹450 - 30 Days",450.0);
        planAmount.put("₹850 - 30 Days",850.0);
        planAmount.put("₹950 - 30 Days",950.0);

        ArrayAdapter<String> providerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        providerPlans.keySet().toArray(new String[0]));

        binding.actProvider.setAdapter(providerAdapter);

        binding.actProvider.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selectedProvider =
                            binding.actProvider
                                    .getText()
                                    .toString();

                    String[] plans =
                            providerPlans.get(selectedProvider);

                    if (plans != null) {

                        ArrayAdapter<String> planAdapter =
                                new ArrayAdapter<>(
                                        this,
                                        android.R.layout.simple_list_item_1,
                                        plans);

                        binding.actPlan.setAdapter(planAdapter);

                        binding.actPlan.setText("", false);
                    }

                    updateSummary();
                });

        binding.actPlan.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String plan =
                            binding.actPlan
                                    .getText()
                                    .toString();

                    Double value =
                            planAmount.get(plan);

                    if (value != null) {

                        amount = value;
                    }

                    updateSummary();
                });
    }
    // ===========================================
    // Listeners
    // ===========================================

    private void setupListeners() {

        binding.etCustomerId.addTextChangedListener(textWatcher);

        binding.actProvider.addTextChangedListener(textWatcher);

        binding.actPlan.addTextChangedListener(textWatcher);

        binding.btnRecharge.setOnClickListener(v -> {

            if (validateInput()) {

                showConfirmationDialog();

            }

        });

    }

    // ===========================================
    // TextWatcher
    // ===========================================

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

    // ===========================================
    // Update Summary
    // ===========================================

    private void updateSummary() {

        binding.tvCustomer.setText(

                binding.etCustomerId
                        .getText()
                        .toString()
                        .trim()

        );

        binding.tvProvider.setText(

                binding.actProvider
                        .getText()
                        .toString()
                        .trim()

        );

        binding.tvPlan.setText(

                binding.actPlan
                        .getText()
                        .toString()
                        .trim()

        );

        String plan =

                binding.actPlan
                        .getText()
                        .toString();

        Double value = planAmount.get(plan);

        if (value == null) {

            binding.tvAmount.setText("₹0");

        } else {

            binding.tvAmount.setText("₹" + value.intValue());

        }

    }

    // ===========================================
    // Validation
    // ===========================================

    private boolean validateInput() {

        customerId =

                binding.etCustomerId
                        .getText()
                        .toString()
                        .trim();

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

        if (TextUtils.isEmpty(customerId)) {

            binding.etCustomerId.setError(
                    "Enter Customer ID");

            return false;

        }

        if (customerId.length() < 8) {

            binding.etCustomerId.setError(
                    "Invalid Customer ID");

            return false;

        }

        if (TextUtils.isEmpty(provider)) {

            binding.actProvider.setError(
                    "Select Broadband Provider");

            return false;

        }

        if (TextUtils.isEmpty(selectedPlan)) {

            binding.actPlan.setError(
                    "Select Recharge Plan");

            return false;

        }

        Double value = planAmount.get(selectedPlan);

        if (value == null) {

            Toast.makeText(
                    this,
                    "Invalid Plan",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        amount = value;

        return true;

    }
    // ===========================================
    // Confirmation Dialog
    // ===========================================

    private void showConfirmationDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Confirm Broadband Recharge")

                .setMessage(
                        "Provider : "
                                + provider
                                + "\n\nCustomer ID : "
                                + customerId
                                + "\n\nPlan : "
                                + selectedPlan
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
    // Recharge Broadband
    // ===========================================

    private void rechargeBroadband() {

        binding.btnRecharge.setEnabled(false);
        binding.btnRecharge.setText("Processing...");

        BroadbandRechargeRequest request =
                new BroadbandRechargeRequest(
                        customerId,
                        provider,
                        BigDecimal.valueOf(amount)
                );

        repository.recharge(request)
                .enqueue(new retrofit2.Callback<BroadbandRechargeResponse>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<BroadbandRechargeResponse> call,
                            retrofit2.Response<BroadbandRechargeResponse> response) {

                        binding.btnRecharge.setEnabled(true);
                        binding.btnRecharge.setText("RECHARGE BROADBAND");

                        if (response.isSuccessful()
                                && response.body() != null) {

                            BroadbandRechargeResponse rechargeResponse =
                                    response.body();

                            if (rechargeResponse.isSuccess()) {

                                showSuccessDialog(
                                        rechargeResponse.getPaymentId(),
                                        rechargeResponse.getMessage());

                            } else {

                                Toast.makeText(
                                        BroadbandRechargeActivity.this,
                                        rechargeResponse.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            }

                        } else {

                            Toast.makeText(
                                    BroadbandRechargeActivity.this,
                                    "Broadband Recharge Failed",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<BroadbandRechargeResponse> call,
                            Throwable t) {

                        binding.btnRecharge.setEnabled(true);
                        binding.btnRecharge.setText("RECHARGE BROADBAND");

                        Toast.makeText(
                                BroadbandRechargeActivity.this,
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
                "Broadband Recharge Successful"
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