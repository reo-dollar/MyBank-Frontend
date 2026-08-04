package com.rohit.mybank.model.transaction;

import java.util.List;

public class TransactionPageResponse {

    private List<Transaction> content;

    private int totalPages;
    private long totalElements;
    private boolean last;
    private int size;
    private int number;

    public TransactionPageResponse() {
    }

    public List<Transaction> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }
}