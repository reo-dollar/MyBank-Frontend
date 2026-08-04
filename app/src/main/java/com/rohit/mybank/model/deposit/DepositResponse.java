package com.rohit.mybank.model.deposit;

public class DepositResponse {

    private String accNo;
    private String accountType;
    private String branchName;
    private String ifscCode;
    private Double balance;
    private String status;

    public String getAccNo() {
        return accNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public Double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }
}