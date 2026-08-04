package com.rohit.mybank.model.insurance;

import java.math.BigDecimal;

public class InsurancePaymentRequest {

    private String policyNumber;
    private String insuranceCompany;
    private String policyType;
    private BigDecimal amount;

    public InsurancePaymentRequest() {
    }

    public InsurancePaymentRequest(String policyNumber,
                                   String insuranceCompany,
                                   String policyType,
                                   BigDecimal amount) {
        this.policyNumber = policyNumber;
        this.insuranceCompany = insuranceCompany;
        this.policyType = policyType;
        this.amount = amount;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}