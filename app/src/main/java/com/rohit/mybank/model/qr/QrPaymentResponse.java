package com.rohit.mybank.model.qr;


/**
 * =========================================================
 * QR PAYMENT RESPONSE
 * =========================================================
 *
 * Returned by:
 *
 * POST /api/qr/pay
 *
 * Example response:
 *
 * {
 *     "success": true,
 *     "paymentId": "PAY_8F02CCC15AF39D4F5986C89417E877CA3183",
 *     "amount": 500.0,
 *     "recipientName": "Rohit Anil Vishwakarma",
 *     "maskedRecipientAccount": "XXXXXXXX0003"
 * }
 *
 * =========================================================
 */
public class QrPaymentResponse {


    // =========================================================
    // FIELDS
    // =========================================================

    private boolean success;

    private String paymentId;

    private double amount;

    private String recipientName;

    private String maskedRecipientAccount;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public QrPaymentResponse() {
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


    public double getAmount() {

        return amount;
    }


    public String getRecipientName() {

        return recipientName;
    }


    public String getMaskedRecipientAccount() {

        return maskedRecipientAccount;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setSuccess(
            boolean success
    ) {

        this.success =
                success;
    }


    public void setPaymentId(
            String paymentId
    ) {

        this.paymentId =
                paymentId;
    }


    public void setAmount(
            double amount
    ) {

        this.amount =
                amount;
    }


    public void setRecipientName(
            String recipientName
    ) {

        this.recipientName =
                recipientName;
    }


    public void setMaskedRecipientAccount(
            String maskedRecipientAccount
    ) {

        this.maskedRecipientAccount =
                maskedRecipientAccount;
    }
}