package com.rohit.mybank.activities.banking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.model.withdraw.WithdrawRequest;
import com.rohit.mybank.model.withdraw.WithdrawResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.WithdrawRepository;
import com.rohit.mybank.utils.CurrencyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    private static final double MAX_WITHDRAW = 1_000_000.00;

    private TextView tvAccountNumber;
    private TextView tvBalance;

    private TextInputEditText etAmount;

    private MaterialButton btnWithdraw;

    private ProgressBar progressBar;

    private DashboardRepository dashboardRepository;
    private WithdrawRepository withdrawRepository;

    private String accountNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        initializeViews();

        dashboardRepository = new DashboardRepository(this);
        withdrawRepository = new WithdrawRepository(this);

        loadAccount();

        btnWithdraw.setOnClickListener(v -> withdrawMoney());
    }

    private void initializeViews() {

        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvBalance = findViewById(R.id.tvBalance);

        etAmount = findViewById(R.id.etAmount);

        btnWithdraw = findViewById(R.id.btnWithdraw);

        progressBar = findViewById(R.id.progressBar);
    }

    private void loadAccount() {

        dashboardRepository.getMyAccount().enqueue(new Callback<DashboardResponse>() {

            @Override
            public void onResponse(Call<DashboardResponse> call,
                                   Response<DashboardResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    DashboardResponse dashboard = response.body();

                    accountNumber = dashboard.getAccNo();

                    tvAccountNumber.setText(accountNumber);

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
            public void onFailure(Call<DashboardResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        WithdrawActivity.this,
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

    private void withdrawMoney() {

        String amountText = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {

            etAmount.setError("Please enter withdrawal amount");
            etAmount.requestFocus();
            return;
        }

        double amount;

        try {

            amount = Double.parseDouble(amountText);

        } catch (NumberFormatException e) {

            etAmount.setError("Invalid amount");
            etAmount.requestFocus();
            return;
        }

        if (amount <= 0) {

            etAmount.setError("Amount must be greater than ₹0");
            etAmount.requestFocus();
            return;
        }

        if (amount > MAX_WITHDRAW) {

            etAmount.setError("Maximum withdrawal limit is ₹10,00,000");
            etAmount.requestFocus();
            return;
        }

        double finalAmount = amount;

        PinVerificationDialog.show(
                WithdrawActivity.this,
                new PinVerificationDialog.OnPinVerifiedListener() {

                    @Override
                    public void onSuccess() {

                        performWithdraw(finalAmount);

                    }

                    @Override
                    public void onFailure() {

                        Toast.makeText(
                                WithdrawActivity.this,
                                "Invalid Transaction PIN",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    private void performWithdraw(double amount) {

        WithdrawRequest request = new WithdrawRequest();

        request.setAccNo(accountNumber);
        request.setAmount(amount);

        progressBar.setVisibility(View.VISIBLE);
        btnWithdraw.setEnabled(false);

        withdrawRepository.withdraw(request).enqueue(new Callback<WithdrawResponse>() {

            @Override
            public void onResponse(Call<WithdrawResponse> call,
                                   Response<WithdrawResponse> response) {

                progressBar.setVisibility(View.GONE);
                btnWithdraw.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    WithdrawResponse withdraw = response.body();

                    tvBalance.setText(
                            CurrencyUtil.format(
                                    withdraw.getBalance()
                            )
                    );

                    etAmount.setText("");

                    Toast.makeText(
                            WithdrawActivity.this,
                            CurrencyUtil.format(amount) + " withdrawn successfully.",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            WithdrawActivity.this,
                            DashboardActivity.class
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(intent);
                    finish();

                } else {

                    String errorMessage = "Withdrawal failed.";

                    try {

                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }

                    } catch (Exception e) {

                        errorMessage = e.getMessage();
                    }

                    Toast.makeText(
                            WithdrawActivity.this,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                }

            }

            @Override
            public void onFailure(Call<WithdrawResponse> call,
                                  Throwable t) {

                progressBar.setVisibility(View.GONE);
                btnWithdraw.setEnabled(true);

                Toast.makeText(
                        WithdrawActivity.this,
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

}