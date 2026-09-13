package com.rohit.mybank.model.profile;

public class ProfileResponse {

    // =========================================================
    // CUSTOMER DETAILS
    // =========================================================

    private String customerId;

    private String firstName;
    private String middleName;
    private String lastName;

    private String email;
    private String mobile;

    private String address;
    private String city;
    private String state;
    private String pincode;

    private String occupation;


    // =========================================================
    // PROFILE PHOTO
    // =========================================================

    /**
     * Relative API URL for the authenticated user's
     * profile photo.
     *
     * Example:
     * /profile/photo
     *
     * The actual image is NOT stored inside this response.
     */
    private String profilePhotoUrl;


    // =========================================================
    // ACCOUNT DETAILS
    // =========================================================

    private String accountNumber;
    private String accountType;
    private String branch;
    private String ifsc;
    private String kycStatus;


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    private boolean transactionPinSet;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ProfileResponse() {
    }


    // =========================================================
    // CUSTOMER
    // =========================================================

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }


    // =========================================================
    // PROFILE PHOTO
    // =========================================================

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }


    /**
     * Convenience method to determine whether the backend
     * supplied a profile-photo URL.
     */
    public boolean hasProfilePhoto() {

        return profilePhotoUrl != null
                && !profilePhotoUrl.isBlank();
    }


    // =========================================================
    // ACCOUNT
    // =========================================================

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }


    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }


    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }


    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }


    // =========================================================
    // TRANSACTION PIN
    // =========================================================

    public boolean isTransactionPinSet() {
        return transactionPinSet;
    }

    public void setTransactionPinSet(
            boolean transactionPinSet
    ) {
        this.transactionPinSet = transactionPinSet;
    }


    // =========================================================
    // HELPER - FULL NAME
    // =========================================================

    public String getFullName() {

        StringBuilder builder =
                new StringBuilder();

        if (firstName != null
                && !firstName.isBlank()) {

            builder.append(firstName.trim());
        }

        if (middleName != null
                && !middleName.isBlank()) {

            if (builder.length() > 0) {
                builder.append(" ");
            }

            builder.append(
                    middleName.trim()
            );
        }

        if (lastName != null
                && !lastName.isBlank()) {

            if (builder.length() > 0) {
                builder.append(" ");
            }

            builder.append(
                    lastName.trim()
            );
        }

        return builder
                .toString()
                .trim();
    }
}