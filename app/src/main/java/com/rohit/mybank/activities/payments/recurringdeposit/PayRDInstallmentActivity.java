package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayRDInstallmentActivity extends AppCompatActivity {

    private TextInputEditText etRDNumber;
    private TextInputEditText etAmount;

    private MaterialButton btnPayInstallment;

    private RecurringDepositRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_rd_installment);

        repository = new RecurringDepositRepository(this);

        initializeViews();

        loadIntentData();

        setupListeners();
    }

    private void initializeViews() {

        etRDNumber = findViewById(R.id.etRDNumber);
        etAmount = findViewById(R.id.etAmount);
        btnPayInstallment = findViewById(R.id.btnPayInstallment);

    }

    private void loadIntentData() {

        if (getIntent() != null) {

            String rdNumber = getIntent().getStringExtra("RD_NUMBER");

            if (rdNumber != null) {
                etRDNumber.setText(rdNumber);
            }
        }
    }

    private void setupListeners() {

        btnPayInstallment.setOnClickListener(v -> validateAndPay());

    }

    private void validateAndPay() {

        String rdNumber = etRDNumber.getText().toString().trim();
        String amountString = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(rdNumber)) {
            etRDNumber.setError("Enter RD Number");
            etRDNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountString)) {
            etAmount.setError("Enter Installment Amount");
            etAmount.requestFocus();
            return;
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString);
        } catch (Exception e) {
            etAmount.setError("Invalid Amount");
            etAmount.requestFocus();
            return;
        }

        PayRecurringDepositInstallmentRequest request =
                new PayRecurringDepositInstallmentRequest();

        request.setRdNumber(rdNumber);
        request.setAmount(amount);

        payInstallment(request);
    }

    private void payInstallment(
            PayRecurringDepositInstallmentRequest request) {

        btnPayInstallment.setEnabled(false);

        repository
                .payRecurringDepositInstallment(request)
                .enqueue(new Callback<PayRecurringDepositInstallmentResponse>() {

                    @Override
                    public void onResponse(
                            Call<PayRecurringDepositInstallmentResponse> call,
                            Response<PayRecurringDepositInstallmentResponse> response) {

                        btnPayInstallment.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {

                            PayRecurringDepositInstallmentResponse payment =
                                    response.body();

                            String message = payment.getMessage();

                            if (message == null || message.trim().isEmpty()) {
                                message = "RD Installment Paid Successfully";
                            }

                            Toast.makeText(
                                    PayRDInstallmentActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            setResult(RESULT_OK);

                            finish();

                        } else {

                            String errorMessage = "";

                            try {

                                if (response.errorBody() != null) {
                                    errorMessage = response.errorBody().string();
                                }

                            } catch (Exception e) {
                                errorMessage = e.getMessage();
                            }

                            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                                errorMessage = "Unknown Server Error";
                            }

                            Toast.makeText(
                                    PayRDInstallmentActivity.this,
                                    "HTTP " + response.code()
                                            + "\n\n"
                                            + errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<PayRecurringDepositInstallmentResponse> call,
                            Throwable t) {

                        btnPayInstallment.setEnabled(true);

                        Toast.makeText(
                                PayRDInstallmentActivity.this,
                                "Network Error\n\n" + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}