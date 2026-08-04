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

import java.util.List;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TransactionViewHolder holder,
            int position) {

        Transaction transaction = transactionList.get(position);

        holder.tvType.setText(transaction.getType());

        holder.tvAccount.setText(
                "Account : " + transaction.getAccount());

        holder.tvTimestamp.setText(
                transaction.getTimestamp());

        holder.tvDirection.setText(
                transaction.getDirection());

        if ("CREDIT".equalsIgnoreCase(transaction.getDirection())) {

            holder.tvAmount.setText(
                    "+ ₹" + transaction.getAmount());

            holder.tvAmount.setTextColor(
                    Color.parseColor("#2E7D32"));

        } else {

            holder.tvAmount.setText(
                    "- ₹" + transaction.getAmount());

            holder.tvAmount.setTextColor(
                    Color.parseColor("#C62828"));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvAccount;
        TextView tvDirection;
        TextView tvAmount;
        TextView tvTimestamp;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvType);
            tvAccount = itemView.findViewById(R.id.tvAccount);
            tvDirection = itemView.findViewById(R.id.tvDirection);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}