package com.rohit.mybank.model.pin;

public class SetPinRequest {

    private String pin;

    public SetPinRequest() {
    }

    public SetPinRequest(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}