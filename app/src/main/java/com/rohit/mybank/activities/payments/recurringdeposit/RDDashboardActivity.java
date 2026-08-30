package com.rohit.mybank.activities.payments.recurringdeposit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.rohit.mybank.R;

public class RDDashboardActivity extends AppCompatActivity {

    // =========================================================
    // CARDS
    // =========================================================

    private MaterialCardView cardOpenRD;
    private MaterialCardView cardCalculator;
    private MaterialCardView cardMyRD;
    private MaterialCardView cardPayInstallment;
    private MaterialCardView cardHistory;
    private MaterialCardView cardMatured;
    private MaterialCardView cardPrematureClose;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_rd_dashboard
        );

        initializeViews();

        setupClickListeners();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        cardOpenRD =
                findViewById(R.id.cardOpenRD);

        cardCalculator =
                findViewById(R.id.cardCalculator);

        cardMyRD =
                findViewById(R.id.cardMyRD);

        cardPayInstallment =
                findViewById(R.id.cardPayInstallment);

        cardHistory =
                findViewById(R.id.cardHistory);

        cardMatured =
                findViewById(R.id.cardMatured);

        cardPrematureClose =
                findViewById(R.id.cardPrematureClose);
    }

    // =========================================================
    // CLICK LISTENERS
    // =========================================================

    private void setupClickListeners() {

        // =====================================================
        // OPEN NEW RD
        // =====================================================

        cardOpenRD.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            OpenRDActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // RD CALCULATOR
        // =====================================================

        cardCalculator.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            RDCalculatorActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // MY RD
        // =====================================================

        cardMyRD.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            RDListActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // PAY INSTALLMENT
        // =====================================================

        cardPayInstallment.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            RDListActivity.class
                    );

            /*
             * Tells RDListActivity that the user came
             * here to pay an installment.
             */
            intent.putExtra(
                    "ACTION",
                    "PAY_INSTALLMENT"
            );

            startActivity(intent);
        });

        // =====================================================
        // RD HISTORY
        // =====================================================

        cardHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            RDListActivity.class
                    );

            /*
             * RDHistoryActivity requires an RD_NUMBER.
             *
             * Therefore we first open the user's RD list.
             * The user selects an RD there, then its history
             * can be opened using that RD number.
             */
            intent.putExtra(
                    "ACTION",
                    "HISTORY"
            );

            startActivity(intent);
        });

        // =====================================================
        // MATURED RD
        // =====================================================

        cardMatured.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            MaturedRDActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // PREMATURE CLOSURE
        // =====================================================

        cardPrematureClose.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RDDashboardActivity.this,
                            RDListActivity.class
                    );

            /*
             * Tells RDListActivity that the user came
             * here for premature closure.
             */
            intent.putExtra(
                    "ACTION",
                    "PREMATURE_CLOSE"
            );

            startActivity(intent);
        });
    }
}