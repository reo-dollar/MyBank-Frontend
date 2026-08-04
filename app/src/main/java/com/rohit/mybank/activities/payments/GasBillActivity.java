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
import com.rohit.mybank.databinding.ActivityGasBillBinding;
import com.rohit.mybank.model.gas.GasBillRequest;
import com.rohit.mybank.model.gas.GasBillResponse;
import com.rohit.mybank.repository.GasBillRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GasBillActivity extends AppCompatActivity {

    private ActivityGasBillBinding binding;
    private GasBillRepository repository;

    private String consumerNumber;
    private String provider;
    private String state;
    private BigDecimal amount;

    private final Map<String, String[]> providerMap = new HashMap<>();

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            bookCylinder();

                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGasBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new GasBillRepository(this);

        setupStates();
        setupProviderMapping();
        setupStateListener();
        setupSummaryWatcher();

        binding.btnBookCylinder.setOnClickListener(v -> validateInput());

        updateSummary();
    }

    // ===========================================
    // State List
    // ===========================================

    private void setupStates() {

        String[] states = {

                "Maharashtra",
                "Delhi",
                "Karnataka",
                "Tamil Nadu",
                "Gujarat",
                "Telangana"

        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        states
                );

        binding.actState.setAdapter(adapter);

    }

    // ===========================================
    // Provider Mapping
    // ===========================================

    private void setupProviderMapping() {

        providerMap.put(
                "Maharashtra",
                new String[]{
                        "HP Gas",
                        "Bharat Gas",
                        "Indane Gas"
                });

        providerMap.put(
                "Delhi",
                new String[]{
                        "Indane Gas",
                        "Bharat Gas"
                });

        providerMap.put(
                "Karnataka",
                new String[]{
                        "HP Gas",
                        "Indane Gas"
                });

        providerMap.put(
                "Tamil Nadu",
                new String[]{
                        "Indane Gas",
                        "HP Gas"
                });

        providerMap.put(
                "Gujarat",
                new String[]{
                        "Bharat Gas",
                        "HP Gas"
                });

        providerMap.put(
                "Telangana",
                new String[]{
                        "HP Gas",
                        "Indane Gas"
                });

    }
    // ===========================================
    // State Selection Listener
    // ===========================================

    private void setupStateListener() {

        binding.actState.setOnItemClickListener((parent, view, position, id) -> {

            String selectedState =
                    binding.actState.getText().toString();

            String[] providers =
                    providerMap.get(selectedState);

            if (providers != null) {

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                providers
                        );

                binding.actProvider.setAdapter(adapter);

                binding.actProvider.setText("", false);
            }

            updateSummary();

        });

        binding.actProvider.setOnItemClickListener(
                (parent, view, position, id) -> updateSummary()
        );

    }

    // ===========================================
    // Live Summary TextWatcher
    // ===========================================

    private void setupSummaryWatcher() {

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

        binding.etConsumerNumber.addTextChangedListener(watcher);
        binding.etAmount.addTextChangedListener(watcher);

    }

    // ===========================================
    // Update Summary Card
    // ===========================================

    private void updateSummary() {

        String consumer =
                binding.etConsumerNumber
                        .getText()
                        .toString()
                        .trim();

        String provider =
                binding.actProvider
                        .getText()
                        .toString()
                        .trim();

        String state =
                binding.actState
                        .getText()
                        .toString()
                        .trim();

        String amount =
                binding.etAmount
                        .getText()
                        .toString()
                        .trim();

        binding.tvConsumer.setText(
                consumer.isEmpty() ? "-" : consumer
        );

        binding.tvProvider.setText(
                provider.isEmpty() ? "-" : provider
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
    // ===========================================
    // Validate Input
    // ===========================================

    private void validateInput() {

        consumerNumber = binding.etConsumerNumber
                .getText()
                .toString()
                .trim();

        provider = binding.actProvider
                .getText()
                .toString()
                .trim();

        state = binding.actState
                .getText()
                .toString()
                .trim();

        String amountText = binding.etAmount
                .getText()
                .toString()
                .trim();

        // Consumer Number

        if (TextUtils.isEmpty(consumerNumber)) {

            binding.etConsumerNumber.setError("Enter Consumer Number");
            binding.etConsumerNumber.requestFocus();
            return;
        }

        if (consumerNumber.length() < 8) {

            binding.etConsumerNumber.setError("Invalid Consumer Number");
            binding.etConsumerNumber.requestFocus();
            return;
        }

        // State

        if (TextUtils.isEmpty(state)) {

            binding.actState.setError("Select State");
            binding.actState.requestFocus();
            return;
        }

        // Provider

        if (TextUtils.isEmpty(provider)) {

            binding.actProvider.setError("Select Gas Provider");
            binding.actProvider.requestFocus();
            return;
        }

        // Amount

        if (TextUtils.isEmpty(amountText)) {

            binding.etAmount.setError("Enter Amount");
            binding.etAmount.requestFocus();
            return;
        }

        try {

            amount = new BigDecimal(amountText);

        } catch (Exception e) {

            binding.etAmount.setError("Invalid Amount");
            binding.etAmount.requestFocus();
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            binding.etAmount.setError("Amount must be greater than 0");
            binding.etAmount.requestFocus();
            return;
        }

        if (amount.compareTo(new BigDecimal("10000")) > 0) {

            binding.etAmount.setError("Maximum booking amount is ₹10000");
            binding.etAmount.requestFocus();
            return;
        }

        showConfirmationDialog();
    }

    // ===========================================
    // Confirmation Dialog
    // ===========================================

    private void showConfirmationDialog() {

        String message =
                "Consumer Number : " + consumerNumber +
                        "\n\nProvider : " + provider +
                        "\n\nState : " + state +
                        "\n\nAmount : ₹" + amount +
                        "\n\nDo you want to continue?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Gas Cylinder Booking")
                .setMessage(message)
                .setCancelable(false)

                .setPositiveButton("Continue",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            this,
                                            VerifyTransactionPinActivity.class
                                    );

                            pinLauncher.launch(intent);

                        })

                .setNegativeButton("Cancel",
                        (dialog, which) -> dialog.dismiss())

                .show();

    }
    // ===========================================
    // Book Gas Cylinder
    // ===========================================

    private void bookCylinder() {

        binding.btnBookCylinder.setEnabled(false);
        binding.btnBookCylinder.setText("Processing...");

        GasBillRequest request = new GasBillRequest(
                consumerNumber,
                provider,
                state,
                amount
        );

        repository.bookCylinder(request)
                .enqueue(new Callback<GasBillResponse>() {

                    @Override
                    public void onResponse(Call<GasBillResponse> call,
                                           Response<GasBillResponse> response) {

                        binding.btnBookCylinder.setEnabled(true);
                        binding.btnBookCylinder.setText("Book Cylinder");

                        if (response.isSuccessful()
                                && response.body() != null) {

                            GasBillResponse billResponse =
                                    response.body();

                            if (billResponse.isSuccess()) {

                                showSuccessDialog(
                                        billResponse.getPaymentId(),
                                        billResponse.getMessage()
                                );

                            } else {

                                Toast.makeText(
                                        GasBillActivity.this,
                                        billResponse.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            }

                        } else {

                            Toast.makeText(
                                    GasBillActivity.this,
                                    "Cylinder booking failed.",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<GasBillResponse> call,
                                          Throwable t) {

                        binding.btnBookCylinder.setEnabled(true);
                        binding.btnBookCylinder.setText("Book Cylinder");

                        Toast.makeText(
                                GasBillActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    // ===========================================
    // Success Dialog
    // ===========================================

    private void showSuccessDialog(String paymentId,
                                   String message) {

        String successMessage =
                "Cylinder booked successfully."
                        + "\n\nPayment ID : "
                        + paymentId
                        + "\n\n"
                        + message;

        new AlertDialog.Builder(this)
                .setTitle("Booking Successful")
                .setMessage(successMessage)
                .setCancelable(false)

                .setPositiveButton("OK",
                        (dialog, which) -> {

                            dialog.dismiss();
                            finish();

                        })

                .show();

    }

}