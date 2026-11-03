package com.rohit.mybank.model.fixeddeposit;

import java.math.BigDecimal;

public class MyFixedDepositResponse {

    private String fdNumber;
    private String accountNumber;

    private BigDecimal principal;
    private BigDecimal interestRate;
    private BigDecimal interestEarned;
    private BigDecimal maturityAmount;

    private Integer tenure;
    private String tenureType;

    private String interestType;
    private String compoundingFrequency;

    private String createdDate;
    private String maturityDate;

    private String status;

    public MyFixedDepositResponse() {
    }

    public String getFdNumber() {
        return fdNumber;
    }

    public void setFdNumber(String fdNumber) {
        this.fdNumber = fdNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(BigDecimal interestEarned) {
        this.interestEarned = interestEarned;
    }

    public BigDecimal getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}