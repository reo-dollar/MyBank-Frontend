package com.rohit.mybank.model.broadband;

import java.math.BigDecimal;

public class BroadbandRechargeRequest {

    private String customerId;
    private String provider;
    private BigDecimal amount;

    public BroadbandRechargeRequest() {
    }

    public BroadbandRechargeRequest(String customerId,
                                    String provider,
                                    BigDecimal amount) {
        this.customerId = customerId;
        this.provider = provider;
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}