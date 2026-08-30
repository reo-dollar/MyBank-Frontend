package com.rohit.mybank.model.transfer;

public class TransferRequest {

    // =========================================================
    // SENDER ACCOUNT
    // =========================================================

    private String fromAcc;


    // =========================================================
    // RECEIVER ACCOUNT
    // =========================================================

    private String toAcc;


    // =========================================================
    // TRANSFER AMOUNT
    // =========================================================

    private double amount;


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    private String transactionPin;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public TransferRequest() {
    }


    // =========================================================
    // GET SENDER ACCOUNT
    // =========================================================

    public String getFromAcc() {
        return fromAcc;
    }


    // =========================================================
    // SET SENDER ACCOUNT
    // =========================================================

    public void setFromAcc(String fromAcc) {
        this.fromAcc = fromAcc;
    }


    // =========================================================
    // GET RECEIVER ACCOUNT
    // =========================================================

    public String getToAcc() {
        return toAcc;
    }


    // =========================================================
    // SET RECEIVER ACCOUNT
    // =========================================================

    public void setToAcc(String toAcc) {
        this.toAcc = toAcc;
    }


    // =========================================================
    // GET AMOUNT
    // =========================================================

    public double getAmount() {
        return amount;
    }


    // =========================================================
    // SET AMOUNT
    // =========================================================

    public void setAmount(double amount) {
        this.amount = amount;
    }


    // =========================================================
    // GET TRANSACTION PIN
    // =========================================================

    public String getTransactionPin() {
        return transactionPin;
    }


    // =========================================================
    // SET TRANSACTION PIN
    // =========================================================

    public void setTransactionPin(String transactionPin) {
        this.transactionPin = transactionPin;
    }
}