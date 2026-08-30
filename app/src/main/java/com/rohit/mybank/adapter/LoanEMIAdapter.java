package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.model.loan.LoanEMIResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LoanEMIAdapter
        extends RecyclerView.Adapter<
        LoanEMIAdapter.EMIViewHolder> {


    // =========================================================
    // DATA
    // =========================================================

    private final List<LoanEMIResponse> emiList =
            new ArrayList<>();


    // =========================================================
    // CLICK LISTENER
    // =========================================================

    public interface OnPayEMIClickListener {

        void onPayEMIClick(
                LoanEMIResponse emi
        );
    }


    private final OnPayEMIClickListener
            payEMIClickListener;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoanEMIAdapter(
            OnPayEMIClickListener payEMIClickListener
    ) {

        this.payEMIClickListener =
                payEMIClickListener;
    }


    // =========================================================
    // SET DATA
    // =========================================================

    public void setEMIs(
            List<LoanEMIResponse> emis
    ) {

        emiList.clear();


        if (emis != null) {

            emiList.addAll(
                    emis
            );
        }


        notifyDataSetChanged();
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public EMIViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_loan_emi,
                                parent,
                                false
                        );


        return new EMIViewHolder(
                view
        );
    }


    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull EMIViewHolder holder,
            int position
    ) {

        LoanEMIResponse emi =
                emiList.get(position);


        if (emi == null) {

            return;
        }


        // -----------------------------------------------------
        // EMI NUMBER
        // -----------------------------------------------------

        holder.tvEmiNumber.setText(
                "EMI #" +
                        safeInteger(
                                emi.getEmiNumber()
                        )
        );


        // -----------------------------------------------------
        // DUE DATE
        // -----------------------------------------------------

        holder.tvDueDate.setText(
                "Due Date: " +
                        safeString(
                                emi.getDueDate()
                        )
        );


        // -----------------------------------------------------
        // EMI AMOUNT
        // -----------------------------------------------------

        holder.tvEmiAmount.setText(
                formatAmount(
                        emi.getEmiAmount()
                )
        );


        // -----------------------------------------------------
        // PRINCIPAL COMPONENT
        // -----------------------------------------------------

        holder.tvPrincipalComponent.setText(
                formatAmount(
                        emi.getPrincipalComponent()
                )
        );


        // -----------------------------------------------------
        // INTEREST COMPONENT
        // -----------------------------------------------------

        holder.tvInterestComponent.setText(
                formatAmount(
                        emi.getInterestComponent()
                )
        );


        // -----------------------------------------------------
        // OPENING PRINCIPAL
        // -----------------------------------------------------

        holder.tvOpeningPrincipal.setText(
                formatAmount(
                        emi.getOpeningPrincipal()
                )
        );


        // -----------------------------------------------------
        // CLOSING PRINCIPAL
        // -----------------------------------------------------

        holder.tvClosingPrincipal.setText(
                formatAmount(
                        emi.getClosingPrincipal()
                )
        );


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        String status =
                emi.getStatus() != null
                        ? emi.getStatus().name()
                        : "N/A";


        holder.tvStatus.setText(
                status
        );


        // -----------------------------------------------------
        // PAYMENT DATE
        // -----------------------------------------------------

        String paymentDate =
                emi.getPaymentDate();


        if (paymentDate == null ||
                paymentDate.trim().isEmpty()) {

            holder.tvPaymentDate.setText(
                    "Payment Date: —"
            );

        } else {

            holder.tvPaymentDate.setText(
                    "Payment Date: " +
                            paymentDate
            );
        }


        // -----------------------------------------------------
        // RESET BUTTON
        // -----------------------------------------------------

        holder.btnPayEmi.setOnClickListener(
                null
        );


        // -----------------------------------------------------
        // PAID EMI
        // -----------------------------------------------------

        if ("PAID".equalsIgnoreCase(
                status
        )) {

            holder.btnPayEmi.setVisibility(
                    View.GONE
            );


            return;
        }


        // -----------------------------------------------------
        // NON-PENDING EMI
        // -----------------------------------------------------

        if (!"PENDING".equalsIgnoreCase(
                status
        )) {

            holder.btnPayEmi.setVisibility(
                    View.GONE
            );


            return;
        }


        // -----------------------------------------------------
        // FIND FIRST PENDING EMI
        // -----------------------------------------------------

        int firstPendingPosition =
                findFirstPendingPosition();


        // -----------------------------------------------------
        // NEXT PAYABLE EMI
        // -----------------------------------------------------

        if (position ==
                firstPendingPosition) {

            holder.btnPayEmi.setVisibility(
                    View.VISIBLE
            );


            holder.btnPayEmi.setEnabled(
                    true
            );


            holder.btnPayEmi.setText(
                    "Pay EMI"
            );


            holder.btnPayEmi.setOnClickListener(
                    v -> {

                        if (payEMIClickListener != null) {

                            payEMIClickListener
                                    .onPayEMIClick(
                                            emi
                                    );
                        }
                    }
            );


        } else {

            /*
             * Later pending EMIs cannot be paid
             * before the previous EMI.
             */

            holder.btnPayEmi.setVisibility(
                    View.VISIBLE
            );


            holder.btnPayEmi.setEnabled(
                    false
            );


            holder.btnPayEmi.setText(
                    "Pay Previous EMI First"
            );
        }
    }


    // =========================================================
    // FIND FIRST PENDING EMI
    // =========================================================

    private int findFirstPendingPosition() {

        for (int i = 0;
             i < emiList.size();
             i++) {

            LoanEMIResponse emi =
                    emiList.get(i);


            if (emi == null) {

                continue;
            }


            if (emi.getStatus() != null &&
                    "PENDING".equalsIgnoreCase(
                            emi.getStatus().name()
                    )) {

                return i;
            }
        }


        return -1;
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return emiList.size();
    }


    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(
            Double amount
    ) {

        if (amount == null) {

            return "₹ 0.00";
        }


        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale(
                                "en",
                                "IN"
                        )
                );


        formatter.setMinimumFractionDigits(
                2
        );


        formatter.setMaximumFractionDigits(
                2
        );


        return "₹ " +
                formatter.format(
                        amount
                );
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "N/A";
        }


        return value;
    }


    // =========================================================
    // SAFE INTEGER
    // =========================================================

    private String safeInteger(
            Integer value
    ) {

        if (value == null) {

            return "N/A";
        }


        return String.valueOf(
                value
        );
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class EMIViewHolder
            extends RecyclerView.ViewHolder {


        TextView tvEmiNumber;

        TextView tvDueDate;

        TextView tvEmiAmount;

        TextView tvPrincipalComponent;

        TextView tvInterestComponent;

        TextView tvOpeningPrincipal;

        TextView tvClosingPrincipal;

        TextView tvStatus;

        TextView tvPaymentDate;


        MaterialButton btnPayEmi;


        EMIViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            tvEmiNumber =
                    itemView.findViewById(
                            R.id.tvEmiNumber
                    );


            tvDueDate =
                    itemView.findViewById(
                            R.id.tvDueDate
                    );


            tvEmiAmount =
                    itemView.findViewById(
                            R.id.tvEmiAmount
                    );


            tvPrincipalComponent =
                    itemView.findViewById(
                            R.id.tvPrincipalComponent
                    );


            tvInterestComponent =
                    itemView.findViewById(
                            R.id.tvInterestComponent
                    );


            tvOpeningPrincipal =
                    itemView.findViewById(
                            R.id.tvOpeningPrincipal
                    );


            tvClosingPrincipal =
                    itemView.findViewById(
                            R.id.tvClosingPrincipal
                    );


            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );


            tvPaymentDate =
                    itemView.findViewById(
                            R.id.tvPaymentDate
                    );


            btnPayEmi =
                    itemView.findViewById(
                            R.id.btnPayEmi
                    );
        }
    }
}