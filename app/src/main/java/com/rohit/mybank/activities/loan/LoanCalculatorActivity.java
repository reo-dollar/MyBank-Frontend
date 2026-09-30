package com.rohit.mybank.activities.loan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.databinding.ActivityLoanCalculatorBinding;
import com.rohit.mybank.model.loan.LoanCalculatorRequest;
import com.rohit.mybank.model.loan.LoanCalculatorResponse;
import com.rohit.mybank.model.loan.LoanType;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoanCalculatorActivity
        extends AppCompatActivity {


    // =========================================================
    // CONSTANTS
    // =========================================================

    public static final String EXTRA_LOAN_AMOUNT =
            "loan_amount";

    public static final String EXTRA_INTEREST_RATE =
            "interest_rate";

    public static final String EXTRA_TENURE_MONTHS =
            "tenure_months";

    public static final String EXTRA_LOAN_TYPE =
            "loan_type";


    // =========================================================
    // VIEW BINDING
    // =========================================================

    private ActivityLoanCalculatorBinding binding;


    // =========================================================
    // API
    // =========================================================

    private LoanApi loanApi;


    // =========================================================
    // CALCULATED VALUES
    // =========================================================

    private double calculatedLoanAmount;

    private double calculatedInterestRate;

    private int calculatedTenureMonths;

    private LoanType calculatedLoanType;


    // =========================================================
    // LOAN TYPES
    // =========================================================

    private final String[] loanTypeNames = {

            "Personal Loan",
            "Home Loan",
            "Education Loan",
            "Vehicle Loan"
    };


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

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
        // API
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
        // SETUP LOAN TYPE
        // =====================================================

        setupLoanType();


        // =====================================================
        // LISTENERS
        // =====================================================

        setupListeners();
    }


    // =========================================================
    // SETUP LOAN TYPE
    // =========================================================

    private void setupLoanType() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        loanTypeNames
                );


        binding.actLoanType.setAdapter(
                adapter
        );


        // -----------------------------------------------------
        // DEFAULT
        // -----------------------------------------------------

        binding.actLoanType.setText(
                loanTypeNames[0],
                false
        );


        updateInterestRate(
                0
        );


        // -----------------------------------------------------
        // SELECTION
        // -----------------------------------------------------

        binding.actLoanType.setOnItemClickListener(
                (parent, view, position, id) -> {

                    updateInterestRate(
                            position
                    );


                    // Hide an old result because
                    // the calculation has changed.

                    binding.resultCard.setVisibility(
                            View.GONE
                    );
                }
        );
    }


    // =========================================================
    // UPDATE INTEREST RATE
    // =========================================================

    private void updateInterestRate(
            int position
    ) {

        LoanType loanType;

        double rate;


        switch (position) {

            case 1:

                loanType =
                        LoanType.HOME;

                rate =
                        8.50;

                break;


            case 2:

                loanType =
                        LoanType.EDUCATION;

                rate =
                        7.50;

                break;


            case 3:

                loanType =
                        LoanType.VEHICLE;

                rate =
                        9.00;

                break;


            default:

                loanType =
                        LoanType.PERSONAL;

                rate =
                        11.50;

                break;
        }


        calculatedLoanType =
                loanType;

        calculatedInterestRate =
                rate;


        binding.tvInterestRate.setText(
                String.format(
                        Locale.US,
                        "%.2f%% p.a.",
                        rate
                )
        );
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
        // CALCULATE
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
        // LOAN AMOUNT
        // =====================================================

        String amountText =
                binding.etLoanAmount
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // TENURE
        // =====================================================

        String tenureText =
                binding.etTenure
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // VALIDATE AMOUNT
        // =====================================================

        if (
                TextUtils.isEmpty(
                        amountText
                )
        ) {

            binding.etLoanAmount.setError(
                    "Enter loan amount"
            );

            binding.etLoanAmount.requestFocus();

            return;
        }


        // =====================================================
        // VALIDATE TENURE
        // =====================================================

        if (
                TextUtils.isEmpty(
                        tenureText
                )
        ) {

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

        int tenureMonths;


        try {

            loanAmount =
                    Double.parseDouble(
                            amountText
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
        // VALIDATE AMOUNT
        // =====================================================

        if (loanAmount <= 0) {

            binding.etLoanAmount.setError(
                    "Loan amount must be greater than zero"
            );

            binding.etLoanAmount.requestFocus();

            return;
        }


        // =====================================================
        // VALIDATE TENURE
        // =====================================================

        if (tenureMonths <= 0) {

            binding.etTenure.setError(
                    "Tenure must be greater than zero"
            );

            binding.etTenure.requestFocus();

            return;
        }


        // =====================================================
        // MAKE SURE LOAN TYPE EXISTS
        // =====================================================

        if (calculatedLoanType == null) {

            calculatedLoanType =
                    LoanType.PERSONAL;

            calculatedInterestRate =
                    11.50;
        }


        // =====================================================
        // SAVE CALCULATED VALUES
        // =====================================================

        calculatedLoanAmount =
                loanAmount;

        calculatedTenureMonths =
                tenureMonths;


        // =====================================================
        // CREATE CALCULATOR REQUEST
        // =====================================================
        //
        // The user does NOT type the interest rate.
        //
        // The selected loan type determines it automatically.
        //
        // =====================================================

        LoanCalculatorRequest request =
                new LoanCalculatorRequest(
                        loanAmount,
                        calculatedInterestRate,
                        tenureMonths
                );


        // =====================================================
        // START LOADING
        // =====================================================

        setLoading(
                true
        );


        // =====================================================
        // API REQUEST
        // =====================================================

        loanApi
                .calculateLoan(
                        request
                )
                .enqueue(
                        new Callback<LoanCalculatorResponse>() {

                            @Override
                            public void onResponse(
                                    Call<LoanCalculatorResponse> call,
                                    Response<LoanCalculatorResponse> response
                            ) {

                                setLoading(
                                        false
                                );


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


                                showError(
                                        "Unable to calculate loan. "
                                                + "Please try again."
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<LoanCalculatorResponse> call,
                                    Throwable throwable
                            ) {

                                setLoading(
                                        false
                                );


                                showError(
                                        "Network error. "
                                                + "Please check your connection."
                                );
                            }
                        }
                );
    }


    // =========================================================
    // SHOW RESULT
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
        // SHOW RESULT
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
                        ||
                        calculatedLoanType == null
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


        intent.putExtra(
                EXTRA_LOAN_TYPE,
                calculatedLoanType.name()
        );


        // =====================================================
        // OPEN APPLICATION
        // =====================================================

        startActivity(
                intent
        );
    }


    // =========================================================
    // LOADING
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


            binding.btnMyLoans.setEnabled(
                    false
            );


            binding.btnApplyLoan.setEnabled(
                    false
            );


            binding.btnCalculate.setText(
                    "Calculating..."
            );

        } else {

            binding.progressBar.setVisibility(
                    View.GONE
            );


            binding.btnCalculate.setEnabled(
                    true
            );


            binding.btnMyLoans.setEnabled(
                    true
            );


            binding.btnApplyLoan.setEnabled(
                    true
            );


            binding.btnCalculate.setText(
                    "Calculate EMI"
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
    // CURRENCY
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


    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        binding = null;
    }
}