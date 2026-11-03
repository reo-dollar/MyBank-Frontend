package com.rohit.mybank.activities.banking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.withdraw.WithdrawRequest;
import com.rohit.mybank.model.withdraw.WithdrawResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.WithdrawRepository;
import com.rohit.mybank.utils.CurrencyUtil;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final double MAX_WITHDRAW =
            1_000_000.00;


    // =========================================================
    // VIEWS
    // =========================================================

    private TextView tvAccountNumber;

    private TextView tvBalance;

    private TextInputEditText etAmount;

    private MaterialButton btnWithdraw;

    private ProgressBar progressBar;


    // =========================================================
    // REPOSITORIES
    // =========================================================

    private DashboardRepository dashboardRepository;

    private WithdrawRepository withdrawRepository;


    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    private String accountNumber = "";


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_withdraw
        );


        // -----------------------------------------------------
        // INITIALIZE
        // -----------------------------------------------------

        initializeViews();


        dashboardRepository =
                new DashboardRepository(this);

        withdrawRepository =
                new WithdrawRepository(this);


        // -----------------------------------------------------
        // LOAD ACCOUNT
        // -----------------------------------------------------

        loadAccount();


        // -----------------------------------------------------
        // WITHDRAW BUTTON
        // -----------------------------------------------------

        btnWithdraw.setOnClickListener(
                v -> withdrawMoney()
        );
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );


        tvBalance =
                findViewById(
                        R.id.tvBalance
                );


        etAmount =
                findViewById(
                        R.id.etAmount
                );


        btnWithdraw =
                findViewById(
                        R.id.btnWithdraw
                );


        progressBar =
                findViewById(
                        R.id.progressBar
                );
    }


    // =========================================================
    // LOAD ACCOUNT
    // =========================================================

    private void loadAccount() {

        dashboardRepository
                .getMyAccount()
                .enqueue(
                        new Callback<DashboardResponse>() {

                            @Override
                            public void onResponse(
                                    Call<DashboardResponse> call,
                                    Response<DashboardResponse> response
                            ) {

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                ) {

                                    DashboardResponse dashboard =
                                            response.body();


                                    accountNumber =
                                            dashboard.getAccNo();


                                    tvAccountNumber.setText(
                                            accountNumber
                                    );


                                    tvBalance.setText(
                                            CurrencyUtil.format(
                                                    dashboard.getBalance()
                                            )
                                    );

                                } else {

                                    Toast.makeText(
                                            WithdrawActivity.this,
                                            "Unable to load account details.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<DashboardResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        WithdrawActivity.this,
                                        "Network error. Please check your connection.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // START WITHDRAWAL
    // =========================================================

    private void withdrawMoney() {

        String amountText = "";


        if (etAmount.getText() != null) {

            amountText =
                    etAmount.getText()
                            .toString()
                            .trim();
        }


        // -----------------------------------------------------
        // EMPTY AMOUNT
        // -----------------------------------------------------

        if (TextUtils.isEmpty(amountText)) {

            etAmount.setError(
                    "Please enter withdrawal amount"
            );

            etAmount.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // PARSE AMOUNT
        // -----------------------------------------------------

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            amountText
                    );

        } catch (NumberFormatException e) {

            etAmount.setError(
                    "Invalid amount"
            );

            etAmount.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // AMOUNT > 0
        // -----------------------------------------------------

        if (amount <= 0) {

            etAmount.setError(
                    "Amount must be greater than ₹0"
            );

            etAmount.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // MAXIMUM WITHDRAWAL
        // -----------------------------------------------------

        if (amount > MAX_WITHDRAW) {

            etAmount.setError(
                    "Maximum withdrawal limit is ₹10,00,000"
            );

            etAmount.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // ACCOUNT CHECK
        // -----------------------------------------------------

        if (
                TextUtils.isEmpty(accountNumber)
        ) {

            Toast.makeText(
                    this,
                    "Account information is not available.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // -----------------------------------------------------
        // FINAL AMOUNT
        // -----------------------------------------------------

        double finalAmount = amount;


        // =========================================================
        // TRANSACTION PIN
        // =========================================================

        PinVerificationDialog.showForTransactionPin(
                WithdrawActivity.this,

                new PinVerificationDialog.OnPinVerifiedWithPinListener() {

                    @Override
                    public void onSuccess(String transactionPin) {

                        performWithdraw(
                                finalAmount,
                                transactionPin
                        );
                    }


                    @Override
                    public void onFailure() {

                        Toast.makeText(
                                WithdrawActivity.this,
                                "Transaction PIN verification failed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // PERFORM WITHDRAW
    // =========================================================

    private void performWithdraw(
            double amount,
            String transactionPin
    ) {

        // -----------------------------------------------------
        // SAFETY CHECK
        // -----------------------------------------------------

        if (
                TextUtils.isEmpty(transactionPin)
                        || !transactionPin.matches("\\d{6}")
        ) {

            Toast.makeText(
                    this,
                    "Invalid Transaction PIN.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // -----------------------------------------------------
        // CREATE REQUEST
        // -----------------------------------------------------

        WithdrawRequest request =
                new WithdrawRequest();


        request.setAccNo(
                accountNumber
        );


        request.setAmount(
                amount
        );


        // -----------------------------------------------------
        // TRANSACTION PIN
        // -----------------------------------------------------

        request.setTransactionPin(
                transactionPin
        );


        // -----------------------------------------------------
        // LOADING
        // -----------------------------------------------------

        progressBar.setVisibility(
                View.VISIBLE
        );

        btnWithdraw.setEnabled(
                false
        );


        // =====================================================
        // API CALL
        // =====================================================

        withdrawRepository
                .withdraw(request)
                .enqueue(
                        new Callback<WithdrawResponse>() {

                            @Override
                            public void onResponse(
                                    Call<WithdrawResponse> call,
                                    Response<WithdrawResponse> response
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                btnWithdraw.setEnabled(
                                        true
                                );


                                // =================================
                                // SUCCESS
                                // =================================

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                ) {

                                    WithdrawResponse withdraw =
                                            response.body();


                                    // -----------------------------
                                    // UPDATE BALANCE
                                    // -----------------------------

                                    tvBalance.setText(
                                            CurrencyUtil.format(
                                                    withdraw.getBalance()
                                            )
                                    );


                                    // -----------------------------
                                    // CLEAR AMOUNT
                                    // -----------------------------

                                    etAmount.setText("");


                                    // -----------------------------
                                    // SUCCESS MESSAGE
                                    // -----------------------------

                                    Toast.makeText(
                                            WithdrawActivity.this,
                                            CurrencyUtil.format(amount)
                                                    + " withdrawn successfully.",
                                            Toast.LENGTH_LONG
                                    ).show();


                                    // -----------------------------
                                    // GO TO DASHBOARD
                                    // -----------------------------

                                    Intent intent =
                                            new Intent(
                                                    WithdrawActivity.this,
                                                    DashboardActivity.class
                                            );


                                    intent.addFlags(
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    );


                                    startActivity(intent);

                                    finish();

                                    return;
                                }


                                // =================================
                                // SERVER / BUSINESS ERROR
                                // =================================

                                String errorMessage =
                                        getErrorMessage(response);

                                showErrorDialog(
                                        "Withdrawal Failed",
                                        errorMessage
                                );
                            }


                            // =====================================
                            // NETWORK FAILURE
                            // =====================================

                            @Override
                            public void onFailure(
                                    Call<WithdrawResponse> call,
                                    Throwable t
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                btnWithdraw.setEnabled(
                                        true
                                );


                                if (!call.isCanceled() && !isFinishing() && !isDestroyed()) {
                                    showErrorDialog(
                                            "Withdrawal Failed",
                                            "Network error. Please check your internet connection."
                                    );
                                }
                            }
                        }
                );
    }


    // =========================================================
    // EXTRACT ERROR MESSAGE
    // =========================================================

    private String getErrorMessage(Response<?> response) {
        String defaultMessage = "Withdrawal failed. Please try again.";

        if (response == null || response.errorBody() == null) {
            return defaultMessage;
        }

        try {
            String errorBody = response.errorBody().string();

            if (TextUtils.isEmpty(errorBody)) {
                return defaultMessage;
            }

            String message = extractMessageFromErrorBody(errorBody);
            if (TextUtils.isEmpty(message)) {
                return defaultMessage;
            }

            return normalizeWithdrawalError(message);

        } catch (IOException e) {
            return defaultMessage;
        }
    }

    private String extractMessageFromErrorBody(String errorBody) {
        String body = errorBody == null ? "" : errorBody.trim();

        if (TextUtils.isEmpty(body)) {
            return "";
        }

        try {
            JSONObject jsonObject = new JSONObject(body);

            String[] fields = {"message", "error", "detail", "description"};
            for (String field : fields) {
                if (jsonObject.has(field) && !jsonObject.isNull(field)) {
                    Object value = jsonObject.get(field);
                    if (value instanceof String) {
                        String message = ((String) value).trim();
                        if (!TextUtils.isEmpty(message)
                                && !"Bad Request".equalsIgnoreCase(message)
                                && !"Internal Server Error".equalsIgnoreCase(message)) {
                            return message;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // The response may be plain text instead of JSON.
        }

        // Use a readable plain-text response when the server provides one.
        if (!body.startsWith("{") && !body.startsWith("[")
                && body.length() <= 500) {
            return body;
        }

        return "";
    }

    private String normalizeWithdrawalError(String message) {
        String normalized = message.trim().toLowerCase(java.util.Locale.ROOT);

        if (normalized.contains("insufficient balance")
                || normalized.contains("insufficient account balance")
                || normalized.contains("not enough balance")
                || normalized.contains("low balance")) {
            return "Insufficient balance.";
        }

        return message.trim();
    }

    private void showErrorDialog(String title, String message) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(TextUtils.isEmpty(message)
                        ? "Withdrawal failed. Please try again."
                        : message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
