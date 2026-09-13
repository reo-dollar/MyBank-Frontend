package com.rohit.mybank.api;

import com.rohit.mybank.model.auth.ForgotPasswordRequest;
import com.rohit.mybank.model.auth.LoginRequest;
import com.rohit.mybank.model.auth.LoginResponse;
import com.rohit.mybank.model.auth.RegisterRequest;
import com.rohit.mybank.model.auth.RegisterResponse;
import com.rohit.mybank.model.auth.ResetPasswordRequest;

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
import com.rohit.mybank.model.recurringdeposit.PayRecurringDepositInstallmentRequest;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;

import com.rohit.mybank.model.admin.AdminDashboardResponse;
import com.rohit.mybank.model.admin.AdminUserResponse;
import com.rohit.mybank.model.admin.AdminCustomerResponse;
import com.rohit.mybank.model.admin.AdminAccountResponse;
import com.rohit.mybank.model.admin.AdminTransactionPageResponse;

/*
 * ============================================================
 * DEBIT CARD
 * ============================================================
 */
import com.rohit.mybank.model.cards.DebitCardResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;


/**
 * =========================================================
 * API SERVICE
 * =========================================================
 *
 * Retrofit API definitions for the MyBank Android application.
 *
 * All protected APIs require JWT authentication.
 *
 * AuthInterceptor automatically attaches the JWT token.
 *
 * =========================================================
 */
public interface ApiService {


    // =========================================================
    // AUTHENTICATION
    // =========================================================

