package com.rohit.mybank.model.loan;

public class LoanCalculatorRequest {

    private Double loanAmount;
    private Double interestRate;
    private Integer tenureMonths;

    public LoanCalculatorRequest() {
    }

    public LoanCalculatorRequest(
            Double loanAmount,
            Double interestRate,
            Integer tenureMonths
    ) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
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
}