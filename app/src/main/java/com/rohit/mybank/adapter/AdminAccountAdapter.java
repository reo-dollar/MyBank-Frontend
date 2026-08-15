package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.admin.AdminAccountResponse;

import java.util.List;
import java.util.Locale;

public class AdminAccountAdapter
        extends RecyclerView.Adapter<
        AdminAccountAdapter.AccountViewHolder> {

    // =========================================================
    // CLICK LISTENER
    // =========================================================

    public interface OnAccountClickListener {

        void onAccountClick(
                AdminAccountResponse account
        );
    }

    // =========================================================
    // DATA
    // =========================================================

    private final List<AdminAccountResponse> accounts;

    private final OnAccountClickListener listener;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminAccountAdapter(
            List<AdminAccountResponse> accounts,
            OnAccountClickListener listener) {

        this.accounts = accounts;
        this.listener = listener;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_account,
                                parent,
                                false
                        );

        return new AccountViewHolder(view);
    }

    // =========================================================
    // BIND
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull AccountViewHolder holder,
            int position) {

        AdminAccountResponse account =
                accounts.get(position);

        // =====================================================
        // ACCOUNT TYPE
        // =====================================================

        holder.tvAccountType.setText(
                safe(account.getAccountType())
        );

        // =====================================================
        // ACCOUNT NUMBER
        // =====================================================

        holder.tvAccountNumber.setText(
                "Account No: "
                        + safe(account.getAccNo())
        );

        // =====================================================
        // CUSTOMER
        // =====================================================

        holder.tvCustomerName.setText(
                safe(account.getCustomerName())
        );

        // =====================================================
        // CUSTOMER ID
        // =====================================================

        holder.tvCustomerId.setText(
                "Customer ID: "
                        + safe(account.getCustomerId())
        );

        // =====================================================
        // BALANCE
        // =====================================================

        holder.tvBalance.setText(
                String.format(
                        Locale.getDefault(),
                        "Balance: ₹%,.2f",
                        account.getBalance()
                )
        );

        // =====================================================
        // STATUS
        // =====================================================

        String status =
                safe(account.getStatus());

        holder.tvStatus.setText(
                "● " + status.toUpperCase(
                        Locale.getDefault()
                )
        );

        // =====================================================
        // STATUS TEXT COLOR
        // =====================================================

        if ("ACTIVE".equalsIgnoreCase(status)) {

            holder.tvStatus.setTextColor(
                    0xFF16A34A
            );

        } else if ("BLOCKED".equalsIgnoreCase(status)
                || "LOCKED".equalsIgnoreCase(status)) {

            holder.tvStatus.setTextColor(
                    0xFFDC2626
            );

        } else {

            holder.tvStatus.setTextColor(
                    0xFFD97706
            );
        }

        // =====================================================
        // CLICK
        // =====================================================

        holder.itemView.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onAccountClick(
                                account
                        );
                    }
                }
        );
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return accounts.size();
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class AccountViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvAccountType;
        TextView tvAccountNumber;
        TextView tvCustomerName;
        TextView tvCustomerId;
        TextView tvBalance;
        TextView tvStatus;
        TextView tvViewDetails;

        AccountViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvAccountType =
                    itemView.findViewById(
                            R.id.tvAccountType
                    );

            tvAccountNumber =
                    itemView.findViewById(
                            R.id.tvAccountNumber
                    );

            tvCustomerName =
                    itemView.findViewById(
                            R.id.tvCustomerName
                    );

            tvCustomerId =
                    itemView.findViewById(
                            R.id.tvCustomerId
                    );

            tvBalance =
                    itemView.findViewById(
                            R.id.tvBalance
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );

            tvViewDetails =
                    itemView.findViewById(
                            R.id.tvViewDetails
                    );
        }
    }
}