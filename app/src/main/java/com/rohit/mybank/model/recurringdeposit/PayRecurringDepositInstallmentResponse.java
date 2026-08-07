package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class PayRecurringDepositInstallmentResponse {

    private String message;
    private String rdNumber;
    private BigDecimal paidAmount;
    private Integer paidInstallments;
    private Integer remainingInstallments;
    private BigDecimal balance;

    public PayRecurringDepositInstallmentResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getRdNumber() {
        return rdNumber;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public Integer getPaidInstallments() {
        return paidInstallments;
    }

    public Integer getRemainingInstallments() {
        return remainingInstallments;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setPaidInstallments(Integer paidInstallments) {
        this.paidInstallments = paidInstallments;
    }

    public void setRemainingInstallments(Integer remainingInstallments) {
        this.remainingInstallments = remainingInstallments;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}