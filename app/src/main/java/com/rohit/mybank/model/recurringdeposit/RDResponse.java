package com.rohit.mybank.model.recurringdeposit;

import java.math.BigDecimal;

public class RDResponse {

    private String rdNumber;
    private String accountNumber;
    private String customerName;

    private BigDecimal monthlyInstallment;
    private BigDecimal interestRate;

    private Integer tenureMonths;

    private BigDecimal totalDeposit;
    private BigDecimal maturityAmount;

    private String startDate;
    private String maturityDate;
    private String nextInstallmentDate;

    private Integer paidInstallments;
    private Integer remainingInstallments;

    private Boolean autoDebit;

    private String status;

    public RDResponse() {
    }

    public String getRdNumber() {
        return rdNumber;
    }

    public void setRdNumber(String rdNumber) {
        this.rdNumber = rdNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public BigDecimal getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(BigDecimal totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public BigDecimal getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getNextInstallmentDate() {
        return nextInstallmentDate;
    }

    public void setNextInstallmentDate(String nextInstallmentDate) {
        this.nextInstallmentDate = nextInstallmentDate;
    }

    public Integer getPaidInstallments() {
        return paidInstallments;
    }

    public void setPaidInstallments(Integer paidInstallments) {
        this.paidInstallments = paidInstallments;
    }

    public Integer getRemainingInstallments() {
        return remainingInstallments;
    }

    public void setRemainingInstallments(Integer remainingInstallments) {
        this.remainingInstallments = remainingInstallments;
    }

    public Boolean getAutoDebit() {
        return autoDebit;
    }

    public void setAutoDebit(Boolean autoDebit) {
        this.autoDebit = autoDebit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}