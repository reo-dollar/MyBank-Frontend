package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class PayRecurringDepositInstallmentRequest {

    private String rdNumber;

    private BigDecimal amount;

    public PayRecurringDepositInstallmentRequest() {
    }

    public PayRecurringDepositInstallmentRequest(
            String rdNumber,
            BigDecimal amount) {

        this.rdNumber = rdNumber;
        this.amount = amount;
    }

    public String getRdNumber() {
        return rdNumber;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PayRecurringDepositInstallmentRequest{" +
                "rdNumber='" + rdNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}