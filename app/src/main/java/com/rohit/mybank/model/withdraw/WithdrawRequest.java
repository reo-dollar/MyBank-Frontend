package com.rohit.mybank.model.withdraw;

public class WithdrawRequest {

    private String accNo;
    private double amount;

    public WithdrawRequest() {
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}