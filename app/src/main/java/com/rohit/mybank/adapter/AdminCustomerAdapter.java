package com.rohit.mybank.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        // =====================================================
        // THEME COLORS
        // =====================================================

        int primaryText =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_text_primary
                );

        int secondaryText =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_text_secondary
                );

        int mutedText =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_text_muted
                );

        int primaryColor =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_primary
                );

        int successColor =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_success
                );

        int errorColor =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_error
                );

        int warningColor =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_warning
                );

        int cardColor =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_card
                );

        int dividerColor =
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.admin_divider
                );


        // =====================================================
        // CARD
        // =====================================================

        holder.itemView.setBackground(
                createCardBackground(
                        cardColor,
                        dividerColor
                )
        );


        // =====================================================
        // FULL NAME
        // =====================================================

        holder.tvFullName.setText(
                safe(customer.getFullName())
        );

        holder.tvFullName.setTextColor(
                primaryText
        );

        holder.tvFullName.setTextSize(
                17
        );

        holder.tvFullName.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        // =====================================================
        // USERNAME
        // =====================================================

        holder.tvUsername.setText(
                "@" + safe(customer.getUsername())
        );

        holder.tvUsername.setTextColor(
                primaryColor
        );

        holder.tvUsername.setTextSize(
                14
        );


        // =====================================================
        // CUSTOMER ID
        // =====================================================

        holder.tvCustomerId.setText(
                "Customer ID: "
                        + safe(customer.getCustomerId())
        );

        holder.tvCustomerId.setTextColor(
                secondaryText
        );


        // =====================================================
        // EMAIL
        // =====================================================

        holder.tvEmail.setText(
                safe(customer.getEmail())
        );

        holder.tvEmail.setTextColor(
                secondaryText
        );


        // =====================================================
        // MOBILE
        // =====================================================

        holder.tvMobile.setText(
                safe(customer.getMobile())
        );

        holder.tvMobile.setTextColor(
                secondaryText
        );


        // =====================================================
        // ROLE
        // =====================================================

        holder.tvRole.setText(
                "Role: "
                        + safe(customer.getUserRole())
        );

        holder.tvRole.setTextColor(
                secondaryText
        );


        // =====================================================
        // STATUS
        // =====================================================

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

            holder.tvStatus.setTextColor(
                    warningColor
            );

        } else if (enabled) {

            holder.tvStatus.setText(
                    "● ACTIVE"
            );

            holder.tvStatus.setTextColor(
                    successColor
            );

        } else {

            holder.tvStatus.setText(
                    "● DISABLED"
            );

            holder.tvStatus.setTextColor(
                    errorColor
            );
        }


        holder.tvStatus.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        // =====================================================
        // VIEW DETAILS
        // =====================================================

        holder.tvViewDetails.setText(
                "Tap to view details →"
        );

        holder.tvViewDetails.setTextColor(
                primaryColor
        );

        holder.tvViewDetails.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        holder.tvViewDetails.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onCustomerClick(
                                customer
                        );
                    }
                }
        );


        // =====================================================
        // WHOLE CARD CLICK
        // =====================================================

        holder.itemView.setOnClickListener(
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

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }

    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private GradientDrawable createCardBackground(
            int cardColor,
            int dividerColor) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                cardColor
        );

        drawable.setCornerRadius(
                dp(
                        16,
                        null
                )
        );

        drawable.setStroke(
                dp(
                        1,
                        null
                ),
                dividerColor
        );

        return drawable;
    }

    // =========================================================
    // DP HELPER
    // =========================================================

    private int dp(
            int value,
            View view) {

        /*
         * This overload is retained for clarity,
         * but card dimensions are handled below using
         * the View's actual display metrics.
         */

        if (view == null) {

            return value;
        }

        return Math.round(
                value
                        * view.getResources()
                        .getDisplayMetrics()
                        .density
        );
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