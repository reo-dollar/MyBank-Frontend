package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class RDCalculatorRequest {

    private BigDecimal monthlyInstallment;
    private BigDecimal interestRate;
    private Integer tenureMonths;

    public RDCalculatorRequest() {
    }

    public RDCalculatorRequest(BigDecimal monthlyInstallment,
                               BigDecimal interestRate,
                               Integer tenureMonths) {
        this.monthlyInstallment = monthlyInstallment;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}