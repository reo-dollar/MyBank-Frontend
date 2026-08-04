package com.rohit.mybank.activities.banking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.deposit.DepositRequest;
import com.rohit.mybank.model.deposit.DepositResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.DepositRepository;
import com.rohit.mybank.utils.CurrencyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private static final double MAX_DEPOSIT = 1_000_000.00;

    private TextView tvAccountNumber;
    private TextView tvBalance;

    private TextInputEditText etAmount;

    private MaterialButton btnDeposit;

    private ProgressBar progressBar;

    private DashboardRepository dashboardRepository;
    private DepositRepository depositRepository;

    private String accountNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        initializeViews();

        dashboardRepository = new DashboardRepository(this);
        depositRepository = new DepositRepository(this);

        loadAccount();

        btnDeposit.setOnClickListener(v -> depositMoney());
    }

    private void initializeViews() {

        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvBalance = findViewById(R.id.tvBalance);

        etAmount = findViewById(R.id.etAmount);

        btnDeposit = findViewById(R.id.btnDeposit);

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
                            DepositActivity.this,
                            "Unable to load account details.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        DepositActivity.this,
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

    private void depositMoney() {

        String amountText = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {

            etAmount.setError("Please enter deposit amount");
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

        if (amount > MAX_DEPOSIT) {

            etAmount.setError("Maximum deposit limit is ₹10,00,000");
            etAmount.requestFocus();
            return;
        }

        DepositRequest request = new DepositRequest();
        request.setAccNo(accountNumber);
        request.setAmount(amount);

        progressBar.setVisibility(View.VISIBLE);
        btnDeposit.setEnabled(false);

        depositRepository.deposit(request).enqueue(new Callback<DepositResponse>() {

            @Override
            public void onResponse(Call<DepositResponse> call,
                                   Response<DepositResponse> response) {

                progressBar.setVisibility(View.GONE);
                btnDeposit.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    DepositResponse deposit = response.body();

                    tvBalance.setText(
                            CurrencyUtil.format(
                                    deposit.getBalance()
                            )
                    );

                    etAmount.setText("");

                    Toast.makeText(
                            DepositActivity.this,
                            CurrencyUtil.format(amount) + " deposited successfully.",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            DepositActivity.this,
                            DashboardActivity.class
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(intent);
                    finish();

                } else {

                    String errorMessage = "Deposit failed.";

                    try {

                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }

                    } catch (Exception e) {

                        errorMessage = e.getMessage();
                    }

                    Toast.makeText(
                            DepositActivity.this,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();

                }

            }

            @Override
            public void onFailure(Call<DepositResponse> call,
                                  Throwable t) {

                progressBar.setVisibility(View.GONE);
                btnDeposit.setEnabled(true);

                Toast.makeText(
                        DepositActivity.this,
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }

        });

    }

}