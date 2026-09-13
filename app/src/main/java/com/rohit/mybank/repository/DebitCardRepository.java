package com.rohit.mybank.repository;

import android.content.Context;

import com.rohit.mybank.api.DebitCardApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.cards.DebitCardResponse;

import retrofit2.Call;

/**
 * ============================================================
 * DEBIT CARD REPOSITORY
 * ============================================================
 *
 * Customer-side debit card repository.
 *
 * ============================================================
 */
public class DebitCardRepository {

    private final DebitCardApi debitCardApi;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DebitCardRepository(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null."
            );
        }

        debitCardApi =
                RetrofitClient
                        .getClient(
                                context.getApplicationContext()
                        )
                        .create(
                                DebitCardApi.class
                        );
    }


    // =========================================================
    // GET MY DEBIT CARD
    // =========================================================
    //
    // Returns masked card information.
    //
    // =========================================================

    public Call<DebitCardResponse> getMyDebitCard() {

        return debitCardApi.getMyDebitCard();
    }


    // =========================================================
    // GET MY DEBIT CARD DETAILS
    // =========================================================
    //
    // Returns full card number + CVV.
    //
    // =========================================================

    public Call<DebitCardResponse> getMyDebitCardDetails() {

        return debitCardApi.getMyDebitCardDetails();
    }
}