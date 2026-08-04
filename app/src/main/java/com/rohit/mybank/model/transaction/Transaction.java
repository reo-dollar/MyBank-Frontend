package com.rohit.mybank.model.transaction;

public class Transaction {

    private String type;
    private double amount;
    private String account;
    private String direction;
    private String timestamp;

    public Transaction() {
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getAccount() {
        return account;
    }

    public String getDirection() {
        return direction;
    }

    public String getTimestamp() {
        return timestamp;
    }
}