package com.rohit.mybank.model.cards;

import com.google.gson.annotations.SerializedName;

/**
 * ============================================================
 * DEBIT CARD RESPONSE
 * ============================================================
 *
 * Matches:
 *
 * com.rohit.bankapi.dto.cards.DebitCardResponse
 *
 * ============================================================
 */
public class DebitCardResponse {

    // =========================================================
    // CARD INFORMATION
    // =========================================================

    @SerializedName("cardNumber")
    private String cardNumber;

    @SerializedName("cvv")
    private String cvv;

    @SerializedName("cardHolderName")
    private String cardHolderName;

    @SerializedName("cardType")
    private String cardType;

    @SerializedName("status")
    private String status;

    @SerializedName("cardNetwork")
    private String cardNetwork;

    @SerializedName("cardVariant")
    private String cardVariant;

    @SerializedName("cardForm")
    private String cardForm;

    @SerializedName("currencyCode")
    private String currencyCode;

    // =========================================================
    // DATES
    // =========================================================

    @SerializedName("expiryDate")
    private String expiryDate;

    @SerializedName("issueDate")
    private String issueDate;

    // =========================================================
    // LIMITS
    // =========================================================

    @SerializedName("dailyLimit")
    private double dailyLimit;

    @SerializedName("spentToday")
    private double spentToday;

    @SerializedName("remainingDailyLimit")
    private double remainingDailyLimit;

    @SerializedName("atmDailyLimit")
    private double atmDailyLimit;

    @SerializedName("posDailyLimit")
    private double posDailyLimit;

    @SerializedName("onlineDailyLimit")
    private double onlineDailyLimit;

    @SerializedName("internationalDailyLimit")
    private double internationalDailyLimit;

    @SerializedName("contactlessTransactionLimit")
    private double contactlessTransactionLimit;

    // =========================================================
    // ACCOUNT
    // =========================================================

    @SerializedName("accountNumber")
    private String accountNumber;

    // =========================================================
    // CARD CONTROLS
    // =========================================================

    @SerializedName("onlineTransactionsEnabled")
    private boolean onlineTransactionsEnabled;

    @SerializedName("contactlessEnabled")
    private boolean contactlessEnabled;

    @SerializedName("internationalTransactionsEnabled")
    private boolean internationalTransactionsEnabled;

    @SerializedName("atmTransactionsEnabled")
    private boolean atmTransactionsEnabled;

    @SerializedName("posTransactionsEnabled")
    private boolean posTransactionsEnabled;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DebitCardResponse() {
    }

    // =========================================================
    // CARD NUMBER
    // =========================================================

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(
            String cardNumber
    ) {
        this.cardNumber = cardNumber;
    }

    // =========================================================
    // CVV
    // =========================================================

    public String getCvv() {
        return cvv;
    }

    public void setCvv(
            String cvv
    ) {
        this.cvv = cvv;
    }

    // =========================================================
    // CARD HOLDER
    // =========================================================

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(
            String cardHolderName
    ) {
        this.cardHolderName = cardHolderName;
    }

    // =========================================================
    // CARD TYPE
    // =========================================================

    public String getCardType() {
        return cardType;
    }

