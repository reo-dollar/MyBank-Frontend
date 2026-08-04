package com.rohit.mybank.model.deposit;

public class DepositRequest {

    private String accNo;
    private Double amount;

    public DepositRequest() {
    }

    public DepositRequest(String accNo, Double amount) {
        this.accNo = accNo;
        this.amount = amount;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}