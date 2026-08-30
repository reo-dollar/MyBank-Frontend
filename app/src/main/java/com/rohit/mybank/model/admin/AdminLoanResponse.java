package com.rohit.mybank.model.admin;

public class AdminLoanResponse {

    private String loanNumber;
    private String accountNumber;
    private String loanType;

    private Double principalAmount;
    private Double interestRate;

    private Integer tenureMonths;

    private Double emiAmount;
    private Double totalInterest;
    private Double totalPayable;

    private Double outstandingPrincipal;
    private Double outstandingInterest;

    private String status;
    private String purpose;

    private String applicationDate;
    private String approvalDate;
    private String disbursementDate;
    private String closureDate;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public AdminLoanResponse() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public String getLoanNumber() {
        return loanNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getLoanType() {
        return loanType;
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public Double getTotalPayable() {
        return totalPayable;
    }

    public Double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public Double getOutstandingInterest() {
        return outstandingInterest;
    }

    public String getStatus() {
        return status;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public String getDisbursementDate() {
        return disbursementDate;
    }

    public String getClosureDate() {
        return closureDate;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public void setTotalPayable(Double totalPayable) {
        this.totalPayable = totalPayable;
    }

    public void setOutstandingPrincipal(
            Double outstandingPrincipal
    ) {
        this.outstandingPrincipal =
                outstandingPrincipal;
    }

    public void setOutstandingInterest(
            Double outstandingInterest
    ) {
        this.outstandingInterest =
                outstandingInterest;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setApplicationDate(
            String applicationDate
    ) {
        this.applicationDate =
                applicationDate;
    }

    public void setApprovalDate(
            String approvalDate
    ) {
        this.approvalDate =
                approvalDate;
    }

    public void setDisbursementDate(
            String disbursementDate
    ) {
        this.disbursementDate =
                disbursementDate;
    }

    public void setClosureDate(
            String closureDate
    ) {
        this.closureDate =
                closureDate;
    }
}