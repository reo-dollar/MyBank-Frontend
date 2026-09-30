package com.rohit.mybank.model.account;

public class AccountResponse {

    private String accNo;
    private String accountType;
    private String branchName;
    private String ifscCode;
    private double balance;
    private String status;

    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================

    public AccountResponse() {
    }

    // =========================================================
    // GETTERS
    // =========================================================

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

    public double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}