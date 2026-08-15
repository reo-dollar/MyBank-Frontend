package com.rohit.mybank.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.transaction.Transaction;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class AdminAccountTransactionAdapter
        extends RecyclerView.Adapter<
        AdminAccountTransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactions;

    // =========================================================
    // COLORS
    // =========================================================

    private static final int COLOR_GREEN =
            Color.parseColor("#16A34A");

    private static final int COLOR_RED =
            Color.parseColor("#DC2626");

    private static final int COLOR_GRAY =
            Color.parseColor("#6B7280");

    private static final int COLOR_DARK =
            Color.parseColor("#111827");

    // =========================================================
    // DATE FORMAT
    // =========================================================

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a",
                    Locale.ENGLISH
            );

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminAccountTransactionAdapter(
            List<Transaction> transactions) {

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

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_account_transaction,
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

        Transaction transaction =
                transactions.get(position);

        if (transaction == null) {
            return;
        }

        // =====================================================
        // TYPE
        // =====================================================

        String type =
                safe(transaction.getType());

        holder.tvType.setText(
                type.toUpperCase(Locale.ENGLISH)
        );

        // =====================================================
        // AMOUNT
        // =====================================================

        NumberFormat currencyFormat =
                NumberFormat.getCurrencyInstance(
                        new Locale("en", "IN")
                );

        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);

        String amount =
                currencyFormat.format(
                        transaction.getAmount()
                );

        // =====================================================
        // DIRECTION
        // =====================================================

        String direction =
                safe(transaction.getDirection());

        if ("CREDIT".equalsIgnoreCase(direction)) {

            holder.tvAmount.setText(
                    "+ " + amount
            );

            holder.tvAmount.setTextColor(
                    COLOR_GREEN
            );

        } else if ("DEBIT".equalsIgnoreCase(direction)) {

            holder.tvAmount.setText(
                    "- " + amount
            );

            holder.tvAmount.setTextColor(
                    COLOR_RED
            );

        } else {

            holder.tvAmount.setText(amount);

            holder.tvAmount.setTextColor(
                    COLOR_DARK
            );
        }

        // =====================================================
        // TRANSACTION ID
        // =====================================================

        /*
         * Customer Transaction model may not contain an ID.
         *
         * Therefore this field is intentionally hidden for
         * account transaction history.
         */

        if (holder.tvTransactionId != null) {

            holder.tvTransactionId.setVisibility(
                    View.GONE
            );
        }

        // =====================================================
        // ACCOUNT
        // =====================================================

        String account =
                safe(transaction.getAccount());

        holder.tvFromAccount.setText(
                "Account: " + account
        );

        /*
         * Account-specific transaction history does not need
         * separate From / To fields.
         */

        holder.tvToAccount.setVisibility(
                View.GONE
        );

        // =====================================================
        // REMARKS
        // =====================================================

        if (holder.tvRemarks != null) {

            holder.tvRemarks.setVisibility(
                    View.GONE
            );
        }

        // =====================================================
        // TIMESTAMP
        // =====================================================

        holder.tvTimestamp.setText(
                formatTimestamp(
                        transaction.getTimestamp()
                )
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
    // SAFE STRING
    // =========================================================

    private String safe(String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value.trim();
    }

    // =========================================================
    // FORMAT TIMESTAMP
    // =========================================================

    private String formatTimestamp(
            String timestamp) {

        if (timestamp == null
                || timestamp.trim().isEmpty()) {

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

        } catch (DateTimeParseException e) {

            return timestamp;
        }
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class TransactionViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvAmount;

        TextView tvTransactionId;

        TextView tvFromAccount;
        TextView tvToAccount;

        TextView tvRemarks;
        TextView tvTimestamp;

        TransactionViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvType =
                    itemView.findViewById(
                            R.id.tvTransactionType
                    );

            tvAmount =
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