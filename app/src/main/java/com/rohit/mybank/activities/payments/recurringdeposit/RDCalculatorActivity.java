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

    private TextInputEditText etMonthlyInstallment;
    private TextInputEditText etInterestRate;
    private TextInputEditText etTenure;

    private MaterialButton btnCalculate;

    private TextView tvTotalDeposit;
    private TextView tvEstimatedInterest;
    private TextView tvMaturityAmount;

    private RecurringDepositRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rd_calculator);

        repository = new RecurringDepositRepository(this);

        initializeViews();

        setupClickListeners();
    }

    private void initializeViews() {

        etMonthlyInstallment =
                findViewById(R.id.etMonthlyInstallment);

        etInterestRate =
                findViewById(R.id.etInterestRate);

        etTenure =
                findViewById(R.id.etTenure);

        btnCalculate =
                findViewById(R.id.btnCalculate);

        tvTotalDeposit =
                findViewById(R.id.tvTotalDeposit);

        tvEstimatedInterest =
                findViewById(R.id.tvEstimatedInterest);

        tvMaturityAmount =
                findViewById(R.id.tvMaturityAmount);

    }

    private void setupClickListeners() {

        btnCalculate.setOnClickListener(v -> {

            calculateRD();

        });

    }

    private void calculateRD() {

        String installmentText =
                etMonthlyInstallment.getText().toString().trim();

        String interestText =
                etInterestRate.getText().toString().trim();

        String tenureText =
                etTenure.getText().toString().trim();

        if (TextUtils.isEmpty(installmentText)) {

            etMonthlyInstallment.setError("Enter monthly installment");
            etMonthlyInstallment.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(interestText)) {

            etInterestRate.setError("Enter interest rate");
            etInterestRate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(tenureText)) {

            etTenure.setError("Enter tenure");
            etTenure.requestFocus();
            return;
        }

        BigDecimal installment =
                new BigDecimal(installmentText);

        BigDecimal interestRate =
                new BigDecimal(interestText);

        Integer tenure =
                Integer.parseInt(tenureText);

        RDCalculatorRequest request =
                new RDCalculatorRequest(
                        installment,
                        interestRate,
                        tenure
                );

        Call<RDCalculatorResponse> call =
                repository.calculateRecurringDeposit(request);

        call.enqueue(new Callback<RDCalculatorResponse>() {

            @Override
            public void onResponse(Call<RDCalculatorResponse> call,
                                   Response<RDCalculatorResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    RDCalculatorResponse result =
                            response.body();

                    tvTotalDeposit.setText(
                            "₹ " + result.getTotalDeposit()
                    );

                    tvEstimatedInterest.setText(
                            "₹ " + result.getEstimatedInterest()
                    );

                    tvMaturityAmount.setText(
                            "₹ " + result.getMaturityAmount()
                    );

                } else {

                    Toast.makeText(
                            RDCalculatorActivity.this,
                            "Calculation failed",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(Call<RDCalculatorResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        RDCalculatorActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }
}