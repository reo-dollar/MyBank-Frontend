package com.rohit.mybank.enums;

/**
 * =========================================================
 * CREDIT CARD STATUS
 * =========================================================
 *
 * Must match the CreditCardStatus enum used by the
 * Spring Boot backend.
 *
 * These values are serialized by Retrofit/Gson as strings.
 */
public enum CreditCardStatus {

    /**
     * Card is active and can be used.
     */
    ACTIVE,

    /**
     * Card is temporarily frozen.
     */
    FROZEN,

    /**
     * Card has been permanently blocked.
     */
    BLOCKED,

    /**
     * Card has been permanently closed.
     */
    CLOSED
}