package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class PrematureCloseRDResponse {

    private String message;
    private String rdNumber;
    private BigDecimal settlementAmount;
    private String status;

    public PrematureCloseRDResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getRdNumber() {
        return rdNumber;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}