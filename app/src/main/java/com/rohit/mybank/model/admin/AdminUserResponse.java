package com.rohit.mybank.model.admin;

import com.google.gson.annotations.SerializedName;

/**
 * =========================================================
 * ADMIN USER RESPONSE
 * =========================================================
 *
 * Represents safe user information returned to the
 * administrator.
 *
 * IMPORTANT:
 *
 * Password, transaction PIN, Aadhaar and PAN are NOT
 * included in this response.
 */
public class AdminUserResponse {

    // =====================================================
    // USER INFORMATION
    // =====================================================

    @SerializedName("username")
    private String username;

    @SerializedName("role")
    private String role;

    // =====================================================
    // USER STATUS
    // =====================================================

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("accountLocked")
    private boolean accountLocked;

    // =====================================================
    // CUSTOMER INFORMATION
    // =====================================================

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("mobile")
    private String mobile;

    // =====================================================
    // CREATED DATE
    // =====================================================

    @SerializedName("createdAt")
    private String createdAt;

    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public AdminUserResponse() {
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}