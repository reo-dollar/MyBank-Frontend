package com.rohit.mybank.model.broadband;

public class BroadbandRechargeResponse {

    private boolean success;
    private String message;
    private String paymentId;

    public BroadbandRechargeResponse() {
    }

    public BroadbandRechargeResponse(boolean success,
                                     String message,
                                     String paymentId) {
        this.success = success;
        this.message = message;
        this.paymentId = paymentId;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}