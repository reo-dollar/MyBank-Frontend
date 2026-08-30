package com.rohit.mybank.model.recurringdeposit;

public class PayRecurringDepositInstallmentRequest {

    // =========================================================
    // RD NUMBER
    // =========================================================

    private String rdNumber;


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    private String transactionPin;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public PayRecurringDepositInstallmentRequest() {
    }


    // =========================================================
    // RD NUMBER
    // =========================================================

    public String getRdNumber() {
        return rdNumber;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    public String getTransactionPin() {
        return transactionPin;
    }

    public void setTransactionPin(String transactionPin) {
        this.transactionPin = transactionPin;
    }
}