package com.rohit.mybank.model.water;

import java.math.BigDecimal;

public class WaterBillRequest {

    private String consumerNumber;
    private String board;
    private String state;
    private BigDecimal amount;

    public WaterBillRequest() {
    }

    public WaterBillRequest(String consumerNumber,
                            String board,
                            String state,
                            BigDecimal amount) {
        this.consumerNumber = consumerNumber;
        this.board = board;
        this.state = state;
        this.amount = amount;
    }

    public String getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(String consumerNumber) {
        this.consumerNumber = consumerNumber;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}