package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositRequest;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositResponse;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
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
    // RD CALCULATOR
    // ===========================================

    public Call<RDCalculatorResponse> calculateRecurringDeposit(
            RDCalculatorRequest request) {

        return apiService.calculateRecurringDeposit(request);
    }

    // ===========================================
    // OPEN / CREATE RD
    // ===========================================

    public Call<CreateRecurringDepositResponse> createRecurringDeposit(
            CreateRecurringDepositRequest request) {

        return apiService.createRecurringDeposit(request);
    }

    // ===========================================
    // MY RD LIST
    // ===========================================

    public Call<List<RDResponse>> getMyRecurringDeposits() {

        return apiService.getMyRecurringDeposits();
    }

    // ===========================================
    // RD DETAILS
    // ===========================================

    public Call<RDResponse> getRecurringDepositDetails(
            String rdNumber) {

        return apiService.getRecurringDepositDetails(
                rdNumber
        );
    }

    // ===========================================
    // MATURED RD LIST
    // ===========================================

    public Call<List<RDResponse>> getMaturedRecurringDeposits() {

        return apiService.getMaturedRecurringDeposits();
    }

    // ===========================================
    // RD HISTORY
    // ===========================================

    public Call<List<RDHistoryResponse>> getRecurringDepositHistory(
            String rdNumber) {

        return apiService.getRecurringDepositHistory(
                rdNumber
        );
    }

    // ===========================================
    // PAY RD INSTALLMENT
    // ===========================================

    public Call<RDResponse> payRecurringDepositInstallment(
            PayRecurringDepositInstallmentRequest request) {

        return apiService.payRecurringDepositInstallment(
                request
        );
    }

    // ===========================================
    // CLOSE MATURED RD
    // ===========================================

    public Call<RDResponse> closeRecurringDeposit(
            String rdNumber) {

        return apiService.closeRecurringDeposit(
                rdNumber
        );
    }

    // ===========================================
    // PREMATURE CLOSE RD
    // ===========================================

    public Call<RDResponse> prematureCloseRecurringDeposit(
            String rdNumber) {

        return apiService.prematureCloseRecurringDeposit(
                rdNumber
        );
    }
}