    public void setCardType(
            String cardType
    ) {
        this.cardType = cardType;
    }

    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status = status;
    }

    // =========================================================
    // CARD NETWORK
    // =========================================================

    public String getCardNetwork() {
        return cardNetwork;
    }

    public void setCardNetwork(
            String cardNetwork
    ) {
        this.cardNetwork = cardNetwork;
    }

    // =========================================================
    // CARD VARIANT
    // =========================================================

    public String getCardVariant() {
        return cardVariant;
    }

    public void setCardVariant(
            String cardVariant
    ) {
        this.cardVariant = cardVariant;
    }

    // =========================================================
    // CARD FORM
    // =========================================================

    public String getCardForm() {
        return cardForm;
    }

    public void setCardForm(
            String cardForm
    ) {
        this.cardForm = cardForm;
    }

    // =========================================================
    // CURRENCY
    // =========================================================

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(
            String currencyCode
    ) {
        this.currencyCode = currencyCode;
    }

    // =========================================================
    // EXPIRY DATE
    // =========================================================

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(
            String expiryDate
    ) {
        this.expiryDate = expiryDate;
    }

    // =========================================================
    // ISSUE DATE
    // =========================================================

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(
            String issueDate
    ) {
        this.issueDate = issueDate;
    }

    // =========================================================
    // DAILY LIMIT
    // =========================================================

    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(
            double dailyLimit
    ) {
        this.dailyLimit = dailyLimit;
    }

    // =========================================================
    // SPENT TODAY
    // =========================================================

    public double getSpentToday() {
        return spentToday;
    }

    public void setSpentToday(
            double spentToday
    ) {
        this.spentToday = spentToday;
    }

    // =========================================================
    // REMAINING DAILY LIMIT
    // =========================================================

    public double getRemainingDailyLimit() {
        return remainingDailyLimit;
    }

    public void setRemainingDailyLimit(
            double remainingDailyLimit
    ) {
        this.remainingDailyLimit =
                remainingDailyLimit;
    }

    // =========================================================
    // ATM DAILY LIMIT
    // =========================================================

    public double getAtmDailyLimit() {
        return atmDailyLimit;
    }

    public void setAtmDailyLimit(
            double atmDailyLimit
    ) {
        this.atmDailyLimit = atmDailyLimit;
    }

    // =========================================================
    // POS DAILY LIMIT
    // =========================================================

    public double getPosDailyLimit() {
        return posDailyLimit;
    }

    public void setPosDailyLimit(
            double posDailyLimit
    ) {
        this.posDailyLimit = posDailyLimit;
    }

    // =========================================================
    // ONLINE DAILY LIMIT
    // =========================================================

    public double getOnlineDailyLimit() {
        return onlineDailyLimit;
    }

    public void setOnlineDailyLimit(
            double onlineDailyLimit
    ) {
        this.onlineDailyLimit = onlineDailyLimit;
    }

    // =========================================================
    // INTERNATIONAL DAILY LIMIT
    // =========================================================

    public double getInternationalDailyLimit() {
        return internationalDailyLimit;
    }

    public void setInternationalDailyLimit(
            double internationalDailyLimit
    ) {
        this.internationalDailyLimit =
                internationalDailyLimit;
    }

    // =========================================================
    // CONTACTLESS LIMIT
    // =========================================================

    public double getContactlessTransactionLimit() {
        return contactlessTransactionLimit;
    }

    public void setContactlessTransactionLimit(
            double contactlessTransactionLimit
    ) {
        this.contactlessTransactionLimit =
                contactlessTransactionLimit;
    }

    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(
            String accountNumber
    ) {
        this.accountNumber = accountNumber;
    }

    // =========================================================
    // ONLINE TRANSACTIONS
    // =========================================================

    public boolean isOnlineTransactionsEnabled() {
        return onlineTransactionsEnabled;
    }

    public void setOnlineTransactionsEnabled(
            boolean onlineTransactionsEnabled
    ) {
        this.onlineTransactionsEnabled =
                onlineTransactionsEnabled;
    }

    // =========================================================
    // CONTACTLESS
    // =========================================================

    public boolean isContactlessEnabled() {
        return contactlessEnabled;
    }

    public void setContactlessEnabled(
            boolean contactlessEnabled
    ) {
        this.contactlessEnabled =
                contactlessEnabled;
    }

    // =========================================================
    // INTERNATIONAL TRANSACTIONS
    // =========================================================

    public boolean isInternationalTransactionsEnabled() {
        return internationalTransactionsEnabled;
    }

    public void setInternationalTransactionsEnabled(
            boolean internationalTransactionsEnabled
    ) {
        this.internationalTransactionsEnabled =
                internationalTransactionsEnabled;
    }

    // =========================================================
    // ATM TRANSACTIONS
    // =========================================================

    public boolean isAtmTransactionsEnabled() {
        return atmTransactionsEnabled;
    }

    public void setAtmTransactionsEnabled(
            boolean atmTransactionsEnabled
    ) {
        this.atmTransactionsEnabled =
                atmTransactionsEnabled;
    }

    // =========================================================
    // POS TRANSACTIONS
    // =========================================================

    public boolean isPosTransactionsEnabled() {
        return posTransactionsEnabled;
    }

    public void setPosTransactionsEnabled(
            boolean posTransactionsEnabled
    ) {
        this.posTransactionsEnabled =
                posTransactionsEnabled;
    }
}