package com.rohit.mybank.activities.payments.recurringdeposit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.dialog.PinVerificationDialog;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentResponse;
import com.rohit.mybank.model.recurringdeposit.RDResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RDDetailsActivity extends AppCompatActivity {

    private TextView tvRDNumber;
    private TextView tvAccountNumber;
    private TextView tvCustomerName;
    private TextView tvMonthlyInstallment;
    private TextView tvInterestRate;
    private TextView tvTenure;
    private TextView tvTotalDeposit;
    private TextView tvMaturityAmount;
    private TextView tvPaidInstallments;
    private TextView tvRemainingInstallments;
    private TextView tvNextInstallment;
    private TextView tvStatus;

    private MaterialButton btnPayInstallment;
    private MaterialButton btnHistory;
    private MaterialButton btnPrematureClose;

    private RecurringDepositRepository repository;

    private String rdNumber;

    private RDResponse currentRD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rd_details);

        repository = new RecurringDepositRepository(this);

        initializeViews();

        readIntent();

        setupClickListeners();

        if (rdNumber != null && !rdNumber.isEmpty()) {

            loadDetails();

        } else {

            Toast.makeText(
                    this,
                    "Invalid RD Number",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
    }

    private void initializeViews() {

        tvRDNumber = findViewById(R.id.tvRDNumber);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvMonthlyInstallment = findViewById(R.id.tvMonthlyInstallment);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvTenure = findViewById(R.id.tvTenure);
        tvTotalDeposit = findViewById(R.id.tvTotalDeposit);
        tvMaturityAmount = findViewById(R.id.tvMaturityAmount);
        tvPaidInstallments = findViewById(R.id.tvPaidInstallments);
        tvRemainingInstallments = findViewById(R.id.tvRemainingInstallments);
        tvNextInstallment = findViewById(R.id.tvNextInstallment);
        tvStatus = findViewById(R.id.tvStatus);

        btnPayInstallment = findViewById(R.id.btnPayInstallment);
        btnHistory = findViewById(R.id.btnHistory);
        btnPrematureClose = findViewById(R.id.btnPrematureClose);
    }

    private void readIntent() {

        Intent intent = getIntent();

        if (intent != null) {

            rdNumber = intent.getStringExtra("RD_NUMBER");

        }
    }

    private void setupClickListeners() {

        // =====================================
        // Pay Installment
        // =====================================

        btnPayInstallment.setOnClickListener(v -> {

            if (currentRD == null) {

                Toast.makeText(
                        RDDetailsActivity.this,
                        "Unable to load RD details.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            PinVerificationDialog.show(
                    RDDetailsActivity.this,
                    new PinVerificationDialog.OnPinVerifiedListener() {

                        @Override
                        public void onSuccess() {

                            performPayInstallment();

                        }

                        @Override
                        public void onFailure() {

                            Toast.makeText(
                                    RDDetailsActivity.this,
                                    "Invalid Transaction PIN",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    });

        });

        // =====================================
        // Installment History
        // =====================================

        btnHistory.setOnClickListener(v -> {

            if (currentRD == null) {

                Toast.makeText(
                        RDDetailsActivity.this,
                        "RD details not loaded.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    RDDetailsActivity.this,
                    RDHistoryActivity.class
            );

            intent.putExtra(
                    "RD_NUMBER",
                    currentRD.getRdNumber()
            );

            startActivity(intent);

        });

        // =====================================
        // Premature Close
        // =====================================

        btnPrematureClose.setOnClickListener(v -> {

            if (currentRD == null) {

                Toast.makeText(
                        RDDetailsActivity.this,
                        "RD details not loaded.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    RDDetailsActivity.this,
                    PrematureCloseRDActivity.class
            );

            intent.putExtra(
                    "RD_NUMBER",
                    currentRD.getRdNumber()
            );

            startActivity(intent);

        });

    }

    private void performPayInstallment() {

        btnPayInstallment.setEnabled(false);

        PayRecurringDepositInstallmentRequest request =
                new PayRecurringDepositInstallmentRequest();

        request.setRdNumber(currentRD.getRdNumber());

        request.setAmount(currentRD.getMonthlyInstallment());

        repository.payRecurringDepositInstallment(request)
                .enqueue(new Callback<PayRecurringDepositInstallmentResponse>() {

                    @Override
                    public void onResponse(
                            Call<PayRecurringDepositInstallmentResponse> call,
                            Response<PayRecurringDepositInstallmentResponse> response) {

                        btnPayInstallment.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Toast.makeText(
                                    RDDetailsActivity.this,
                                    response.body().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            // Refresh RD Details
                            loadDetails();

                        } else {

                            String error = "";

                            try {

                                if (response.errorBody() != null) {

                                    error = response.errorBody().string();

                                }

                            } catch (Exception e) {

                                error = e.getMessage();

                            }

                            Toast.makeText(
                                    RDDetailsActivity.this,
                                    "Payment Failed\n\nHTTP "
                                            + response.code()
                                            + "\n\n"
                                            + error,
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
                                RDDetailsActivity.this,
                                "Network Error\n\n"
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void loadDetails() {

        repository.getRecurringDepositDetails(rdNumber)
                .enqueue(new Callback<RDResponse>() {

                    @Override
                    public void onResponse(
                            Call<RDResponse> call,
                            Response<RDResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            populateData(response.body());

                        } else {

                            String error = "";

                            try {

                                if (response.errorBody() != null) {

                                    error = response.errorBody().string();

                                }

                            } catch (Exception e) {

                                error = e.getMessage();

                            }

                            Toast.makeText(
                                    RDDetailsActivity.this,
                                    "Unable to load RD Details\n\nHTTP "
                                            + response.code()
                                            + "\n\n"
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<RDResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                RDDetailsActivity.this,
                                "Network Error\n\n"
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void populateData(RDResponse rd) {

        currentRD = rd;

        tvRDNumber.setText(rd.getRdNumber());

        tvAccountNumber.setText(rd.getAccountNumber());

        tvCustomerName.setText(rd.getCustomerName());

        tvMonthlyInstallment.setText(
                "₹ " + rd.getMonthlyInstallment());

        tvInterestRate.setText(
                rd.getInterestRate() + " %");

        tvTenure.setText(
                rd.getTenureMonths() + " Months");

        tvTotalDeposit.setText(
                "₹ " + rd.getTotalDeposit());

        tvMaturityAmount.setText(
                "₹ " + rd.getMaturityAmount());

        tvPaidInstallments.setText(
                String.valueOf(rd.getPaidInstallments()));

        tvRemainingInstallments.setText(
                String.valueOf(rd.getRemainingInstallments()));

        // ============================
        // Next Installment
        // ============================

        if (rd.getRemainingInstallments() <= 0
                || rd.getNextInstallmentDate() == null) {

            tvNextInstallment.setText("Completed");

        } else {

            tvNextInstallment.setText(
                    String.valueOf(
                            rd.getNextInstallmentDate()));

        }

        // ============================
        // Status
        // ============================

        tvStatus.setText(
                String.valueOf(rd.getStatus()));

        // ============================
        // Button State
        // ============================

        boolean completed =
                rd.getRemainingInstallments() <= 0
                        || "MATURED".equalsIgnoreCase(
                        String.valueOf(rd.getStatus()))
                        || "PREMATURE_CLOSED".equalsIgnoreCase(
                        String.valueOf(rd.getStatus()))
                        || "CLOSED".equalsIgnoreCase(
                        String.valueOf(rd.getStatus()));

        if (completed) {

            btnPayInstallment.setEnabled(false);

            btnPayInstallment.setText(
                    "No Installment Due");

            btnPrematureClose.setEnabled(false);

            btnPrematureClose.setText(
                    "RD Closed");

        } else {

            btnPayInstallment.setEnabled(true);

            btnPayInstallment.setText(
                    "Pay Installment");

            btnPrematureClose.setEnabled(true);

            btnPrematureClose.setText(
                    "Premature Close");

        }

    }
}