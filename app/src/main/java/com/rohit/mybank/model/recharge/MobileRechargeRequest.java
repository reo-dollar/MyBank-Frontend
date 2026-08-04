package com.rohit.mybank.model.recharge;

public class MobileRechargeRequest {

    private String mobileNumber;
    private String operator;
    private String circle;
    private double amount;

    public MobileRechargeRequest() {
    }

    public MobileRechargeRequest(String mobileNumber,
                                 String operator,
                                 String circle,
                                 double amount) {
        this.mobileNumber = mobileNumber;
        this.operator = operator;
        this.circle = circle;
        this.amount = amount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}