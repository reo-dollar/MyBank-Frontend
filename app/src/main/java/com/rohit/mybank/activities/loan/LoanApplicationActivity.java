package com.rohit.mybank.activities.loan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.api.LoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.databinding.ActivityLoanApplicationBinding;
import com.rohit.mybank.model.loan.LoanApplicationRequest;
import com.rohit.mybank.model.loan.LoanResponse;
import com.rohit.mybank.model.loan.LoanType;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanApplicationActivity extends AppCompatActivity {

    private ActivityLoanApplicationBinding binding;

    private LoanApi loanApi;

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
                ActivityLoanApplicationBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        // =====================================================
        // RETROFIT
        // =====================================================

        loanApi =
                RetrofitClient
                        .getClient(this)
                        .create(LoanApi.class);

        // =====================================================
        // INITIAL STATE
        // =====================================================

        binding.progressBar.setVisibility(
                View.GONE
        );

        // =====================================================
        // LOAN TYPES
        // =====================================================

        setupLoanTypeSpinner();

        // =====================================================
        // BUTTONS
        // =====================================================

        binding.btnBack.setOnClickListener(
                v -> finish()
        );

        binding.btnApplyLoan.setOnClickListener(
                v -> submitLoanApplication()
        );
    }

    // =========================================================
    // LOAN TYPE SPINNER
    // =========================================================

    private void setupLoanTypeSpinner() {

        String[] loanTypes = {
                "PERSONAL",
                "HOME",
                "EDUCATION",
                "VEHICLE"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        loanTypes
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerLoanType.setAdapter(
                adapter
        );
    }

    // =========================================================
    // SUBMIT APPLICATION
    // =========================================================

    private void submitLoanApplication() {

        // =====================================================
        // READ ACCOUNT NUMBER
        // =====================================================

        String accountNumber =
                binding.etAccountNumber
                        .getText()
                        .toString()
                        .trim();

        // =====================================================
        // READ LOAN AMOUNT
        // =====================================================

        String loanAmountText =
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
        // READ PURPOSE
        // =====================================================

        String purpose =
                binding.etPurpose
                        .getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDATE ACCOUNT NUMBER
        // =====================================================

        if (TextUtils.isEmpty(accountNumber)) {

            binding.etAccountNumber.setError(
                    "Account number is required"
            );

            binding.etAccountNumber.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE LOAN AMOUNT
        // =====================================================

        if (TextUtils.isEmpty(loanAmountText)) {

            binding.etLoanAmount.setError(
                    "Loan amount is required"
            );

            binding.etLoanAmount.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE INTEREST RATE
        // =====================================================

        if (TextUtils.isEmpty(interestRateText)) {

            binding.etInterestRate.setError(
                    "Interest rate is required"
            );

            binding.etInterestRate.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE TENURE
        // =====================================================

        if (TextUtils.isEmpty(tenureText)) {

            binding.etTenure.setError(
                    "Tenure is required"
            );

            binding.etTenure.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE PURPOSE
        // =====================================================

        if (TextUtils.isEmpty(purpose)) {

            binding.etPurpose.setError(
                    "Loan purpose is required"
            );

            binding.etPurpose.requestFocus();

            return;
        }

        // =====================================================
        // PARSE NUMERIC VALUES
        // =====================================================

        double loanAmount;

        double interestRate;

        int tenureMonths;

        try {

            loanAmount =
                    Double.parseDouble(
                            loanAmountText
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
                    "Please enter valid numeric values.",
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
        // GET SELECTED LOAN TYPE
        // =====================================================

        int selectedPosition =
                binding.spinnerLoanType
                        .getSelectedItemPosition();

        LoanType loanType;

        switch (selectedPosition) {

            case 1:
                loanType = LoanType.HOME;
                break;

            case 2:
                loanType = LoanType.EDUCATION;
                break;

            case 3:
                loanType = LoanType.VEHICLE;
                break;

            default:
                loanType = LoanType.PERSONAL;
                break;
        }

        // =====================================================
        // CREATE REQUEST
        // =====================================================

        LoanApplicationRequest request =
                new LoanApplicationRequest(
                        accountNumber,
                        loanType,
                        loanAmount,
                        interestRate,
                        tenureMonths,
                        purpose
                );

        // =====================================================
        // START LOADING
        // =====================================================

        setLoading(true);

        // =====================================================
        // API CALL
        // =====================================================

        loanApi
                .applyForLoan(request)
                .enqueue(
                        new Callback<LoanResponse>() {

                            @Override
                            public void onResponse(
                                    Call<LoanResponse> call,
                                    Response<LoanResponse> response
                            ) {

                                setLoading(false);

                                // ---------------------------------
                                // SUCCESS
                                // ---------------------------------

                                if (
                                        response.isSuccessful()
                                                &&
                                                response.body() != null
                                ) {

                                    handleSuccess(
                                            response.body()
                                    );

                                    return;
                                }

                                // ---------------------------------
                                // SERVER ERROR
                                // ---------------------------------

                                handleServerError(
                                        response
                                );
                            }

                            @Override
                            public void onFailure(
                                    Call<LoanResponse> call,
                                    Throwable throwable
                            ) {

                                setLoading(false);

                                Toast.makeText(
                                        LoanApplicationActivity.this,
                                        "Network error. Please check your connection.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // SUCCESS
    // =========================================================

    private void handleSuccess(
            LoanResponse loan
    ) {

        String loanNumber =
                loan.getLoanNumber();

        Toast.makeText(
                this,
                "Loan application submitted successfully.\n"
                        + "Loan Number: "
                        + loanNumber,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    // =========================================================
    // SERVER ERROR
    // =========================================================

    private void handleServerError(
            Response<LoanResponse> response
    ) {

        String message;

        if (response.code() == 400) {

            message =
                    "Invalid loan application.";

        } else if (response.code() == 401) {

            message =
                    "Session expired. Please login again.";

        } else if (response.code() == 403) {

            message =
                    "You are not authorized to apply for a loan.";

        } else if (response.code() == 404) {

            message =
                    "Account not found.";

        } else if (response.code() == 409) {

            message =
                    "Loan application cannot be submitted.";

        } else {

            message =
                    "Unable to submit loan application.";
        }

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
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

            binding.btnApplyLoan.setEnabled(
                    false
            );

            binding.btnBack.setEnabled(
                    false
            );

            binding.btnApplyLoan.setText(
                    "Submitting..."
            );

        } else {

            binding.progressBar.setVisibility(
                    View.GONE
            );

            binding.btnApplyLoan.setEnabled(
                    true
            );

            binding.btnBack.setEnabled(
                    true
            );

            binding.btnApplyLoan.setText(
                    "Apply for Loan"
            );
        }
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