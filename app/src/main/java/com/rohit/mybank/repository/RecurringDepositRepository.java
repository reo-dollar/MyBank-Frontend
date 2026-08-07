package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositRequest;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositResponse;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentResponse;
import com.rohit.mybank.model.recurringdeposit.PrematureCloseRDRequest;
import com.rohit.mybank.model.recurringdeposit.PrematureCloseRDResponse;
import com.rohit.mybank.model.recurringdeposit.RDCalculatorRequest;
import com.rohit.mybank.model.recurringdeposit.RDCalculatorResponse;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;
import com.rohit.mybank.model.recurringdeposit.RDResponse;

import java.util.List;

import retrofit2.Call;

public class RecurringDepositRepository {

    private final ApiService apiService;

    public RecurringDepositRepository(Context context) {

        apiService = RetrofitClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ===========================================
    // RD Calculator
    // ===========================================

    public Call<RDCalculatorResponse> calculateRecurringDeposit(
            RDCalculatorRequest request) {

        return apiService.calculateRecurringDeposit(request);
    }

    // ===========================================
    // Open RD
    // ===========================================

    public Call<CreateRecurringDepositResponse> createRecurringDeposit(
            CreateRecurringDepositRequest request) {

        return apiService.createRecurringDeposit(request);
    }

    // ===========================================
    // My RD List
    // ===========================================

    public Call<List<RDResponse>> getMyRecurringDeposits() {

        return apiService.getMyRecurringDeposits();
    }

    // ===========================================
    // RD Details
    // ===========================================

    public Call<RDResponse> getRecurringDepositDetails(
            String rdNumber) {

        return apiService.getRecurringDepositDetails(rdNumber);
    }

    // ===========================================
    // Matured RD List
    // ===========================================

    public Call<List<RDResponse>> getMaturedRecurringDeposits() {

        return apiService.getMaturedRecurringDeposits();
    }

    // ===========================================
    // RD History
    // ===========================================

    public Call<List<RDHistoryResponse>> getRecurringDepositHistory(
            String rdNumber) {

        return apiService.getRecurringDepositHistory(rdNumber);
    }

    // ===========================================
    // Pay RD Installment
    // ===========================================

    public Call<PayRecurringDepositInstallmentResponse> payRecurringDepositInstallment(
            PayRecurringDepositInstallmentRequest request) {

        return apiService.payRecurringDepositInstallment(request);
    }

    // ===========================================
    // Premature Close RD
    // ===========================================

    public Call<PrematureCloseRDResponse> prematureCloseRecurringDeposit(
            PrematureCloseRDRequest request) {

        return apiService.prematureCloseRecurringDeposit(request);
    }
}