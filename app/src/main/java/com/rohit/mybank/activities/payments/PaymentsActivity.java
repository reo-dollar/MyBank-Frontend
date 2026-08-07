package com.rohit.mybank.activities.payments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.payments.recurringdeposit.RDDashboardActivity;

public class PaymentsActivity extends AppCompatActivity {

    // Bills Payment
    private MaterialCardView cardMobileRecharge;
    private MaterialCardView cardElectricity;
    private MaterialCardView cardWater;
    private MaterialCardView cardGasCylinder;
    private MaterialCardView cardDth;
    private MaterialCardView cardBroadband;
    private MaterialCardView cardFastag;
    private MaterialCardView cardInsurance;

    // Finance
    private MaterialCardView cardFixedDeposit;
    private MaterialCardView cardRecurringDeposit;
    private MaterialCardView cardLoans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        initializeViews();

        // ==========================================
        // Mobile Recharge
        // ==========================================

        cardMobileRecharge.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        MobileRechargeActivity.class)));

        // ==========================================
        // Electricity
        // ==========================================

        cardElectricity.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        ElectricityBillActivity.class)));

        // ==========================================
        // Water
        // ==========================================

        cardWater.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        WaterBillActivity.class)));

        // ==========================================
        // Gas
        // ==========================================

        cardGasCylinder.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        GasBillActivity.class)));

        // ==========================================
        // DTH
        // ==========================================

        cardDth.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        DthRechargeActivity.class)));

        // ==========================================
        // Broadband
        // ==========================================

        cardBroadband.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        BroadbandRechargeActivity.class)));

        // ==========================================
        // FASTag
        // ==========================================

        cardFastag.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        FastagRechargeActivity.class)));

        // ==========================================
        // Insurance
        // ==========================================

        cardInsurance.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        InsurancePaymentActivity.class)));

        // ==========================================
        // Fixed Deposit
        // ==========================================

        cardFixedDeposit.setOnClickListener(v ->
                startActivity(new Intent(
                        PaymentsActivity.this,
                        FixedDepositActivity.class)));
        // ==========================================
        // Recurring Deposit
        // ==========================================

        cardRecurringDeposit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PaymentsActivity.this,
                    RDDashboardActivity.class
            );

            startActivity(intent);

        });

        // ==========================================
        // Loan Calculator
        // ==========================================

        cardLoans.setOnClickListener(v -> {

            // Coming Soon
            // Replace with LoanActivity later

        });

    }

    private void initializeViews() {

        // Bills

        cardMobileRecharge = findViewById(R.id.cardMobileRecharge);

        cardElectricity = findViewById(R.id.cardElectricity);

        cardWater = findViewById(R.id.cardWater);

        cardGasCylinder = findViewById(R.id.cardGas);

        cardDth = findViewById(R.id.cardDth);

        cardBroadband = findViewById(R.id.cardBroadband);

        cardFastag = findViewById(R.id.cardFastag);

        cardInsurance = findViewById(R.id.cardInsurance);

        // Finance

        cardFixedDeposit = findViewById(R.id.cardFixedDeposit);

        cardRecurringDeposit = findViewById(R.id.cardRecurringDeposit);

        cardLoans = findViewById(R.id.cardLoans);

    }

}