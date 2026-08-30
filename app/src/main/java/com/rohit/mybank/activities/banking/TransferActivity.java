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

    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final double MAX_TRANSFER = 100000.00;


    // =========================================================
    // VIEWS
    // =========================================================

    private TextView tvFromAccount;
    private TextView tvBalance;

    private TextInputEditText etReceiverAccount;
    private TextInputEditText etAmount;

    private MaterialButton btnTransfer;

    private ProgressBar progressBar;


    // =========================================================
    // REPOSITORIES
    // =========================================================

    private DashboardRepository dashboardRepository;
    private TransferRepository transferRepository;


    // =========================================================
    // ACCOUNT
    // =========================================================

    private String fromAccount = "";


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transfer);

        initializeViews();

        dashboardRepository =
                new DashboardRepository(this);

        transferRepository =
                new TransferRepository(this);

        loadAccount();

        btnTransfer.setOnClickListener(
                v -> transferMoney()
        );
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvFromAccount =
                findViewById(R.id.tvFromAccount);

        tvBalance =
                findViewById(R.id.tvBalance);

        etReceiverAccount =
                findViewById(R.id.etReceiverAccount);

        etAmount =
                findViewById(R.id.etAmount);

        btnTransfer =
                findViewById(R.id.btnTransfer);

        progressBar =
                findViewById(R.id.progressBar);
    }


    // =========================================================
    // LOAD LOGGED-IN USER ACCOUNT
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

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    DashboardResponse dashboard =
                                            response.body();

                                    // ---------------------------------
                                    // ACCOUNT NUMBER
                                    // ---------------------------------

                                    fromAccount =
                                            dashboard.getAccNo();

                                    tvFromAccount.setText(
                                            fromAccount
                                    );

                                    // ---------------------------------
                                    // BALANCE
                                    // ---------------------------------

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
                            public void onFailure(
                                    Call<DashboardResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        TransferActivity.this,
                                        "Network Error : "
                                                + t.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // START TRANSFER
    // =========================================================

    private void transferMoney() {

        // =====================================================
        // RECEIVER ACCOUNT
        // =====================================================

        String receiverAccount = "";

        if (etReceiverAccount.getText() != null) {

            receiverAccount =
                    etReceiverAccount
                            .getText()
                            .toString()
                            .trim();
        }


        // =====================================================
        // AMOUNT
        // =====================================================

        String amountText = "";

        if (etAmount.getText() != null) {

            amountText =
                    etAmount
                            .getText()
                            .toString()
                            .trim();
        }


        // =====================================================
        // RECEIVER VALIDATION
        // =====================================================

        if (TextUtils.isEmpty(receiverAccount)) {

            etReceiverAccount.setError(
                    "Enter receiver account number"
            );

            etReceiverAccount.requestFocus();

            return;
        }


        // =====================================================
        // RECEIVER ACCOUNT FORMAT
        // =====================================================

        if (!receiverAccount.matches("\\d{12}")) {

            etReceiverAccount.setError(
                    "Account number must be exactly 12 digits"
            );

            etReceiverAccount.requestFocus();

            return;
        }


        // =====================================================
        // OWN ACCOUNT CHECK
        // =====================================================

        if (receiverAccount.equals(fromAccount)) {

            etReceiverAccount.setError(
                    "Cannot transfer to your own account"
            );

            etReceiverAccount.requestFocus();

            return;
        }


        // =====================================================
        // AMOUNT REQUIRED
        // =====================================================

        if (TextUtils.isEmpty(amountText)) {

            etAmount.setError(
                    "Enter transfer amount"
            );

            etAmount.requestFocus();

            return;
        }


        // =====================================================
        // PARSE AMOUNT
        // =====================================================

        double amount;

        try {

            amount =
                    Double.parseDouble(amountText);

        } catch (NumberFormatException e) {

            etAmount.setError(
                    "Invalid amount"
            );

            etAmount.requestFocus();

            return;
        }


        // =====================================================
        // CHECK FINITE VALUE
        // =====================================================

        if (Double.isNaN(amount)
                || Double.isInfinite(amount)) {

            etAmount.setError(
                    "Invalid amount"
            );

            etAmount.requestFocus();

            return;
        }


        // =====================================================
        // POSITIVE AMOUNT
        // =====================================================

        if (amount <= 0) {

            etAmount.setError(
                    "Amount must be greater than ₹0"
            );

            etAmount.requestFocus();

            return;
        }


        // =====================================================
        // MAXIMUM TRANSFER
        // =====================================================

        if (amount > MAX_TRANSFER) {

            etAmount.setError(
                    "Maximum transfer limit is ₹1,00,000"
            );

            etAmount.requestFocus();

            return;
        }


        // =====================================================
        // FINAL VARIABLES
        // =====================================================

        final double finalAmount =
                amount;

        final String finalReceiver =
                receiverAccount;


        // =========================================================
// TRANSACTION PIN VERIFICATION
// =========================================================

        PinVerificationDialog.showForTransactionPin(
                TransferActivity.this,

                new PinVerificationDialog
                        .OnPinVerifiedWithPinListener() {

                    @Override
                    public void onSuccess(
                            String transactionPin
                    ) {

                        // =================================================
                        // PIN VERIFIED
                        // =================================================

                        if (TextUtils.isEmpty(transactionPin)) {

                            Toast.makeText(
                                    TransferActivity.this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        // =================================================
                        // PERFORM TRANSFER
                        // =================================================

                        performTransfer(
                                finalReceiver,
                                finalAmount,
                                transactionPin
                        );
                    }

                    @Override
                    public void onFailure() {

                        // PIN verification failed.
                        // PinVerificationDialog handles the error message.
                    }
                }
        );
    }


    // =========================================================
    // PERFORM TRANSFER
    // =========================================================

    private void performTransfer(
            String receiverAccount,
            double amount,
            String transactionPin
    ) {

        // =====================================================
        // CREATE REQUEST
        // =====================================================

        TransferRequest request =
                new TransferRequest();

        request.setFromAcc(
                fromAccount
        );

        request.setToAcc(
                receiverAccount
        );

        request.setAmount(
                amount
        );

        // IMPORTANT:
        // Send the verified transaction PIN
        // to the backend.

        request.setTransactionPin(
                transactionPin
        );


        // =====================================================
        // SHOW LOADING
        // =====================================================

        progressBar.setVisibility(
                View.VISIBLE
        );

        btnTransfer.setEnabled(
                false
        );


        // =====================================================
        // API CALL
        // =====================================================

        transferRepository
                .transfer(request)
                .enqueue(
                        new Callback<TransferResponse>() {

                            @Override
                            public void onResponse(
                                    Call<TransferResponse> call,
                                    Response<TransferResponse> response
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                btnTransfer.setEnabled(
                                        true
                                );


                                // =================================
                                // SUCCESS
                                // =================================

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    TransferResponse transfer =
                                            response.body();


                                    // -----------------------------
                                    // UPDATE BALANCE
                                    // -----------------------------

                                    if (transfer.getFromAccount() != null) {

                                        tvBalance.setText(
                                                CurrencyUtil.format(
                                                        transfer
                                                                .getFromAccount()
                                                                .getBalance()
                                                )
                                        );
                                    }


                                    // -----------------------------
                                    // CLEAR INPUTS
                                    // -----------------------------

                                    etReceiverAccount.setText("");

                                    etAmount.setText("");


                                    // -----------------------------
                                    // SUCCESS MESSAGE
                                    // -----------------------------

                                    Toast.makeText(
                                            TransferActivity.this,
                                            CurrencyUtil.format(amount)
                                                    + " transferred successfully.",
                                            Toast.LENGTH_LONG
                                    ).show();


                                    // -----------------------------
                                    // RETURN TO DASHBOARD
                                    // -----------------------------

                                    Intent intent =
                                            new Intent(
                                                    TransferActivity.this,
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
                                // SERVER ERROR
                                // =================================

                                String error =
                                        "Transfer failed.";

                                try {

                                    if (response.errorBody()
                                            != null) {

                                        error =
                                                response.errorBody()
                                                        .string();
                                    }

                                } catch (Exception e) {

                                    if (e.getMessage() != null
                                            && !e.getMessage()
                                            .isEmpty()) {

                                        error =
                                                e.getMessage();
                                    }
                                }


                                Toast.makeText(
                                        TransferActivity.this,
                                        error,
                                        Toast.LENGTH_LONG
                                ).show();
                            }


                            // =====================================
                            // NETWORK FAILURE
                            // =====================================

                            @Override
                            public void onFailure(
                                    Call<TransferResponse> call,
                                    Throwable t
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                btnTransfer.setEnabled(
                                        true
                                );


                                Toast.makeText(
                                        TransferActivity.this,
                                        "Network Error : "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // ACTIVITY DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.GONE
            );
        }

        if (btnTransfer != null) {

            btnTransfer.setEnabled(
                    true
            );
        }
    }
}