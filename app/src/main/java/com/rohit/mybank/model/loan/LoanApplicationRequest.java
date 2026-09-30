package com.rohit.mybank.model.loan;

public class LoanApplicationRequest {

    private LoanType loanType;
    private Double loanAmount;
    private Integer tenureMonths;
    private String purpose;

    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================

    public LoanApplicationRequest() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoanApplicationRequest(
            LoanType loanType,
            Double loanAmount,
            Integer tenureMonths,
            String purpose
    ) {
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.tenureMonths = tenureMonths;
        this.purpose = purpose;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public LoanType getLoanType() {
        return loanType;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}