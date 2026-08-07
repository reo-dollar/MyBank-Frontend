package com.rohit.mybank.activities.payments.recurringdeposit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.rohit.mybank.R;

public class RDDashboardActivity extends AppCompatActivity {

    private MaterialCardView cardOpenRD;
    private MaterialCardView cardCalculator;
    private MaterialCardView cardMyRD;
    private MaterialCardView cardPayInstallment;
    private MaterialCardView cardHistory;
    private MaterialCardView cardMatured;
    private MaterialCardView cardPrematureClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rd_dashboard);

        initializeViews();

        setupClickListeners();
    }

    private void initializeViews() {

        cardOpenRD = findViewById(R.id.cardOpenRD);

        cardCalculator = findViewById(R.id.cardCalculator);

        cardMyRD = findViewById(R.id.cardMyRD);

        cardPayInstallment = findViewById(R.id.cardPayInstallment);

        cardHistory = findViewById(R.id.cardHistory);

        cardMatured = findViewById(R.id.cardMatured);

        cardPrematureClose = findViewById(R.id.cardPrematureClose);
    }

    private void setupClickListeners() {

        // ===========================
        // Open New RD
        // ===========================

        cardOpenRD.setOnClickListener(v ->
                startActivity(new Intent(
                        RDDashboardActivity.this,
                        OpenRDActivity.class)));

        // ===========================
        // RD Calculator
        // ===========================

        cardCalculator.setOnClickListener(v ->
                startActivity(new Intent(
                        RDDashboardActivity.this,
                        RDCalculatorActivity.class)));

        // ===========================
        // My RD
        // ===========================

        cardMyRD.setOnClickListener(v ->
                startActivity(new Intent(
                        RDDashboardActivity.this,
                        RDListActivity.class)));

        // ===========================
        // Pay Installment
        // ===========================

        cardPayInstallment.setOnClickListener(v ->
                startActivity(new Intent(
                        RDDashboardActivity.this,
                        RDListActivity.class)));

        // ===========================
        // RD History
        // ===========================

        cardHistory.setOnClickListener(v ->

                Toast.makeText(
                        RDDashboardActivity.this,
                        "Open an RD from 'My RD' to view its installment history.",
                        Toast.LENGTH_LONG
                ).show()

        );

        // ===========================
        // Matured RD
        // ===========================

        cardMatured.setOnClickListener(v ->
                startActivity(new Intent(
                        RDDashboardActivity.this,
                        MaturedRDActivity.class)));

        // ===========================
        // Premature Closure
        // ===========================

        cardPrematureClose.setOnClickListener(v ->
                startActivity(new Intent(
                        RDDashboardActivity.this,
                        RDListActivity.class)));
    }
}