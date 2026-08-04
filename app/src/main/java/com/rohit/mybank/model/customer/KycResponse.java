package com.rohit.mybank.model.customer;

public class KycResponse {

    private boolean success;

    private String customerId;
    private String accountNumber;
    private String username;
    private String customerName;
    private String message;

    public KycResponse() {
    }

    public KycResponse(boolean success,
                       String customerId,
                       String accountNumber,
                       String username,
                       String customerName,
                       String message) {

        this.success = success;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.username = username;
        this.customerName = customerName;
        this.message = message;
    }

    // ==========================
    // Getters & Setters
    // ==========================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "KycResponse{" +
                "success=" + success +
                ", customerId='" + customerId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", username='" + username + '\'' +
                ", customerName='" + customerName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}