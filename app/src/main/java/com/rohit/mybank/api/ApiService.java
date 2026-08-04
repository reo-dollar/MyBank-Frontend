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
}