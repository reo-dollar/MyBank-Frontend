package com.rohit.mybank.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    private static final int COLOR_DARK =
            Color.parseColor("#111827");

    private static final int COLOR_GRAY =
            Color.parseColor("#6B7280");

    private static final int COLOR_BLUE =
            Color.parseColor("#2563EB");

    private static final int COLOR_GREEN =
            Color.parseColor("#16A34A");

    private static final int COLOR_RED =
            Color.parseColor("#DC2626");

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a",
                    Locale.ENGLISH
            );

    public AdminTransactionAdapter(
            List<AdminTransactionResponse> transactions) {

        this.transactions = transactions;
    }

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
        // TYPE
        // =====================================================

        String type = transaction.getType();

        if (type == null || type.trim().isEmpty()) {
            type = "TRANSACTION";
        }

        type = type.trim().toUpperCase(Locale.ENGLISH);

        holder.tvTransactionType.setText(type);

        // =====================================================
        // AMOUNT
        // =====================================================

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("en", "IN")
                );

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        holder.tvTransactionAmount.setText(
                formatter.format(transaction.getAmount())
        );

        holder.tvTransactionAmount.setTextColor(
                COLOR_DARK
        );

        // =====================================================
        // TRANSACTION ID
        // =====================================================

        Long id = transaction.getId();

        holder.tvTransactionId.setText(
                "Transaction ID: #"
                        + (id == null ? "N/A" : id)
        );

        // =====================================================
        // FROM ACCOUNT
        // =====================================================

        String from = transaction.getFromAcc();

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

        String to = transaction.getToAcc();

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
        // REMARKS
        // =====================================================

        String remarks = transaction.getRemarks();

        if (isEmpty(remarks)) {
            holder.tvRemarks.setText("No remarks");
        } else {
            holder.tvRemarks.setText(remarks);
        }

        holder.tvRemarks.setVisibility(View.VISIBLE);

        // =====================================================
        // TRANSACTION COLORS
        // =====================================================

        if ("TRANSFER".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    COLOR_BLUE
            );

            holder.tvToAccount.setTextColor(
                    COLOR_BLUE
            );

        } else if ("DEPOSIT".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    COLOR_GRAY
            );

            holder.tvToAccount.setTextColor(
                    COLOR_GREEN
            );

        } else if ("WITHDRAW".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    COLOR_RED
            );

            holder.tvToAccount.setTextColor(
                    COLOR_GRAY
            );

        } else if ("PAYMENT".equalsIgnoreCase(type)) {

            holder.tvFromAccount.setTextColor(
                    COLOR_RED
            );

            holder.tvToAccount.setTextColor(
                    COLOR_DARK
            );

        } else {

            holder.tvFromAccount.setTextColor(
                    COLOR_GRAY
            );

            holder.tvToAccount.setTextColor(
                    COLOR_GRAY
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

    @Override
    public int getItemCount() {

        return transactions == null
                ? 0
                : transactions.size();
    }

    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    private String formatTimestamp(String timestamp) {

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

    static class TransactionViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTransactionType;
        TextView tvTransactionAmount;
        TextView tvTransactionId;
        TextView tvFromAccount;
        TextView tvToAccount;
        TextView tvRemarks;
        TextView tvTimestamp;

        TransactionViewHolder(
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