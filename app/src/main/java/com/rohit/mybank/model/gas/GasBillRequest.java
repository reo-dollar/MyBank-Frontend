package com.rohit.mybank.model.gas;

import java.math.BigDecimal;

public class GasBillRequest {

    private String consumerNumber;
    private String provider;
    private String state;
    private BigDecimal amount;

    public GasBillRequest() {
    }

    public GasBillRequest(String consumerNumber,
                          String provider,
                          String state,
                          BigDecimal amount) {
        this.consumerNumber = consumerNumber;
        this.provider = provider;
        this.state = state;
        this.amount = amount;
    }

    public String getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(String consumerNumber) {
        this.consumerNumber = consumerNumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}