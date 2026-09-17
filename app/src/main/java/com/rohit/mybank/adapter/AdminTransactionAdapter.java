package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.admin.AdminTransactionResponse;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class AdminTransactionAdapter
        extends RecyclerView.Adapter<AdminTransactionAdapter.TransactionViewHolder> {

    private final List<AdminTransactionResponse> transactions;

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a",
                    Locale.ENGLISH
            );

    public AdminTransactionAdapter(
            List<AdminTransactionResponse> transactions) {

        this.transactions = transactions;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_admin_transaction,
                        parent,
                        false
                );

        return new TransactionViewHolder(view);
    }

    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull TransactionViewHolder holder,
            int position) {

        AdminTransactionResponse transaction =
                transactions.get(position);

        if (transaction == null) {
            return;
        }

        // =====================================================
        // CONTEXT
        // =====================================================

        android.content.Context context =
                holder.itemView.getContext();

        // =====================================================
        // COLORS
        // =====================================================

        int primaryColor =
                ContextCompat.getColor(
                        context,
                        R.color.admin_text_primary
                );

        int secondaryColor =
                ContextCompat.getColor(
                        context,
                        R.color.admin_text_secondary
                );

        int mutedColor =
                ContextCompat.getColor(
                        context,
                        R.color.admin_text_muted
                );

        int primaryBlue =
                ContextCompat.getColor(
                        context,
                        R.color.admin_primary
                );

        int successColor =
                ContextCompat.getColor(
                        context,
                        R.color.admin_success
                );

        int errorColor =
                ContextCompat.getColor(
                        context,
                        R.color.admin_error
                );

        // =====================================================
        // TYPE
        // =====================================================

        String type = transaction.getType();

        if (isEmpty(type)) {
            type = "TRANSACTION";
        }

        type = type.trim()
                .toUpperCase(Locale.ENGLISH);

        holder.tvTransactionType.setText(type);

        holder.tvTransactionType.setTextColor(
                primaryColor
        );

        // =====================================================
        // AMOUNT
        // =====================================================

        Double amount = transaction.getAmount();

        if (amount == null) {
            amount = 0.0;
        }

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("en", "IN")
                );

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        double absoluteAmount =
                Math.abs(amount);

        String formattedAmount =
                formatter.format(absoluteAmount);

        /*
         * Bank transaction presentation:
         *
         * DEPOSIT                         → + amount
         * TRANSFER / WITHDRAW / PAYMENT   → - amount
         * RD / FD deposits                → - amount
         *
         * If the backend already provides a negative amount,
         * Math.abs() prevents a double minus sign.
         */

        if (isDebitTransaction(type)) {

            holder.tvTransactionAmount.setText(
                    "- " + formattedAmount
            );

            holder.tvTransactionAmount.setTextColor(
                    errorColor
            );

        } else if (isCreditTransaction(type)) {

            holder.tvTransactionAmount.setText(
                    "+ " + formattedAmount
            );

            holder.tvTransactionAmount.setTextColor(
                    successColor
            );

        } else {

            holder.tvTransactionAmount.setText(
                    formattedAmount
            );

            holder.tvTransactionAmount.setTextColor(
                    primaryColor
            );
        }

        // =====================================================
        // TRANSACTION ID
        // =====================================================

        Long id = transaction.getId();

        String transactionId =
                id == null
                        ? "N/A"
                        : String.valueOf(id);

        holder.tvTransactionId.setText(
                "Transaction ID: #" + transactionId
        );

        holder.tvTransactionId.setTextColor(
                secondaryColor
        );

        // =====================================================
        // FROM ACCOUNT
        // =====================================================

        String from =
                transaction.getFromAcc();

        if (isEmpty(from)) {

            switch (type) {

                case "DEPOSIT":
                    from = "Cash / Deposit";
                    break;

                case "PAYMENT":
                    from = "Not available";
                    break;

                default:
                    from = "Not available";
                    break;
            }
        }

        holder.tvFromAccount.setText(
                "From: " + from
        );

        // =====================================================
        // TO ACCOUNT
        // =====================================================

        String to =
                transaction.getToAcc();

        if (isEmpty(to)) {

            switch (type) {

                case "DEPOSIT":
                    to = "Customer Account";
                    break;

                case "WITHDRAW":
                    to = "Cash / Withdrawal";
                    break;

                case "PAYMENT":
                    to = "External / Merchant";
                    break;

                default:
                    to = "Not available";
                    break;
            }
        }

        holder.tvToAccount.setText(
                "To: " + to
        );

        // =====================================================
        // ACCOUNT COLORS
        // =====================================================

        if ("TRANSFER".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    primaryBlue
            );

            holder.tvToAccount.setTextColor(
                    primaryBlue
            );

        } else if ("DEPOSIT".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    secondaryColor
            );

            holder.tvToAccount.setTextColor(
                    successColor
            );

        } else if ("WITHDRAW".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    errorColor
            );

            holder.tvToAccount.setTextColor(
                    secondaryColor
            );

        } else if ("PAYMENT".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    errorColor
            );

            holder.tvToAccount.setTextColor(
                    secondaryColor
            );

        } else {

            holder.tvFromAccount.setTextColor(
                    secondaryColor
            );

            holder.tvToAccount.setTextColor(
                    secondaryColor
            );
        }

        // =====================================================
        // REMARKS
        // =====================================================

        String remarks =
                transaction.getRemarks();

        if (isEmpty(remarks)) {

            holder.tvRemarks.setText(
                    "No remarks"
            );

        } else {

            holder.tvRemarks.setText(
                    remarks
            );
        }

        holder.tvRemarks.setTextColor(
                secondaryColor
        );

        holder.tvRemarks.setVisibility(
                View.VISIBLE
        );

        // =====================================================
        // TIMESTAMP
        // =====================================================

        holder.tvTimestamp.setText(
                formatTimestamp(
                        transaction.getTimestamp()
                )
        );

        holder.tvTimestamp.setTextColor(
                mutedColor
        );
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return transactions == null
                ? 0
                : transactions.size();
    }

    // =========================================================
    // CHECK EMPTY
    // =========================================================

    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    // =========================================================
    // DEBIT TRANSACTION
    // =========================================================

    private boolean isDebitTransaction(String type) {

        if (type == null) {
            return false;
        }

        switch (type.toUpperCase(Locale.ENGLISH)) {

            case "TRANSFER":
            case "WITHDRAW":
            case "PAYMENT":
            case "RD_INSTALLMENT":
            case "RECURRING_DEPOSIT":
            case "FIXED_DEPOSIT":
                return true;

            default:
                return false;
        }
    }

    // =========================================================
    // CREDIT TRANSACTION
    // =========================================================

    private boolean isCreditTransaction(String type) {

        if (type == null) {
            return false;
        }

        return "DEPOSIT".equalsIgnoreCase(type);
    }

    // =========================================================
    // FORMAT TIMESTAMP
    // =========================================================

    private String formatTimestamp(
            String timestamp) {

        if (isEmpty(timestamp)) {
            return "Date unavailable";
        }

        try {

            LocalDateTime dateTime =
                    LocalDateTime.parse(
                            timestamp.trim()
                    );

            return dateTime.format(
                    OUTPUT_FORMAT
            );

        } catch (Exception e) {

            return timestamp;
        }
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static final class TransactionViewHolder
            extends RecyclerView.ViewHolder {

        final TextView tvTransactionType;
        final TextView tvTransactionAmount;
        final TextView tvTransactionId;
        final TextView tvFromAccount;
        final TextView tvToAccount;
        final TextView tvRemarks;
        final TextView tvTimestamp;

        public TransactionViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvTransactionType =
                    itemView.findViewById(
                            R.id.tvTransactionType
                    );

            tvTransactionAmount =
                    itemView.findViewById(
                            R.id.tvTransactionAmount
                    );

            tvTransactionId =
                    itemView.findViewById(
                            R.id.tvTransactionId
                    );

            tvFromAccount =
                    itemView.findViewById(
                            R.id.tvFromAccount
                    );

            tvToAccount =
                    itemView.findViewById(
                            R.id.tvToAccount
                    );

            tvRemarks =
                    itemView.findViewById(
                            R.id.tvRemarks
                    );

            tvTimestamp =
                    itemView.findViewById(
                            R.id.tvTimestamp
                    );
        }
    }
}