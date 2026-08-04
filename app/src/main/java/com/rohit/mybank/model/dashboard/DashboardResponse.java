package com.rohit.mybank.model.dashboard;

public class DashboardResponse {

    private String accNo;
    private String accountType;
    private String branchName;
    private String ifscCode;
    private double balance;
    private String status;

    public DashboardResponse() {
    }

    public DashboardResponse(String accNo,
                             String accountType,
                             String branchName,
                             String ifscCode,
                             double balance,
                             String status) {

        this.accNo = accNo;
        this.accountType = accountType;
        this.branchName = branchName;
        this.ifscCode = ifscCode;
        this.balance = balance;
        this.status = status;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}