package com.rohit.mybank.model.admin;

import java.util.List;

public class AdminTransactionPageResponse {

    private List<AdminTransactionResponse> content;

    private int number;
    private int size;
    private int totalPages;
    private int totalElements;
    private boolean first;
    private boolean last;
    private boolean empty;
    private int numberOfElements;

    public List<AdminTransactionResponse> getContent() {
        return content;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setContent(List<AdminTransactionResponse> content) {
        this.content = content;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}