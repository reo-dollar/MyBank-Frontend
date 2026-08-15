package com.rohit.mybank.api;

import com.rohit.mybank.model.auth.LoginRequest;
import com.rohit.mybank.model.auth.LoginResponse;
import com.rohit.mybank.model.auth.RegisterRequest;
import com.rohit.mybank.model.auth.RegisterResponse;
import com.rohit.mybank.model.customer.KycRequest;
import com.rohit.mybank.model.customer.KycResponse;
import com.rohit.mybank.model.dashboard.DashboardResponse;
import com.rohit.mybank.model.deposit.DepositRequest;
import com.rohit.mybank.model.deposit.DepositResponse;
import com.rohit.mybank.model.pin.ApiResponse;
import com.rohit.mybank.model.pin.SetPinRequest;
import com.rohit.mybank.model.pin.VerifyPinRequest;
import com.rohit.mybank.model.pin.VerifyPinResponse;
import com.rohit.mybank.model.profile.ChangePasswordRequest;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.profile.UpdateProfileRequest;
import com.rohit.mybank.model.transaction.TransactionPageResponse;
import com.rohit.mybank.model.transfer.TransferRequest;
import com.rohit.mybank.model.transfer.TransferResponse;
import com.rohit.mybank.model.withdraw.WithdrawRequest;
import com.rohit.mybank.model.withdraw.WithdrawResponse;
import com.rohit.mybank.model.recharge.MobileRechargeRequest;
import com.rohit.mybank.model.recharge.MobileRechargeResponse;
import com.rohit.mybank.model.electricity.ElectricityBillRequest;
import com.rohit.mybank.model.electricity.ElectricityBillResponse;
import com.rohit.mybank.model.water.WaterBillRequest;
import com.rohit.mybank.model.water.WaterBillResponse;
import com.rohit.mybank.model.gas.GasBillRequest;
import com.rohit.mybank.model.gas.GasBillResponse;
import com.rohit.mybank.model.dth.DthRechargeRequest;
import com.rohit.mybank.model.dth.DthRechargeResponse;
import com.rohit.mybank.model.broadband.BroadbandRechargeRequest;
import com.rohit.mybank.model.broadband.BroadbandRechargeResponse;
import com.rohit.mybank.model.fastag.FastagRechargeRequest;
import com.rohit.mybank.model.fastag.FastagRechargeResponse;
import com.rohit.mybank.model.insurance.InsurancePaymentRequest;
import com.rohit.mybank.model.insurance.InsurancePaymentResponse;
import com.rohit.mybank.model.fixeddeposit.FixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.FixedDepositResponse;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositRequest;
import com.rohit.mybank.model.fixeddeposit.CreateFixedDepositResponse;
import com.rohit.mybank.model.recurringdeposit.RDCalculatorRequest;
import com.rohit.mybank.model.recurringdeposit.RDCalculatorResponse;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositRequest;
import com.rohit.mybank.model.recurringdeposit.CreateRecurringDepositResponse;
import com.rohit.mybank.model.recurringdeposit.RDResponse;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentResponse;
import com.rohit.mybank.model.recurringdeposit.PrematureCloseRDRequest;
import com.rohit.mybank.model.recurringdeposit.PrematureCloseRDResponse;
import com.rohit.mybank.model.auth.ForgotPasswordRequest;
import com.rohit.mybank.model.auth.ResetPasswordRequest;
import com.rohit.mybank.model.admin.AdminDashboardResponse;
import com.rohit.mybank.model.admin.AdminUserResponse;
import com.rohit.mybank.model.admin.AdminCustomerResponse;
import com.rohit.mybank.model.admin.AdminAccountResponse;
import com.rohit.mybank.model.admin.AdminTransactionPageResponse;
import java.util.List;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==========================
    // Login
    // ==========================

    @POST("auth/login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    // ==========================
    // Register
    // ==========================

    @POST("auth/register")
    Call<RegisterResponse> register(
            @Body RegisterRequest request
    );

    // ==========================
// Forgot Password
// ==========================

    @POST("auth/forgot-password")
    Call<ResponseBody> forgotPassword(
            @Body ForgotPasswordRequest request
    );

    @POST("auth/reset-password")
    Call<ResponseBody> resetPassword(
            @Body ResetPasswordRequest request
    );

    // ==========================
    // KYC Registration
    // ==========================

    @POST("kyc/register")
    Call<KycResponse> registerKyc(
            @Body KycRequest request
    );

    // ==========================
    // Dashboard
    // ==========================

    @GET("accounts/me")
    Call<DashboardResponse> getMyAccount();


    @GET("admin/dashboard")
    Call<AdminDashboardResponse> getAdminDashboard();

    // ==========================================================
    // ADMIN USER MANAGEMENT
    // ==========================================================

    @GET("admin/users")
    Call<List<AdminUserResponse>> getAdminUsers();

    // ==========================================================
    // ADMIN USER DETAILS
    // ==========================================================

    @GET("admin/users/{username}")
    Call<AdminUserResponse> getAdminUser(
            @Path("username") String username
    );

    // ==========================================================
    // ENABLE USER
    // ==========================================================

    @PUT("admin/users/{username}/enable")
    Call<AdminUserResponse> enableAdminUser(
            @Path("username") String username
    );

    // ==========================================================
    // DISABLE USER
    // ==========================================================

    @PUT("admin/users/{username}/disable")
    Call<AdminUserResponse> disableAdminUser(
            @Path("username") String username
    );

    // ==========================================================
    // LOCK USER
    // ==========================================================

    @PUT("admin/users/{username}/lock")
    Call<AdminUserResponse> lockAdminUser(
            @Path("username") String username
    );

    // ==========================================================
    // UNLOCK USER
    // ==========================================================

    @PUT("admin/users/{username}/unlock")
    Call<AdminUserResponse> unlockAdminUser(
            @Path("username") String username
    );

    // ==========================================================
// ADMIN CUSTOMER MANAGEMENT
// ==========================================================

    @GET("admin/customers")
    Call<List<AdminCustomerResponse>> getAdminCustomers();

    @GET("admin/customers/search")
    Call<List<AdminCustomerResponse>> searchAdminCustomers(
            @Query("query") String query
    );

    @GET("admin/customers/{customerId}")
    Call<AdminCustomerResponse> getAdminCustomer(
            @Path("customerId") String customerId
    );

    // =========================================================
// ADMIN ACCOUNT MANAGEMENT
// =========================================================

    @GET("admin/accounts")
    Call<List<AdminAccountResponse>> getAdminAccounts();

    @GET("admin/accounts/search")
    Call<List<AdminAccountResponse>> searchAdminAccounts(
            @Query("query") String query
    );

    @GET("admin/accounts/{accNo}")
    Call<AdminAccountResponse> getAdminAccount(
            @Path("accNo") String accNo
    );

    // ==========================================================
// ADMIN ALL TRANSACTIONS
// ==========================================================

    @GET("admin/transactions")
    Call<AdminTransactionPageResponse> getAdminTransactions(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );


    // ==========================
    // Deposit
    // ==========================

    @POST("accounts/deposit")
    Call<DepositResponse> deposit(
            @Body DepositRequest request
    );

    // ==========================
    // Withdraw
    // ==========================

    @POST("accounts/withdraw")
    Call<WithdrawResponse> withdraw(
            @Body WithdrawRequest request
    );

    // ==========================
    // Transfer
    // ==========================

    @POST("accounts/transfer")
    Call<TransferResponse> transfer(
            @Body TransferRequest request
    );

    // ==========================
    // Transaction History
    // ==========================

    @GET("accounts/{accNo}/transactions")
    Call<TransactionPageResponse> getTransactions(

            @Path("accNo") String accNo,

            @Query("page") int page,

            @Query("size") int size,

            @Query("sort") String sort
    );

    // ==========================
    // Profile
    // ==========================

    @GET("profile")
    Call<ProfileResponse> getProfile();

    @PUT("profile")
    Call<ProfileResponse> updateProfile(
            @Body UpdateProfileRequest request
    );

    // ==========================
    // Change Password
    // ==========================

    @PUT("profile/change-password")
    Call<ResponseBody> changePassword(
            @Body ChangePasswordRequest request
    );

    // ==========================
    // Set Transaction PIN
    // ==========================

    @POST("kyc/set-pin")
    Call<ApiResponse> setTransactionPin(
            @Body SetPinRequest request
    );

    // ==========================
    // Verify Transaction PIN
    // ==========================

    @POST("kyc/verify-pin")
    Call<VerifyPinResponse> verifyTransactionPin(
            @Body VerifyPinRequest request
    );

    // ==========================
    // Mobile Recharge
    // ==========================

    @POST("payments/mobile-recharge")
    Call<MobileRechargeResponse> mobileRecharge(
            @Body MobileRechargeRequest request
    );

    @POST("payments/electricity")
    Call<ElectricityBillResponse> payElectricityBill(
            @Body ElectricityBillRequest request
    );

    @POST("payments/water")
    Call<WaterBillResponse> payWaterBill(
            @Body WaterBillRequest request
    );
    @POST("payments/gas")
    Call<GasBillResponse> bookGasCylinder(
            @Body GasBillRequest request
    );

    @POST("payments/dth")
    Call<DthRechargeResponse> rechargeDth(
            @Body DthRechargeRequest request
    );

    @POST("payments/broadband")
    Call<BroadbandRechargeResponse> rechargeBroadband(
            @Body BroadbandRechargeRequest request
    );

    @POST("payments/fastag")
    Call<FastagRechargeResponse> rechargeFastag(
            @Body FastagRechargeRequest request
    );

    @POST("payments/insurance")
    Call<InsurancePaymentResponse> payInsurance(
            @Body InsurancePaymentRequest request
    );

    // ==========================
    // Fixed Deposit
    // ==========================

    @POST("payments/fixed-deposit")
    Call<FixedDepositResponse> calculateFixedDeposit(
            @Body FixedDepositRequest request
    );

    // ==========================
// Create Fixed Deposit
// ==========================

    @POST("payments/fixed-deposit/create")
    Call<CreateFixedDepositResponse> createFixedDeposit(
            @Body CreateFixedDepositRequest request
    );

    // ==========================================
// Recurring Deposit
// ==========================================

// RD Calculator

    @POST("payments/recurring-deposit/calculate")
    Call<RDCalculatorResponse> calculateRecurringDeposit(
            @Body RDCalculatorRequest request
    );

// Open RD

    @POST("payments/recurring-deposit/create")
    Call<CreateRecurringDepositResponse> createRecurringDeposit(
            @Body CreateRecurringDepositRequest request
    );

// My RD List

    @GET("payments/recurring-deposit")
    Call<List<RDResponse>> getMyRecurringDeposits();

// RD Details

    @GET("payments/recurring-deposit/{rdNumber}")
    Call<RDResponse> getRecurringDepositDetails(
            @Path("rdNumber") String rdNumber
    );

// Matured RD List

    @GET("payments/recurring-deposit/matured")
    Call<List<RDResponse>> getMaturedRecurringDeposits();

// RD History

    @GET("payments/recurring-deposit/history/{rdNumber}")
    Call<List<RDHistoryResponse>> getRecurringDepositHistory(
            @Path("rdNumber") String rdNumber
    );

// Pay RD Installment

    @POST("payments/recurring-deposit/pay-installment")
    Call<PayRecurringDepositInstallmentResponse> payRecurringDepositInstallment(
            @Body PayRecurringDepositInstallmentRequest request
    );

// Premature Close RD

    @POST("payments/recurring-deposit/premature-close")
    Call<PrematureCloseRDResponse> prematureCloseRecurringDeposit(
            @Body PrematureCloseRDRequest request
    );

    // ==========================
// Refresh Token
// ==========================

    @POST("auth/refresh")
    Call<LoginResponse> refreshToken(
            @Query("token") String refreshToken
    );


}