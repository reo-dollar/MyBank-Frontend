package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.databinding.ActivityMobileRechargeBinding;
import com.rohit.mybank.repository.MobileRechargeRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.model.recharge.MobileRechargeRequest;
import com.rohit.mybank.model.recharge.MobileRechargeResponse;


import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileRechargeActivity extends AppCompatActivity {

    private ActivityMobileRechargeBinding binding;

    private MobileRechargeRepository repository;

    private String mobile = "";

    private String operator = "";

    private String circle = "";

    private double rechargeAmount = 0;

    private final ActivityResultLauncher<Intent> pinVerificationLauncher =
            registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),

                    result -> {

                        if (result.getResultCode() != RESULT_OK) {
                            return;
                        }

                        if (result.getData() == null) {
                            return;
                        }

                        boolean verified =
                                result.getData().getBooleanExtra(
                                        VerifyTransactionPinActivity.EXTRA_PIN_VERIFIED,
                                        false
                                );

                        if (verified) {

                            performRecharge();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityMobileRechargeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        repository =
                new MobileRechargeRepository(this);

        initializeViews();

        loadOperators();

        loadCircles();

        setupListeners();

    }

    private void initializeViews() {

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnRecharge.setEnabled(false);

    }
    // =====================================================
// Load Operators
// =====================================================

    private void loadOperators() {

        List<String> operators = Arrays.asList(

                "Select Operator",

                "Airtel",

                "Jio",

                "Vi",

                "BSNL",

                "MTNL"

        );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_dropdown_item_1line,

                        operators

                );

        binding.actOperator.setAdapter(adapter);

    }

// =====================================================
// Load Circles
// =====================================================

    private void loadCircles() {

        List<String> circles = Arrays.asList(

                "Select Circle",

                "Maharashtra",

                "Delhi",

                "Gujarat",

                "Karnataka",

                "Tamil Nadu",

                "Kerala",

                "Punjab",

                "Rajasthan",

                "Uttar Pradesh",

                "Madhya Pradesh",

                "West Bengal"

        );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_dropdown_item_1line,

                        circles

                );

        binding.actCircle.setAdapter(adapter);

    }

// =====================================================
// Listeners
// =====================================================

    private void setupListeners() {

        binding.etMobileNumber.addTextChangedListener(

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

                    }

                    @Override
                    public void afterTextChanged(
                            Editable editable
                    ) {

                        updateSummary();

                    }

                }

        );

        binding.etAmount.addTextChangedListener(

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

                    }

                    @Override
                    public void afterTextChanged(
                            Editable editable
                    ) {

                        updateSummary();

                    }

                }

        );

        binding.actOperator.setOnItemClickListener(

                (parent, view, position, id) ->

                        updateSummary()

        );

        binding.actCircle.setOnItemClickListener(

                (parent, view, position, id) ->

                        updateSummary()

        );

        binding.btnRecharge.setOnClickListener(

                v -> {

                    if (validateInput()) {

                        showConfirmation();

                    }

                }

        );

    }

// =====================================================
// Update Summary
// =====================================================

    private void updateSummary() {

        mobile = "";

        operator = "";

        circle = "";

        rechargeAmount = 0;

        if (binding.etMobileNumber.getText() != null) {

            mobile = binding.etMobileNumber
                    .getText()
                    .toString()
                    .trim();

        }

        if (binding.actOperator.getText() != null) {

            operator = binding.actOperator
                    .getText()
                    .toString()
                    .trim();

        }

        if (binding.actCircle.getText() != null) {

            circle = binding.actCircle
                    .getText()
                    .toString()
                    .trim();

        }

        if (binding.etAmount.getText() != null &&
                !binding.etAmount.getText().toString().trim().isEmpty()) {

            rechargeAmount = Double.parseDouble(

                    binding.etAmount
                            .getText()
                            .toString()
                            .trim()

            );

        }

        binding.tvSummaryMobile.setText(mobile);

        binding.tvSummaryOperator.setText(operator);

        binding.tvSummaryCircle.setText(circle);

        binding.tvSummaryAmount.setText(

                "₹" + rechargeAmount

        );

        binding.btnRecharge.setEnabled(

                !mobile.isEmpty()
                        && !operator.equals("Select Operator")
                        && !circle.equals("Select Circle")
                        && rechargeAmount > 0

        );

    }
    // =====================================================
