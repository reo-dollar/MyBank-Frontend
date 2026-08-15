package com.rohit.mybank.model.admin;

public class AdminTransactionResponse {

    // =====================================================
    // FIELDS
    // =====================================================

    private Long id;

    private String type;

    private String fromAcc;

    private String toAcc;

    private double amount;

    private String remarks;

    private String timestamp;


    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public AdminTransactionResponse() {
    }


    // =====================================================
    // GETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getFromAcc() {
        return fromAcc;
    }

    public String getToAcc() {
        return toAcc;
    }

    public double getAmount() {
        return amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTimestamp() {
        return timestamp;
    }


    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFromAcc(String fromAcc) {
        this.fromAcc = fromAcc;
    }

    public void setToAcc(String toAcc) {
        this.toAcc = toAcc;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}