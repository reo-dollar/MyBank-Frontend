package com.rohit.mybank.activities.payments.recurringdeposit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.model.recurringdeposit.PrematureCloseRDRequest;
import com.rohit.mybank.model.recurringdeposit.PrematureCloseRDResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrematureCloseRDActivity extends AppCompatActivity {

    private TextView tvRDNumber;

    private MaterialButton btnCloseRD;

    private RecurringDepositRepository repository;

    private String rdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premature_close_rd);

        repository = new RecurringDepositRepository(this);

        initializeViews();

        loadIntent();

        setupListeners();

    }

    private void initializeViews() {

        tvRDNumber = findViewById(R.id.tvRDNumber);

        btnCloseRD = findViewById(R.id.btnCloseRD);

    }

    private void loadIntent() {

        if (getIntent() != null) {

            rdNumber = getIntent().getStringExtra("RD_NUMBER");

            if (rdNumber != null) {

                tvRDNumber.setText(rdNumber);

            }

        }

    }

    private void setupListeners() {

        btnCloseRD.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Premature Close")
                    .setMessage("Are you sure you want to close this Recurring Deposit?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        closeRecurringDeposit();

                    })
                    .setNegativeButton("No", null)
                    .show();

        });

    }

    private void closeRecurringDeposit() {

        PrematureCloseRDRequest request =
                new PrematureCloseRDRequest();

        request.setRdNumber(rdNumber);

        btnCloseRD.setEnabled(false);

        repository
                .prematureCloseRecurringDeposit(request)
                .enqueue(new Callback<PrematureCloseRDResponse>() {

                    @Override
                    public void onResponse(
                            Call<PrematureCloseRDResponse> call,
                            Response<PrematureCloseRDResponse> response) {

                        btnCloseRD.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            PrematureCloseRDResponse result =
                                    response.body();

                            new AlertDialog.Builder(
                                    PrematureCloseRDActivity.this)
                                    .setTitle("Recurring Deposit Closed")
                                    .setMessage(
                                            "Settlement Amount : ₹"
                                                    + result.getSettlementAmount()
                                                    + "\n\n"
                                                    + result.getMessage()
                                    )
                                    .setPositiveButton("OK",
                                            (d, w) -> finish())
                                    .show();

                        } else {

                            Toast.makeText(
                                    PrematureCloseRDActivity.this,
                                    "Unable to Close RD",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<PrematureCloseRDResponse> call,
                            Throwable t) {

                        btnCloseRD.setEnabled(true);

                        Toast.makeText(
                                PrematureCloseRDActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}