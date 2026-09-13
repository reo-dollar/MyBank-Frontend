package com.rohit.mybank.api;

import com.rohit.mybank.model.cards.DebitCardResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * ============================================================
 * DEBIT CARD API
 * ============================================================
 */
public interface DebitCardApi {

    // =========================================================
    // GET MY DEBIT CARD
    // =========================================================
    //
    // Returns masked card number.
    //
    // Example:
    //
    // 0981 **** **** 9471
    //
    // =========================================================

    @GET("api/debit-cards/my")
    Call<DebitCardResponse> getMyDebitCard();


    // =========================================================
    // GET MY DEBIT CARD DETAILS
    // =========================================================
    //
    // Returns full card number and CVV.
    //
    // Example:
    //
    // 0981 1473 8608 9471
    // CVV: 201
    //
    // =========================================================

    @GET("api/debit-cards/my/details")
    Call<DebitCardResponse> getMyDebitCardDetails();
}