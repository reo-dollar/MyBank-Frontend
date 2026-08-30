package com.rohit.mybank.model.loan;

public class LoanEMIScheduleResponse {

    private String loanNumber;

    private Integer emiNumber;
    private String dueDate;

    private Double openingPrincipal;
    private Double emiAmount;
    private Double interestComponent;
    private Double principalComponent;
    private Double closingPrincipal;

    private EMIStatus status;

    private String paymentDate;

    public LoanEMIScheduleResponse() {
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public Integer getEmiNumber() {
        return emiNumber;
    }

    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Double getOpeningPrincipal() {
        return openingPrincipal;
    }

    public void setOpeningPrincipal(Double openingPrincipal) {
        this.openingPrincipal = openingPrincipal;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getInterestComponent() {
        return interestComponent;
    }

    public void setInterestComponent(Double interestComponent) {
        this.interestComponent = interestComponent;
    }

    public Double getPrincipalComponent() {
        return principalComponent;
    }

    public void setPrincipalComponent(Double principalComponent) {
        this.principalComponent = principalComponent;
    }

    public Double getClosingPrincipal() {
        return closingPrincipal;
    }

    public void setClosingPrincipal(Double closingPrincipal) {
        this.closingPrincipal = closingPrincipal;
    }

    public EMIStatus getStatus() {
        return status;
    }

    public void setStatus(EMIStatus status) {
        this.status = status;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}