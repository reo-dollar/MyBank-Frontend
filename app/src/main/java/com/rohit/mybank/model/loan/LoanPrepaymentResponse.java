package com.rohit.mybank.model.loan;

public class LoanPrepaymentResponse {

    private String loanNumber;
    private String accountNumber;

    private Double prepaymentAmount;

    private Double principalBefore;
    private Double interestBefore;

    private Double outstandingPrincipal;
    private Double outstandingInterest;

    private Double accountBalanceAfterPrepayment;

    private LoanStatus status;

    private String prepaymentDate;
    private String closureDate;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoanPrepaymentResponse() {
    }


    // =========================================================
    // LOAN NUMBER
    // =========================================================

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }


    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    // =========================================================
    // PREPAYMENT AMOUNT
    // =========================================================

    public Double getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public void setPrepaymentAmount(
            Double prepaymentAmount
    ) {
        this.prepaymentAmount = prepaymentAmount;
    }


    // =========================================================
    // PRINCIPAL BEFORE
    // =========================================================

    public Double getPrincipalBefore() {
        return principalBefore;
    }

    public void setPrincipalBefore(
            Double principalBefore
    ) {
        this.principalBefore = principalBefore;
    }


    // =========================================================
    // INTEREST BEFORE
    // =========================================================

    public Double getInterestBefore() {
        return interestBefore;
    }

    public void setInterestBefore(
            Double interestBefore
    ) {
        this.interestBefore = interestBefore;
    }


    // =========================================================
    // OUTSTANDING PRINCIPAL
    // =========================================================

    public Double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(
            Double outstandingPrincipal
    ) {
        this.outstandingPrincipal = outstandingPrincipal;
    }


    // =========================================================
    // OUTSTANDING INTEREST
    // =========================================================

    public Double getOutstandingInterest() {
        return outstandingInterest;
    }

    public void setOutstandingInterest(
            Double outstandingInterest
    ) {
        this.outstandingInterest = outstandingInterest;
    }


    // =========================================================
    // ACCOUNT BALANCE AFTER PREPAYMENT
    // =========================================================

    public Double getAccountBalanceAfterPrepayment() {
        return accountBalanceAfterPrepayment;
    }

    public void setAccountBalanceAfterPrepayment(
            Double accountBalanceAfterPrepayment
    ) {
        this.accountBalanceAfterPrepayment =
                accountBalanceAfterPrepayment;
    }


    // =========================================================
    // STATUS
    // =========================================================

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(
            LoanStatus status
    ) {
        this.status = status;
    }


    // =========================================================
    // PREPAYMENT DATE
    // =========================================================

    public String getPrepaymentDate() {
        return prepaymentDate;
    }

    public void setPrepaymentDate(
            String prepaymentDate
    ) {
        this.prepaymentDate = prepaymentDate;
    }


    // =========================================================
    // CLOSURE DATE
    // =========================================================

    public String getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(
            String closureDate
    ) {
        this.closureDate = closureDate;
    }
}