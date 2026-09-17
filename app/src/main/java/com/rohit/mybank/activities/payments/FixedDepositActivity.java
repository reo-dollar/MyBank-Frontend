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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositResponse;
import com.rohit.mybank.model.fixeddeposit.FixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.FixedDepositResponse;
import com.rohit.mybank.repository.FixedDepositRepository;

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

                    }
            );


    //==================================================
    // ON CREATE
    //==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fixed_deposit);

        initializeViews();

        repository = new FixedDepositRepository(this);

        setupDropdowns();

        setupTextWatchers();

        updateSummary();


        //==================================================
        // Calculate Button
        //==================================================

        btnCalculate.setOnClickListener(v -> {

            calculateFD();

        });


        //==================================================
        // Reset Button
        //==================================================

        btnReset.setOnClickListener(v -> {

            resetForm();

        });

    }


    //==================================================
    // Initialize Views
    //==================================================

    private void initializeViews() {


        //==================================================
        // Input Layouts
        //==================================================

        layoutPrincipal =
                findViewById(R.id.layoutPrincipal);

        layoutInterest =
                findViewById(R.id.layoutInterest);

        layoutTenure =
                findViewById(R.id.layoutTenure);

        layoutTenureType =
                findViewById(R.id.layoutTenureType);

        layoutInterestType =
                findViewById(R.id.layoutInterestType);

        layoutCompounding =
                findViewById(R.id.layoutCompounding);


        //==================================================
        // EditTexts
        //==================================================

        etPrincipal =
                findViewById(R.id.etPrincipal);

        etInterest =
                findViewById(R.id.etInterest);

        etTenure =
                findViewById(R.id.etTenure);


        //==================================================
        // Dropdowns
        //==================================================

        actTenureType =
                findViewById(R.id.actTenureType);

        actInterestType =
                findViewById(R.id.actInterestType);

        actCompounding =
                findViewById(R.id.actCompounding);


        //==================================================
        // Summary
        //==================================================

        tvPrincipal =
                findViewById(R.id.tvPrincipal);

        tvInterest =
                findViewById(R.id.tvInterest);

        tvMaturity =
                findViewById(R.id.tvMaturity);

        tvTenure =
                findViewById(R.id.tvTenure);

        tvRate =
                findViewById(R.id.tvRate);


        //==================================================
        // Buttons
        //==================================================

        btnCalculate =
                findViewById(R.id.btnCalculate);

        btnReset =
                findViewById(R.id.btnReset);

    }


    //==================================================
    // Dropdowns
    //==================================================

    private void setupDropdowns() {


        //==================================================
        // Tenure Type
        //==================================================

        ArrayAdapter<String> tenureAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        tenureTypes
                );


        actTenureType.setAdapter(tenureAdapter);

        actTenureType.setText(
                tenureType,
                false
        );


        // IMPORTANT:
        // Set initial suffix based on default value
        updateTenureSuffix();


        actTenureType.setOnItemClickListener(
                (parent, view, position, id) -> {

                    tenureType =
                            tenureTypes.get(position);


                    // Update "Years" / "Months"
                    // inside the Tenure field
                    updateTenureSuffix();


                    // Update summary
                    updateSummary();

                }
        );


        //==================================================
        // Interest Type
        //==================================================

        ArrayAdapter<String> interestAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        interestTypes
                );


        actInterestType.setAdapter(interestAdapter);

        actInterestType.setText(
                interestType,
                false
        );


        actInterestType.setOnItemClickListener(
                (parent, view, position, id) -> {

                    interestType =
                            interestTypes.get(position);


                    updateSummary();

                }
        );


        //==================================================
        // Compounding
        //==================================================

        ArrayAdapter<String> compoundingAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        compoundingTypes
                );


        actCompounding.setAdapter(compoundingAdapter);

        actCompounding.setText(
                compounding,
                false
        );


        actCompounding.setOnItemClickListener(
                (parent, view, position, id) -> {

                    compounding =
                            compoundingTypes.get(position);


                    updateSummary();

                }
        );

    }


    //==================================================
    // UPDATE TENURE SUFFIX
    //==================================================

    private void updateTenureSuffix() {

        if (tenureType != null
                && tenureType.equalsIgnoreCase("Months")) {

            layoutTenure.setSuffixText("Months");

        } else {

            layoutTenure.setSuffixText("Years");

        }

    }


    //==================================================
    // Text Watchers
    //==================================================

    private void setupTextWatchers() {

        TextWatcher watcher =
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {

                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        updateSummary();

                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {

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
                        : etPrincipal
                        .getText()
                        .toString()
                        .trim();


        String rate =
                etInterest.getText() == null
                        ? ""
                        : etInterest
                        .getText()
                        .toString()
                        .trim();


        String tenure =
                etTenure.getText() == null
                        ? ""
                        : etTenure
                        .getText()
                        .toString()
                        .trim();


        //==================================================
        // Principal
        //==================================================

        if (principal.isEmpty()) {

            tvPrincipal.setText("₹0.00");

        } else {

            try {

                tvPrincipal.setText(
                        "₹" +
                                formatter.format(
                                        Double.parseDouble(principal)
                                )
                );

            } catch (NumberFormatException e) {

                tvPrincipal.setText("₹0.00");

            }

        }


        //==================================================
        // Interest Rate
        //==================================================

        tvRate.setText(
                rate.isEmpty()
                        ? "-"
                        : rate + " %"
        );


        //==================================================
        // Tenure
        //==================================================

        tvTenure.setText(
                tenure.isEmpty()
                        ? "-"
                        : tenure + " " + tenureType
        );


        //==================================================
        // Check Required Fields
        //==================================================

        if (principal.isEmpty()
                || rate.isEmpty()
                || tenure.isEmpty()) {

            tvInterest.setText("₹0.00");

            tvMaturity.setText("₹0.00");

            return;

        }


        //==================================================
        // Calculate Preview
        //==================================================

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
                        : etPrincipal
                        .getText()
                        .toString()
                        .trim();


        String rate =
                etInterest.getText() == null
                        ? ""
                        : etInterest
                        .getText()
                        .toString()
                        .trim();


        String tenure =
                etTenure.getText() == null
                        ? ""
                        : etTenure
                        .getText()
                        .toString()
                        .trim();


        //==================================================
        // Principal
        //==================================================

        if (principal.isEmpty()) {

            layoutPrincipal.setError(
                    "Please enter deposit amount"
            );

            return false;

        }


        //==================================================
        // Interest
        //==================================================

        if (rate.isEmpty()) {

            layoutInterest.setError(
                    "Please enter interest rate"
            );

            return false;

        }


        //==================================================
        // Tenure
        //==================================================

        if (tenure.isEmpty()) {

            layoutTenure.setError(
                    "Please enter tenure"
            );

            return false;

        }


        try {

            double p =
                    Double.parseDouble(principal);

            double r =
                    Double.parseDouble(rate);

            double t =
                    Double.parseDouble(tenure);


            //==================================================
            // Principal Validation
            //==================================================

            if (p <= 0) {

                layoutPrincipal.setError(
                        "Amount must be greater than zero"
                );

                return false;

            }


            //==================================================
            // Interest Validation
            //==================================================

            if (r <= 0) {

                layoutInterest.setError(
                        "Interest rate must be greater than zero"
                );

                return false;

            }


            //==================================================
            // Tenure Validation
            //==================================================

            if (t <= 0) {

                layoutTenure.setError(
                        "Tenure must be greater than zero"
                );

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
                        etPrincipal
                                .getText()
                                .toString()
                                .trim()
                );


        double rate =
                Double.parseDouble(
                        etInterest
                                .getText()
                                .toString()
                                .trim()
                );


        double tenure =
                Double.parseDouble(
                        etTenure
                                .getText()
                                .toString()
                                .trim()
                );


        //==================================================
        // IMPORTANT:
        // Convert Months to Years for calculation
        //==================================================

        if (tenureType.equalsIgnoreCase("Months")) {

            tenure = tenure / 12.0;

        }


        double interest;

        double maturity;


        //==================================================
        // Simple Interest
        //==================================================

        if (interestType.equalsIgnoreCase("Simple")) {

            interest =
                    (principal * rate * tenure)
                            / 100;


            maturity =
                    principal + interest;

        }


        //==================================================
        // Compound Interest
        //==================================================

        else {

            int frequency =
                    getCompoundingFrequency();


            maturity =
                    principal *
                            Math.pow(
                                    1 +
                                            (
                                                    rate /
                                                            (100 * frequency)
                                            ),
                                    frequency * tenure
                            );


            interest =
                    maturity - principal;

        }


        //==================================================
        // Update Summary
        //==================================================

        tvInterest.setText(
                "₹" +
                        formatter.format(interest)
        );


        tvMaturity.setText(
                "₹" +
                        formatter.format(maturity)
        );

    }


    //==================================================
    // Calculate Button
    //==================================================

    private void calculateFD() {


        //==================================================
        // Get Current Dropdown Values
        //==================================================

        String selectedTenureType =
                actTenureType
                        .getText()
                        .toString()
                        .trim();


        String selectedInterestType =
                actInterestType
                        .getText()
                        .toString()
                        .trim();


        String selectedCompounding =
                actCompounding
                        .getText()
                        .toString()
                        .trim();


        //==================================================
        // Keep Variables in Sync
        //==================================================

        if (!selectedTenureType.isEmpty()) {

            tenureType =
                    selectedTenureType;

        }


        if (!selectedInterestType.isEmpty()) {

            interestType =
                    selectedInterestType;

        }


        if (!selectedCompounding.isEmpty()) {

            compounding =
                    selectedCompounding;

        }


        //==================================================
        // Make Sure Suffix Is Correct
        //==================================================

        updateTenureSuffix();


        //==================================================
        // Validate
        //==================================================

        if (!validateInputs()) {

            return;

        }


        //==================================================
        // Create Request
        //==================================================

        FixedDepositRequest request =
                new FixedDepositRequest(

                        Double.parseDouble(
                                etPrincipal
                                        .getText()
                                        .toString()
                                        .trim()
                        ),

                        Double.parseDouble(
                                etInterest
                                        .getText()
                                        .toString()
                                        .trim()
                        ),

                        Double.parseDouble(
                                etTenure
                                        .getText()
                                        .toString()
                                        .trim()
                        ),

                        tenureType,

                        interestType,

                        compounding
                );


        //==================================================
        // Call Backend
        //==================================================

        repository
                .calculateFixedDeposit(request)
                .enqueue(
                        new Callback<FixedDepositResponse>() {

                            @Override
                            public void onResponse(
                                    Call<FixedDepositResponse> call,
                                    Response<FixedDepositResponse> response) {


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {


                                    //==================================
                                    // Update Interest
                                    //==================================

                                    tvInterest.setText(
                                            "₹" +
                                                    formatter.format(
                                                            response.body()
                                                                    .getInterestEarned()
                                                    )
                                    );


                                    //==================================
                                    // Update Maturity
                                    //==================================

                                    tvMaturity.setText(
                                            "₹" +
                                                    formatter.format(
                                                            response.body()
                                                                    .getMaturityAmount()
                                                    )
                                    );


                                    //==================================
                                    // Show Result
                                    //==================================

                                    showResultDialog(
                                            response.body()
                                    );


                                } else {


                                    String message =
                                            "Calculation Failed";


                                    if (response.body() != null
                                            && response.body()
                                            .getMessage() != null) {

                                        message =
                                                response.body()
                                                        .getMessage();

                                    }


                                    Toast.makeText(
                                            FixedDepositActivity.this,
                                            message,
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

                        }
                );

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


            case "Yearly":

            default:

                return 1;

        }

    }


    //==================================================
    // Reset
    //==================================================

    private void resetForm() {


        //==================================================
        // Clear Errors
        //==================================================

        layoutPrincipal.setError(null);

        layoutInterest.setError(null);

        layoutTenure.setError(null);


        //==================================================
        // Clear Inputs
        //==================================================

        etPrincipal.setText("");

        etInterest.setText("");

        etTenure.setText("");


        //==================================================
        // Restore Defaults
        //==================================================

        tenureType = "Years";

        interestType = "Compound";

        compounding = "Yearly";


        //==================================================
        // Restore Dropdowns
        //==================================================

        actTenureType.setText(
                "Years",
                false
        );


        actInterestType.setText(
                "Compound",
                false
        );


        actCompounding.setText(
                "Yearly",
                false
        );


        //==================================================
        // IMPORTANT:
        // Restore Tenure suffix to Years
        //==================================================

        updateTenureSuffix();


        //==================================================
        // Reset Summary
        //==================================================

        tvPrincipal.setText("₹0.00");

        tvInterest.setText("₹0.00");

        tvMaturity.setText("₹0.00");

        tvTenure.setText("-");

        tvRate.setText("-");


        //==================================================
        // Focus
        //==================================================

        etPrincipal.requestFocus();

    }


    //==================================================
    // Result Dialog
    //==================================================

    private void showResultDialog(
            FixedDepositResponse response) {


        StringBuilder builder =
                new StringBuilder();


        builder.append(
                        "Principal Amount : ₹"
                )
                .append(
                        formatter.format(
                                response.getPrincipal()
                        )
                );


        builder.append(
                        "\n\nInterest Earned : ₹"
                )
                .append(
                        formatter.format(
                                response.getInterestEarned()
                        )
                );


        builder.append(
                        "\n\nMaturity Amount : ₹"
                )
                .append(
                        formatter.format(
                                response.getMaturityAmount()
                        )
                );


        builder.append(
                        "\n\nInterest Rate : "
                )
                .append(
                        etInterest
                                .getText()
                                .toString()
                                .trim()
                )
                .append("%");


        builder.append(
                        "\n\nTenure : "
                )
                .append(
                        etTenure
                                .getText()
                                .toString()
                                .trim()
                )
                .append(" ")
                .append(tenureType);


        builder.append(
                        "\n\nInterest Type : "
                )
                .append(interestType);


        builder.append(
                        "\n\nCompounding : "
                )
                .append(compounding);


        //==================================================
        // Result Dialog
        //==================================================

        new AlertDialog.Builder(this)

                .setTitle(
                        "Fixed Deposit Summary"
                )

                .setCancelable(false)

                .setMessage(
                        builder.toString()
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Create FD",
                        (dialog, which) -> {

                            verifyTransactionPin();

                        }
                )

                .show();

    }


    //==================================================
    // Verify Transaction PIN
    //==================================================

    private void verifyTransactionPin() {


        Intent intent =
                new Intent(
                        FixedDepositActivity.this,
                        VerifyTransactionPinActivity.class
                );


        intent.putExtra(
                "paymentType",
                "FIXED_DEPOSIT"
        );


        intent.putExtra(
                "amount",
                Double.parseDouble(
                        etPrincipal
                                .getText()
                                .toString()
                                .trim()
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
                        etPrincipal
                                .getText()
                                .toString()
                                .trim()
                );


        double rate =
                Double.parseDouble(
                        etInterest
                                .getText()
                                .toString()
                                .trim()
                );


        int tenure =
                Integer.parseInt(
                        etTenure
                                .getText()
                                .toString()
                                .trim()
                );


        CreateFixedDepositRequest request =
                new CreateFixedDepositRequest(

                        principal,

                        rate,

                        tenure,

                        tenureType,

                        interestType,

                        compounding
                );


        //==================================================
        // Create FD API
        //==================================================

        repository
                .createFixedDeposit(request)
                .enqueue(
                        new Callback<CreateFixedDepositResponse>() {

                            @Override
                            public void onResponse(
                                    Call<CreateFixedDepositResponse> call,
                                    Response<CreateFixedDepositResponse> response) {


                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {


                                    showSuccessDialog(
                                            response.body()
                                    );


                                } else {


                                    String message =
                                            "Unable to create Fixed Deposit.";


                                    if (response.body() != null
                                            && response.body()
                                            .getMessage() != null) {

                                        message =
                                                response.body()
                                                        .getMessage();

                                    }


                                    new AlertDialog.Builder(
                                            FixedDepositActivity.this
                                    )

                                            .setTitle(
                                                    "Fixed Deposit Failed"
                                            )

                                            .setMessage(
                                                    message
                                            )

                                            .setPositiveButton(
                                                    "OK",
                                                    null
                                            )

                                            .show();

                                }

                            }


                            @Override
                            public void onFailure(
                                    Call<CreateFixedDepositResponse> call,
                                    Throwable t) {


                                new AlertDialog.Builder(
                                        FixedDepositActivity.this
                                )

                                        .setTitle(
                                                "Network Error"
                                        )

                                        .setMessage(
                                                t.getMessage()
                                        )

                                        .setPositiveButton(
                                                "OK",
                                                null
                                        )

                                        .show();

                            }

                        }
                );

    }


    //==================================================
    // Success Dialog
    //==================================================

    private void showSuccessDialog(
            CreateFixedDepositResponse response) {


        StringBuilder builder =
                new StringBuilder();


        builder.append(
                "🎉 Fixed Deposit Created Successfully\n\n"
        );


        builder.append(
                        "FD Number : "
                )
                .append(
                        response.getFdNumber()
                );


        builder.append(
                        "\n\nAccount Number : "
                )
                .append(
                        response.getAccountNumber()
                );


        builder.append(
                        "\n\nPrincipal Amount : ₹"
                )
                .append(
                        formatter.format(
                                response.getPrincipal()
                        )
                );


        builder.append(
                        "\n\nInterest Earned : ₹"
                )
                .append(
                        formatter.format(
                                response.getInterestEarned()
                        )
                );


        builder.append(
                        "\n\nMaturity Amount : ₹"
                )
                .append(
                        formatter.format(
                                response.getMaturityAmount()
                        )
                );


        builder.append(
                        "\n\nMaturity Date : "
                )
                .append(
                        response.getMaturityDate()
                );


        builder.append(
                        "\n\nStatus : "
                )
                .append(
                        response.getStatus()
                );


        //==================================================
        // Success Dialog
        //==================================================

        new AlertDialog.Builder(this)

                .setTitle("Success")

                .setCancelable(false)

                .setMessage(
                        builder.toString()
                )

                .setPositiveButton(
                        "Done",
                        (dialog, which) -> {

                            resetForm();

                        }
                )

                .show();

    }

}