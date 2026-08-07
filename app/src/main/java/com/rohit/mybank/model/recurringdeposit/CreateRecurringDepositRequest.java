package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class CreateRecurringDepositRequest {

    private String accountNumber;

    private BigDecimal monthlyInstallment;

    private BigDecimal interestRate;

    private Integer tenureMonths;

    private Boolean autoDebit;

    public CreateRecurringDepositRequest() {
    }

    public CreateRecurringDepositRequest(
            String accountNumber,
            BigDecimal monthlyInstallment,
            BigDecimal interestRate,
            Integer tenureMonths,
            Boolean autoDebit) {

        this.accountNumber = accountNumber;
        this.monthlyInstallment = monthlyInstallment;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.autoDebit = autoDebit;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Boolean getAutoDebit() {
        return autoDebit;
    }

    public void setAutoDebit(Boolean autoDebit) {
        this.autoDebit = autoDebit;
    }
}