// Validate Input
// =====================================================

    private boolean validateInput() {

        if (mobile.isEmpty()) {

            binding.layoutMobileNumber.setError(
                    "Enter Mobile Number"
            );

            return false;

        }

        binding.layoutMobileNumber.setError(null);

        if (!mobile.matches("\\d{10}")) {

            binding.layoutMobileNumber.setError(
                    "Enter Valid 10-digit Mobile Number"
            );

            return false;

        }

        if (operator.isEmpty()
                || operator.equals("Select Operator")) {

            Toast.makeText(
                    this,
                    "Select Operator",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        if (circle.isEmpty()
                || circle.equals("Select Circle")) {

            Toast.makeText(
                    this,
                    "Select Circle",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        if (rechargeAmount <= 0) {

            binding.layoutAmount.setError(
                    "Enter Recharge Amount"
            );

            return false;

        }

        binding.layoutAmount.setError(null);

        return true;

    }

// =====================================================
// Confirmation Dialog
// =====================================================

    private void showConfirmation() {

        String message =

                "Mobile Number : " + mobile +

                        "\n\nOperator : " + operator +

                        "\nCircle : " + circle +

                        "\nAmount : ₹" + rechargeAmount +

                        "\n\nDo you want to continue?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Mobile Recharge")

                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(

                        "Recharge",

                        (dialog, which) ->

                                new PaymentSecurityHelper(

                                        MobileRechargeActivity.this,

                                        pinVerificationLauncher,

                                        new PaymentCallback() {

                                            @Override
                                            public void onSuccess() {

                                                performRecharge();

                                            }

                                        }

                                ).verifyPayment()

                )

                .show();

    }
    // =====================================================
// Perform Recharge
// =====================================================

    private void performRecharge() {

        MobileRechargeRequest request =
                new MobileRechargeRequest();

        request.setMobileNumber(mobile);

        request.setOperator(operator);

        request.setCircle(circle);

        request.setAmount(rechargeAmount);

        showLoading(true);

        repository.recharge(request)

                .enqueue(new Callback<MobileRechargeResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<MobileRechargeResponse> call,
                            @NonNull Response<MobileRechargeResponse> response) {

                        showLoading(false);

                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    MobileRechargeActivity.this,
                                    "Server Error : " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        if (response.body() == null) {

                            Toast.makeText(
                                    MobileRechargeActivity.this,
                                    "Empty server response.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        MobileRechargeResponse rechargeResponse =
                                response.body();

                        if (rechargeResponse.isSuccess()) {

                            showSuccessDialog(rechargeResponse);

                        } else {

                            showErrorDialog(
                                    rechargeResponse.getMessage()
                            );

                        }

                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<MobileRechargeResponse> call,
                            @NonNull Throwable t) {

                        showLoading(false);

                        Toast.makeText(
                                MobileRechargeActivity.this,
                                "Network Error\n\n"
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

// =====================================================
// Loading
// =====================================================

    private void showLoading(boolean loading) {

        binding.progressBar.setVisibility(

                loading
                        ? View.VISIBLE
                        : View.GONE

        );

        binding.btnRecharge.setEnabled(!loading);

    }
    // =====================================================
// Success Dialog
// =====================================================

    private void showSuccessDialog(
            MobileRechargeResponse response
    ) {

        String receipt =

                "✓ " + response.getMessage() +

                        "\n\nPayment ID : " + response.getPaymentId() +

                        "\n\nMobile Number : " + mobile +

                        "\nOperator : " + operator +

                        "\nCircle : " + circle +

                        "\nAmount : ₹" + rechargeAmount +

                        "\n\nStatus : SUCCESS";

        new AlertDialog.Builder(this)

                .setTitle("Recharge Successful")

                .setCancelable(false)

                .setMessage(receipt)

                .setPositiveButton(

                        "Done",

                        (dialog, which) -> {

                            dialog.dismiss();

                            finish();

                        }

                )

                .show();

    }

// =====================================================
// Error Dialog
// =====================================================

    private void showErrorDialog(
            String message
    ) {

        if (message == null || message.trim().isEmpty()) {

            message = "Recharge failed.";

        }

        new AlertDialog.Builder(this)

                .setTitle("Recharge Failed")

                .setMessage(message)

                .setPositiveButton(

                        "OK",

                        (dialog, which) -> dialog.dismiss()

                )

                .show();

    }

// =====================================================
// Activity Destroy
// =====================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        binding = null;

    }

}