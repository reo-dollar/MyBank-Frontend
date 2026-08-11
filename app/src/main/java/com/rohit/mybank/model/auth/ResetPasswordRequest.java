package com.rohit.mybank.model.auth;

public class ResetPasswordRequest {

    private String token;
    private String newPassword;

    // Empty constructor
    public ResetPasswordRequest() {
    }

    // Parameterized constructor
    public ResetPasswordRequest(
            String token,
            String newPassword
    ) {
        this.token = token;
        this.newPassword = newPassword;
    }

    // Get token
    public String getToken() {
        return token;
    }

    // Set token
    public void setToken(String token) {
        this.token = token;
    }

    // Get new password
    public String getNewPassword() {
        return newPassword;
    }

    // Set new password
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}