    /**
     * LOGIN
     *
     * POST /auth/login
     */
    @POST("auth/login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );


    /**
     * REGISTER
     *
     * POST /auth/register
     */
    @POST("auth/register")
    Call<RegisterResponse> register(
            @Body RegisterRequest request
    );


    /**
     * FORGOT PASSWORD
     *
     * POST /auth/forgot-password
     */
    @POST("auth/forgot-password")
    Call<ResponseBody> forgotPassword(
            @Body ForgotPasswordRequest request
    );


    /**
     * RESET PASSWORD
     *
     * POST /auth/reset-password
     */
    @POST("auth/reset-password")
    Call<ResponseBody> resetPassword(
            @Body ResetPasswordRequest request
    );


    /**
     * REFRESH TOKEN
     *
     * POST /auth/refresh?token=...
     */
    @POST("auth/refresh")
    Call<LoginResponse> refreshToken(
            @Query("token") String refreshToken
    );


    // =========================================================
    // KYC
    // =========================================================

    /**
     * KYC REGISTRATION
     *
     * POST /kyc/register
     */
    @POST("kyc/register")
    Call<KycResponse> registerKyc(
            @Body KycRequest request
    );


    /**
     * SET TRANSACTION PIN
     *
     * POST /kyc/set-pin
     */
    @POST("kyc/set-pin")
    Call<ApiResponse> setTransactionPin(
            @Body SetPinRequest request
    );


    /**
     * VERIFY TRANSACTION PIN
     *
     * POST /kyc/verify-pin
     */
    @POST("kyc/verify-pin")
    Call<VerifyPinResponse> verifyTransactionPin(
            @Body VerifyPinRequest request
    );


    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * GET MY ACCOUNT / DASHBOARD
     *
     * GET /accounts/me
     */
    @GET("accounts/me")
    Call<DashboardResponse> getMyAccount();


    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    /**
     * GET ADMIN DASHBOARD
     *
     * GET /admin/dashboard
     */
    @GET("admin/dashboard")
    Call<AdminDashboardResponse> getAdminDashboard();


    // =========================================================
    // ADMIN USER MANAGEMENT
    // =========================================================

    @GET("admin/users")
    Call<List<AdminUserResponse>> getAdminUsers();


    @GET("admin/users/{username}")
    Call<AdminUserResponse> getAdminUser(
            @Path("username") String username
    );


    @PUT("admin/users/{username}/enable")
    Call<AdminUserResponse> enableAdminUser(
            @Path("username") String username
    );


    @PUT("admin/users/{username}/disable")
    Call<AdminUserResponse> disableAdminUser(
            @Path("username") String username
    );


    @PUT("admin/users/{username}/lock")
    Call<AdminUserResponse> lockAdminUser(
            @Path("username") String username
    );


    @PUT("admin/users/{username}/unlock")
    Call<AdminUserResponse> unlockAdminUser(
            @Path("username") String username
    );


    // =========================================================
    // ADMIN CUSTOMER MANAGEMENT
    // =========================================================

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


    // =========================================================
    // ADMIN TRANSACTIONS
    // =========================================================

    @GET("admin/transactions")
    Call<AdminTransactionPageResponse> getAdminTransactions(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );


    // =========================================================
    // DEPOSIT
    // =========================================================

    @POST("accounts/deposit")
    Call<DepositResponse> deposit(
            @Body DepositRequest request
    );


    // =========================================================
    // WITHDRAW
    // =========================================================

    @POST("accounts/withdraw")
    Call<WithdrawResponse> withdraw(
            @Body WithdrawRequest request
    );


    // =========================================================
    // TRANSFER
    // =========================================================

    @POST("accounts/transfer")
    Call<TransferResponse> transfer(
            @Body TransferRequest request
    );


    // =========================================================
    // TRANSACTION HISTORY
    // =========================================================

    @GET("accounts/{accNo}/transactions")
    Call<TransactionPageResponse> getTransactions(
            @Path("accNo") String accNo,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );


    // =========================================================
    // PROFILE
    // =========================================================

    /**
     * GET PROFILE
     *
     * GET /profile
     */
    @GET("profile")
    Call<ProfileResponse> getProfile();


    /**
     * UPDATE PROFILE
     *
     * PUT /profile
     *
     * Updates profile text/details.
     *
     * Profile photo is uploaded separately using
     * uploadProfilePhoto().
     */
    @PUT("profile")
    Call<ProfileResponse> updateProfile(
            @Body UpdateProfileRequest request
    );


    // =========================================================
    // PROFILE PHOTO
    // =========================================================

    /**
     * UPLOAD PROFILE PHOTO
     *
     * POST /profile/photo
     *
     * Content-Type:
     * multipart/form-data
     *
     * Multipart field:
     * photo
     *
     * The backend determines the authenticated customer
     * from the JWT token.
     */
    @Multipart
    @POST("profile/photo")
    Call<ProfileResponse> uploadProfilePhoto(
            @Part MultipartBody.Part photo
    );


    /**
     * GET PROFILE PHOTO
     *
     * GET /profile/photo
     *
     * Returns the authenticated user's JPEG profile photo.
     *
     * @Streaming prevents Retrofit from unnecessarily
     * buffering the complete response before exposing it.
     */
    @Streaming
    @GET("profile/photo")
    Call<ResponseBody> getProfilePhoto();


    /**
     * DELETE PROFILE PHOTO
     *
     * DELETE /profile/photo
     *
     * Removes the authenticated user's profile photo.
     */
    @DELETE("profile/photo")
    Call<ProfileResponse> deleteProfilePhoto();


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    /**
     * CHANGE PASSWORD
     *
     * PUT /profile/change-password
     */
    @PUT("profile/change-password")
    Call<ResponseBody> changePassword(
            @Body ChangePasswordRequest request
    );


    // =========================================================
    // MOBILE RECHARGE
    // =========================================================

    @POST("payments/mobile-recharge")
    Call<MobileRechargeResponse> mobileRecharge(
            @Body MobileRechargeRequest request
    );


    // =========================================================
    // ELECTRICITY BILL
    // =========================================================

    @POST("payments/electricity")
    Call<ElectricityBillResponse> payElectricityBill(
            @Body ElectricityBillRequest request
    );


    // =========================================================
    // WATER BILL
    // =========================================================

    @POST("payments/water")
    Call<WaterBillResponse> payWaterBill(
            @Body WaterBillRequest request
    );


    // =========================================================
    // GAS
    // =========================================================

    @POST("payments/gas")
    Call<GasBillResponse> bookGasCylinder(
            @Body GasBillRequest request
    );


    // =========================================================
    // DTH
    // =========================================================

    @POST("payments/dth")
    Call<DthRechargeResponse> rechargeDth(
            @Body DthRechargeRequest request
    );


    // =========================================================
    // BROADBAND
    // =========================================================

    @POST("payments/broadband")
    Call<BroadbandRechargeResponse> rechargeBroadband(
            @Body BroadbandRechargeRequest request
    );


    // =========================================================
    // FASTAG
    // =========================================================

    @POST("payments/fastag")
    Call<FastagRechargeResponse> rechargeFastag(
            @Body FastagRechargeRequest request
    );


    // =========================================================
    // INSURANCE
    // =========================================================

    @POST("payments/insurance")
    Call<InsurancePaymentResponse> payInsurance(
            @Body InsurancePaymentRequest request
    );


    // =========================================================
    // FIXED DEPOSIT
    // =========================================================

    /**
     * FD CALCULATOR
     *
     * POST /payments/fixed-deposit
     */
    @POST("payments/fixed-deposit")
    Call<FixedDepositResponse> calculateFixedDeposit(
            @Body FixedDepositRequest request
    );


    /**
     * CREATE FD
     *
     * POST /payments/fixed-deposit/create
     */
    @POST("payments/fixed-deposit/create")
    Call<CreateFixedDepositResponse> createFixedDeposit(
            @Body CreateFixedDepositRequest request
    );


    // =========================================================
    // RECURRING DEPOSIT
    // =========================================================

    /**
     * RD CALCULATOR
     *
     * POST /payments/recurring-deposit/calculate
     */
    @POST("payments/recurring-deposit/calculate")
    Call<RDCalculatorResponse> calculateRecurringDeposit(
            @Body RDCalculatorRequest request
    );


    /**
     * CREATE RD
     *
     * POST /payments/recurring-deposit/create
     */
    @POST("payments/recurring-deposit/create")
    Call<CreateRecurringDepositResponse> createRecurringDeposit(
            @Body CreateRecurringDepositRequest request
    );


    /**
     * MY RD LIST
     *
     * GET /payments/recurring-deposit
     */
    @GET("payments/recurring-deposit")
    Call<List<RDResponse>> getMyRecurringDeposits();


    /**
     * RD DETAILS
     *
     * GET /payments/recurring-deposit/{rdNumber}
     */
    @GET("payments/recurring-deposit/{rdNumber}")
    Call<RDResponse> getRecurringDepositDetails(
            @Path("rdNumber") String rdNumber
    );


    /**
     * MATURED RD LIST
     *
     * GET /payments/recurring-deposit/matured
     */
    @GET("payments/recurring-deposit/matured")
    Call<List<RDResponse>> getMaturedRecurringDeposits();


    /**
     * RD HISTORY
     *
     * GET /payments/recurring-deposit/history/{rdNumber}
     */
    @GET("payments/recurring-deposit/history/{rdNumber}")
    Call<List<RDHistoryResponse>> getRecurringDepositHistory(
            @Path("rdNumber") String rdNumber
    );


    /**
     * PAY RD INSTALLMENT
     *
     * POST /payments/recurring-deposit/pay-installment
     */
    @POST("payments/recurring-deposit/pay-installment")
    Call<RDResponse> payRecurringDepositInstallment(
            @Body PayRecurringDepositInstallmentRequest request
    );


    /**
     * CLOSE MATURED RD
     *
     * POST /payments/recurring-deposit/close/{rdNumber}
     */
    @POST("payments/recurring-deposit/close/{rdNumber}")
    Call<RDResponse> closeRecurringDeposit(
            @Path("rdNumber") String rdNumber
    );


    /**
     * PREMATURE CLOSE RD
     *
     * POST /payments/recurring-deposit/premature-close/{rdNumber}
     */
    @POST("payments/recurring-deposit/premature-close/{rdNumber}")
    Call<RDResponse> prematureCloseRecurringDeposit(
            @Path("rdNumber") String rdNumber
    );


    // =========================================================
    // DEBIT CARD
    // =========================================================

    /**
     * GET MY DEBIT CARD
     *
     * GET /api/debit-cards/my
     *
     * Returns the debit card belonging to the authenticated
     * customer.
     */
    @GET("api/debit-cards/my")
    Call<DebitCardResponse> getMyDebitCard();


    // =========================================================
    // FREEZE MY DEBIT CARD
    // =========================================================

    /**
     * PUT /api/debit-cards/my/freeze
     */
    @PUT("debit-cards/my/freeze")
    Call<DebitCardResponse> freezeMyDebitCard();


    // =========================================================
    // UNFREEZE MY DEBIT CARD
    // =========================================================

    /**
     * PUT /api/debit-cards/my/unfreeze
     */
    @PUT("debit-cards/my/unfreeze")
    Call<DebitCardResponse> unfreezeMyDebitCard();


    // =========================================================
    // ONLINE TRANSACTIONS
    // =========================================================

    @PUT("debit-cards/my/controls/online/enable")
    Call<DebitCardResponse> enableOnlineTransactions();


    @PUT("debit-cards/my/controls/online/disable")
    Call<DebitCardResponse> disableOnlineTransactions();


    // =========================================================
    // CONTACTLESS
    // =========================================================

    @PUT("debit-cards/my/controls/contactless/enable")
    Call<DebitCardResponse> enableContactless();


    @PUT("debit-cards/my/controls/contactless/disable")
    Call<DebitCardResponse> disableContactless();


    // =========================================================
    // INTERNATIONAL TRANSACTIONS
    // =========================================================

    @PUT("debit-cards/my/controls/international/enable")
    Call<DebitCardResponse> enableInternationalTransactions();


    @PUT("debit-cards/my/controls/international/disable")
    Call<DebitCardResponse> disableInternationalTransactions();


    // =========================================================
    // ATM TRANSACTIONS
    // =========================================================

    @PUT("debit-cards/my/controls/atm/enable")
    Call<DebitCardResponse> enableAtmTransactions();


    @PUT("debit-cards/my/controls/atm/disable")
    Call<DebitCardResponse> disableAtmTransactions();


    // =========================================================
    // POS TRANSACTIONS
    // =========================================================

    @PUT("debit-cards/my/controls/pos/enable")
    Call<DebitCardResponse> enablePosTransactions();


    @PUT("debit-cards/my/controls/pos/disable")
    Call<DebitCardResponse> disablePosTransactions();

}