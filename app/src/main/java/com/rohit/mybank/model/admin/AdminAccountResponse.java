package com.rohit.mybank.model.admin;

public class AdminAccountResponse {

    // =========================================================
    // ACCOUNT INFORMATION
    // =========================================================

    private String accNo;

    private String accountType;

    private String branchName;

    private String ifscCode;

    private double balance;

    private String status;

    // =========================================================
    // CUSTOMER INFORMATION
    // =========================================================

    private String customerId;

    private String customerName;

    private String username;

    private String mobile;

    private String email;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public AdminAccountResponse() {
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

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getUsername() {
        return username;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
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

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}