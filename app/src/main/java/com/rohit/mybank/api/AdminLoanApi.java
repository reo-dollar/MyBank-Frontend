package com.rohit.mybank.api;

import com.rohit.mybank.model.admin.AdminLoanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminLoanApi {

    // =========================================================
    // GET ALL LOANS
    // =========================================================

    @GET("admin/loans")
    Call<List<AdminLoanResponse>> getAllLoans();


    // =========================================================
    // GET PENDING LOANS
    // =========================================================

    @GET("admin/loans/pending")
    Call<List<AdminLoanResponse>> getPendingLoans();


    // =========================================================
    // GET LOAN DETAILS
    // =========================================================

    @GET("admin/loans/{loanNumber}")
    Call<AdminLoanResponse> getLoan(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // APPROVE
    // =========================================================

    @PUT("admin/loans/{loanNumber}/approve")
    Call<AdminLoanResponse> approveLoan(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // REJECT
    // =========================================================

    @PUT("admin/loans/{loanNumber}/reject")
    Call<AdminLoanResponse> rejectLoan(
            @Path("loanNumber") String loanNumber
    );


    // =========================================================
    // DISBURSE
    // =========================================================

    @PUT("admin/loans/{loanNumber}/disburse")
    Call<AdminLoanResponse> disburseLoan(
            @Path("loanNumber") String loanNumber
    );
}