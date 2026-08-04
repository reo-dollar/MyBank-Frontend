package com.rohit.mybank.model.dth;

import java.math.BigDecimal;

public class DthRechargeRequest {

    private String subscriberId;
    private String operator;
    private BigDecimal amount;

    public DthRechargeRequest() {
    }

    public DthRechargeRequest(String subscriberId,
                              String operator,
                              BigDecimal amount) {
        this.subscriberId = subscriberId;
        this.operator = operator;
        this.amount = amount;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}