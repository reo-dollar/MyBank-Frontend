package com.rohit.mybank.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.payments.recurringdeposit.PayRDInstallmentActivity;
import com.rohit.mybank.activities.payments.recurringdeposit.PrematureCloseRDActivity;
import com.rohit.mybank.activities.payments.recurringdeposit.RDDetailsActivity;
import com.rohit.mybank.activities.payments.recurringdeposit.RDHistoryActivity;
import com.rohit.mybank.model.recurringdeposit.RDResponse;

import java.util.List;

public class RDAdapter
        extends RecyclerView.Adapter<RDViewHolder> {

    private final Context context;

    private final List<RDResponse> rdList;

    private final String action;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RDAdapter(
            Context context,
            List<RDResponse> rdList,
            String action) {

        this.context = context;

        this.rdList = rdList;

        this.action = action;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public RDViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_rd,
                                parent,
                                false
                        );

        return new RDViewHolder(view);
    }

    // =========================================================
    // BIND DATA
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull RDViewHolder holder,
            int position) {

        RDResponse rd =
                rdList.get(position);

        if (rd == null) {
            return;
        }

        // =====================================================
        // RD NUMBER
        // =====================================================

        holder.tvRDNumber.setText(
                safeText(
                        rd.getRdNumber()
                )
        );

        // =====================================================
        // CUSTOMER NAME
        // =====================================================

        holder.tvCustomerName.setText(
                safeText(
                        rd.getCustomerName()
                )
        );

        // =====================================================
        // MONTHLY INSTALLMENT
        // =====================================================

        holder.tvMonthlyInstallment.setText(
                "Monthly Installment : ₹"
                        + safeText(
                        rd.getMonthlyInstallment()
                )
        );

        // =====================================================
        // MATURITY AMOUNT
        // =====================================================

        holder.tvMaturityAmount.setText(
                "Maturity Amount : ₹"
                        + safeText(
                        rd.getMaturityAmount()
                )
        );

        // =====================================================
        // STATUS
        // =====================================================

        holder.tvStatus.setText(
                safeText(
                        rd.getStatus()
                )
        );

        // =====================================================
        // CLICK
        // =====================================================

        holder.itemView.setOnClickListener(v -> {

            String rdNumber =
                    rd.getRdNumber();

            if (rdNumber == null
                    || rdNumber.trim().isEmpty()) {

                return;
            }

            openRDAction(rdNumber);
        });
    }

    // =========================================================
    // OPEN RD ACTION
    // =========================================================

    private void openRDAction(
            String rdNumber) {

        Intent intent;

        // =====================================================
        // HISTORY
        // =====================================================

        if ("HISTORY".equalsIgnoreCase(action)) {

            intent =
                    new Intent(
                            context,
                            RDHistoryActivity.class
                    );

            intent.putExtra(
                    "RD_NUMBER",
                    rdNumber
            );

            context.startActivity(intent);

            return;
        }

        // =====================================================
        // PAY INSTALLMENT
        // =====================================================

        if ("PAY_INSTALLMENT".equalsIgnoreCase(action)) {

            intent =
                    new Intent(
                            context,
                            PayRDInstallmentActivity.class
                    );

            intent.putExtra(
                    "RD_NUMBER",
                    rdNumber
            );

            context.startActivity(intent);

            return;
        }

        // =====================================================
        // PREMATURE CLOSE
        // =====================================================

        if ("PREMATURE_CLOSE".equalsIgnoreCase(action)) {

            intent =
                    new Intent(
                            context,
                            PrematureCloseRDActivity.class
                    );

            intent.putExtra(
                    "RD_NUMBER",
                    rdNumber
            );

            context.startActivity(intent);

            return;
        }

        // =====================================================
        // DEFAULT → RD DETAILS
        // =====================================================

        intent =
                new Intent(
                        context,
                        RDDetailsActivity.class
                );

        intent.putExtra(
                "RD_NUMBER",
                rdNumber
        );

        context.startActivity(intent);
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        if (rdList == null) {
            return 0;
        }

        return rdList.size();
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            Object value) {

        if (value == null) {
            return "-";
        }

        return String.valueOf(value);
    }
}