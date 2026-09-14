package com.rohit.mybank.model.qr;

/**
 * =========================================================
 * MY QR RESPONSE
 * =========================================================
 *
 * Backend:
 *
 * GET /api/qr/me
 *
 * The authenticated user's personal MyBank QR identity.
 *
 * =========================================================
 */
public class QrMyResponse {

    private boolean success;

    private String paymentId;

    private String qrPayload;

    private String customerName;

    private String accountNumber;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public QrMyResponse() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public boolean isSuccess() {
        return success;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getQrPayload() {
        return qrPayload;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setQrPayload(String qrPayload) {
        this.qrPayload = qrPayload;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}