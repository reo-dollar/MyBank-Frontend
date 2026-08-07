package com.rohit.mybank.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.payments.recurringdeposit.RDDetailsActivity;
import com.rohit.mybank.model.recurringdeposit.RDResponse;

import java.util.List;

public class RDAdapter extends RecyclerView.Adapter<RDViewHolder> {

    private final Context context;
    private final List<RDResponse> rdList;

    public RDAdapter(Context context,
                     List<RDResponse> rdList) {

        this.context = context;
        this.rdList = rdList;
    }

    @NonNull
    @Override
    public RDViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_rd,
                        parent,
                        false
                );

        return new RDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RDViewHolder holder,
            int position) {

        RDResponse rd = rdList.get(position);

        holder.tvRDNumber.setText(rd.getRdNumber());

        holder.tvCustomerName.setText(rd.getCustomerName());

        holder.tvMonthlyInstallment.setText(
                "Monthly Installment : ₹" +
                        rd.getMonthlyInstallment()
        );

        holder.tvMaturityAmount.setText(
                "Maturity Amount : ₹" +
                        rd.getMaturityAmount()
        );

        holder.tvStatus.setText(
                String.valueOf(rd.getStatus())
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    RDDetailsActivity.class
            );

            intent.putExtra(
                    "RD_NUMBER",
                    rd.getRdNumber()
            );

            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {

        return rdList.size();

    }

}