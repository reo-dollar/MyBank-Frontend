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
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.transfer.TransferRequest;
import com.rohit.mybank.model.transfer.TransferResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.TransferRepository;
import com.rohit.mybank.utils.CurrencyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferActivity extends AppCompatActivity {

    private static final double MAX_TRANSFER = 100000.00;

    private TextView tvFromAccount;
    private TextView tvBalance;

    private TextInputEditText etReceiverAccount;
    private TextInputEditText etAmount;

    private MaterialButton btnTransfer;

    private ProgressBar progressBar;

    private DashboardRepository dashboardRepository;
    private TransferRepository transferRepository;

    private String fromAccount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        initializeViews();

        dashboardRepository = new DashboardRepository(this);
        transferRepository = new TransferRepository(this);

        loadAccount();

        btnTransfer.setOnClickListener(v -> transferMoney());
    }

    private void initializeViews() {

        tvFromAccount = findViewById(R.id.tvFromAccount);
        tvBalance = findViewById(R.id.tvBalance);

        etReceiverAccount = findViewById(R.id.etReceiverAccount);
        etAmount = findViewById(R.id.etAmount);

        btnTransfer = findViewById(R.id.btnTransfer);

        progressBar = findViewById(R.id.progressBar);
    }

    private void loadAccount() {

        dashboardRepository.getMyAccount().enqueue(new Callback<DashboardResponse>() {

            @Override
            public void onResponse(Call<DashboardResponse> call,
                                   Response<DashboardResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    DashboardResponse dashboard = response.body();

                    fromAccount = dashboard.getAccNo();

                    tvFromAccount.setText(fromAccount);

                    tvBalance.setText(
                            CurrencyUtil.format(
                                    dashboard.getBalance()
                            )
                    );

                } else {

                    Toast.makeText(
                            TransferActivity.this,
                            "Unable to load account details.",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            }

            @Override
            public void onFailure(Call<DashboardResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        TransferActivity.this,
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

    private void transferMoney() {

        String receiverAccount =
                etReceiverAccount.getText().toString().trim();

        String amountText =
                etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(receiverAccount)) {

            etReceiverAccount.setError("Enter receiver account number");
            etReceiverAccount.requestFocus();
            return;
        }

        if (receiverAccount.equals(fromAccount)) {

            etReceiverAccount.setError(
                    "Cannot transfer to your own account"
            );

            etReceiverAccount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountText)) {

            etAmount.setError("Enter transfer amount");
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

        if (amount > MAX_TRANSFER) {

            etAmount.setError(
                    "Maximum transfer limit is ₹1,00,000"
            );

            etAmount.requestFocus();
            return;
        }

        double finalAmount = amount;
        String finalReceiver = receiverAccount;

        PinVerificationDialog.show(
                TransferActivity.this,
                new PinVerificationDialog.OnPinVerifiedListener() {

                    @Override
                    public void onSuccess() {

                        performTransfer(
                                finalReceiver,
                                finalAmount
                        );

                    }

                    @Override
                    public void onFailure() {

                        Toast.makeText(
                                TransferActivity.this,
                                "Invalid Transaction PIN",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }

    private void performTransfer(
            String receiverAccount,
            double amount) {

        TransferRequest request = new TransferRequest();

        request.setFromAcc(fromAccount);
        request.setToAcc(receiverAccount);
        request.setAmount(amount);

        progressBar.setVisibility(View.VISIBLE);
        btnTransfer.setEnabled(false);

        transferRepository.transfer(request)
                .enqueue(new Callback<TransferResponse>() {

                    @Override
                    public void onResponse(
                            Call<TransferResponse> call,
                            Response<TransferResponse> response) {

                        progressBar.setVisibility(View.GONE);
                        btnTransfer.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            TransferResponse transfer =
                                    response.body();

                            if (transfer.getFromAccount() != null) {

                                tvBalance.setText(
                                        CurrencyUtil.format(
                                                transfer.getFromAccount().getBalance()
                                        )
                                );

                            }

                            etReceiverAccount.setText("");
                            etAmount.setText("");

                            Toast.makeText(
                                    TransferActivity.this,
                                    CurrencyUtil.format(amount)
                                            + " transferred successfully.",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(
                                    TransferActivity.this,
                                    DashboardActivity.class
                            );

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            String error = "Transfer failed.";

                            try {

                                if (response.errorBody() != null) {

                                    error = response.errorBody().string();

                                }

                            } catch (Exception e) {

                                error = e.getMessage();

                            }

                            Toast.makeText(
                                    TransferActivity.this,
                                    error,
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<TransferResponse> call,
                            Throwable t) {

                        progressBar.setVisibility(View.GONE);
                        btnTransfer.setEnabled(true);

                        Toast.makeText(
                                TransferActivity.this,
                                "Network Error : " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }
}