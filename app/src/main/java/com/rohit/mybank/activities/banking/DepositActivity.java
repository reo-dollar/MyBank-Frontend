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
import com.rohit.mybank.model.deposit.DepositRequest;
import com.rohit.mybank.model.deposit.DepositResponse;
import com.rohit.mybank.repository.DashboardRepository;
import com.rohit.mybank.repository.DepositRepository;
import com.rohit.mybank.utils.CurrencyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * DEPOSIT ACTIVITY
 * =========================================================
 *
 * Deposit flow:
 *
 * 1. Load account
 * 2. User enters amount
 * 3. Validate amount
 * 4. Open Transaction PIN dialog
 * 5. User enters 6-digit PIN
 * 6. Backend verifies PIN
 * 7. Verified PIN is returned
 * 8. Deposit request contains:
 *
 *       accNo
 *       amount
 *       transactionPin
 *
 * 9. Backend processes deposit
 * 10. Balance is updated
 * 11. Return to dashboard
 *
 * =========================================================
 */
public class DepositActivity extends AppCompatActivity {


    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final double MAX_DEPOSIT =
            1_000_000.00;


    // =========================================================
    // VIEWS
    // =========================================================

    private TextView tvAccountNumber;

    private TextView tvBalance;

    private TextInputEditText etAmount;

    private MaterialButton btnDeposit;

    private ProgressBar progressBar;


    // =========================================================
    // REPOSITORIES
    // =========================================================

    private DashboardRepository dashboardRepository;

    private DepositRepository depositRepository;


    // =========================================================
    // ACCOUNT
    // =========================================================

