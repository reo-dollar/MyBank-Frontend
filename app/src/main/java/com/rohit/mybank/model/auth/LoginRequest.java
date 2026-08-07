package com.rohit.mybank.model.auth;

public class LoginRequest {

    private String username;
    private String password;
    private String deviceId;

    // ===========================
    // Default Constructor
    // ===========================

    public LoginRequest() {
    }

    // ===========================
    // Constructor
    // ===========================

    public LoginRequest(String username,
                        String password) {

        this.username = username;
        this.password = password;
        this.deviceId = "ANDROID";

    }

    public LoginRequest(String username,
                        String password,
                        String deviceId) {

        this.username = username;
        this.password = password;
        this.deviceId = deviceId;

    }

    // ===========================
    // Username
    // ===========================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ===========================
    // Password
    // ===========================

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ===========================
    // Device ID
    // ===========================

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}