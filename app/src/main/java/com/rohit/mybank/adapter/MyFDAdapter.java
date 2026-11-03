package com.rohit.mybank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.model.fixeddeposit.MyFixedDepositResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyFDAdapter
        extends RecyclerView.Adapter<MyFDAdapter.FDViewHolder> {


    //==================================================
    // FD LIST
    //==================================================

    private final List<MyFixedDepositResponse> fdList =
            new ArrayList<>();


    //==================================================
    // SET FD LIST
    //==================================================

    public void setFdList(
            List<MyFixedDepositResponse> newList
    ) {

        fdList.clear();

        if (newList != null) {

            fdList.addAll(newList);

        }

        notifyDataSetChanged();

    }


    //==================================================
    // CREATE VIEW HOLDER
    //==================================================

    @NonNull
    @Override
    public FDViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_my_fd,
                                parent,
                                false
                        );

        return new FDViewHolder(view);

    }


    //==================================================
    // BIND VIEW HOLDER
    //==================================================

    @Override
    public void onBindViewHolder(
            @NonNull FDViewHolder holder,
            int position
    ) {

        MyFixedDepositResponse fd =
                fdList.get(position);


        //==================================================
        // FD NUMBER
        //==================================================

        holder.tvFdNumber.setText(
                fd.getFdNumber() != null
                        ? fd.getFdNumber()
                        : "—"
        );


        //==================================================
        // STATUS
        //==================================================

        holder.tvStatus.setText(
                fd.getStatus() != null
                        ? fd.getStatus()
                        : "—"
        );


        //==================================================
        // PRINCIPAL
        //==================================================

        holder.tvPrincipal.setText(
                formatAmount(
                        fd.getPrincipal()
                )
        );


        //==================================================
        // INTEREST RATE
        //==================================================

        holder.tvInterestRate.setText(
                formatAmount(
                        fd.getInterestRate()
                )
                        + "% p.a."
        );


        //==================================================
        // INTEREST EARNED
        //==================================================

        holder.tvInterestEarned.setText(
                formatAmount(
                        fd.getInterestEarned()
                )
        );


        //==================================================
        // MATURITY AMOUNT
        //==================================================

        holder.tvMaturityAmount.setText(
                formatAmount(
                        fd.getMaturityAmount()
                )
        );


        //==================================================
        // TENURE
        //==================================================

        String tenure = "—";

        if (fd.getTenure() != null) {

            tenure =
                    fd.getTenure()
                            + " "
                            + (
                            fd.getTenureType() != null
                                    ? fd.getTenureType()
                                    : ""
                    );

        }

        holder.tvTenure.setText(
                tenure
        );


        //==================================================
        // CREATED DATE
        //==================================================

        holder.tvCreatedDate.setText(
                fd.getCreatedDate() != null
                        ? fd.getCreatedDate()
                        : "—"
        );


        //==================================================
        // MATURITY DATE
        //==================================================

        holder.tvMaturityDate.setText(
                fd.getMaturityDate() != null
                        ? fd.getMaturityDate()
                        : "—"
        );

    }


    //==================================================
    // ITEM COUNT
    //==================================================

    @Override
    public int getItemCount() {

        return fdList.size();

    }


    //==================================================
    // FORMAT AMOUNT
    //==================================================

    private String formatAmount(
            BigDecimal amount
    ) {

        if (amount == null) {

            return "₹0.00";

        }

        return "₹"
                + String.format(
                Locale.US,
                "%.2f",
                amount
        );

    }


    //==================================================
    // VIEW HOLDER
    //==================================================

    static class FDViewHolder
            extends RecyclerView.ViewHolder {


        TextView tvFdNumber;

        TextView tvStatus;

        TextView tvPrincipal;

        TextView tvInterestRate;

        TextView tvInterestEarned;

        TextView tvMaturityAmount;

        TextView tvTenure;

        TextView tvCreatedDate;

        TextView tvMaturityDate;


        FDViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            //==================================================
            // INITIALIZE VIEWS
            //==================================================

            tvFdNumber =
                    itemView.findViewById(
                            R.id.tvFdNumber
                    );


            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );


            tvPrincipal =
                    itemView.findViewById(
                            R.id.tvPrincipal
                    );


            tvInterestRate =
                    itemView.findViewById(
                            R.id.tvInterestRate
                    );


            tvInterestEarned =
                    itemView.findViewById(
                            R.id.tvInterestEarned
                    );


            tvMaturityAmount =
                    itemView.findViewById(
                            R.id.tvMaturityAmount
                    );


            tvTenure =
                    itemView.findViewById(
                            R.id.tvTenure
                    );


            tvCreatedDate =
                    itemView.findViewById(
                            R.id.tvCreatedDate
                    );


            tvMaturityDate =
                    itemView.findViewById(
                            R.id.tvMaturityDate
                    );

        }

    }

}