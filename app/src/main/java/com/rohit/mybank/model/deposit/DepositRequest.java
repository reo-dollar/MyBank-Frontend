package com.rohit.mybank.model.deposit;

public class DepositRequest {

    private String accNo;

    private Double amount;

    private String transactionPin;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public DepositRequest() {
    }


    // =========================================================
    // FULL CONSTRUCTOR
    // =========================================================

    public DepositRequest(
            String accNo,
            Double amount,
            String transactionPin
    ) {

        this.accNo = accNo;

        this.amount = amount;

        this.transactionPin =
                transactionPin;
    }


    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    public String getAccNo() {

        return accNo;
    }


    public void setAccNo(
            String accNo
    ) {

        this.accNo =
                accNo;
    }


    // =========================================================
    // AMOUNT
    // =========================================================

    public Double getAmount() {

        return amount;
    }


    public void setAmount(
            Double amount
    ) {

        this.amount =
                amount;
    }


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    public String getTransactionPin() {

        return transactionPin;
    }


    public void setTransactionPin(
            String transactionPin
    ) {

        this.transactionPin =
                transactionPin;
    }
}