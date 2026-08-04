package com.rohit.mybank.model.withdraw;

public class WithdrawResponse {

    private String accNo;
    private String accountType;
    private String branchName;
    private String ifscCode;
    private Double balance;
    private String status;

    public WithdrawResponse() {
    }

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