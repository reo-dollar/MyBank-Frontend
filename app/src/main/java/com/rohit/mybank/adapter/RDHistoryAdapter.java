package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class RDHistoryAdapter
        extends RecyclerView.Adapter<RDHistoryViewHolder> {

    private final List<RDHistoryResponse> historyList;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RDHistoryAdapter(
            List<RDHistoryResponse> historyList) {

        this.historyList = historyList;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public RDHistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_rd_history,
                        parent,
                        false
                );

        return new RDHistoryViewHolder(view);
    }

    // =========================================================
    // BIND VIEW
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull RDHistoryViewHolder holder,
            int position) {

        RDHistoryResponse history =
                historyList.get(position);

        if (history == null) {
            return;
        }

        // =====================================================
        // TRANSACTION TYPE
        // =====================================================

        holder.tvHistoryTransactionType.setText(
                formatTransactionType(
                        history.getTransactionType()
                )
        );

        // =====================================================
        // AMOUNT
        // =====================================================

        holder.tvHistoryAmount.setText(
                formatAmount(
                        history.getAmount()
                )
        );

        // =====================================================
        // DATE
        // =====================================================

        holder.tvHistoryDate.setText(
                formatDate(
                        history.getPaymentDate()
                )
        );

        // =====================================================
        // PAYMENT MODE
        // =====================================================

        holder.tvHistoryPaymentMode.setText(
                safeText(
                        history.getPaymentMode()
                )
        );

        // =====================================================
        // STATUS
        // =====================================================

        holder.tvHistoryStatus.setText(
                safeText(
                        history.getStatus()
                )
        );

        // =====================================================
        // REMARKS
        // =====================================================

        holder.tvHistoryRemarks.setText(
                safeText(
                        history.getRemarks()
                )
        );
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        if (historyList == null) {
            return 0;
        }

        return historyList.size();
    }

    // =========================================================
    // FORMAT AMOUNT
    // =========================================================

    private String formatAmount(
            Double amount) {

        if (amount == null) {
            return "₹-";
        }

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("en", "IN")
                );

        return formatter.format(amount);
    }

    // =========================================================
    // FORMAT TRANSACTION TYPE
    // =========================================================

    private String formatTransactionType(
            String type) {

        if (type == null
                || type.trim().isEmpty()) {

            return "-";
        }

        switch (
                type.toUpperCase(
                        Locale.ROOT
                )
        ) {

            case "RD_INSTALLMENT":
                return "RD INSTALLMENT";

            case "RD_CLOSURE":
                return "RD MATURITY CLOSURE";

            case "RD_PREMATURE_CLOSURE":
                return "RD PREMATURE CLOSURE";

            case "RD_PREMATURE_CLOSE":
                return "RD PREMATURE CLOSURE";

            default:
                return type.replace(
                        "_",
                        " "
                );
        }
    }

    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        try {

            LocalDateTime date =
                    LocalDateTime.parse(
                            value
                    );

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy, hh:mm a",
                            Locale.ENGLISH
                    );

            return date.format(
                    formatter
            );

        } catch (Exception ignored) {

            return value;
        }
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }
}