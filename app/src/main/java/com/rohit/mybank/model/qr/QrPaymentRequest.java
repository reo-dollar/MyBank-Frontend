package com.rohit.mybank.model.qr;


/**
 * =========================================================
 * QR PAYMENT REQUEST
 * =========================================================
 *
 * Sent to:
 *
 * POST /api/qr/pay
 *
 * =========================================================
 */
public class QrPaymentRequest {

    private String paymentId;

    private double amount;

    private String transactionPin;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public QrPaymentRequest() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public String getPaymentId() {

        return paymentId;
    }


    public double getAmount() {

        return amount;
    }


    public String getTransactionPin() {

        return transactionPin;
    }


    // =========================================================
    // SETTERS
    // =========================================================

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


    public void setTransactionPin(
            String transactionPin
    ) {

        this.transactionPin =
                transactionPin;
    }
}