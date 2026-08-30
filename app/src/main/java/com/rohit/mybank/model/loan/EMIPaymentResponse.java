package com.rohit.mybank.model.loan;

public class EMIPaymentResponse {

    private boolean success;
    private String message;

    private String loanNumber;
    private String accountNumber;

    private Integer emiNumber;

    private Double amountPaid;
    private Double principalPaid;
    private Double interestPaid;

    private Double remainingPrincipal;
    private Double remainingInterest;
    private Double remainingAccountBalance;

    private EMIStatus emiStatus;
    private LoanStatus loanStatus;

    private String paymentDate;

    public EMIPaymentResponse() {
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

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getEmiNumber() {
        return emiNumber;
    }

    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(Double principalPaid) {
        this.principalPaid = principalPaid;
    }

    public Double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(Double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Double getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(Double remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }

    public Double getRemainingInterest() {
        return remainingInterest;
    }

    public void setRemainingInterest(Double remainingInterest) {
        this.remainingInterest = remainingInterest;
    }

    public Double getRemainingAccountBalance() {
        return remainingAccountBalance;
    }

    public void setRemainingAccountBalance(
            Double remainingAccountBalance
    ) {
        this.remainingAccountBalance = remainingAccountBalance;
    }

    public EMIStatus getEmiStatus() {
        return emiStatus;
    }

    public void setEmiStatus(EMIStatus emiStatus) {
        this.emiStatus = emiStatus;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}