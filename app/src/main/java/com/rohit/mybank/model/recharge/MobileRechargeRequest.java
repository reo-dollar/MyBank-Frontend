package com.rohit.mybank.model.recharge;

public class MobileRechargeRequest {

    private String mobileNumber;
    private String operator;
    private String circle;
    private double amount;

    // Default Constructor
    public MobileRechargeRequest() {
    }

    // Parameterized Constructor
    public MobileRechargeRequest(String mobileNumber,
                                 String operator,
                                 String circle,
                                 double amount) {

        this.mobileNumber = mobileNumber;
        this.operator = operator;
        this.circle = circle;
        this.amount = amount;

    }

    // Getters

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getOperator() {
        return operator;
    }

    public String getCircle() {
        return circle;
    }

    public double getAmount() {
        return amount;
    }

    // Setters

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}