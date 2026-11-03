package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import com.rohit.mybank.R;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositRequest;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenRDActivity extends AppCompatActivity {

    // ==================================================
    // FIXED RD INTEREST RATE
    // ==================================================

    private static final double FIXED_RD_INTEREST_RATE = 7.00;

    // ==================================================
    // VIEWS
    // ==================================================

    private TextInputEditText etAccountNumber;
    private TextInputEditText etMonthlyInstallment;
    private TextInputEditText etInterestRate;
    private TextInputEditText etTenure;
    private MaterialSwitch switchAutoDebit;
    private MaterialButton btnOpenRD;

    // ==================================================
    // REPOSITORIES
    // ==================================================

    private DashboardRepository dashboardRepository;
    private RecurringDepositRepository recurringDepositRepository;

    // ==================================================
    // ACCOUNT STATE
    // ==================================================

    private String accountNumber = "";
    private boolean accountLoaded = false;
    private Call<DashboardResponse> accountCall;
    private Call<CreateRecurringDepositResponse> createRdCall;

    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_rd);

        initializeViews();

        dashboardRepository = new DashboardRepository(this);
        recurringDepositRepository = new RecurringDepositRepository(this);

        setupClickListeners();
        loadAccountNumber();
    }

    // ==================================================
    // INITIALIZE VIEWS
    // ==================================================

    private void initializeViews() {
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etMonthlyInstallment = findViewById(R.id.etMonthlyInstallment);
        etInterestRate = findViewById(R.id.etInterestRate);
        etTenure = findViewById(R.id.etTenure);
        switchAutoDebit = findViewById(R.id.switchAutoDebit);
        btnOpenRD = findViewById(R.id.btnOpenRD);

        // Account number is fetched from the logged-in user's account.
        // Prevent manual typing/editing.
        etAccountNumber.setFocusable(false);
        etAccountNumber.setFocusableInTouchMode(false);
        etAccountNumber.setClickable(false);
        etAccountNumber.setLongClickable(false);

        // The bank controls the RD interest rate; users cannot edit it.
        etInterestRate.setText("7.00");
        etInterestRate.setFocusable(false);
        etInterestRate.setFocusableInTouchMode(false);
        etInterestRate.setClickable(false);
        etInterestRate.setLongClickable(false);

        // Do not allow an RD to be opened before account details load.
        btnOpenRD.setEnabled(false);
    }

    // ==================================================
    // CLICK LISTENERS
    // ==================================================

    private void setupClickListeners() {
        btnOpenRD.setOnClickListener(v -> openRecurringDeposit());
    }

    // ==================================================
    // LOAD ACCOUNT NUMBER AUTOMATICALLY
    // ==================================================

    private void loadAccountNumber() {
        btnOpenRD.setEnabled(false);

        accountCall = dashboardRepository.getMyAccount();
        accountCall.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(
                    Call<DashboardResponse> call,
                    Response<DashboardResponse> response
            ) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse dashboard = response.body();
                    String fetchedAccountNumber = dashboard.getAccNo();

                    if (!TextUtils.isEmpty(fetchedAccountNumber)) {
                        accountNumber = fetchedAccountNumber.trim();
                        etAccountNumber.setText(accountNumber);
                        accountLoaded = true;
                        btnOpenRD.setEnabled(true);
                    } else {
                        showAccountLoadError(
                                "Your account number was not returned by the server. Please try again."
                        );
                    }
                } else {
                    showAccountLoadError(
                            "Unable to load your account details. Please try again."
                    );
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }

                showAccountLoadError(
                        "Network error while loading your account. Check your connection and try again."
                );
            }
        });
    }

    private void showAccountLoadError(String message) {
        accountLoaded = false;
        accountNumber = "";
        etAccountNumber.setText("");
        btnOpenRD.setEnabled(false);

        if (isFinishing() || isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Account Details Unavailable")
                .setMessage(message)
                .setPositiveButton("Retry", (dialog, which) -> loadAccountNumber())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // ==================================================
    // OPEN RECURRING DEPOSIT
    // ==================================================

    private void openRecurringDeposit() {
        // Always use the account number fetched from the account API.
        if (!accountLoaded || TextUtils.isEmpty(accountNumber)) {
            Toast.makeText(
                    this,
                    "Please wait until your account details are loaded.",
                    Toast.LENGTH_LONG
            ).show();
            loadAccountNumber();
            return;
        }

        String monthlyInstallment = etMonthlyInstallment.getText() == null
                ? ""
                : etMonthlyInstallment.getText().toString().trim();

        String tenureText = etTenure.getText() == null
                ? ""
                : etTenure.getText().toString().trim();

        if (TextUtils.isEmpty(monthlyInstallment)) {
            etMonthlyInstallment.setError("Enter Monthly Installment");
            etMonthlyInstallment.requestFocus();
            return;
        }

        final BigDecimal installment;
        try {
            installment = new BigDecimal(monthlyInstallment);
        } catch (NumberFormatException e) {
            etMonthlyInstallment.setError("Enter a valid monthly installment");
            etMonthlyInstallment.requestFocus();
            return;
        }

        if (installment.compareTo(BigDecimal.ZERO) <= 0) {
            etMonthlyInstallment.setError("Installment must be greater than ₹0");
            etMonthlyInstallment.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(tenureText)) {
            etTenure.setError("Enter Tenure");
            etTenure.requestFocus();
            return;
        }

        final int tenureMonths;
        try {
            tenureMonths = Integer.parseInt(tenureText);
        } catch (NumberFormatException e) {
            etTenure.setError("Enter a valid tenure in months");
            etTenure.requestFocus();
            return;
        }

        if (tenureMonths <= 0) {
            etTenure.setError("Tenure must be at least 1 month");
            etTenure.requestFocus();
            return;
        }

        CreateRecurringDepositRequest request = new CreateRecurringDepositRequest();
        request.setAccountNumber(accountNumber);
        request.setMonthlyInstallment(installment);
        request.setInterestRate(BigDecimal.valueOf(FIXED_RD_INTEREST_RATE));
        request.setTenureMonths(tenureMonths);
        request.setAutoDebit(switchAutoDebit.isChecked());

        btnOpenRD.setEnabled(false);

        createRdCall = recurringDepositRepository.createRecurringDeposit(request);
        createRdCall.enqueue(new Callback<CreateRecurringDepositResponse>() {
            @Override
            public void onResponse(
                    Call<CreateRecurringDepositResponse> call,
                    Response<CreateRecurringDepositResponse> response
            ) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                btnOpenRD.setEnabled(accountLoaded);

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();

                    Toast.makeText(
                            OpenRDActivity.this,
                            TextUtils.isEmpty(message)
                                    ? "Recurring Deposit opened successfully."
                                    : message,
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                } else {
                    showCreateRdError(response);
                }
            }

            @Override
            public void onFailure(
                    Call<CreateRecurringDepositResponse> call,
                    Throwable t
            ) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) {
                    return;
                }

                btnOpenRD.setEnabled(accountLoaded);

                new AlertDialog.Builder(OpenRDActivity.this)
                        .setTitle("Unable to Open Recurring Deposit")
                        .setMessage("Network error. Please check your internet connection and try again.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    private void showCreateRdError(Response<CreateRecurringDepositResponse> response) {
        String message = "Unable to open Recurring Deposit. Please check your details and try again.";

        if (response.errorBody() != null) {
            try {
                String body = response.errorBody().string().trim();
                if (!TextUtils.isEmpty(body)) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(body);
                        String[] fields = {"message", "error", "detail", "description"};
                        for (String field : fields) {
                            if (json.has(field) && !json.isNull(field)) {
                                String value = json.optString(field, "").trim();
                                if (!TextUtils.isEmpty(value)
                                        && !"Bad Request".equalsIgnoreCase(value)
                                        && !"Internal Server Error".equalsIgnoreCase(value)) {
                                    message = value;
                                    break;
                                }
                            }
                        }
                    } catch (org.json.JSONException ignored) {
                        if (!body.startsWith("{") && !body.startsWith("[") && body.length() <= 500) {
                            message = body;
                        }
                    }
                }
            } catch (java.io.IOException ignored) {
                // Keep the safe fallback message.
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Unable to Open Recurring Deposit")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ==================================================
    // CLEAN UP
    // ==================================================

    @Override
    protected void onDestroy() {
        if (accountCall != null) {
            accountCall.cancel();
        }
        if (createRdCall != null) {
            createRdCall.cancel();
        }
        super.onDestroy();
    }
}
