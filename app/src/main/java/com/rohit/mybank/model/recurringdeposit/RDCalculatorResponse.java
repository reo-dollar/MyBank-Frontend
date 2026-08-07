package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class RDCalculatorResponse {

    private BigDecimal totalDeposit;
    private BigDecimal estimatedInterest;
    private BigDecimal maturityAmount;

    public RDCalculatorResponse() {
    }

    public BigDecimal getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(BigDecimal totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public BigDecimal getEstimatedInterest() {
        return estimatedInterest;
    }

    public void setEstimatedInterest(BigDecimal estimatedInterest) {
        this.estimatedInterest = estimatedInterest;
    }

    public BigDecimal getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }
}