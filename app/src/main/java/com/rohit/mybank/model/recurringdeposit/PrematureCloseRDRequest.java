package com.rohit.mybank.model.recurringdeposit;

public class PrematureCloseRDRequest {

    private String rdNumber;

    public PrematureCloseRDRequest() {
    }

    public PrematureCloseRDRequest(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    public String getRdNumber() {
        return rdNumber;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

}