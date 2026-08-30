package com.rohit.mybank.activities.loan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.databinding.ActivityLoanCalculatorBinding;
import com.rohit.mybank.model.loan.LoanCalculatorRequest;
import com.rohit.mybank.model.loan.LoanCalculatorResponse;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanCalculatorActivity extends AppCompatActivity {

    // =========================================================
    // CONSTANTS
    // =========================================================

    public static final String EXTRA_LOAN_AMOUNT =
            "loan_amount";

    public static final String EXTRA_INTEREST_RATE =
            "interest_rate";

    public static final String EXTRA_TENURE_MONTHS =
            "tenure_months";


    // =========================================================
    // VIEW BINDING
    // =========================================================

    private ActivityLoanCalculatorBinding binding;


    // =========================================================
    // API
    // =========================================================

    private LoanApi loanApi;


    // =========================================================
    // LAST CALCULATED VALUES
    // =========================================================

    private double calculatedLoanAmount;

    private double calculatedInterestRate;

    private int calculatedTenureMonths;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // =====================================================
        // VIEW BINDING
        // =====================================================

        binding =
                ActivityLoanCalculatorBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );


        // =====================================================
        // INITIALIZE API
        // =====================================================

        loanApi =
                RetrofitClient
                        .getClient(this)
                        .create(LoanApi.class);


        // =====================================================
        // INITIAL STATE
        // =====================================================

        binding.resultCard.setVisibility(
                View.GONE
        );

        binding.progressBar.setVisibility(
                View.GONE
        );


        // =====================================================
        // LISTENERS
        // =====================================================

        setupListeners();
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    private void setupListeners() {

        // =====================================================
        // BACK
        // =====================================================

        binding.btnBack.setOnClickListener(
                v -> finish()
        );


        // =====================================================
        // CALCULATE EMI
        // =====================================================

        binding.btnCalculate.setOnClickListener(
                v -> calculateLoan()
        );


        // =====================================================
        // VIEW MY LOANS
        // =====================================================

        binding.btnMyLoans.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    LoanCalculatorActivity.this,
                                    MyLoansActivity.class
                            );

                    startActivity(intent);
                }
        );


        // =====================================================
        // APPLY FOR LOAN
        // =====================================================

        binding.btnApplyLoan.setOnClickListener(
                v -> openLoanApplication()
        );
    }


    // =========================================================
    // CALCULATE LOAN
    // =========================================================

    private void calculateLoan() {

        // =====================================================
        // READ LOAN AMOUNT
        // =====================================================

        String amountText =
                binding.etLoanAmount
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // READ INTEREST RATE
        // =====================================================

        String interestRateText =
                binding.etInterestRate
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // READ TENURE
        // =====================================================

        String tenureText =
                binding.etTenure
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // VALIDATE AMOUNT
        // =====================================================

        if (TextUtils.isEmpty(amountText)) {

            binding.etLoanAmount.setError(
                    "Enter loan amount"
            );

            binding.etLoanAmount.requestFocus();

            return;
        }


        // =====================================================
        // VALIDATE INTEREST RATE
        // =====================================================

        if (TextUtils.isEmpty(interestRateText)) {

            binding.etInterestRate.setError(
                    "Enter interest rate"
            );

            binding.etInterestRate.requestFocus();

            return;
        }


        // =====================================================
        // VALIDATE TENURE
        // =====================================================

        if (TextUtils.isEmpty(tenureText)) {

            binding.etTenure.setError(
                    "Enter tenure"
            );

            binding.etTenure.requestFocus();

            return;
        }


        // =====================================================
        // CONVERT VALUES
        // =====================================================

        double loanAmount;

        double interestRate;

        int tenureMonths;


        try {

            loanAmount =
                    Double.parseDouble(
                            amountText
                    );

            interestRate =
                    Double.parseDouble(
                            interestRateText
                    );

            tenureMonths =
                    Integer.parseInt(
                            tenureText
                    );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // BUSINESS VALIDATION
        // =====================================================

        if (loanAmount <= 0) {

            binding.etLoanAmount.setError(
                    "Loan amount must be greater than zero"
            );

            binding.etLoanAmount.requestFocus();

            return;
        }


        if (interestRate < 0) {

            binding.etInterestRate.setError(
                    "Interest rate cannot be negative"
            );

            binding.etInterestRate.requestFocus();

            return;
        }


        if (tenureMonths <= 0) {

            binding.etTenure.setError(
                    "Tenure must be greater than zero"
            );

            binding.etTenure.requestFocus();

            return;
        }


        // =====================================================
        // SAVE INPUT VALUES
        //
        // These will be passed to LoanApplicationActivity.
        // =====================================================

        calculatedLoanAmount =
                loanAmount;

        calculatedInterestRate =
                interestRate;

        calculatedTenureMonths =
                tenureMonths;


        // =====================================================
        // CREATE REQUEST
        // =====================================================

        LoanCalculatorRequest request =
                new LoanCalculatorRequest(
                        loanAmount,
                        interestRate,
                        tenureMonths
                );


        // =====================================================
        // START LOADING
        // =====================================================

        setLoading(true);


        // =====================================================
        // API REQUEST
        // =====================================================

        loanApi
                .calculateLoan(request)
                .enqueue(
                        new Callback<LoanCalculatorResponse>() {

                            @Override
                            public void onResponse(
                                    Call<LoanCalculatorResponse> call,
                                    Response<LoanCalculatorResponse> response
                            ) {

                                setLoading(false);


                                // =================================
                                // SUCCESS
                                // =================================

                                if (
                                        response.isSuccessful()
                                                &&
                                                response.body() != null
                                ) {

                                    showCalculationResult(
                                            response.body()
                                    );

                                    return;
                                }


                                // =================================
                                // ERROR
                                // =================================

                                showError(
                                        "Unable to calculate loan. " +
                                                "Please try again."
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<LoanCalculatorResponse> call,
                                    Throwable throwable
                            ) {

                                setLoading(false);

                                showError(
                                        "Network error. " +
                                                "Please check your connection."
                                );
                            }
                        }
                );
    }


    // =========================================================
    // SHOW CALCULATION RESULT
    // =========================================================

    private void showCalculationResult(
            LoanCalculatorResponse response
    ) {

        // =====================================================
        // EMI
        // =====================================================

        binding.tvEmiAmount.setText(
                formatCurrency(
                        response.getEmiAmount()
                )
        );


        // =====================================================
        // TOTAL INTEREST
        // =====================================================

        binding.tvTotalInterest.setText(
                formatCurrency(
                        response.getTotalInterest()
                )
        );


        // =====================================================
        // TOTAL PAYABLE
        // =====================================================

        binding.tvTotalPayable.setText(
                formatCurrency(
                        response.getTotalPayable()
                )
        );


        // =====================================================
        // SHOW RESULT CARD
        // =====================================================

        binding.resultCard.setVisibility(
                View.VISIBLE
        );
    }


    // =========================================================
    // OPEN LOAN APPLICATION
    // =========================================================

    private void openLoanApplication() {

        // =====================================================
        // SAFETY CHECK
        // =====================================================

        if (
                calculatedLoanAmount <= 0
                        ||
                        calculatedInterestRate < 0
                        ||
                        calculatedTenureMonths <= 0
        ) {

            Toast.makeText(
                    this,
                    "Please calculate the EMI first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // CREATE INTENT
        // =====================================================

        Intent intent =
                new Intent(
                        LoanCalculatorActivity.this,
                        LoanApplicationActivity.class
                );


        // =====================================================
        // PASS CALCULATOR VALUES
        // =====================================================

        intent.putExtra(
                EXTRA_LOAN_AMOUNT,
                calculatedLoanAmount
        );

        intent.putExtra(
                EXTRA_INTEREST_RATE,
                calculatedInterestRate
        );

        intent.putExtra(
                EXTRA_TENURE_MONTHS,
                calculatedTenureMonths
        );


        // =====================================================
        // START APPLICATION
        // =====================================================

        startActivity(intent);
    }


    // =========================================================
    // LOADING STATE
    // =========================================================

    private void setLoading(
            boolean loading
    ) {

        if (loading) {

            binding.progressBar.setVisibility(
                    View.VISIBLE
            );

            binding.btnCalculate.setEnabled(
                    false
            );

        } else {

            binding.progressBar.setVisibility(
                    View.GONE
            );

            binding.btnCalculate.setEnabled(
                    true
            );
        }
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // CURRENCY FORMAT
    // =========================================================

    private String formatCurrency(
            Double amount
    ) {

        if (amount == null) {

            return "₹0.00";
        }


        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale(
                                "en",
                                "IN"
                        )
                );


        return formatter.format(
                amount
        );
    }
}