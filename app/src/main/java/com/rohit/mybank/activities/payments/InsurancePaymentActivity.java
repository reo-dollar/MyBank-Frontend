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

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.databinding.ActivityInsurancePaymentBinding;
import com.rohit.mybank.model.insurance.InsurancePaymentRequest;
import com.rohit.mybank.model.insurance.InsurancePaymentResponse;
import com.rohit.mybank.repository.InsuranceRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsurancePaymentActivity extends AppCompatActivity {

    private ActivityInsurancePaymentBinding binding;
    private InsuranceRepository repository;

    private String selectedCompany = "";
    private String selectedPolicyType = "";

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

    private final List<String> policyTypes = Arrays.asList(
            "Life Insurance",
            "Health Insurance",
            "Motor Insurance",
            "Term Insurance",
            "Travel Insurance"
    );

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null
                                && result.getData().getBooleanExtra(
                                VerifyTransactionPinActivity.EXTRA_PIN_VERIFIED,
                                false)) {

                            payInsurance();

                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInsurancePaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InsuranceRepository(this);

        setupCompanyDropdown();

        setupPolicyTypeDropdown();

        setupListeners();
    }

    private void setupCompanyDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        companies
                );

        binding.actCompany.setAdapter(adapter);

        binding.actCompany.setOnItemClickListener((parent, view, position, id) -> {

            selectedCompany = companies.get(position);

            updateSummary();

        });
    }

    private void setupPolicyTypeDropdown() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        policyTypes
                );

        binding.actPolicyType.setAdapter(adapter);

        binding.actPolicyType.setOnItemClickListener((parent, view, position, id) -> {

            selectedPolicyType = policyTypes.get(position);

            updateSummary();

        });

    }
    private void setupListeners() {

        binding.etPolicyNumber.addTextChangedListener(textWatcher);
        binding.etAmount.addTextChangedListener(textWatcher);

        binding.btnPayInsurance.setOnClickListener(v -> {

            if (!validateInput()) {
                return;
            }

            showConfirmationDialog();

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

        String policyNumber =
                binding.etPolicyNumber.getText() == null
                        ? ""
                        : binding.etPolicyNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText() == null
                        ? ""
                        : binding.etAmount.getText().toString().trim();

        binding.tvCompany.setText(
                "Company : " +
                        (selectedCompany.isEmpty()
                                ? "-"
                                : selectedCompany));

        binding.tvPolicy.setText(
                "Policy No : " +
                        (policyNumber.isEmpty()
                                ? "-"
                                : policyNumber));

        binding.tvType.setText(
                "Policy Type : " +
                        (selectedPolicyType.isEmpty()
                                ? "-"
                                : selectedPolicyType));

        binding.tvAmount.setText(
                "Amount : ₹" +
                        (amount.isEmpty()
                                ? "0"
                                : amount));

    }

    private boolean validateInput() {

        String policyNumber =
                binding.etPolicyNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText().toString().trim();

        if (policyNumber.isEmpty()) {

            binding.etPolicyNumber.setError(
                    "Enter Policy Number");

            return false;
        }

        if (policyNumber.length() < 6) {

            binding.etPolicyNumber.setError(
                    "Invalid Policy Number");

            return false;
        }

        if (selectedCompany.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select Insurance Company",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (selectedPolicyType.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select Policy Type",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (amount.isEmpty()) {

            binding.etAmount.setError(
                    "Enter Premium Amount");

            return false;
        }

        try {

            double premium =
                    Double.parseDouble(amount);

            if (premium <= 0) {

                binding.etAmount.setError(
                        "Invalid Premium Amount");

                return false;
            }

        } catch (Exception e) {

            binding.etAmount.setError(
                    "Invalid Premium Amount");

            return false;
        }

        return true;

    }
    private void showConfirmationDialog() {

        String policyNumber =
                binding.etPolicyNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("Confirm Insurance Premium Payment")
                .setMessage(
                        "Insurance Company : " + selectedCompany +
                                "\n\nPolicy Number : " + policyNumber +
                                "\n\nPolicy Type : " + selectedPolicyType +
                                "\n\nPremium Amount : ₹" + amount +
                                "\n\nDo you want to continue?"
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

    private void payInsurance() {

        String policyNumber =
                binding.etPolicyNumber.getText().toString().trim();

        BigDecimal amount =
                new BigDecimal(
                        binding.etAmount.getText().toString().trim()
                );

        InsurancePaymentRequest request =
                new InsurancePaymentRequest(
                        policyNumber,
                        selectedCompany,
                        selectedPolicyType,
                        amount
                );

        binding.btnPayInsurance.setEnabled(false);

        repository.payInsurance(request)
                .enqueue(new Callback<InsurancePaymentResponse>() {

                    @Override
                    public void onResponse(
                            Call<InsurancePaymentResponse> call,
                            Response<InsurancePaymentResponse> response) {

                        binding.btnPayInsurance.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            showSuccessDialog(response.body());

                        } else {

                            Toast.makeText(
                                    InsurancePaymentActivity.this,
                                    "Insurance Premium Payment Failed",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<InsurancePaymentResponse> call,
                            Throwable t) {

                        binding.btnPayInsurance.setEnabled(true);

                        Toast.makeText(
                                InsurancePaymentActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void showSuccessDialog(
            InsurancePaymentResponse response) {

        String policyNumber =
                binding.etPolicyNumber.getText().toString().trim();

        String amount =
                binding.etAmount.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("Insurance Premium Paid Successfully")
                .setMessage(
                        "Payment ID : "
                                + response.getPaymentId()

                                + "\n\nInsurance Company : "
                                + selectedCompany

                                + "\n\nPolicy Number : "
                                + policyNumber

                                + "\n\nPolicy Type : "
                                + selectedPolicyType

                                + "\n\nPremium Amount : ₹"
                                + amount
                )
                .setCancelable(false)
                .setPositiveButton("Done",
                        (dialog, which) -> finish())
                .show();

    }

}