package com.rohit.mybank.api;

import com.rohit.mybank.model.loan.EMIPaymentResponse;
import com.rohit.mybank.model.loan.LoanApplicationRequest;
import com.rohit.mybank.model.loan.LoanCalculatorRequest;
import com.rohit.mybank.model.loan.LoanCalculatorResponse;
import com.rohit.mybank.model.loan.LoanDashboardResponse;
import com.rohit.mybank.model.loan.LoanEMIPaymentHistoryResponse;
import com.rohit.mybank.model.loan.LoanEMIResponse;
import com.rohit.mybank.model.loan.LoanPrepaymentResponse;
import com.rohit.mybank.model.loan.LoanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LoanApi {

    // =========================================================
    // LOAN CALCULATOR
    // =========================================================

    @POST("loans/calculate")
    Call<LoanCalculatorResponse> calculateLoan(
            @Body LoanCalculatorRequest request
    );


    // =========================================================
    // APPLY FOR LOAN
    // =========================================================

    @POST("loans/apply")
    Call<LoanResponse> applyForLoan(
            @Body LoanApplicationRequest request
    );


    // =========================================================
    // GET MY LOANS
    // =========================================================

    @GET("loans/my-loans")
    Call<List<LoanResponse>> getMyLoans();


    // =========================================================
    // GET SINGLE LOAN
    // =========================================================

    @GET("loans/{loanNumber}")
    Call<LoanResponse> getLoan(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // LOAN DASHBOARD
    // =========================================================

    @GET("loans/{loanNumber}/dashboard")
    Call<LoanDashboardResponse> getLoanDashboard(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // EMI SCHEDULE
    // =========================================================
    //
    // IMPORTANT:
    //
    // Backend controller:
    //
    // GET /loans/{loanNumber}/schedule
    //
    // NOT:
    //
    // /emi-schedule
    //
    // =========================================================

    @GET("loans/{loanNumber}/schedule")
    Call<List<LoanEMIResponse>> getEMISchedule(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // EMI PAYMENT HISTORY
    // =========================================================
    //
    // Backend controller:
    //
    // GET /loans/{loanNumber}/payment-history
    //
    // =========================================================

    @GET("loans/{loanNumber}/payment-history")
    Call<List<LoanEMIPaymentHistoryResponse>> getLoanPaymentHistory(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // PAY EMI
    // =========================================================
    //
    // Keep this only if your backend has:
    //
    // POST /loans/{loanNumber}/emis/{emiNumber}/pay
    //
    // =========================================================

    @POST("loans/{loanNumber}/emis/{emiNumber}/pay")
    Call<EMIPaymentResponse> payEMI(
            @Path("loanNumber") String loanNumber,
            @Path("emiNumber") Integer emiNumber
    );


    // =========================================================
    // FULL LOAN PREPAYMENT
    // =========================================================

    @POST("loans/{loanNumber}/prepay")
    Call<LoanPrepaymentResponse> prepayLoan(
            @Path("loanNumber") String loanNumber
    );
}