package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.loan.LoanEMIPaymentHistoryResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoanEMIPaymentHistoryAdapter
        extends RecyclerView.Adapter<
        LoanEMIPaymentHistoryAdapter.PaymentHistoryViewHolder> {

    // =========================================================
    // DATA
    // =========================================================

    private final List<LoanEMIPaymentHistoryResponse> historyList =
            new ArrayList<>();


    // =========================================================
    // SET HISTORY
    // =========================================================

    public void setHistory(
            List<LoanEMIPaymentHistoryResponse> history
    ) {

        historyList.clear();

        if (history != null) {
            historyList.addAll(history);
        }

        notifyDataSetChanged();
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public PaymentHistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_loan_payment_history,
                        parent,
                        false
                );

        return new PaymentHistoryViewHolder(view);
    }


    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull PaymentHistoryViewHolder holder,
            int position
    ) {

        LoanEMIPaymentHistoryResponse payment =
                historyList.get(position);

        if (payment == null) {
            return;
        }


        // =====================================================
        // EMI NUMBER
        // =====================================================

        Integer emiNumber = payment.getEmiNumber();

        if (emiNumber != null) {

            holder.tvEmiNumber.setText(
                    "EMI #" + emiNumber
            );

        } else {

            holder.tvEmiNumber.setText(
                    "EMI #N/A"
            );
        }


        // =====================================================
        // LOAN NUMBER
        // =====================================================

        holder.tvLoanNumber.setText(
                safeString(
                        payment.getLoanNumber()
                )
        );


        // =====================================================
        // PAYMENT DATE
        // =====================================================

        holder.tvPaymentDate.setText(
                "Payment Date: " +
                        safeString(
                                payment.getPaymentDate()
                        )
        );


        // =====================================================
        // DUE DATE
        // =====================================================

        holder.tvDueDate.setText(
                "Due Date: " +
                        safeString(
                                payment.getDueDate()
                        )
        );


        // =====================================================
        // PAYMENT AMOUNT
        // =====================================================

        holder.tvPaymentAmount.setText(
                formatAmount(
                        payment.getPaymentAmount()
                )
        );


        // =====================================================
        // PRINCIPAL PAID
        // =====================================================

        holder.tvPrincipalPaid.setText(
                formatAmount(
                        payment.getPrincipalPaid()
                )
        );


        // =====================================================
        // INTEREST PAID
        // =====================================================

        holder.tvInterestPaid.setText(
                formatAmount(
                        payment.getInterestPaid()
                )
        );


        // =====================================================
        // STATUS
        // =====================================================

        if (payment.getStatus() != null) {

            holder.tvStatus.setText(
                    payment.getStatus().name()
            );

        } else {

            holder.tvStatus.setText(
                    "PAID"
            );
        }
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {
        return historyList.size();
    }


    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(Double amount) {

        if (amount == null) {
            return "₹ 0.00";
        }

        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        Locale.ENGLISH
                );

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return "₹ " + formatter.format(amount);
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class PaymentHistoryViewHolder
            extends RecyclerView.ViewHolder {

        final TextView tvEmiNumber;
        final TextView tvLoanNumber;

        final TextView tvPaymentDate;
        final TextView tvDueDate;

        final TextView tvPaymentAmount;
        final TextView tvPrincipalPaid;
        final TextView tvInterestPaid;

        final TextView tvStatus;


        // =====================================================
        // CONSTRUCTOR
        // =====================================================

        public PaymentHistoryViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            tvEmiNumber =
                    itemView.findViewById(
                            R.id.tvEmiNumber
                    );


            tvLoanNumber =
                    itemView.findViewById(
                            R.id.tvLoanNumber
                    );


            tvPaymentDate =
                    itemView.findViewById(
                            R.id.tvPaymentDate
                    );


            tvDueDate =
                    itemView.findViewById(
                            R.id.tvDueDate
                    );


            tvPaymentAmount =
                    itemView.findViewById(
                            R.id.tvPaymentAmount
                    );


            tvPrincipalPaid =
                    itemView.findViewById(
                            R.id.tvPrincipalPaid
                    );


            tvInterestPaid =
                    itemView.findViewById(
                            R.id.tvInterestPaid
                    );


            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );
        }
    }
}