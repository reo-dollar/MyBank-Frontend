package com.rohit.mybank.model.electricity;

public class ElectricityBillRequest {

    private String consumerNumber;
    private String board;
    private String state;
    private double amount;

    public ElectricityBillRequest() {
    }

    public ElectricityBillRequest(String consumerNumber,
                                  String board,
                                  String state,
                                  double amount) {
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}