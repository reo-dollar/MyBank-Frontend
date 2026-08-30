package com.rohit.mybank.model.recurringdeposit;

import com.google.gson.annotations.SerializedName;

public class RDHistoryResponse {

    // =========================================================
    // TRANSACTION ID
    // =========================================================

    @SerializedName("transactionId")
    private Long transactionId;

    // =========================================================
    // RD NUMBER
    // =========================================================

    @SerializedName("rdNumber")
    private String rdNumber;

    // =========================================================
    // PAYMENT DATE
    // =========================================================

    @SerializedName("paymentDate")
    private String paymentDate;

    // =========================================================
    // AMOUNT
    // =========================================================

    @SerializedName("amount")
    private Double amount;

    // =========================================================
    // TRANSACTION TYPE
    // =========================================================

    @SerializedName("transactionType")
    private String transactionType;

    // =========================================================
    // PAYMENT MODE
    // =========================================================

    @SerializedName("paymentMode")
    private String paymentMode;

    // =========================================================
    // STATUS
    // =========================================================

    @SerializedName("status")
    private String status;

    // =========================================================
    // REMARKS
    // =========================================================

    @SerializedName("remarks")
    private String remarks;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RDHistoryResponse() {
    }

    // =========================================================
    // GET TRANSACTION ID
    // =========================================================

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    // =========================================================
    // GET RD NUMBER
    // =========================================================

    public String getRdNumber() {
        return rdNumber;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    // =========================================================
    // GET PAYMENT DATE
    // =========================================================

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    // =========================================================
    // GET AMOUNT
    // =========================================================

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    // =========================================================
    // GET TRANSACTION TYPE
    // =========================================================

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    // =========================================================
    // GET PAYMENT MODE
    // =========================================================

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    // =========================================================
    // GET STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =========================================================
    // GET REMARKS
    // =========================================================

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}