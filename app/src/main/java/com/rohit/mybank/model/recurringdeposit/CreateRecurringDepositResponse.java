package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class CreateRecurringDepositResponse {

    private boolean success;
    private String message;
    private String rdNumber;
    private String accountNumber;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalDeposit;
    private BigDecimal maturityAmount;
    private String maturityDate;
    private String status;

    public CreateRecurringDepositResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public String getRdNumber() {
        return rdNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public BigDecimal getTotalDeposit() {
        return totalDeposit;
    }

    public BigDecimal getMaturityAmount() {
        return maturityAmount;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public String getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public void setTotalDeposit(BigDecimal totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}