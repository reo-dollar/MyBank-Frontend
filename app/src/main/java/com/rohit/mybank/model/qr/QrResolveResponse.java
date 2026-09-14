package com.rohit.mybank.model.qr;

public class QrResolveResponse {

    private boolean success;
    private String paymentId;
    private String customerName;
    private String maskedAccountNumber;

    public QrResolveResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setMaskedAccountNumber(String maskedAccountNumber) {
        this.maskedAccountNumber = maskedAccountNumber;
    }
}