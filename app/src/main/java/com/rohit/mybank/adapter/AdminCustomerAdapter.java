package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.admin.AdminCustomerResponse;

import java.util.List;

public class AdminCustomerAdapter
        extends RecyclerView.Adapter<AdminCustomerAdapter.CustomerViewHolder> {

    // =========================================================
    // CLICK LISTENER
    // =========================================================

    public interface OnCustomerClickListener {

        void onCustomerClick(
                AdminCustomerResponse customer
        );
    }

    // =========================================================
    // DATA
    // =========================================================

    private final List<AdminCustomerResponse> customers;

    private final OnCustomerClickListener listener;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminCustomerAdapter(
            List<AdminCustomerResponse> customers,
            OnCustomerClickListener listener) {

        this.customers = customers;
        this.listener = listener;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_customer,
                                parent,
                                false
                        );

        return new CustomerViewHolder(view);
    }

    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull CustomerViewHolder holder,
            int position) {

        AdminCustomerResponse customer =
                customers.get(position);

        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        holder.tvFullName.setText(
                safe(customer.getFullName())
        );

        // -----------------------------------------------------
        // USERNAME
        // -----------------------------------------------------

        holder.tvUsername.setText(
                "@" + safe(customer.getUsername())
        );

        // -----------------------------------------------------
        // CUSTOMER ID
        // -----------------------------------------------------

        holder.tvCustomerId.setText(
                "Customer ID: "
                        + safe(customer.getCustomerId())
        );

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        holder.tvEmail.setText(
                safe(customer.getEmail())
        );

        // -----------------------------------------------------
        // MOBILE
        // -----------------------------------------------------

        holder.tvMobile.setText(
                safe(customer.getMobile())
        );

        // -----------------------------------------------------
        // ROLE
        // -----------------------------------------------------

        holder.tvRole.setText(
                "Role: "
                        + safe(customer.getUserRole())
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        boolean enabled =
                Boolean.TRUE.equals(
                        customer.getUserEnabled()
                );

        boolean locked =
                Boolean.TRUE.equals(
                        customer.getAccountLocked()
                );

        if (locked) {

            holder.tvStatus.setText(
                    "● LOCKED"
            );

        } else if (enabled) {

            holder.tvStatus.setText(
                    "● ACTIVE"
            );

        } else {

            holder.tvStatus.setText(
                    "● DISABLED"
            );
        }

        // =====================================================
        // VIEW DETAILS CLICK
        // =====================================================

        holder.tvViewDetails.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onCustomerClick(
                                customer
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

        return customers.size();
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

    static class CustomerViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvFullName;
        TextView tvUsername;
        TextView tvCustomerId;
        TextView tvEmail;
        TextView tvMobile;
        TextView tvRole;
        TextView tvStatus;
        TextView tvViewDetails;

        CustomerViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvFullName =
                    itemView.findViewById(
                            R.id.tvCustomerFullName
                    );

            tvUsername =
                    itemView.findViewById(
                            R.id.tvCustomerUsername
                    );

            tvCustomerId =
                    itemView.findViewById(
                            R.id.tvCustomerId
                    );

            tvEmail =
                    itemView.findViewById(
                            R.id.tvCustomerEmail
                    );

            tvMobile =
                    itemView.findViewById(
                            R.id.tvCustomerMobile
                    );

            tvRole =
                    itemView.findViewById(
                            R.id.tvCustomerRole
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tvCustomerStatus
                    );

            tvViewDetails =
                    itemView.findViewById(
                            R.id.tvCustomerViewDetails
                    );
        }
    }
}