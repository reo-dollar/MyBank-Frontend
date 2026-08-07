package com.rohit.mybank.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;

public class RDHistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tvTransactionId;
    public TextView tvPaymentDate;
    public TextView tvAmount;
    public TextView tvPaymentMode;
    public TextView tvStatus;

    public RDHistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
        tvPaymentDate = itemView.findViewById(R.id.tvPaymentDate);
        tvAmount = itemView.findViewById(R.id.tvAmount);
        tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
        tvStatus = itemView.findViewById(R.id.tvStatus);
    }
}