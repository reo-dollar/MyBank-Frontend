package com.rohit.mybank.model.admin;

/**
 * Response model for the Admin Dashboard API.
 *
 * Backend endpoint:
 *
 * GET /admin/dashboard
 *
 * This class maps the JSON response returned by
 * the Spring Boot backend.
 */
public class AdminDashboardResponse {

    // ==========================================================
    // USER / CUSTOMER / ACCOUNT COUNTS
    // ==========================================================

    private long totalUsers;

    private long totalCustomers;

    private long totalAccounts;


    // ==========================================================
    // TRANSACTION COUNTS
    // ==========================================================

    private long totalTransactions;

    private long totalDeposits;

    private long totalWithdrawals;

    private long totalTransfers;

    private long totalPayments;


    // ==========================================================
    // TRANSACTION AMOUNTS
    // ==========================================================

    private double totalDepositAmount;

    private double totalWithdrawalAmount;

    private double totalTransferAmount;

    private double totalPaymentAmount;


    // ==========================================================
    // DEFAULT CONSTRUCTOR
    //
    // Required by Gson when Retrofit converts JSON into
    // this Java object.
    // ==========================================================

    public AdminDashboardResponse() {
    }


    // ==========================================================
    // GETTERS
    // ==========================================================

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public long getTotalDeposits() {
        return totalDeposits;
    }

    public long getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public long getTotalTransfers() {
        return totalTransfers;
    }

    public long getTotalPayments() {
        return totalPayments;
    }

    public double getTotalDepositAmount() {
        return totalDepositAmount;
    }

    public double getTotalWithdrawalAmount() {
        return totalWithdrawalAmount;
    }

    public double getTotalTransferAmount() {
        return totalTransferAmount;
    }

    public double getTotalPaymentAmount() {
        return totalPaymentAmount;
    }


    // ==========================================================
    // SETTERS
    // ==========================================================

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public void setTotalDeposits(long totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public void setTotalWithdrawals(long totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public void setTotalTransfers(long totalTransfers) {
        this.totalTransfers = totalTransfers;
    }

    public void setTotalPayments(long totalPayments) {
        this.totalPayments = totalPayments;
    }

    public void setTotalDepositAmount(double totalDepositAmount) {
        this.totalDepositAmount = totalDepositAmount;
    }

    public void setTotalWithdrawalAmount(double totalWithdrawalAmount) {
        this.totalWithdrawalAmount = totalWithdrawalAmount;
    }

    public void setTotalTransferAmount(double totalTransferAmount) {
        this.totalTransferAmount = totalTransferAmount;
    }

    public void setTotalPaymentAmount(double totalPaymentAmount) {
        this.totalPaymentAmount = totalPaymentAmount;
    }
}