package com.rohit.mybank.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;

public class RDHistoryViewHolder extends RecyclerView.ViewHolder {

    TextView tvHistoryTransactionType;
    TextView tvHistoryAmount;
    TextView tvHistoryDate;
    TextView tvHistoryPaymentMode;
    TextView tvHistoryStatus;
    TextView tvHistoryRemarks;

    public RDHistoryViewHolder(
            @NonNull View itemView) {

        super(itemView);

        tvHistoryTransactionType =
                itemView.findViewById(
                        R.id.tvHistoryTransactionType
                );

        tvHistoryAmount =
                itemView.findViewById(
                        R.id.tvHistoryAmount
                );

        tvHistoryDate =
                itemView.findViewById(
                        R.id.tvHistoryDate
                );

        tvHistoryPaymentMode =
                itemView.findViewById(
                        R.id.tvHistoryPaymentMode
                );

        tvHistoryStatus =
                itemView.findViewById(
                        R.id.tvHistoryStatus
                );

        tvHistoryRemarks =
                itemView.findViewById(
                        R.id.tvHistoryRemarks
                );
    }
}