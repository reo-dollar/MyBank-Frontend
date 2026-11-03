package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.rohit.mybank.R;
import com.rohit.mybank.model.recurringdeposit.RDCalculatorRequest;
import com.rohit.mybank.model.recurringdeposit.RDCalculatorResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RDCalculatorActivity extends AppCompatActivity {


    //==================================================
    // FIXED RD INTEREST RATE
    //==================================================

    private static final double FIXED_RD_INTEREST_RATE = 7.00;


    //==================================================
    // INPUT FIELDS
    //==================================================

    private TextInputEditText etMonthlyInstallment;

    private TextInputEditText etInterestRate;

    private TextInputEditText etTenure;


    //==================================================
    // BUTTON
    //==================================================

    private MaterialButton btnCalculate;


    //==================================================
    // RESULT VIEWS
    //==================================================

    private TextView tvTotalDeposit;

    private TextView tvEstimatedInterest;

    private TextView tvMaturityAmount;


    //==================================================
    // REPOSITORY
    //==================================================

    private RecurringDepositRepository repository;


    //==================================================
    // ON CREATE
    //==================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_rd_calculator
        );


        repository =
                new RecurringDepositRepository(this);


        initializeViews();

        setupClickListeners();

    }


    //==================================================
    // INITIALIZE VIEWS
    //==================================================

    private void initializeViews() {

        etMonthlyInstallment =
                findViewById(
                        R.id.etMonthlyInstallment
                );


        etInterestRate =
                findViewById(
                        R.id.etInterestRate
                );


        etTenure =
                findViewById(
                        R.id.etTenure
                );


        btnCalculate =
                findViewById(
                        R.id.btnCalculate
                );


        tvTotalDeposit =
                findViewById(
                        R.id.tvTotalDeposit
                );


        tvEstimatedInterest =
                findViewById(
                        R.id.tvEstimatedInterest
                );


        tvMaturityAmount =
                findViewById(
                        R.id.tvMaturityAmount
                );


        //==================================================
        // SHOW FIXED INTEREST RATE
        //==================================================

        etInterestRate.setText(
                "7.00"
        );

    }


    //==================================================
    // CLICK LISTENERS
    //==================================================

    private void setupClickListeners() {

        btnCalculate.setOnClickListener(
                v -> calculateRD()
        );

    }


    //==================================================
    // CALCULATE RD
    //==================================================

    private void calculateRD() {


        //==================================================
        // MONTHLY INSTALLMENT
        //==================================================

        String installmentText =
                etMonthlyInstallment.getText() == null
                        ? ""
                        : etMonthlyInstallment
                        .getText()
                        .toString()
                        .trim();


        //==================================================
        // TENURE
        //==================================================

        String tenureText =
                etTenure.getText() == null
                        ? ""
                        : etTenure
                        .getText()
                        .toString()
                        .trim();


        //==================================================
        // VALIDATE INSTALLMENT
        //==================================================

        if (TextUtils.isEmpty(installmentText)) {

            etMonthlyInstallment.setError(
                    "Enter monthly installment"
            );

            etMonthlyInstallment.requestFocus();

            return;

        }


        //==================================================
        // VALIDATE TENURE
        //==================================================

        if (TextUtils.isEmpty(tenureText)) {

            etTenure.setError(
                    "Enter tenure"
            );

            etTenure.requestFocus();

            return;

        }


        //==================================================
        // CONVERT VALUES
        //==================================================

        BigDecimal installment =
                new BigDecimal(
                        installmentText
                );


        BigDecimal interestRate =
                BigDecimal.valueOf(
                        FIXED_RD_INTEREST_RATE
                );


        Integer tenure =
                Integer.parseInt(
                        tenureText
                );


        //==================================================
        // CREATE CALCULATOR REQUEST
        //==================================================

        RDCalculatorRequest request =
                new RDCalculatorRequest(
                        installment,
                        interestRate,
                        tenure
                );


        //==================================================
        // API CALL
        //==================================================

        Call<RDCalculatorResponse> call =
                repository.calculateRecurringDeposit(
                        request
                );


        call.enqueue(
                new Callback<RDCalculatorResponse>() {

                    @Override
                    public void onResponse(
                            Call<RDCalculatorResponse> call,
                            Response<RDCalculatorResponse> response
                    ) {


                        //==================================================
                        // SUCCESS
                        //==================================================

                        if (response.isSuccessful()
                                && response.body() != null) {


                            RDCalculatorResponse result =
                                    response.body();


                            tvTotalDeposit.setText(
                                    "₹ "
                                            + result.getTotalDeposit()
                            );


                            tvEstimatedInterest.setText(
                                    "₹ "
                                            + result.getEstimatedInterest()
                            );


                            tvMaturityAmount.setText(
                                    "₹ "
                                            + result.getMaturityAmount()
                            );


                        } else {


                            //==================================================
                            // FAILED
                            //==================================================

                            Toast.makeText(
                                    RDCalculatorActivity.this,
                                    "Calculation failed",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }


                    @Override
                    public void onFailure(
                            Call<RDCalculatorResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                RDCalculatorActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                }
        );

    }

}