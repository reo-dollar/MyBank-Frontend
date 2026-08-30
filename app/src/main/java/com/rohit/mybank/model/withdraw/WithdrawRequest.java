package com.rohit.mybank.model.withdraw;

public class WithdrawRequest {

    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    private String accNo;


    // =========================================================
    // AMOUNT
    // =========================================================

    private double amount;


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    private String transactionPin;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public WithdrawRequest() {
    }


    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }


    // =========================================================
    // AMOUNT
    // =========================================================

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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