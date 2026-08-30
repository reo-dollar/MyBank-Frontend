package com.rohit.mybank.model.loan;

public class LoanDashboardResponse {

    private String loanNumber;
    private String accountNumber;
    private LoanType loanType;

    private Double principalAmount;
    private Double interestRate;
    private Integer tenureMonths;

    private Double emiAmount;
    private Double totalInterest;
    private Double totalPayable;

    private Double outstandingPrincipal;
    private Double outstandingInterest;

    private long totalEMIs;
    private long paidEMIs;
    private long pendingEMIs;

    private Integer nextEMINumber;
    private String nextEMIDate;
    private Double nextEMIAmount;

    private LoanStatus status;

    private String purpose;

    private String applicationDate;
    private String approvalDate;
    private String disbursementDate;
    private String closureDate;

    public LoanDashboardResponse() {
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

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(Double totalPayable) {
        this.totalPayable = totalPayable;
    }

    public Double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(Double outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public Double getOutstandingInterest() {
        return outstandingInterest;
    }

    public void setOutstandingInterest(Double outstandingInterest) {
        this.outstandingInterest = outstandingInterest;
    }

    public long getTotalEMIs() {
        return totalEMIs;
    }

    public void setTotalEMIs(long totalEMIs) {
        this.totalEMIs = totalEMIs;
    }

    public long getPaidEMIs() {
        return paidEMIs;
    }

    public void setPaidEMIs(long paidEMIs) {
        this.paidEMIs = paidEMIs;
    }

    public long getPendingEMIs() {
        return pendingEMIs;
    }

    public void setPendingEMIs(long pendingEMIs) {
        this.pendingEMIs = pendingEMIs;
    }

    public Integer getNextEMINumber() {
        return nextEMINumber;
    }

    public void setNextEMINumber(Integer nextEMINumber) {
        this.nextEMINumber = nextEMINumber;
    }

    public String getNextEMIDate() {
        return nextEMIDate;
    }

    public void setNextEMIDate(String nextEMIDate) {
        this.nextEMIDate = nextEMIDate;
    }

    public Double getNextEMIAmount() {
        return nextEMIAmount;
    }

    public void setNextEMIAmount(Double nextEMIAmount) {
        this.nextEMIAmount = nextEMIAmount;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(String disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public String getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(String closureDate) {
        this.closureDate = closureDate;
    }
}