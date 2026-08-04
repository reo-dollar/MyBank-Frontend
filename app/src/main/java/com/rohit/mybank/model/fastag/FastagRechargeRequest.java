package com.rohit.mybank.model.fastag;

import java.math.BigDecimal;

public class FastagRechargeRequest {

    private String vehicleNumber;
    private String provider;
    private BigDecimal amount;

    public FastagRechargeRequest() {
    }

    public FastagRechargeRequest(String vehicleNumber,
                                 String provider,
                                 BigDecimal amount) {
        this.vehicleNumber = vehicleNumber;
        this.provider = provider;
        this.amount = amount;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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