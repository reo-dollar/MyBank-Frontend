package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.databinding.ActivityMobileRechargeBinding;
import com.rohit.mybank.model.recharge.MobileRechargeRequest;
import com.rohit.mybank.model.recharge.MobileRechargeResponse;
import com.rohit.mybank.repository.MobileRechargeRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileRechargeActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS AND REPOSITORY
    // ============================================================

    private ActivityMobileRechargeBinding binding;

    private MobileRechargeRepository repository;

    // ============================================================
    // RECHARGE DATA
    // ============================================================

    private String mobile = "";

    private String operator = "";

    private String circle = "";

    private double rechargeAmount = 0;

    private boolean rechargeInProgress = false;

    // ============================================================
    // TRANSACTION PIN VERIFICATION
    // ============================================================

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

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityMobileRechargeBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        repository = new MobileRechargeRepository(this);

        initializeViews();

        loadOperators();

        loadCircles();

        setupListeners();
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );

        binding.btnRecharge.setEnabled(false);

        binding.progressBar.setVisibility(View.GONE);
    }

    // ============================================================
    // LOAD OPERATORS
    // ============================================================

    private void loadOperators() {

        List<String> operators = Arrays.asList(
                "Select Operator",
                "Airtel",
                "Jio",
                "Vi",
                "BSNL",
                "MTNL"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                operators
        );

        binding.actOperator.setAdapter(adapter);
    }

    // ============================================================
    // LOAD CIRCLES
    // ============================================================

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                circles
        );

        binding.actCircle.setAdapter(adapter);
    }

    // ============================================================
    // SETUP LISTENERS
    // ============================================================

    private void setupListeners() {

        // --------------------------------------------------------
        // MOBILE NUMBER LISTENER
        // --------------------------------------------------------

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

                        binding.layoutMobileNumber.setError(null);

                        updateSummary();
                    }
                }
        );

        // --------------------------------------------------------
        // AMOUNT LISTENER
        // --------------------------------------------------------

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

                        binding.layoutAmount.setError(null);

                        updateSummary();
                    }
                }
        );

        // --------------------------------------------------------
        // OPERATOR LISTENER
        // --------------------------------------------------------

        binding.actOperator.setOnItemClickListener(
                (parent, view, position, id) -> updateSummary()
        );

        // --------------------------------------------------------
        // CIRCLE LISTENER
        // --------------------------------------------------------

        binding.actCircle.setOnItemClickListener(
                (parent, view, position, id) -> updateSummary()
        );

        // --------------------------------------------------------
        // RECHARGE BUTTON
        // --------------------------------------------------------

        binding.btnRecharge.setOnClickListener(v -> {

            if (rechargeInProgress) {
                return;
            }

            updateSummary();

            if (validateInput()) {

                hideKeyboard();

                showConfirmation();
            }
        });
    }

    // ============================================================
    // UPDATE RECHARGE SUMMARY
    // ============================================================

    private void updateSummary() {

        mobile = "";

        operator = "";

        circle = "";

        rechargeAmount = 0;

        // --------------------------------------------------------
        // MOBILE NUMBER
        // --------------------------------------------------------

        if (binding.etMobileNumber.getText() != null) {

            mobile = binding.etMobileNumber
                    .getText()
                    .toString()
                    .trim();
        }

        // --------------------------------------------------------
        // OPERATOR
        // --------------------------------------------------------

        if (binding.actOperator.getText() != null) {

            operator = binding.actOperator
                    .getText()
                    .toString()
                    .trim();
        }

        // --------------------------------------------------------
        // CIRCLE
        // --------------------------------------------------------

        if (binding.actCircle.getText() != null) {

            circle = binding.actCircle
                    .getText()
                    .toString()
                    .trim();
        }

        // --------------------------------------------------------
        // RECHARGE AMOUNT
        // --------------------------------------------------------

        if (binding.etAmount.getText() != null) {

            String amountText = binding.etAmount
                    .getText()
                    .toString()
                    .trim();

            if (!amountText.isEmpty()) {

                try {

                    rechargeAmount = Double.parseDouble(amountText);

                    if (Double.isNaN(rechargeAmount)
                            || Double.isInfinite(rechargeAmount)) {

                        rechargeAmount = 0;
                    }

                } catch (NumberFormatException ignored) {

                    rechargeAmount = 0;
                }
            }
        }

        // --------------------------------------------------------
        // SUMMARY VIEWS
        // --------------------------------------------------------

        binding.tvSummaryMobile.setText(mobile);

        binding.tvSummaryOperator.setText(operator);

        binding.tvSummaryCircle.setText(circle);

        binding.tvSummaryAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "₹%.2f",
                        rechargeAmount
                )
        );

        // --------------------------------------------------------
        // BUTTON STATE
        // --------------------------------------------------------

        boolean validInput =
                mobile.matches("\\d{10}")
                        && !operator.isEmpty()
                        && !operator.equals("Select Operator")
                        && !circle.isEmpty()
                        && !circle.equals("Select Circle")
                        && rechargeAmount > 0;

        binding.btnRecharge.setEnabled(
                validInput && !rechargeInProgress
        );
    }

    // ============================================================
    // VALIDATE INPUT
    // ============================================================

    private boolean validateInput() {

        // --------------------------------------------------------
        // MOBILE NUMBER
        // --------------------------------------------------------

        if (mobile.isEmpty()) {

            binding.layoutMobileNumber.setError(
                    "Enter Mobile Number"
            );

            binding.etMobileNumber.requestFocus();

            return false;
        }

        if (!mobile.matches("\\d{10}")) {

            binding.layoutMobileNumber.setError(
                    "Enter valid 10-digit mobile number"
            );

            binding.etMobileNumber.requestFocus();

            return false;
        }

        binding.layoutMobileNumber.setError(null);

        // --------------------------------------------------------
        // OPERATOR
        // --------------------------------------------------------

        if (operator.isEmpty()
                || operator.equals("Select Operator")) {

            Toast.makeText(
                    this,
                    "Please select an operator.",
                    Toast.LENGTH_SHORT
            ).show();

            binding.actOperator.requestFocus();

            binding.actOperator.showDropDown();

            return false;
        }

        // --------------------------------------------------------
        // CIRCLE
        // --------------------------------------------------------

        if (circle.isEmpty()
                || circle.equals("Select Circle")) {

            Toast.makeText(
                    this,
                    "Please select a telecom circle.",
                    Toast.LENGTH_SHORT
            ).show();

            binding.actCircle.requestFocus();

            binding.actCircle.showDropDown();

            return false;
        }

        // --------------------------------------------------------
        // AMOUNT
        // --------------------------------------------------------

        if (rechargeAmount <= 0
                || Double.isNaN(rechargeAmount)
                || Double.isInfinite(rechargeAmount)) {

            binding.layoutAmount.setError(
                    "Enter a valid recharge amount"
            );

            binding.etAmount.requestFocus();

            return false;
        }

        binding.layoutAmount.setError(null);

        return true;
    }

    // ============================================================
    // CONFIRMATION DIALOG
    // ============================================================

    private void showConfirmation() {

        String message =
                "Mobile Number: " + mobile
                        + "\n\nOperator: " + operator
                        + "\nCircle: " + circle
                        + "\nAmount: " + formatCurrency(rechargeAmount)
                        + "\n\nDo you want to continue?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Mobile Recharge")

                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Recharge",
                        (dialog, which) -> {

                            PaymentSecurityHelper securityHelper =
                                    new PaymentSecurityHelper(
                                            MobileRechargeActivity.this,
                                            pinVerificationLauncher,
                                            new PaymentCallback() {

                                                @Override
                                                public void onSuccess() {

                                                    performRecharge();
                                                }
                                            }
                                    );

                            securityHelper.verifyPayment();
                        }
                )

                .show();
    }

    // ============================================================
    // PERFORM RECHARGE
    // ============================================================

    private void performRecharge() {

        if (rechargeInProgress || isFinishing()) {
            return;
        }

        updateSummary();

        if (!validateInput()) {
            return;
        }

        MobileRechargeRequest rechargeRequest =
                new MobileRechargeRequest();

        rechargeRequest.setMobileNumber(mobile);

        rechargeRequest.setOperator(operator);

        rechargeRequest.setCircle(circle);

        rechargeRequest.setAmount(rechargeAmount);

        rechargeInProgress = true;

        showLoading(true);

        repository.recharge(rechargeRequest)
                .enqueue(new Callback<MobileRechargeResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<MobileRechargeResponse> call,
                            @NonNull Response<MobileRechargeResponse> response
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        rechargeInProgress = false;

                        showLoading(false);

                        // -----------------------------------------
                        // HTTP ERROR RESPONSE
                        // -----------------------------------------

                        if (!response.isSuccessful()) {

                            String errorMessage =
                                    extractRechargeError(response);

                            showErrorDialog(errorMessage);

                            return;
                        }

                        // -----------------------------------------
                        // EMPTY RESPONSE
                        // -----------------------------------------

                        if (response.body() == null) {

                            showErrorDialog(
                                    "Unable to process recharge. Please try again later."
                            );

                            return;
                        }

                        // -----------------------------------------
                        // PROCESS RESPONSE
                        // -----------------------------------------

                        MobileRechargeResponse rechargeResponse =
                                response.body();

                        if (rechargeResponse.isSuccess()) {

                            showSuccessDialog(rechargeResponse);

                        } else {

                            showErrorDialog(
                                    normalizeRechargeError(
                                            rechargeResponse.getMessage()
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<MobileRechargeResponse> call,
                            @NonNull Throwable throwable
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        rechargeInProgress = false;

                        showLoading(false);

                        if (call.isCanceled()) {
                            return;
                        }

                        showErrorDialog(
                                "Unable to connect to the server. Please try again."
                        );
                    }
                });
    }

    // ============================================================
    // EXTRACT ERROR FROM UNSUCCESSFUL HTTP RESPONSE
    // ============================================================

    private String extractRechargeError(
            Response<MobileRechargeResponse> response
    ) {

        String errorMessage = null;

        try {

            if (response.errorBody() != null) {

                String errorBody =
                        response.errorBody().string();

                errorMessage =
                        extractMessageFromBody(errorBody);
            }

        } catch (IOException ignored) {

            // Use the safe fallback message below.
        }

        String normalizedMessage =
                normalizeRechargeError(errorMessage);

        if (!normalizedMessage.equals(
                "Unable to process recharge. Please try again later."
        )) {

            return normalizedMessage;
        }

        // --------------------------------------------------------
        // AUTHENTICATION ERRORS
        // --------------------------------------------------------

        if (response.code() == 401
                || response.code() == 403) {

            return "Your session may have expired. Please log in again.";
        }

        // --------------------------------------------------------
        // GENERIC SERVER ERROR
        // --------------------------------------------------------

        return "Unable to process recharge. Please try again later.";
    }

    // ============================================================
    // EXTRACT MESSAGE FROM ERROR BODY
    // ============================================================

    private String extractMessageFromBody(String body) {

        if (body == null || body.trim().isEmpty()) {
            return null;
        }

        String trimmedBody = body.trim();

        // --------------------------------------------------------
        // PLAIN-TEXT RESPONSE
        // --------------------------------------------------------

        if (!trimmedBody.startsWith("{")) {

            return trimmedBody;
        }

        // --------------------------------------------------------
        // JSON RESPONSE
        // --------------------------------------------------------

        try {

            JSONObject json =
                    new JSONObject(trimmedBody);

            String[] possibleKeys = {
                    "message",
                    "error",
                    "detail",
                    "description"
            };

            for (String key : possibleKeys) {

                if (json.has(key) && !json.isNull(key)) {

                    String value =
                            json.optString(key, "").trim();

                    if (!value.isEmpty()) {
                        return value;
                    }
                }
            }

        } catch (JSONException ignored) {

            // Fall through to the safe fallback.
        }

        return null;
    }

    // ============================================================
    // NORMALIZE RECHARGE ERROR
    // ============================================================

    private String normalizeRechargeError(String message) {

        if (message == null || message.trim().isEmpty()) {

            return "Unable to process recharge. Please try again later.";
        }

        String normalized =
                message.trim().toLowerCase(Locale.ROOT);

        // --------------------------------------------------------
        // INSUFFICIENT BALANCE
        // --------------------------------------------------------

        if (normalized.contains("insufficient balance")
                || normalized.contains("insufficient funds")
                || normalized.contains("low balance")
                || normalized.contains("not enough balance")
                || normalized.contains("not enough funds")) {

            return "Insufficient balance";
        }

        // --------------------------------------------------------
        // INVALID RECHARGE DETAILS
        // --------------------------------------------------------

        if (normalized.contains("invalid mobile")) {

            return "Please enter a valid mobile number.";
        }

        if (normalized.contains("invalid operator")) {

            return "Please select a valid mobile operator.";
        }

        if (normalized.contains("invalid circle")) {

            return "Please select a valid telecom circle.";
        }

        // --------------------------------------------------------
        // SERVER ERRORS
        // --------------------------------------------------------

        if (normalized.matches(".*\\b5\\d\\d\\b.*")
                || normalized.contains("internal server error")
                || normalized.contains("server error")
                || normalized.contains("exception")
                || normalized.contains("stacktrace")) {

            return "Unable to process recharge. Please try again later.";
        }

        /*
         * Avoid exposing arbitrary backend exception details
         * to the customer.
         */
        return "Unable to process recharge. Please try again later.";
    }

    // ============================================================
    // LOADING STATE
    // ============================================================

    private void showLoading(boolean loading) {

        if (binding == null) {
            return;
        }

        binding.progressBar.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        binding.btnRecharge.setEnabled(
                !loading
                        && mobile.matches("\\d{10}")
                        && !operator.isEmpty()
                        && !operator.equals("Select Operator")
                        && !circle.isEmpty()
                        && !circle.equals("Select Circle")
                        && rechargeAmount > 0
        );
    }

    // ============================================================
    // SUCCESS DIALOG
    // ============================================================

    private void showSuccessDialog(
            MobileRechargeResponse response
    ) {

        String receipt =
                "✓ " + response.getMessage()
                        + "\n\nPayment ID: " + response.getPaymentId()
                        + "\n\nMobile Number: " + mobile
                        + "\nOperator: " + operator
                        + "\nCircle: " + circle
                        + "\nAmount: " + formatCurrency(rechargeAmount)
                        + "\n\nStatus: SUCCESS";

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

    // ============================================================
    // ERROR DIALOG
    // ============================================================

    private void showErrorDialog(String message) {

        if (isFinishing() || isDestroyed()) {
            return;
        }

        if (message == null || message.trim().isEmpty()) {

            message =
                    "Unable to process recharge. Please try again later.";
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

    // ============================================================
    // FORMAT CURRENCY
    // ============================================================

    private String formatCurrency(double amount) {

        return String.format(
                Locale.getDefault(),
                "₹%.2f",
                amount
        );
    }

    // ============================================================
    // HIDE KEYBOARD
    // ============================================================

    private void hideKeyboard() {

        if (binding == null) {
            return;
        }

        InputMethodManager inputMethodManager =
                (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {

            inputMethodManager.hideSoftInputFromWindow(
                    binding.getRoot().getWindowToken(),
                    0
            );
        }
    }

    // ============================================================
    // ACTIVITY DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        if (binding != null) {

            binding.getRoot().clearFocus();

            binding = null;
        }

        super.onDestroy();
    }
}