    private String accountNumber = "";


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
                R.layout.activity_deposit
        );


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();


        // =====================================================
        // REPOSITORIES
        // =====================================================

        dashboardRepository =
                new DashboardRepository(this);

        depositRepository =
                new DepositRepository(this);


        // =====================================================
        // LOAD ACCOUNT
        // =====================================================

        loadAccount();


        // =====================================================
        // DEPOSIT BUTTON
        // =====================================================

        btnDeposit.setOnClickListener(
                v -> depositMoney()
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


        btnDeposit =
                findViewById(
                        R.id.btnDeposit
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
                                                &&
                                                response.body() != null
                                ) {

                                    DashboardResponse dashboard =
                                            response.body();


                                    // =============================
                                    // ACCOUNT NUMBER
                                    // =============================

                                    accountNumber =
                                            dashboard.getAccNo();


                                    if (
                                            accountNumber == null
                                                    ||
                                                    accountNumber.trim().isEmpty()
                                    ) {

                                        accountNumber = "";
                                    }


                                    tvAccountNumber.setText(
                                            accountNumber
                                    );


                                    // =============================
                                    // BALANCE
                                    // =============================

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
                            public void onFailure(
                                    Call<DashboardResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        DepositActivity.this,
                                        "Network Error : "
                                                + getSafeMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // DEPOSIT MONEY
    // =========================================================

    private void depositMoney() {


        // =====================================================
        // READ AMOUNT
        // =====================================================

        String amountText = "";

        if (etAmount.getText() != null) {

            amountText =
                    etAmount.getText()
                            .toString()
                            .trim();
        }


        // =====================================================
        // EMPTY AMOUNT
        // =====================================================

        if (TextUtils.isEmpty(amountText)) {

            etAmount.setError(
                    "Please enter deposit amount"
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
        // MAXIMUM DEPOSIT
        // =====================================================

        if (amount > MAX_DEPOSIT) {

            etAmount.setError(
                    "Maximum deposit limit is ₹10,00,000"
            );

            etAmount.requestFocus();

            return;
        }


        // =====================================================
        // ACCOUNT CHECK
        // =====================================================

        if (TextUtils.isEmpty(accountNumber)) {

            Toast.makeText(
                    this,
                    "Account information is not loaded yet.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // FINAL AMOUNT
        // =====================================================

        final double finalAmount =
                amount;


        // =====================================================
        // TRANSACTION PIN
        // =====================================================
        //
        // IMPORTANT:
        //
        // Do NOT call performDeposit() directly.
        //
        // First:
        //
        //       Transaction PIN verification
        //
        // Then:
        //
        //       performDeposit()
        //
        // =====================================================

        PinVerificationDialog.showForTransactionPin(
                DepositActivity.this,

                new PinVerificationDialog
                        .OnPinVerifiedWithPinListener() {

                    @Override
                    public void onSuccess(
                            String transactionPin
                    ) {

                        // =====================================
                        // PIN VERIFIED
                        // =====================================

                        if (
                                TextUtils.isEmpty(
                                        transactionPin
                                )
                        ) {

                            Toast.makeText(
                                    DepositActivity.this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }


                        // =====================================
                        // PERFORM DEPOSIT
                        // =====================================

                        performDeposit(
                                finalAmount,
                                transactionPin
                        );
                    }


                    @Override
                    public void onFailure() {

                        /*
                         * Invalid PIN or network error.
                         *
                         * PinVerificationDialog already
                         * displays the relevant error.
                         */
                    }
                }
        );
    }


    // =========================================================
    // PERFORM DEPOSIT
    // =========================================================

    private void performDeposit(
            double amount,
            String transactionPin
    ) {


        // =====================================================
        // PIN VALIDATION
        // =====================================================

        if (
                TextUtils.isEmpty(
                        transactionPin
                )
        ) {

            Toast.makeText(
                    this,
                    "Transaction PIN is required.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // ACCOUNT VALIDATION
        // =====================================================

        if (
                TextUtils.isEmpty(
                        accountNumber
                )
        ) {

            Toast.makeText(
                    this,
                    "Account number is unavailable.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // CREATE REQUEST
        // =====================================================

        DepositRequest request =
                new DepositRequest();


        request.setAccNo(
                accountNumber
        );


        request.setAmount(
                amount
        );


        request.setTransactionPin(
                transactionPin
        );


        // =====================================================
        // SHOW PROGRESS
        // =====================================================

        progressBar.setVisibility(
                View.VISIBLE
        );


        btnDeposit.setEnabled(
                false
        );


        // =====================================================
        // BACKEND REQUEST
        // =====================================================

        depositRepository
                .deposit(request)
                .enqueue(
                        new Callback<DepositResponse>() {

                            @Override
                            public void onResponse(
                                    Call<DepositResponse> call,
                                    Response<DepositResponse> response
                            ) {

                                // =============================
                                // HIDE PROGRESS
                                // =============================

                                progressBar.setVisibility(
                                        View.GONE
                                );


                                btnDeposit.setEnabled(
                                        true
                                );


                                // =============================
                                // SUCCESS
                                // =============================

                                if (
                                        response.isSuccessful()
                                                &&
                                                response.body() != null
                                ) {

                                    DepositResponse deposit =
                                            response.body();


                                    // =============================
                                    // UPDATE BALANCE
                                    // =============================

                                    tvBalance.setText(
                                            CurrencyUtil.format(
                                                    deposit.getBalance()
                                            )
                                    );


                                    // =============================
                                    // CLEAR AMOUNT
                                    // =============================

                                    etAmount.setText("");


                                    // =============================
                                    // SUCCESS MESSAGE
                                    // =============================

                                    Toast.makeText(
                                            DepositActivity.this,

                                            CurrencyUtil.format(
                                                    amount
                                            )
                                                    +
                                                    " deposited successfully.",

                                            Toast.LENGTH_LONG
                                    ).show();


                                    // =============================
                                    // DASHBOARD
                                    // =============================

                                    Intent intent =
                                            new Intent(
                                                    DepositActivity.this,
                                                    DashboardActivity.class
                                            );


                                    intent.addFlags(
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    |
                                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    );


                                    startActivity(
                                            intent
                                    );


                                    finish();

                                    return;
                                }


                                // =============================
                                // SERVER ERROR
                                // =============================

                                String errorMessage =
                                        getResponseError(
                                                response
                                        );


                                Toast.makeText(
                                        DepositActivity.this,

                                        "Deposit Failed\n\n"
                                                +
                                                "HTTP "
                                                +
                                                response.code()
                                                +
                                                "\n\n"
                                                +
                                                errorMessage,

                                        Toast.LENGTH_LONG
                                ).show();
                            }


                            @Override
                            public void onFailure(
                                    Call<DepositResponse> call,
                                    Throwable t
                            ) {

                                progressBar.setVisibility(
                                        View.GONE
                                );


                                btnDeposit.setEnabled(
                                        true
                                );


                                Toast.makeText(
                                        DepositActivity.this,

                                        "Network Error : "
                                                +
                                                getSafeMessage(t),

                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // RESPONSE ERROR
    // =========================================================

    private String getResponseError(
            Response<?> response
    ) {

        String errorMessage =
                "Deposit failed.";


        try {

            if (
                    response.errorBody() != null
            ) {

                String serverError =
                        response.errorBody()
                                .string();


                if (
                        !TextUtils.isEmpty(
                                serverError
                        )
                ) {

                    errorMessage =
                            serverError;
                }
            }

        } catch (Exception e) {

            if (
                    !TextUtils.isEmpty(
                            e.getMessage()
                    )
            ) {

                errorMessage =
                        e.getMessage();
            }
        }


        return errorMessage;
    }


    // =========================================================
    // SAFE THROWABLE MESSAGE
    // =========================================================

    private String getSafeMessage(
            Throwable throwable
    ) {

        if (throwable == null) {

            return "Unknown error";
        }


        if (
                TextUtils.isEmpty(
                        throwable.getMessage()
                )
        ) {

            return "Unknown error";
        }


        return throwable.getMessage();
    }
}