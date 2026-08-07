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
import com.rohit.mybank.databinding.ActivityFastagRechargeBinding;
import com.rohit.mybank.model.fastag.FastagRechargeRequest;
import com.rohit.mybank.model.fastag.FastagRechargeResponse;
import com.rohit.mybank.repository.FastagRechargeRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FastagRechargeActivity extends AppCompatActivity {

    // =====================================================
    // View Binding
    // =====================================================

    private ActivityFastagRechargeBinding binding;

    // =====================================================
    // Repository
    // =====================================================

    private FastagRechargeRepository repository;

    // =====================================================
    // Providers
    // =====================================================

    private final List<String> providers = Arrays.asList(
            "NHAI FASTag",
            "ICICI FASTag",
            "HDFC FASTag",
            "SBI FASTag",
            "Axis FASTag",
            "IDFC FIRST FASTag",
            "Airtel Payments Bank FASTag"
    );

    // =====================================================
    // Recharge Data
    // =====================================================

    private String vehicleNumber;

    private String selectedProvider = "";

    private double amount;

    // =====================================================
    // Transaction PIN Launcher
    // =====================================================

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),

                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            rechargeFastag();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Recharge cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

            );

    // =====================================================
    // onCreate
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityFastagRechargeBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        repository =
                new FastagRechargeRepository(this);

        setupProviderDropdown();

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
// Setup Provider Dropdown
// =====================================================

    private void setupProviderDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_dropdown_item_1line,

                        providers

                );

        binding.actProvider.setAdapter(adapter);

        binding.actProvider.setOnItemClickListener(

                (parent, view, position, id) -> {

                    selectedProvider = providers.get(position);

                    updateSummary();

                }

        );

    }
    // =====================================================
// Setup Listeners
// =====================================================

    private void setupListeners() {

        binding.etVehicleNumber.addTextChangedListener(
                summaryWatcher
        );

        binding.etAmount.addTextChangedListener(
                summaryWatcher
        );

        binding.btnRecharge.setOnClickListener(v -> {

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

        vehicleNumber = "";

        if (binding.etVehicleNumber.getText() != null) {

            vehicleNumber =

                    binding.etVehicleNumber
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

        binding.tvVehicle.setText(

                "Vehicle : " +

                        (TextUtils.isEmpty(vehicleNumber)
                                ? "-"
                                : vehicleNumber)

        );

        binding.tvProvider.setText(

                "Provider : " +

                        (TextUtils.isEmpty(selectedProvider)
                                ? "-"
                                : selectedProvider)

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

        binding.etVehicleNumber.setError(null);
        binding.etAmount.setError(null);
        binding.actProvider.setError(null);

        vehicleNumber = "";

        if (binding.etVehicleNumber.getText() != null) {

            vehicleNumber =
                    binding.etVehicleNumber
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

        // Vehicle Number

        if (TextUtils.isEmpty(vehicleNumber)) {

            binding.etVehicleNumber.setError(
                    "Enter Vehicle Number"
            );

            binding.etVehicleNumber.requestFocus();

            return false;

        }

        if (vehicleNumber.length() < 8) {

            binding.etVehicleNumber.setError(
                    "Invalid Vehicle Number"
            );

            binding.etVehicleNumber.requestFocus();

            return false;

        }

        // Provider

        if (TextUtils.isEmpty(selectedProvider)) {

            binding.actProvider.setError(
                    "Select FASTag Provider"
            );

            binding.actProvider.requestFocus();

            return false;

        }

        // Amount

        if (TextUtils.isEmpty(amountText)) {

            binding.etAmount.setError(
                    "Enter Recharge Amount"
            );

            binding.etAmount.requestFocus();

            return false;

        }

        try {

            amount = Double.parseDouble(amountText);

        } catch (Exception e) {

            binding.etAmount.setError(
                    "Invalid Amount"
            );

            binding.etAmount.requestFocus();

            return false;

        }

        if (amount <= 0) {

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

                "Provider : " + selectedProvider +

                        "\n\nVehicle Number : " + vehicleNumber +

                        "\n\nRecharge Amount : ₹" + amount +

                        "\n\nProceed with recharge?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm FASTag Recharge")

                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(

                        "Continue",

                        (dialog, which) ->

                                new PaymentSecurityHelper(

                                        FastagRechargeActivity.this,

                                        pinLauncher,

                                        new PaymentCallback() {

                                            @Override
                                            public void onSuccess() {

                                                rechargeFastag();

                                            }

                                        }

                                ).verifyPayment()

                )

                .show();

    }
    // =====================================================
// Recharge FASTag
// =====================================================

    private void rechargeFastag() {

        binding.btnRecharge.setEnabled(false);

        binding.btnRecharge.setText("Processing...");

        FastagRechargeRequest request =
                new FastagRechargeRequest();

        request.setVehicleNumber(
                vehicleNumber
        );

        request.setProvider(
                selectedProvider
        );

        request.setAmount(
                BigDecimal.valueOf(amount)
        );

        repository.recharge(request)

                .enqueue(

                        new Callback<FastagRechargeResponse>() {

                            @Override
                            public void onResponse(

                                    Call<FastagRechargeResponse> call,

                                    Response<FastagRechargeResponse> response) {

                                binding.btnRecharge.setEnabled(true);

                                binding.btnRecharge.setText(
                                        "RECHARGE FASTAG"
                                );

                                if (!response.isSuccessful()) {

                                    Toast.makeText(

                                            FastagRechargeActivity.this,

                                            "HTTP Error : "
                                                    + response.code(),

                                            Toast.LENGTH_LONG

                                    ).show();

                                    return;

                                }

                                if (response.body() == null) {

                                    Toast.makeText(

                                            FastagRechargeActivity.this,

                                            "Empty server response.",

                                            Toast.LENGTH_LONG

                                    ).show();

                                    return;

                                }

                                FastagRechargeResponse rechargeResponse =
                                        response.body();

                                if (rechargeResponse.isSuccess()) {

                                    showSuccessDialog(
                                            rechargeResponse
                                    );

                                } else {

                                    Toast.makeText(

                                            FastagRechargeActivity.this,

                                            rechargeResponse.getMessage(),

                                            Toast.LENGTH_LONG

                                    ).show();

                                }

                            }

                            @Override
                            public void onFailure(

                                    Call<FastagRechargeResponse> call,

                                    Throwable t) {

                                binding.btnRecharge.setEnabled(true);

                                binding.btnRecharge.setText(
                                        "RECHARGE FASTAG"
                                );

                                Toast.makeText(

                                        FastagRechargeActivity.this,

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

    private void showSuccessDialog(FastagRechargeResponse response) {

        String paymentId = response.getPaymentId();

        if (paymentId == null) {
            paymentId = "N/A";
        }

        String receipt =

                "✅ FASTag Recharge Successful"

                        + "\n\nVehicle Number : "
                        + vehicleNumber

                        + "\n\nProvider : "
                        + selectedProvider

                        + "\n\nRecharge Amount : ₹"
                        + String.format("%.2f", amount)

                        + "\n\nPayment ID : "
                        + paymentId

                        + "\n\nStatus : SUCCESS";

        new AlertDialog.Builder(this)

                .setTitle("Recharge Successful")

                .setMessage(receipt)

                .setCancelable(false)

                .setPositiveButton("Done", (dialog, which) -> {

                    setResult(RESULT_OK);

                    finish();

                })

                .show();

    }
    // =====================================================
// Clear Errors
// =====================================================

    private void clearErrors() {

        binding.etVehicleNumber.setError(null);

        binding.etAmount.setError(null);

        binding.actProvider.setError(null);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }
}