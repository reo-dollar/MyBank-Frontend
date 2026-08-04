package com.rohit.mybank.model.water;

public class WaterBillResponse {

    private boolean success;
    private String message;
    private String paymentId;

    public WaterBillResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}