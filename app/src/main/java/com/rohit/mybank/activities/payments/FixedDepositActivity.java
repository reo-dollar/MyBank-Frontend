package com.rohit.mybank.activities.payments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositResponse;
import com.rohit.mybank.repository.FixedDepositRepository;
import com.rohit.mybank.model.fixeddeposit.FixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.FixedDepositResponse;
import androidx.appcompat.app.AlertDialog;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FixedDepositActivity extends AppCompatActivity {

    //==================================================
    // Repository
    //==================================================

    private FixedDepositRepository repository;

    //==================================================
    // Input Layouts
    //==================================================

    private TextInputLayout layoutPrincipal;
    private TextInputLayout layoutInterest;
    private TextInputLayout layoutTenure;
    private TextInputLayout layoutTenureType;
    private TextInputLayout layoutInterestType;
    private TextInputLayout layoutCompounding;

    //==================================================
    // EditTexts
    //==================================================

    private TextInputEditText etPrincipal;
    private TextInputEditText etInterest;
    private TextInputEditText etTenure;

    //==================================================
    // Dropdowns
    //==================================================

    private MaterialAutoCompleteTextView actTenureType;
    private MaterialAutoCompleteTextView actInterestType;
    private MaterialAutoCompleteTextView actCompounding;

    //==================================================
    // Summary
    //==================================================

    private TextView tvPrincipal;
    private TextView tvInterest;
    private TextView tvMaturity;
    private TextView tvTenure;
    private TextView tvRate;

    //==================================================
    // Buttons
    //==================================================

    private MaterialButton btnCalculate;
    private MaterialButton btnReset;

    //==================================================
    // Selected Values
    //==================================================

    private String tenureType = "Years";
    private String interestType = "Compound";
    private String compounding = "Yearly";

    //==================================================
    // Formatter
    //==================================================

    private final DecimalFormat formatter =
            new DecimalFormat("#,##0.00");

    //==================================================
    // Dropdown Data
    //==================================================

    private final List<String> tenureTypes =
            Arrays.asList(
                    "Years",
                    "Months"
            );

    private final List<String> interestTypes =
            Arrays.asList(
                    "Simple",
                    "Compound"
            );

    private final List<String> compoundingTypes =
            Arrays.asList(
                    "Yearly",
                    "Half-Yearly",
                    "Quarterly",
                    "Monthly"
            );

    //==================================================
    // PIN Launcher
    //==================================================

    private final ActivityResultLauncher<Intent> pinVerificationLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            performFixedDeposit();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction Cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fixed_deposit);

        initializeViews();

        repository = new FixedDepositRepository(this);

        setupDropdowns();

        setupTextWatchers();

        updateSummary();

        btnCalculate.setOnClickListener(v -> {

            calculateFD();

        });

        btnReset.setOnClickListener(v -> {

            resetForm();

        });

    }
    //==================================================
    // Initialize Views
    //==================================================

    private void initializeViews() {

        // Input Layouts

        layoutPrincipal = findViewById(R.id.layoutPrincipal);
        layoutInterest = findViewById(R.id.layoutInterest);
        layoutTenure = findViewById(R.id.layoutTenure);
        layoutTenureType = findViewById(R.id.layoutTenureType);
        layoutInterestType = findViewById(R.id.layoutInterestType);
        layoutCompounding = findViewById(R.id.layoutCompounding);

        // EditTexts

        etPrincipal = findViewById(R.id.etPrincipal);
        etInterest = findViewById(R.id.etInterest);
        etTenure = findViewById(R.id.etTenure);

        // Dropdowns

        actTenureType = findViewById(R.id.actTenureType);
        actInterestType = findViewById(R.id.actInterestType);
        actCompounding = findViewById(R.id.actCompounding);

        // Summary

        tvPrincipal = findViewById(R.id.tvPrincipal);
        tvInterest = findViewById(R.id.tvInterest);
        tvMaturity = findViewById(R.id.tvMaturity);
        tvTenure = findViewById(R.id.tvTenure);
        tvRate = findViewById(R.id.tvRate);

        // Buttons

        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);

    }

    //==================================================
    // Dropdowns
    //==================================================

    private void setupDropdowns() {

        ArrayAdapter<String> tenureAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        tenureTypes
                );

        actTenureType.setAdapter(tenureAdapter);
        actTenureType.setText(tenureType, false);

        actTenureType.setOnItemClickListener((parent, view, position, id) -> {

            tenureType = tenureTypes.get(position);

            updateSummary();

        });

        ArrayAdapter<String> interestAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        interestTypes
                );

        actInterestType.setAdapter(interestAdapter);
        actInterestType.setText(interestType, false);

        actInterestType.setOnItemClickListener((parent, view, position, id) -> {

            interestType = interestTypes.get(position);

            updateSummary();

        });

        ArrayAdapter<String> compoundingAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        compoundingTypes
                );

        actCompounding.setAdapter(compoundingAdapter);
        actCompounding.setText(compounding, false);

        actCompounding.setOnItemClickListener((parent, view, position, id) -> {

            compounding = compoundingTypes.get(position);

            updateSummary();

        });

    }

    //==================================================
    // Text Watchers
    //==================================================

    private void setupTextWatchers() {

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

        etPrincipal.addTextChangedListener(watcher);
        etInterest.addTextChangedListener(watcher);
        etTenure.addTextChangedListener(watcher);

    }
    //==================================================
    // Live Summary
    //==================================================

    private void updateSummary() {

        String principal =
                etPrincipal.getText() == null
                        ? ""
                        : etPrincipal.getText().toString().trim();

        String rate =
                etInterest.getText() == null
                        ? ""
                        : etInterest.getText().toString().trim();

        String tenure =
                etTenure.getText() == null
                        ? ""
                        : etTenure.getText().toString().trim();

        tvPrincipal.setText(
                principal.isEmpty()
                        ? "₹0.00"
                        : "₹" + formatter.format(
                        Double.parseDouble(principal))
        );

        tvRate.setText(
                rate.isEmpty()
                        ? "-"
                        : rate + " %"
        );

        tvTenure.setText(
                tenure.isEmpty()
                        ? "-"
                        : tenure + " " + tenureType
        );

        if (principal.isEmpty()
                || rate.isEmpty()
                || tenure.isEmpty()) {

            tvInterest.setText("₹0.00");
            tvMaturity.setText("₹0.00");

            return;
        }

        try {

            calculatePreview();

        } catch (Exception e) {

            tvInterest.setText("₹0.00");
            tvMaturity.setText("₹0.00");

        }

    }

    //==================================================
    // Validate Inputs
    //==================================================

    private boolean validateInputs() {

        layoutPrincipal.setError(null);
        layoutInterest.setError(null);
        layoutTenure.setError(null);

        String principal =
                etPrincipal.getText() == null
                        ? ""
                        : etPrincipal.getText().toString().trim();

        String rate =
                etInterest.getText() == null
                        ? ""
                        : etInterest.getText().toString().trim();

        String tenure =
                etTenure.getText() == null
                        ? ""
                        : etTenure.getText().toString().trim();

        if (principal.isEmpty()) {

            layoutPrincipal.setError(
                    "Please enter deposit amount");

            return false;

        }

        if (rate.isEmpty()) {

            layoutInterest.setError(
                    "Please enter interest rate");

            return false;

        }

        if (tenure.isEmpty()) {

            layoutTenure.setError(
                    "Please enter tenure");

            return false;

        }

        try {

            double p = Double.parseDouble(principal);
            double r = Double.parseDouble(rate);
            double t = Double.parseDouble(tenure);

            if (p <= 0) {

                layoutPrincipal.setError(
                        "Amount must be greater than zero");

                return false;

            }

            if (r <= 0) {

                layoutInterest.setError(
                        "Interest rate must be greater than zero");

                return false;

            }

            if (t <= 0) {

                layoutTenure.setError(
                        "Tenure must be greater than zero");

                return false;

            }

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Invalid numeric input",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        return true;

    }

    //==================================================
    // Live Preview
    //==================================================

    private void calculatePreview() {

        double principal =
                Double.parseDouble(
                        etPrincipal.getText().toString().trim());

        double rate =
                Double.parseDouble(
                        etInterest.getText().toString().trim());

        double tenure =
                Double.parseDouble(
                        etTenure.getText().toString().trim());

        if (tenureType.equalsIgnoreCase("Months")) {

            tenure /= 12.0;

        }

        double interest;
        double maturity;

        if (interestType.equalsIgnoreCase("Simple")) {

            interest =
                    (principal * rate * tenure) / 100;

            maturity =
                    principal + interest;

        } else {

            int frequency =
                    getCompoundingFrequency();

            maturity =
                    principal *
                            Math.pow(
                                    1 +
                                            (rate /
                                                    (100 * frequency)),
                                    frequency * tenure
                            );

            interest =
                    maturity - principal;

        }

        tvInterest.setText(
                "₹" + formatter.format(interest));

        tvMaturity.setText(
                "₹" + formatter.format(maturity));

    }
    //==================================================
    // Calculate Button
    //==================================================

    private void calculateFD() {

        tenureType =
                actTenureType.getText().toString().trim();

        interestType =
                actInterestType.getText().toString().trim();

        compounding =
                actCompounding.getText().toString().trim();

        if (!validateInputs()) {
            return;
        }

        FixedDepositRequest request =
                new FixedDepositRequest(

                        Double.parseDouble(
                                etPrincipal.getText().toString().trim()),

                        Double.parseDouble(
                                etInterest.getText().toString().trim()),

                        Double.parseDouble(
                                etTenure.getText().toString().trim()),

                        tenureType,

                        interestType,

                        compounding
                );

        repository.calculateFixedDeposit(request)
                .enqueue(new Callback<FixedDepositResponse>() {

                    @Override
                    public void onResponse(
                            Call<FixedDepositResponse> call,
                            Response<FixedDepositResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            tvInterest.setText(
                                    "₹" + formatter.format(
                                            response.body()
                                                    .getInterestEarned()));

                            tvMaturity.setText(
                                    "₹" + formatter.format(
                                            response.body()
                                                    .getMaturityAmount()));

                            showResultDialog(response.body());

                        } else {

                            Toast.makeText(
                                    FixedDepositActivity.this,
                                    response.body() != null
                                            ? response.body().getMessage()
                                            : "Calculation Failed",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<FixedDepositResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                FixedDepositActivity.this,
                                "Network Error : "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    //==================================================
    // Compounding Frequency
    //==================================================

    private int getCompoundingFrequency() {

        switch (compounding) {

            case "Monthly":
                return 12;

            case "Quarterly":
                return 4;

            case "Half-Yearly":
                return 2;

            default:
                return 1;

        }

    }

    //==================================================
    // Reset
    //==================================================

    private void resetForm() {

        layoutPrincipal.setError(null);
        layoutInterest.setError(null);
        layoutTenure.setError(null);

        etPrincipal.setText("");
        etInterest.setText("");
        etTenure.setText("");

        tenureType = "Years";
        interestType = "Compound";
        compounding = "Yearly";

        actTenureType.setText("Years", false);
        actInterestType.setText("Compound", false);
        actCompounding.setText("Yearly", false);

        tvPrincipal.setText("₹0.00");
        tvInterest.setText("₹0.00");
        tvMaturity.setText("₹0.00");
        tvTenure.setText("-");
        tvRate.setText("-");

        etPrincipal.requestFocus();

    }
    //==================================================
// Result Dialog
//==================================================

    private void showResultDialog(
            FixedDepositResponse response) {

        StringBuilder builder = new StringBuilder();

        builder.append("Principal Amount : ₹")
                .append(formatter.format(
                        response.getPrincipal()))

                .append("\n\nInterest Earned : ₹")
                .append(formatter.format(
                        response.getInterestEarned()))

                .append("\n\nMaturity Amount : ₹")
                .append(formatter.format(
                        response.getMaturityAmount()))

                .append("\n\nInterest Rate : ")
                .append(
                        etInterest.getText().toString().trim())
                .append("%")

                .append("\n\nTenure : ")
                .append(
                        etTenure.getText().toString().trim())
                .append(" ")
                .append(tenureType)

                .append("\n\nInterest Type : ")
                .append(interestType)

                .append("\n\nCompounding : ")
                .append(compounding);

        new AlertDialog.Builder(this)

                .setTitle("Fixed Deposit Summary")

                .setCancelable(false)

                .setMessage(builder.toString())

                .setNegativeButton(
                        "Cancel",
                        null)

                .setPositiveButton(
                        "Create FD",
                        (dialog, which) -> {

                            verifyTransactionPin();

                        })

                .show();

    }
    //==================================================
// Verify Transaction PIN
//==================================================

    private void verifyTransactionPin() {

        Intent intent = new Intent(
                FixedDepositActivity.this,
                VerifyTransactionPinActivity.class
        );

        intent.putExtra("paymentType", "FIXED_DEPOSIT");

        intent.putExtra(
                "amount",
                Double.parseDouble(
                        etPrincipal.getText().toString().trim()
                )
        );

        pinVerificationLauncher.launch(intent);

    }
    //==================================================
// Create Fixed Deposit
//==================================================

    private void performFixedDeposit() {

        double principal =
                Double.parseDouble(
                        etPrincipal.getText().toString().trim());

        double rate =
                Double.parseDouble(
                        etInterest.getText().toString().trim());

        int tenure =
                Integer.parseInt(
                        etTenure.getText().toString().trim());

        CreateFixedDepositRequest request =
                new CreateFixedDepositRequest(
                        principal,
                        rate,
                        tenure,
                        tenureType,
                        interestType,
                        compounding
                );

        repository.createFixedDeposit(request)
                .enqueue(new Callback<CreateFixedDepositResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreateFixedDepositResponse> call,
                            Response<CreateFixedDepositResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            showSuccessDialog(response.body());

                        } else {

                            String message = "Unable to create Fixed Deposit.";

                            if (response.body() != null
                                    && response.body().getMessage() != null) {

                                message = response.body().getMessage();

                            }

                            new AlertDialog.Builder(FixedDepositActivity.this)
                                    .setTitle("Fixed Deposit Failed")
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<CreateFixedDepositResponse> call,
                            Throwable t) {

                        new AlertDialog.Builder(FixedDepositActivity.this)
                                .setTitle("Network Error")
                                .setMessage(t.getMessage())
                                .setPositiveButton("OK", null)
                                .show();

                    }

                });

    }
    //==================================================
// Success Dialog
//==================================================

    private void showSuccessDialog(
            CreateFixedDepositResponse response) {

        StringBuilder builder = new StringBuilder();

        builder.append("🎉 Fixed Deposit Created Successfully\n\n")

                .append("FD Number : ")
                .append(response.getFdNumber())

                .append("\n\nAccount Number : ")
                .append(response.getAccountNumber())

                .append("\n\nPrincipal Amount : ₹")
                .append(formatter.format(response.getPrincipal()))

                .append("\n\nInterest Earned : ₹")
                .append(formatter.format(response.getInterestEarned()))

                .append("\n\nMaturity Amount : ₹")
                .append(formatter.format(response.getMaturityAmount()))

                .append("\n\nMaturity Date : ")
                .append(response.getMaturityDate())

                .append("\n\nStatus : ")
                .append(response.getStatus());

        new AlertDialog.Builder(this)

                .setTitle("Success")

                .setCancelable(false)

                .setMessage(builder.toString())

                .setPositiveButton("Done", (dialog, which) -> {

                    resetForm();

                })

                .show();

    }
}