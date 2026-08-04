package com.rohit.mybank.activities.payments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.repository.MobileRechargeRepository;
import androidx.appcompat.app.AlertDialog;
import com.rohit.mybank.model.recharge.MobileRechargeRequest;
import com.rohit.mybank.model.recharge.MobileRechargeResponse;

public class MobileRechargeActivity extends AppCompatActivity {

    //=============================
    // Input Layouts
    //=============================

    private TextInputLayout layoutMobile;
    private TextInputLayout layoutOperator;
    private TextInputLayout layoutCircle;
    private TextInputLayout layoutAmount;

    //=============================
    // EditTexts
    //=============================

    private TextInputEditText etMobileNumber;
    private TextInputEditText etAmount;

    //=============================
    // Dropdowns
    //=============================

    private AutoCompleteTextView actOperator;
    private AutoCompleteTextView actCircle;

    //=============================
    // Summary Views
    //=============================

    private TextView tvSummaryMobile;
    private TextView tvSummaryOperator;
    private TextView tvSummaryCircle;
    private TextView tvSummaryAmount;

    //=============================
    // Button
    //=============================

    private MaterialButton btnRecharge;

    //=============================
    // Repository
    //=============================

    private MobileRechargeRepository repository;

    //=============================
    // Recharge Data
    //=============================

    private String mobile;
    private String operator;
    private String circle;
    private int rechargeAmount;

    //=============================
    // PIN Launcher
    //=============================

