package com.rohit.mybank.model.fixeddeposit;

public class CreateFixedDepositRequest {

    private double principal;
    private double interestRate;
    private int tenure;

    private String tenureType;
    private String interestType;
    private String compoundingFrequency;

    public CreateFixedDepositRequest() {
    }

    public CreateFixedDepositRequest(
            double principal,
            double interestRate,
            int tenure,
            String tenureType,
            String interestType,
            String compoundingFrequency
    ) {
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenure = tenure;
        this.tenureType = tenureType;
        this.interestType = interestType;
        this.compoundingFrequency = compoundingFrequency;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }

    public String getTenureType() {
        return tenureType;
    }

    public void setTenureType(String tenureType) {
        this.tenureType = tenureType;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getCompoundingFrequency() {
        return compoundingFrequency;
    }

    public void setCompoundingFrequency(String compoundingFrequency) {
        this.compoundingFrequency = compoundingFrequency;
    }
}