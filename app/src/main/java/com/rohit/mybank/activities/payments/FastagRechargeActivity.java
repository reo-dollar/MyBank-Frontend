package com.rohit.mybank.activities.payments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.databinding.ActivityFastagRechargeBinding;
import com.rohit.mybank.model.fastag.FastagRechargeRequest;
import com.rohit.mybank.model.fastag.FastagRechargeResponse;
import com.rohit.mybank.repository.FastagRechargeRepository;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FastagRechargeActivity extends AppCompatActivity {

    private ActivityFastagRechargeBinding binding;
    private FastagRechargeRepository repository;

    private String selectedProvider = "";

    private final List<String> providers = Arrays.asList(
            "NHAI FASTag",
            "ICICI FASTag",
            "HDFC FASTag",
            "SBI FASTag",
            "Axis FASTag",
            "IDFC FIRST FASTag",
            "Airtel Payments Bank FASTag"
    );

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            rechargeFastag();

                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFastagRechargeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new FastagRechargeRepository(this);

        setupProviderDropdown();

        setupListeners();
    }

    private void setupProviderDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        providers
                );

        binding.actProvider.setAdapter(adapter);

        binding.actProvider.setOnItemClickListener((parent, view, position, id) -> {

            selectedProvider = providers.get(position);

            updateSummary();

        });

    }

    private void setupListeners() {

        binding.etVehicleNumber.addTextChangedListener(textWatcher);
        binding.etAmount.addTextChangedListener(textWatcher);

        binding.btnRecharge.setOnClickListener(v -> {

            if (!validateInput()) {
                return;
            }

            showConfirmationDialog();

        });

    }

    private final TextWatcher textWatcher = new TextWatcher() {

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

    private void updateSummary() {

        String vehicle =
                binding.etVehicleNumber.getText() == null
                        ? ""
                        : binding.etVehicleNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText() == null
                        ? ""
                        : binding.etAmount.getText().toString().trim();

        binding.tvProvider.setText(
                "Provider : " +
                        (selectedProvider.isEmpty() ? "-" : selectedProvider));

        binding.tvVehicle.setText(
                "Vehicle : " +
                        (vehicle.isEmpty() ? "-" : vehicle));

        binding.tvAmount.setText(
                "Amount : ₹" +
                        (amount.isEmpty() ? "0" : amount));

    }

    private boolean validateInput() {

        String vehicle =
                binding.etVehicleNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText().toString().trim();

        if (vehicle.isEmpty()) {

            binding.etVehicleNumber.setError("Enter Vehicle Number");

            return false;
        }

        if (vehicle.length() < 8) {

            binding.etVehicleNumber.setError("Invalid Vehicle Number");

            return false;
        }

        if (selectedProvider.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select FASTag Provider",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (amount.isEmpty()) {

            binding.etAmount.setError("Enter Recharge Amount");

            return false;
        }

        try {

            double rechargeAmount = Double.parseDouble(amount);

            if (rechargeAmount <= 0) {

                binding.etAmount.setError("Invalid Amount");

                return false;
            }

        } catch (Exception e) {

            binding.etAmount.setError("Invalid Amount");

            return false;
        }

        return true;

    }

    private void showConfirmationDialog() {

        String vehicle = binding.etVehicleNumber.getText().toString().trim();
        String amount = binding.etAmount.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("Confirm FASTag Recharge")
                .setMessage(
                        "Provider : " + selectedProvider +
                                "\n\nVehicle : " + vehicle +
                                "\n\nAmount : ₹" + amount +
                                "\n\nProceed with recharge?"
                )
                .setPositiveButton("Proceed", (dialog, which) -> {

                    Intent intent = new Intent(
                            this,
                            VerifyTransactionPinActivity.class
                    );

                    pinLauncher.launch(intent);

                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void rechargeFastag() {

        String vehicle =
                binding.etVehicleNumber.getText().toString().trim();

        BigDecimal amount =
                new BigDecimal(
                        binding.etAmount.getText().toString().trim()
                );

        FastagRechargeRequest request =
                new FastagRechargeRequest(
                        vehicle,
                        selectedProvider,
                        amount
                );

        binding.btnRecharge.setEnabled(false);

        repository.recharge(request).enqueue(
                new Callback<FastagRechargeResponse>() {

                    @Override
                    public void onResponse(
                            Call<FastagRechargeResponse> call,
                            Response<FastagRechargeResponse> response) {

                        binding.btnRecharge.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            showSuccessDialog(response.body());

                        } else {

                            Toast.makeText(
                                    FastagRechargeActivity.this,
                                    "FASTag Recharge Failed",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<FastagRechargeResponse> call,
                            Throwable t) {

                        binding.btnRecharge.setEnabled(true);

                        Toast.makeText(
                                FastagRechargeActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void showSuccessDialog(
            FastagRechargeResponse response) {

        String vehicle =
                binding.etVehicleNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("FASTag Recharge Successful")
                .setMessage(
                        "Payment ID : "
                                + response.getPaymentId()

                                + "\n\nProvider : "
                                + selectedProvider

                                + "\n\nVehicle : "
                                + vehicle

                                + "\n\nAmount : ₹"
                                + amount
                )
                .setCancelable(false)
                .setPositiveButton("Done",
                        (dialog, which) -> finish())
                .show();

    }
}