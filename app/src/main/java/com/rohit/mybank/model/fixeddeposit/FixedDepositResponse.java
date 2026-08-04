package com.rohit.mybank.model.fixeddeposit;

public class FixedDepositResponse {

    private boolean success;

    private String message;

    private double principal;

    private double interestEarned;

    private double maturityAmount;

    private double interestRate;

    private double tenure;

    private String tenureType;

    private String interestType;

    private String compoundingFrequency;

    public FixedDepositResponse() {
    }

    public FixedDepositResponse(boolean success,
                                String message,
                                double principal,
                                double interestEarned,
                                double maturityAmount,
                                double interestRate,
                                double tenure,
                                String tenureType,
                                String interestType,
                                String compoundingFrequency) {

        this.success = success;
        this.message = message;
        this.principal = principal;
        this.interestEarned = interestEarned;
        this.maturityAmount = maturityAmount;
        this.interestRate = interestRate;
        this.tenure = tenure;
        this.tenureType = tenureType;
        this.interestType = interestType;
        this.compoundingFrequency = compoundingFrequency;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }

    public double getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(double maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getTenure() {
        return tenure;
    }

    public void setTenure(double tenure) {
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