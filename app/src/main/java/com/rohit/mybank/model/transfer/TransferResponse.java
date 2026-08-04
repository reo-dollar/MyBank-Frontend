package com.rohit.mybank.model.transfer;

import com.rohit.mybank.model.dashboard.DashboardResponse;

public class TransferResponse {

    private String message;
    private DashboardResponse fromAccount;
    private DashboardResponse toAccount;

    public TransferResponse() {
    }

    public String getMessage() {
        return message;
    }

    public DashboardResponse getFromAccount() {
        return fromAccount;
    }

    public DashboardResponse getToAccount() {
        return toAccount;
    }
}