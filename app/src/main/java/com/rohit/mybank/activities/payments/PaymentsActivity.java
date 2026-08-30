package com.rohit.mybank.activities.payments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.loan.LoanCalculatorActivity;
import com.rohit.mybank.activities.payments.recurringdeposit.RDDashboardActivity;

public class PaymentsActivity extends AppCompatActivity {

    // =========================================================
    // BILLS PAYMENT
    // =========================================================

    private MaterialCardView cardMobileRecharge;
    private MaterialCardView cardElectricity;
    private MaterialCardView cardWater;
    private MaterialCardView cardGasCylinder;
    private MaterialCardView cardDth;
    private MaterialCardView cardBroadband;
    private MaterialCardView cardFastag;
    private MaterialCardView cardInsurance;


    // =========================================================
    // FINANCE
    // =========================================================

    private MaterialCardView cardFixedDeposit;
    private MaterialCardView cardRecurringDeposit;
    private MaterialCardView cardLoans;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_payments
        );


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        initializeViews();


        // =====================================================
        // MOBILE RECHARGE
        // =====================================================

        cardMobileRecharge.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            MobileRechargeActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // ELECTRICITY
        // =====================================================

        cardElectricity.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            ElectricityBillActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // WATER
        // =====================================================

        cardWater.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            WaterBillActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // GAS
        // =====================================================

        cardGasCylinder.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            GasBillActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // DTH
        // =====================================================

        cardDth.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            DthRechargeActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // BROADBAND
        // =====================================================

        cardBroadband.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            BroadbandRechargeActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // FASTAG
        // =====================================================

        cardFastag.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            FastagRechargeActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // INSURANCE
        // =====================================================

        cardInsurance.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            InsurancePaymentActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // FIXED DEPOSIT
        // =====================================================

        cardFixedDeposit.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            FixedDepositActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // RECURRING DEPOSIT
        // =====================================================

        cardRecurringDeposit.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            RDDashboardActivity.class
                    );

            startActivity(intent);
        });


        // =====================================================
        // LOANS
        // =====================================================

        cardLoans.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PaymentsActivity.this,
                            LoanCalculatorActivity.class
                    );

            startActivity(intent);
        });
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // =====================================================
        // BILLS
        // =====================================================

        cardMobileRecharge =
                findViewById(
                        R.id.cardMobileRecharge
                );


        cardElectricity =
                findViewById(
                        R.id.cardElectricity
                );


        cardWater =
                findViewById(
                        R.id.cardWater
                );


        cardGasCylinder =
                findViewById(
                        R.id.cardGas
                );


        cardDth =
                findViewById(
                        R.id.cardDth
                );


        cardBroadband =
                findViewById(
                        R.id.cardBroadband
                );


        cardFastag =
                findViewById(
                        R.id.cardFastag
                );


        cardInsurance =
                findViewById(
                        R.id.cardInsurance
                );


        // =====================================================
        // FINANCE
        // =====================================================

        cardFixedDeposit =
                findViewById(
                        R.id.cardFixedDeposit
                );


        cardRecurringDeposit =
                findViewById(
                        R.id.cardRecurringDeposit
                );


        cardLoans =
                findViewById(
                        R.id.cardLoans
                );
    }
}