    private final ActivityResultLauncher<Intent> pinVerificationLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            performRecharge();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);

        initializeViews();

        repository = new MobileRechargeRepository(this);

        loadDropdowns();

        setupSummaryListeners();

        updateSummary();

        btnRecharge.setOnClickListener(v -> validateInputs());
    }

    private void initializeViews() {

        layoutMobile = findViewById(R.id.layoutMobile);
        layoutOperator = findViewById(R.id.layoutOperator);
        layoutCircle = findViewById(R.id.layoutCircle);
        layoutAmount = findViewById(R.id.layoutAmount);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAmount = findViewById(R.id.etAmount);

        actOperator = findViewById(R.id.actOperator);
        actCircle = findViewById(R.id.actCircle);

        tvSummaryMobile = findViewById(R.id.tvSummaryMobile);
        tvSummaryOperator = findViewById(R.id.tvSummaryOperator);
        tvSummaryCircle = findViewById(R.id.tvSummaryCircle);
        tvSummaryAmount = findViewById(R.id.tvSummaryAmount);

        btnRecharge = findViewById(R.id.btnRecharge);
    }

    private void loadDropdowns() {

        String[] operators = {
                "Airtel",
                "Jio",
                "Vi",
                "BSNL"
        };

        String[] circles = {
                "Maharashtra",
                "Delhi",
                "Karnataka",
                "Tamil Nadu",
                "Gujarat",
                "Punjab",
                "Rajasthan",
                "West Bengal"
        };

        ArrayAdapter<String> operatorAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        operators);

        actOperator.setAdapter(operatorAdapter);

        actOperator.setOnItemClickListener((parent, view, position, id) ->
                updateSummary());

        ArrayAdapter<String> circleAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        circles);

        actCircle.setAdapter(circleAdapter);

        actCircle.setOnItemClickListener((parent, view, position, id) ->
                updateSummary());
    }

    private void setupSummaryListeners() {

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

        etMobileNumber.addTextChangedListener(watcher);
        etAmount.addTextChangedListener(watcher);
    }

    private void updateSummary() {

        String mobileValue =
                etMobileNumber.getText() == null
                        ? ""
                        : etMobileNumber.getText().toString().trim();

        String operatorValue =
                actOperator.getText().toString().trim();

        String circleValue =
                actCircle.getText().toString().trim();

        String amountValue =
                etAmount.getText() == null
                        ? ""
                        : etAmount.getText().toString().trim();

        tvSummaryMobile.setText(
                "Mobile : " +
                        (mobileValue.isEmpty() ? "-" : mobileValue));

        tvSummaryOperator.setText(
                "Operator : " +
                        (operatorValue.isEmpty() ? "-" : operatorValue));

        tvSummaryCircle.setText(
                "Circle : " +
                        (circleValue.isEmpty() ? "-" : circleValue));

        tvSummaryAmount.setText(
                "Amount : ₹" +
                        (amountValue.isEmpty() ? "0" : amountValue));
    }
    //=========================================================
    // Validate Inputs
    //=========================================================

    private void validateInputs() {

        layoutMobile.setError(null);
        layoutOperator.setError(null);
        layoutCircle.setError(null);
        layoutAmount.setError(null);

        mobile = etMobileNumber.getText() == null
                ? ""
                : etMobileNumber.getText().toString().trim();

        operator = actOperator.getText().toString().trim();

        circle = actCircle.getText().toString().trim();

        String amountText = etAmount.getText() == null
                ? ""
                : etAmount.getText().toString().trim();

        if (mobile.isEmpty()) {
            layoutMobile.setError("Enter mobile number");
            return;
        }

        if (mobile.length() != 10) {
            layoutMobile.setError("Mobile number must be 10 digits");
            return;
        }

        if (operator.isEmpty()) {
            layoutOperator.setError("Select operator");
            return;
        }

        if (circle.isEmpty()) {
            layoutCircle.setError("Select circle");
            return;
        }

        if (amountText.isEmpty()) {
            layoutAmount.setError("Enter recharge amount");
            return;
        }

        rechargeAmount = Integer.parseInt(amountText);

        if (rechargeAmount < 10) {
            layoutAmount.setError("Minimum recharge amount is ₹10");
            return;
        }

        showConfirmation();
    }

    //=========================================================
    // Confirmation Dialog
    //=========================================================

    private void showConfirmation() {

        String message =
                "Mobile Number : " + mobile +
                        "\n\nOperator : " + operator +
                        "\nCircle : " + circle +
                        "\nAmount : ₹" + rechargeAmount +
                        "\n\nProceed with recharge?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Mobile Recharge")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Recharge", (dialog, which) -> {

                    Intent intent = new Intent(
                            MobileRechargeActivity.this,
                            VerifyTransactionPinActivity.class
                    );

                    pinVerificationLauncher.launch(intent);

                })
                .show();
    }

    //=========================================================
    // Perform Recharge
    //=========================================================

    private void performRecharge() {

        MobileRechargeRequest request =
                new MobileRechargeRequest(
                        mobile,
                        operator,
                        circle,
                        rechargeAmount
                );

        repository.mobileRecharge(request)
                .enqueue(new retrofit2.Callback<MobileRechargeResponse>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<MobileRechargeResponse> call,
                            retrofit2.Response<MobileRechargeResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            MobileRechargeResponse rechargeResponse =
                                    response.body();

                            showSuccessDialog(rechargeResponse);

                        } else {

                            try {

                                String error =
                                        response.errorBody() != null
                                                ? response.errorBody().string()
                                                : "Recharge failed.";

                                new AlertDialog.Builder(
                                        MobileRechargeActivity.this)
                                        .setTitle("Recharge Failed")
                                        .setMessage(error)
                                        .setPositiveButton("OK", null)
                                        .show();

                            } catch (Exception e) {

                                Toast.makeText(
                                        MobileRechargeActivity.this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            }
                        }

                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<MobileRechargeResponse> call,
                            Throwable t) {

                        new AlertDialog.Builder(
                                MobileRechargeActivity.this)
                                .setTitle("Network Error")
                                .setMessage(t.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                    }

                });
    }

    //=========================================================
    // Success Dialog
    //=========================================================

    private void showSuccessDialog(MobileRechargeResponse response) {

        String receipt =
                "✓ " + response.getMessage() +
                        "\n\nPayment ID : " + response.getPaymentId() +
                        "\n\nMobile Number : " + mobile +
                        "\nOperator : " + operator +
                        "\nCircle : " + circle +
                        "\nAmount : ₹" + rechargeAmount;

        new AlertDialog.Builder(this)
                .setTitle("Recharge Successful")
                .setCancelable(false)
                .setMessage(receipt)
                .setPositiveButton("Done", (dialog, which) -> finish())
                .show();
    }
}