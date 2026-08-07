package com.rohit.mybank.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;

public class RDViewHolder extends RecyclerView.ViewHolder {

    public TextView tvRDNumber;
    public TextView tvCustomerName;
    public TextView tvMonthlyInstallment;
    public TextView tvMaturityAmount;
    public TextView tvStatus;

    public RDViewHolder(@NonNull View itemView) {
        super(itemView);

        tvRDNumber = itemView.findViewById(R.id.tvRDNumber);

        tvCustomerName = itemView.findViewById(R.id.tvCustomerName);

        tvMonthlyInstallment =
                itemView.findViewById(R.id.tvMonthlyInstallment);

        tvMaturityAmount =
                itemView.findViewById(R.id.tvMaturityAmount);

        tvStatus =
                itemView.findViewById(R.id.tvStatus);
    }
}