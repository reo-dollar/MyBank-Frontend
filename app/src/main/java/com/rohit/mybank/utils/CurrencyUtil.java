package com.rohit.mybank.utils;

import java.text.DecimalFormat;

public final class CurrencyUtil {

    private static final DecimalFormat FORMAT =
            new DecimalFormat("#,##0.00");

    private CurrencyUtil() {
        // Prevent instantiation
    }

    /**
     * Formats a currency amount.
     *
     * Example:
     * 1000      -> ₹1,000.00
     * 1000000   -> ₹1,000,000.00
     * 100000000 -> ₹100,000,000.00
     */
    public static String format(double amount) {
        return "₹" + FORMAT.format(amount);
    }

}