package com.rohit.mybank.activities.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rohit.mybank.R;
import com.rohit.mybank.api.AdminLoanApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.admin.AdminLoanResponse;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * =========================================================
 * ADMIN LOAN MANAGEMENT ACTIVITY
 * =========================================================
 *
 * Allows ADMIN to:
 *
 * 1. View ALL loans
 * 2. Approve PENDING loans
 * 3. Reject PENDING loans
 * 4. Disburse APPROVED loans
 * 5. View other loan statuses
 *
 * =========================================================
 *
 * LOAN STATE FLOW
 *
 * PENDING
 *      |
 *      +---- APPROVE ----> APPROVED
 *      |
 *      +---- REJECT -----> REJECTED
 *
 * APPROVED
 *      |
 *      +---- DISBURSE ---> DISBURSED / ACTIVE
 *
 * =========================================================
 */
public class AdminLoanManagementActivity
        extends AppCompatActivity {

    // =========================================================
    // API
    // =========================================================

    private AdminLoanApi adminLoanApi;


    // =========================================================
    // VIEWS
    // =========================================================

    private LinearLayout loanContainer;

    private ProgressBar progressBar;

    private TextView tvEmpty;


    // =========================================================
    // ACTIVITY CREATED
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_loan_management
        );

        initializeViews();

        initializeApi();

        /*
         * IMPORTANT:
         *
         * Do NOT load only pending loans.
         *
         * We need APPROVED loans as well so that
         * the admin can disburse them.
         */
        loadAllLoans();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        loanContainer =
                findViewById(
                        R.id.loanContainer
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        tvEmpty =
                findViewById(
                        R.id.tvEmpty
                );
    }


    // =========================================================
    // INITIALIZE API
    // =========================================================

    private void initializeApi() {

        Retrofit retrofit =
                RetrofitClient.getClient(
                        getApplicationContext()
                );

        adminLoanApi =
                retrofit.create(
                        AdminLoanApi.class
                );
    }


    // =========================================================
    // LOAD ALL LOANS
    // =========================================================
    //
    // IMPORTANT FIX:
    //
    // Previously:
    //
    // getPendingLoans()
    //
    // Now:
    //
    // getAllLoans()
    //
    // This allows APPROVED loans to appear.
    // =========================================================

    private void loadAllLoans() {

        showLoading();

        adminLoanApi
                .getAllLoans()
                .enqueue(
                        new Callback<List<AdminLoanResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<AdminLoanResponse>> call,
                                    Response<List<AdminLoanResponse>> response
                            ) {

                                hideLoading();

                                if (!response.isSuccessful()) {

                                    showError(
                                            "Unable to load loans.\nHTTP " +
                                                    response.code()
                                    );

                                    return;
                                }

                                List<AdminLoanResponse> loans =
                                        response.body();

                                if (loans == null ||
                                        loans.isEmpty()) {

                                    showEmpty();

                                    return;
                                }

                                showLoans(loans);
                            }


                            @Override
                            public void onFailure(
                                    Call<List<AdminLoanResponse>> call,
                                    Throwable t
                            ) {

                                hideLoading();

                                showError(
                                        "Network Error\n\n" +
                                                safe(t.getMessage())
                                );
                            }
                        }
                );
    }


    // =========================================================
    // DISPLAY ALL LOANS
    // =========================================================

    private void showLoans(
            List<AdminLoanResponse> loans
    ) {

        loanContainer.removeAllViews();

        tvEmpty.setVisibility(
                View.GONE
        );

        loanContainer.setVisibility(
                View.VISIBLE
        );

        for (AdminLoanResponse loan : loans) {

            if (loan == null) {
                continue;
            }

            addLoanCard(loan);
        }

        /*
         * In case the list contained only null values.
         */
        if (loanContainer.getChildCount() == 0) {

            showEmpty();
        }
    }


    // =========================================================
    // ADD LOAN CARD
    // =========================================================

    private void addLoanCard(
            AdminLoanResponse loan
    ) {

        View card =
                getLayoutInflater()
                        .inflate(
                                R.layout.item_admin_loan,
                                loanContainer,
                                false
                        );


        // =====================================================
        // FIND VIEWS
        // =====================================================

        TextView tvLoanNumber =
                card.findViewById(
                        R.id.tvLoanNumber
                );

        TextView tvLoanType =
                card.findViewById(
                        R.id.tvLoanType
                );

        TextView tvAccountNumber =
                card.findViewById(
                        R.id.tvAccountNumber
                );

        TextView tvAmount =
                card.findViewById(
                        R.id.tvAmount
                );

        TextView tvInterest =
                card.findViewById(
                        R.id.tvInterest
                );

        TextView tvTenure =
                card.findViewById(
                        R.id.tvTenure
                );

        TextView tvStatus =
                card.findViewById(
                        R.id.tvStatus
                );


        MaterialButton btnApprove =
                card.findViewById(
                        R.id.btnApprove
                );

        MaterialButton btnReject =
                card.findViewById(
                        R.id.btnReject
                );

        MaterialButton btnDisburse =
                card.findViewById(
                        R.id.btnDisburse
                );


        // =====================================================
        // SET LOAN DATA
        // =====================================================

        tvLoanNumber.setText(
                safe(
                        loan.getLoanNumber()
                )
        );


        tvLoanType.setText(
                "Loan Type: " +
                        safe(
                                loan.getLoanType()
                        )
        );


        tvAccountNumber.setText(
                "Account: " +
                        safe(
                                loan.getAccountNumber()
                        )
        );


        tvAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "₹ %.2f",
                        value(
                                loan.getPrincipalAmount()
                        )
                )
        );


        tvInterest.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f%%",
                        value(
                                loan.getInterestRate()
                        )
                )
        );


        tvTenure.setText(
                String.format(
                        Locale.getDefault(),
                        "%d months",
                        loan.getTenureMonths() != null
                                ? loan.getTenureMonths()
                                : 0
                )
        );


        String status =
                safe(
                        loan.getStatus()
                );


        tvStatus.setText(
                status
        );


        // =====================================================
        // HIDE ALL ACTION BUTTONS FIRST
        // =====================================================
        //
        // This is important.
        //
        // Every card starts with all buttons hidden.
        // Then the correct button(s) are enabled based
        // on the loan status.
        // =====================================================

        btnApprove.setVisibility(
                View.GONE
        );

        btnReject.setVisibility(
                View.GONE
        );

        btnDisburse.setVisibility(
                View.GONE
        );


        // =====================================================
        // PENDING
        // =====================================================

        if ("PENDING".equalsIgnoreCase(status)) {

            btnApprove.setVisibility(
                    View.VISIBLE
            );

            btnReject.setVisibility(
                    View.VISIBLE
            );


            btnApprove.setOnClickListener(
                    v -> confirmApprove(
                            loan
                    )
            );


            btnReject.setOnClickListener(
                    v -> confirmReject(
                            loan
                    )
            );
        }


        // =====================================================
        // APPROVED
        // =====================================================

        else if ("APPROVED".equalsIgnoreCase(status)) {

            /*
             * APPROVED loans must show DISBURSE.
             */
            btnDisburse.setVisibility(
                    View.VISIBLE
            );


            btnDisburse.setOnClickListener(
                    v -> confirmDisbursement(
                            loan
                    )
            );
        }


        // =====================================================
        // DISBURSED
        // =====================================================

        else if ("DISBURSED".equalsIgnoreCase(status)) {

            /*
             * Loan has already been disbursed.
             *
             * No admin action required here.
             */
        }


        // =====================================================
        // ACTIVE
        // =====================================================

        else if ("ACTIVE".equalsIgnoreCase(status)) {

            /*
             * Loan is active.
             *
             * No approval/disbursement button.
             */
        }


        // =====================================================
        // REJECTED
        // =====================================================

        else if ("REJECTED".equalsIgnoreCase(status)) {

            /*
             * Rejected loan.
             *
             * No action.
             */
        }


        // =====================================================
        // CLOSED
        // =====================================================

        else if ("CLOSED".equalsIgnoreCase(status)) {

            /*
             * Closed loan.
             *
             * No action.
             */
        }


        // =====================================================
        // ADD CARD
        // =====================================================

        loanContainer.addView(
                card
        );
    }


    // =========================================================
    // CONFIRM APPROVAL
    // =========================================================

    private void confirmApprove(
            AdminLoanResponse loan
    ) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Approve Loan?"
                )

                .setMessage(
                        "Are you sure you want to approve loan:\n\n" +
                                safe(
                                        loan.getLoanNumber()
                                )
                )

                .setNegativeButton(
                        "CANCEL",
                        null
                )

                .setPositiveButton(
                        "APPROVE",
                        (dialog, which) ->
                                approveLoan(
                                        loan.getLoanNumber()
                                )
                )

                .show();
    }


    // =========================================================
    // APPROVE LOAN
    // =========================================================

    private void approveLoan(
            String loanNumber
    ) {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            showError(
                    "Invalid loan number."
            );

            return;
        }


        showLoading();

        adminLoanApi
                .approveLoan(
                        loanNumber
                )
                .enqueue(
                        new Callback<AdminLoanResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminLoanResponse> call,
                                    Response<AdminLoanResponse> response
                            ) {

                                hideLoading();

                                if (response.isSuccessful() &&
                                        response.body() != null) {

                                    Toast.makeText(
                                            AdminLoanManagementActivity.this,
                                            "Loan approved successfully.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    /*
                                     * Reload ALL loans.
                                     *
                                     * The loan will now appear as
                                     * APPROVED and show DISBURSE.
                                     */
                                    loadAllLoans();

                                } else {

                                    showError(
                                            "Approval failed.\nHTTP " +
                                                    response.code()
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<AdminLoanResponse> call,
                                    Throwable t
                            ) {

                                hideLoading();

                                showError(
                                        "Network Error\n\n" +
                                                safe(
                                                        t.getMessage()
                                                )
                                );
                            }
                        }
                );
    }


    // =========================================================
    // CONFIRM REJECTION
    // =========================================================

    private void confirmReject(
            AdminLoanResponse loan
    ) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Reject Loan?"
                )

                .setMessage(
                        "Are you sure you want to reject loan:\n\n" +
                                safe(
                                        loan.getLoanNumber()
                                )
                )

                .setNegativeButton(
                        "CANCEL",
                        null
                )

                .setPositiveButton(
                        "REJECT",
                        (dialog, which) ->
                                rejectLoan(
                                        loan.getLoanNumber()
                                )
                )

                .show();
    }


    // =========================================================
    // REJECT LOAN
    // =========================================================

    private void rejectLoan(
            String loanNumber
    ) {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            showError(
                    "Invalid loan number."
            );

            return;
        }


        showLoading();

        adminLoanApi
                .rejectLoan(
                        loanNumber
                )
                .enqueue(
                        new Callback<AdminLoanResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminLoanResponse> call,
                                    Response<AdminLoanResponse> response
                            ) {

                                hideLoading();

                                if (response.isSuccessful() &&
                                        response.body() != null) {

                                    Toast.makeText(
                                            AdminLoanManagementActivity.this,
                                            "Loan rejected successfully.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    /*
                                     * Reload ALL loans.
                                     */
                                    loadAllLoans();

                                } else {

                                    showError(
                                            "Rejection failed.\nHTTP " +
                                                    response.code()
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<AdminLoanResponse> call,
                                    Throwable t
                            ) {

                                hideLoading();

                                showError(
                                        "Network Error\n\n" +
                                                safe(
                                                        t.getMessage()
                                                )
                                );
                            }
                        }
                );
    }


    // =========================================================
    // CONFIRM DISBURSEMENT
    // =========================================================

    private void confirmDisbursement(
            AdminLoanResponse loan
    ) {

        String loanNumber =
                safe(
                        loan.getLoanNumber()
                );


        String accountNumber =
                safe(
                        loan.getAccountNumber()
                );


        String amount =
                String.format(
                        Locale.getDefault(),
                        "₹ %.2f",
                        value(
                                loan.getPrincipalAmount()
                        )
                );


        new AlertDialog.Builder(this)

                .setTitle(
                        "Disburse Loan?"
                )

                .setMessage(
                        "Loan Number:\n" +
                                loanNumber +

                                "\n\nAccount Number:\n" +
                                accountNumber +

                                "\n\nAmount:\n" +
                                amount +

                                "\n\n" +
                                "This will credit the loan amount " +
                                "to the customer's account."
                )

                .setNegativeButton(
                        "CANCEL",
                        null
                )

                .setPositiveButton(
                        "DISBURSE",
                        (dialog, which) ->
                                disburseLoan(
                                        loanNumber
                                )
                )

                .show();
    }


    // =========================================================
    // DISBURSE LOAN
    // =========================================================

    private void disburseLoan(
            String loanNumber
    ) {

        if (loanNumber == null ||
                loanNumber.trim().isEmpty()) {

            showError(
                    "Invalid loan number."
            );

            return;
        }


        showLoading();

        adminLoanApi
                .disburseLoan(
                        loanNumber
                )
                .enqueue(
                        new Callback<AdminLoanResponse>() {

                            @Override
                            public void onResponse(
                                    Call<AdminLoanResponse> call,
                                    Response<AdminLoanResponse> response
                            ) {

                                hideLoading();

                                if (response.isSuccessful() &&
                                        response.body() != null) {

                                    AdminLoanResponse updatedLoan =
                                            response.body();


                                    Toast.makeText(
                                            AdminLoanManagementActivity.this,
                                            "Loan disbursed successfully.",
                                            Toast.LENGTH_LONG
                                    ).show();


                                    /*
                                     * Reload ALL loans.
                                     *
                                     * The approved loan should now
                                     * change to DISBURSED or ACTIVE,
                                     * depending on your backend status.
                                     */
                                    loadAllLoans();

                                } else {

                                    String message =
                                            "Disbursement failed.\nHTTP " +
                                                    response.code();


                                    showError(
                                            message
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<AdminLoanResponse> call,
                                    Throwable t
                            ) {

                                hideLoading();

                                showError(
                                        "Network Error\n\n" +
                                                safe(
                                                        t.getMessage()
                                                )
                                );
                            }
                        }
                );
    }


    // =========================================================
    // SHOW LOADING
    // =========================================================

    private void showLoading() {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.VISIBLE
            );
        }
    }


    // =========================================================
    // HIDE LOADING
    // =========================================================

    private void hideLoading() {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // SHOW EMPTY
    // =========================================================

    private void showEmpty() {

        loanContainer.removeAllViews();

        loanContainer.setVisibility(
                View.GONE
        );

        tvEmpty.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setText(
                "No loans found."
        );
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }


    // =========================================================
    // SAFE DOUBLE
    // =========================================================

    private double value(
            Double value
    ) {

        return value != null
                ? value
                : 0.0;
    }
}