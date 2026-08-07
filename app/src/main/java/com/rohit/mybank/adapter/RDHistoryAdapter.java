package com.rohit.mybank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;

import java.util.List;

public class RDHistoryAdapter extends RecyclerView.Adapter<RDHistoryViewHolder> {

    private final Context context;
    private final List<RDHistoryResponse> historyList;

    public RDHistoryAdapter(Context context,
                            List<RDHistoryResponse> historyList) {

        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public RDHistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_rd_history, parent, false);

        return new RDHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RDHistoryViewHolder holder,
            int position) {

        RDHistoryResponse history = historyList.get(position);

        holder.tvTransactionId.setText(history.getTransactionId());

        holder.tvPaymentDate.setText(history.getPaymentDate());

        holder.tvAmount.setText("₹ " + history.getAmount());

        holder.tvPaymentMode.setText(history.getPaymentMode());

        holder.tvStatus.setText(history.getStatus());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}