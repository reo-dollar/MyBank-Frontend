package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.model.loan.LoanResponse;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * =========================================================
 * LOAN ADAPTER
 * =========================================================
 *
 * Displays customer's loans inside RecyclerView.
 *
 * Used by:
 *
 * MyLoansActivity
 *
 * =========================================================
 */
public class LoanAdapter
        extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {

    // =========================================================
    // LOAN CLICK LISTENER
    // =========================================================

    public interface OnLoanClickListener {

        void onLoanClick(LoanResponse loan);
    }

    // =========================================================
    // DATA
    // =========================================================

    private final List<LoanResponse> loanList =
            new ArrayList<>();

    // =========================================================
    // LISTENER
    // =========================================================

    private final OnLoanClickListener listener;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoanAdapter(
            OnLoanClickListener listener
    ) {

        this.listener = listener;
    }

    // =========================================================
    // SET LOANS
    // =========================================================

    public void setLoans(
            List<LoanResponse> loans
    ) {

        loanList.clear();

        if (loans != null) {

            loanList.addAll(loans);
        }

        notifyDataSetChanged();
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_loan,
                                parent,
                                false
                        );

        return new LoanViewHolder(view);
    }

    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull LoanViewHolder holder,
            int position
    ) {

        LoanResponse loan =
                loanList.get(position);

        // =====================================================
        // LOAN NUMBER
        // =====================================================

        String loanNumber =
                loan.getLoanNumber();

        if (loanNumber != null &&
                !loanNumber.trim().isEmpty()) {

            holder.tvLoanNumber.setText(
                    loanNumber
            );

        } else {

            holder.tvLoanNumber.setText(
                    "Loan Number Unavailable"
            );
        }

        // =====================================================
        // LOAN TYPE
        // =====================================================

        if (loan.getLoanType() != null) {

            holder.tvLoanType.setText(
                    loan.getLoanType().name()
            );

        } else {

            holder.tvLoanType.setText(
                    "N/A"
            );
        }

        // =====================================================
        // PRINCIPAL
        // =====================================================

        if (loan.getPrincipalAmount() != null) {

            holder.tvPrincipal.setText(
                    formatAmount(
                            loan.getPrincipalAmount()
                    )
            );

        } else {

            holder.tvPrincipal.setText(
                    "₹ 0.00"
            );
        }

        // =====================================================
        // EMI
        // =====================================================

        if (loan.getEmiAmount() != null) {

            holder.tvEmi.setText(
                    formatAmount(
                            loan.getEmiAmount()
                    )
            );

        } else {

            holder.tvEmi.setText(
                    "₹ 0.00"
            );
        }

        // =====================================================
        // OUTSTANDING PRINCIPAL
        // =====================================================

        if (loan.getOutstandingPrincipal() != null) {

            holder.tvOutstanding.setText(
                    formatAmount(
                            loan.getOutstandingPrincipal()
                    )
            );

        } else {

            holder.tvOutstanding.setText(
                    "₹ 0.00"
            );
        }

        // =====================================================
        // STATUS
        // =====================================================

        if (loan.getStatus() != null) {

            holder.tvStatus.setText(
                    loan.getStatus().name()
            );

        } else {

            holder.tvStatus.setText(
                    "UNKNOWN"
            );
        }

        // =====================================================
        // VIEW DETAILS BUTTON
        // =====================================================

        holder.btnViewDetails.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onLoanClick(
                                loan
                        );
                    }
                }
        );

        // =====================================================
        // CARD CLICK
        // =====================================================

        holder.itemView.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onLoanClick(
                                loan
                        );
                    }
                }
        );
    }

    // =========================================================
    // FORMAT MONEY
    // =========================================================
    //
    // Number is intentionally used instead of BigDecimal
    // because Android LoanResponse currently uses Double
    // for financial values.
    //
    // This also keeps the adapter compatible if the model
    // is later changed to BigDecimal.
    // =========================================================

    private String formatAmount(
            Number amount
    ) {

        if (amount == null) {

            return "₹ 0.00";
        }

        return String.format(
                Locale.US,
                "₹ %,.2f",
                amount.doubleValue()
        );
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return loanList.size();
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class LoanViewHolder
            extends RecyclerView.ViewHolder {

        // =====================================================
        // TEXT VIEWS
        // =====================================================

        final TextView tvLoanNumber;

        final TextView tvLoanType;

        final TextView tvPrincipal;

        final TextView tvEmi;

        final TextView tvOutstanding;

        final TextView tvStatus;

        // =====================================================
        // BUTTON
        // =====================================================

        final MaterialButton btnViewDetails;

        // =====================================================
        // CONSTRUCTOR
        // =====================================================

        LoanViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            // =================================================
            // FIND VIEWS
            // =================================================

            tvLoanNumber =
                    itemView.findViewById(
                            R.id.tvLoanNumber
                    );

            tvLoanType =
                    itemView.findViewById(
                            R.id.tvLoanType
                    );

            tvPrincipal =
                    itemView.findViewById(
                            R.id.tvPrincipal
                    );

            tvEmi =
                    itemView.findViewById(
                            R.id.tvEmi
                    );

            tvOutstanding =
                    itemView.findViewById(
                            R.id.tvOutstanding
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );

            btnViewDetails =
                    itemView.findViewById(
                            R.id.btnViewDetails
                    );
        }
    }
}