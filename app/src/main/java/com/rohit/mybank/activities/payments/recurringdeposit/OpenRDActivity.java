package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositRequest;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenRDActivity extends AppCompatActivity {

    private TextInputEditText etAccountNumber;
    private TextInputEditText etMonthlyInstallment;
    private TextInputEditText etInterestRate;
    private TextInputEditText etTenure;

    private MaterialSwitch switchAutoDebit;

    private MaterialButton btnOpenRD;

    private RecurringDepositRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_rd);

        repository = new RecurringDepositRepository(this);

        initializeViews();

        setupClickListeners();
    }

    private void initializeViews() {

        etAccountNumber = findViewById(R.id.etAccountNumber);

        etMonthlyInstallment = findViewById(R.id.etMonthlyInstallment);

        etInterestRate = findViewById(R.id.etInterestRate);

        etTenure = findViewById(R.id.etTenure);

        switchAutoDebit = findViewById(R.id.switchAutoDebit);

        btnOpenRD = findViewById(R.id.btnOpenRD);

    }

    private void setupClickListeners() {

        btnOpenRD.setOnClickListener(v -> openRecurringDeposit());

    }

    private void openRecurringDeposit() {

        String accountNumber = etAccountNumber.getText() == null
                ? ""
                : etAccountNumber.getText().toString().trim();

        String monthlyInstallment = etMonthlyInstallment.getText() == null
                ? ""
                : etMonthlyInstallment.getText().toString().trim();

        String interestRate = etInterestRate.getText() == null
                ? ""
                : etInterestRate.getText().toString().trim();

        String tenure = etTenure.getText() == null
                ? ""
                : etTenure.getText().toString().trim();

        if (TextUtils.isEmpty(accountNumber)) {

            etAccountNumber.setError("Enter Account Number");

            etAccountNumber.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(monthlyInstallment)) {

            etMonthlyInstallment.setError("Enter Monthly Installment");

            etMonthlyInstallment.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(interestRate)) {

            etInterestRate.setError("Enter Interest Rate");

            etInterestRate.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(tenure)) {

            etTenure.setError("Enter Tenure");

            etTenure.requestFocus();

            return;

        }

        CreateRecurringDepositRequest request =
                new CreateRecurringDepositRequest();

        request.setAccountNumber(accountNumber);

        request.setMonthlyInstallment(
                new BigDecimal(monthlyInstallment)
        );

        request.setInterestRate(
                new BigDecimal(interestRate)
        );

        request.setTenureMonths(
                Integer.parseInt(tenure)
        );

        request.setAutoDebit(
                switchAutoDebit.isChecked()
        );

        btnOpenRD.setEnabled(false);

        repository.createRecurringDeposit(request)
                .enqueue(new Callback<CreateRecurringDepositResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreateRecurringDepositResponse> call,
                            Response<CreateRecurringDepositResponse> response) {

                        btnOpenRD.setEnabled(true);

                        if (response.isSuccessful() &&
                                response.body() != null) {

                            Toast.makeText(
                                    OpenRDActivity.this,
                                    response.body().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    OpenRDActivity.this,
                                    "Unable to Open Recurring Deposit",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<CreateRecurringDepositResponse> call,
                            Throwable t) {

                        btnOpenRD.setEnabled(true);

                        Toast.makeText(
                                OpenRDActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}