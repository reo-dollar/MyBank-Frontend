package com.rohit.mybank.activities.cards;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.cards.DebitCardResponse;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ============================================================
 * DEBIT CARD ACTIVITY
 * ============================================================
 *
 * COMPLETE DEBIT CARD DETAILS SCREEN
 *
 * This screen uses:
 *
 * GET /api/debit-cards/my/details
 *
 * This endpoint must return the complete DebitCardResponse.
 *
 * ============================================================
 */
public class DebitCardActivity extends AppCompatActivity {

    private static final String TAG =
            "DebitCardActivity";


    // =========================================================
    // CARD INFORMATION
    // =========================================================

    private TextView tvCardNumber;
    private TextView tvCardCvv;
    private TextView tvCardHolderName;
    private TextView tvCardType;
    private TextView tvCardStatus;


    // =========================================================
    // DATES
    // =========================================================

    private TextView tvExpiryDate;
    private TextView tvIssueDate;


    // =========================================================
    // ACCOUNT
    // =========================================================

    private TextView tvAccountNumber;


    // =========================================================
    // LIMITS
    // =========================================================

    private TextView tvDailyLimit;
    private TextView tvSpentToday;
    private TextView tvRemainingDailyLimit;


    // =========================================================
    // CARD CONTROLS
    // =========================================================

    private TextView tvOnlineTransactions;
    private TextView tvContactless;
    private TextView tvInternationalTransactions;
    private TextView tvAtmTransactions;
    private TextView tvPosTransactions;


    // =========================================================
    // CARD / EMPTY STATE
    // =========================================================

    private View cardDebitCard;
    private View tvNoCard;


    // =========================================================
    // LOADING
    // =========================================================

    private ProgressBar progressBar;


    // =========================================================
    // API
    // =========================================================

    private ApiService apiService;

    private Call<DebitCardResponse> debitCardCall;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_debit_card
        );

        initializeViews();

        initializeApi();

        loadDebitCardDetails();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // -----------------------------------------------------
        // CARD NUMBER
        // -----------------------------------------------------

        tvCardNumber =
                findViewById(
                        R.id.tvCardNumber
                );


        // -----------------------------------------------------
        // CVV
        // -----------------------------------------------------

        tvCardCvv =
                findViewById(
                        R.id.tvCardCvv
                );


        // -----------------------------------------------------
        // CARD HOLDER
        // -----------------------------------------------------

        tvCardHolderName =
                findViewById(
                        R.id.tvCardHolderName
                );


        // -----------------------------------------------------
        // CARD TYPE
        // -----------------------------------------------------

        tvCardType =
                findViewById(
                        R.id.tvCardType
                );


        // -----------------------------------------------------
        // CARD STATUS
        // -----------------------------------------------------

        tvCardStatus =
                findViewById(
                        R.id.tvCardStatus
                );


        // -----------------------------------------------------
        // EXPIRY DATE
        // -----------------------------------------------------

        tvExpiryDate =
                findViewById(
                        R.id.tvExpiryDate
                );


        // -----------------------------------------------------
        // ISSUE DATE
        // -----------------------------------------------------

        tvIssueDate =
                findViewById(
                        R.id.tvIssueDate
                );


        // -----------------------------------------------------
        // ACCOUNT NUMBER
        // -----------------------------------------------------

        tvAccountNumber =
                findViewById(
                        R.id.tvAccountNumber
                );


        // -----------------------------------------------------
        // DAILY LIMIT
        // -----------------------------------------------------

        tvDailyLimit =
                findViewById(
                        R.id.tvDailyLimit
                );


        // -----------------------------------------------------
        // SPENT TODAY
        // -----------------------------------------------------

        tvSpentToday =
                findViewById(
                        R.id.tvSpentToday
                );


        // -----------------------------------------------------
        // REMAINING DAILY LIMIT
        // -----------------------------------------------------

        tvRemainingDailyLimit =
                findViewById(
                        R.id.tvRemainingDailyLimit
                );


        // -----------------------------------------------------
        // ONLINE TRANSACTIONS
        // -----------------------------------------------------

        tvOnlineTransactions =
                findViewById(
                        R.id.tvOnlineTransactions
                );


        // -----------------------------------------------------
        // CONTACTLESS
        // -----------------------------------------------------

        tvContactless =
                findViewById(
                        R.id.tvContactless
                );


        // -----------------------------------------------------
        // INTERNATIONAL TRANSACTIONS
        // -----------------------------------------------------

        tvInternationalTransactions =
                findViewById(
                        R.id.tvInternationalTransactions
                );


        // -----------------------------------------------------
        // ATM TRANSACTIONS
        // -----------------------------------------------------

        tvAtmTransactions =
                findViewById(
                        R.id.tvAtmTransactions
                );


        // -----------------------------------------------------
        // POS TRANSACTIONS
        // -----------------------------------------------------

        tvPosTransactions =
                findViewById(
                        R.id.tvPosTransactions
                );


        // -----------------------------------------------------
        // CARD
        // -----------------------------------------------------

        cardDebitCard =
                findViewById(
                        R.id.cardDebitCard
                );


        // -----------------------------------------------------
        // NO CARD
        // -----------------------------------------------------

        tvNoCard =
                findViewById(
                        R.id.tvNoCard
                );


        // -----------------------------------------------------
        // PROGRESS BAR
        // -----------------------------------------------------

        progressBar =
                findViewById(
                        R.id.progressBar
                );
    }


    // =========================================================
    // INITIALIZE API
    // =========================================================

    private void initializeApi() {

        apiService =
                RetrofitClient
                        .getClient(
                                getApplicationContext()
                        )
                        .create(
                                ApiService.class
                        );
    }


    // =========================================================
    // LOAD COMPLETE DEBIT CARD DETAILS
    // =========================================================

    private void loadDebitCardDetails() {

        // -----------------------------------------------------
        // CANCEL PREVIOUS REQUEST
        // -----------------------------------------------------

        if (debitCardCall != null
                && !debitCardCall.isCanceled()) {

            debitCardCall.cancel();
        }


        // -----------------------------------------------------
        // SHOW LOADING
        // -----------------------------------------------------

        showLoading(true);

        showCard(false);


        // -----------------------------------------------------
        // LOG
        // -----------------------------------------------------

        Log.d(
                TAG,
                "Loading COMPLETE debit card details..."
        );


        // =====================================================
        // IMPORTANT
        // =====================================================
        //
        // DO NOT USE:
        //
        // apiService.getMyDebitCard()
        //
        // Use:
        //
        // apiService.getMyDebitCardDetails()
        //
        // =====================================================

        debitCardCall =
                apiService.getMyDebitCard();


        // -----------------------------------------------------
        // SEND REQUEST
        // -----------------------------------------------------

        debitCardCall.enqueue(
                new Callback<DebitCardResponse>() {

                    @Override
                    public void onResponse(
                            Call<DebitCardResponse> call,
                            Response<DebitCardResponse> response
                    ) {

                        // -------------------------------------
                        // ACTIVITY SAFETY
                        // -------------------------------------

                        if (isFinishing()
                                || isDestroyed()) {

                            return;
                        }


                        // -------------------------------------
                        // HIDE LOADING
                        // -------------------------------------

                        showLoading(false);


                        // -------------------------------------
                        // SUCCESS
                        // -------------------------------------

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Log.d(
                                    TAG,
                                    "Complete debit card details received."
                            );

                            displayDebitCard(
                                    response.body()
                            );

                            return;
                        }


                        // -------------------------------------
                        // SERVER ERROR
                        // -------------------------------------

                        handleServerError(
                                response
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<DebitCardResponse> call,
                            Throwable throwable
                    ) {

                        // -------------------------------------
                        // REQUEST CANCELLED
                        // -------------------------------------

                        if (call.isCanceled()) {

                            Log.d(
                                    TAG,
                                    "Debit card request cancelled."
                            );

                            return;
                        }


                        // -------------------------------------
                        // ACTIVITY SAFETY
                        // -------------------------------------

                        if (isFinishing()
                                || isDestroyed()) {

                            return;
                        }


                        // -------------------------------------
                        // HIDE LOADING
                        // -------------------------------------

                        showLoading(false);


                        // -------------------------------------
                        // LOG ERROR
                        // -------------------------------------

                        Log.e(
                                TAG,
                                "Debit card request failed.",
                                throwable
                        );


                        // -------------------------------------
                        // MESSAGE
                        // -------------------------------------

                        Toast.makeText(
                                DebitCardActivity.this,
                                "Unable to connect to server.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // DISPLAY COMPLETE DEBIT CARD
    // =========================================================

    private void displayDebitCard(
            DebitCardResponse card
    ) {

        if (card == null) {

            showCard(false);

            showNoCard(
                    "Debit card information unavailable."
            );

            return;
        }


        // =====================================================
        // FULL CARD NUMBER
        // =====================================================

        if (tvCardNumber != null) {

            String cardNumber =
                    card.getCardNumber();


            if (cardNumber == null
                    || cardNumber.trim().isEmpty()) {

                tvCardNumber.setText(
                        "---- ---- ---- ----"
                );

                Log.w(
                        TAG,
                        "Card number is NULL or EMPTY."
                );

            } else {

                tvCardNumber.setText(
                        formatCardNumber(
                                cardNumber
                        )
                );

                Log.d(
                        TAG,
                        "Full card number displayed."
                );
            }
        }


        // =====================================================
        // CVV
        // =====================================================

        if (tvCardCvv != null) {

            String cvv =
                    card.getCvv();


            if (cvv == null
                    || cvv.trim().isEmpty()) {

                tvCardCvv.setText(
                        "---"
                );

                Log.w(
                        TAG,
                        "CVV is NULL or EMPTY."
                );

            } else {

                tvCardCvv.setText(
                        cvv.trim()
                );

                Log.d(
                        TAG,
                        "CVV displayed."
                );
            }
        }


        // =====================================================
        // CARD HOLDER
        // =====================================================

        if (tvCardHolderName != null) {

            tvCardHolderName.setText(
                    safeValue(
                            card.getCardHolderName()
                    )
            );
        }


        // =====================================================
        // CARD TYPE
        // =====================================================

        if (tvCardType != null) {

            tvCardType.setText(
                    safeValue(
                            card.getCardType()
                    )
            );
        }


        // =====================================================
        // CARD STATUS
        // =====================================================

        if (tvCardStatus != null) {

            tvCardStatus.setText(
                    safeValue(
                            card.getStatus()
                    )
            );
        }


        // =====================================================
        // EXPIRY DATE
        // =====================================================

        if (tvExpiryDate != null) {

            tvExpiryDate.setText(
                    safeValue(
                            card.getExpiryDate()
                    )
            );
        }


        // =====================================================
        // ISSUE DATE
        // =====================================================

        if (tvIssueDate != null) {

            tvIssueDate.setText(
                    safeValue(
                            card.getIssueDate()
                    )
            );
        }


        // =====================================================
        // ACCOUNT NUMBER
        // =====================================================

        if (tvAccountNumber != null) {

            tvAccountNumber.setText(
                    safeValue(
                            card.getAccountNumber()
                    )
            );
        }


        // =====================================================
        // DAILY LIMIT
        // =====================================================

        if (tvDailyLimit != null) {

            tvDailyLimit.setText(
                    formatAmount(
                            card.getDailyLimit()
                    )
            );
        }


        // =====================================================
        // SPENT TODAY
        // =====================================================

        if (tvSpentToday != null) {

            tvSpentToday.setText(
                    formatAmount(
                            card.getSpentToday()
                    )
            );
        }


        // =====================================================
        // REMAINING DAILY LIMIT
        // =====================================================

        if (tvRemainingDailyLimit != null) {

            tvRemainingDailyLimit.setText(
                    formatAmount(
                            card.getRemainingDailyLimit()
                    )
            );
        }


        // =====================================================
        // ONLINE TRANSACTIONS
        // =====================================================

        setControlStatus(
                tvOnlineTransactions,
                card.isOnlineTransactionsEnabled()
        );


        // =====================================================
        // CONTACTLESS
        // =====================================================

        setControlStatus(
                tvContactless,
                card.isContactlessEnabled()
        );


        // =====================================================
        // INTERNATIONAL TRANSACTIONS
        // =====================================================

        setControlStatus(
                tvInternationalTransactions,
                card.isInternationalTransactionsEnabled()
        );


        // =====================================================
        // ATM TRANSACTIONS
        // =====================================================

        setControlStatus(
                tvAtmTransactions,
                card.isAtmTransactionsEnabled()
        );


        // =====================================================
        // POS TRANSACTIONS
        // =====================================================

        setControlStatus(
                tvPosTransactions,
                card.isPosTransactionsEnabled()
        );


        // =====================================================
        // SHOW CARD
        // =====================================================

        showCard(true);


        // =====================================================
        // HIDE NO-CARD MESSAGE
        // =====================================================

        if (tvNoCard != null) {

            tvNoCard.setVisibility(
                    View.GONE
            );
        }


        Log.d(
                TAG,
                "Complete debit card information displayed."
        );
    }


    // =========================================================
    // FORMAT CARD NUMBER
    // =========================================================

    private String formatCardNumber(
            String cardNumber
    ) {

        if (cardNumber == null) {

            return "---- ---- ---- ----";
        }


        String digits =
                cardNumber.replaceAll(
                        "\\s+",
                        ""
                );


        // -----------------------------------------------------
        // 16 DIGIT CARD
        // -----------------------------------------------------

        if (digits.length() == 16) {

            return digits.substring(0, 4)
                    + " "
                    + digits.substring(4, 8)
                    + " "
                    + digits.substring(8, 12)
                    + " "
                    + digits.substring(12, 16);
        }


        // -----------------------------------------------------
        // IF ALREADY FORMATTED / UNEXPECTED LENGTH
        // -----------------------------------------------------

        return cardNumber;
    }


    // =========================================================
    // CONTROL STATUS
    // =========================================================

    private void setControlStatus(
            TextView textView,
            boolean enabled
    ) {

        if (textView == null) {

            return;
        }


        textView.setText(
                enabled
                        ? "Enabled"
                        : "Disabled"
        );
    }


    // =========================================================
    // SHOW / HIDE CARD
    // =========================================================

    private void showCard(
            boolean show
    ) {

        if (cardDebitCard != null) {

            cardDebitCard.setVisibility(
                    show
                            ? View.VISIBLE
                            : View.GONE
            );
        }
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeValue(
            String value
    ) {

        if (value == null) {

            return "-";
        }


        String trimmed =
                value.trim();


        if (trimmed.isEmpty()) {

            return "-";
        }


        return trimmed;
    }


    // =========================================================
    // FORMAT MONEY
    // =========================================================

    private String formatAmount(
            Double amount
    ) {

        if (amount == null) {

            return "₹0.00";
        }


        return String.format(
                Locale.getDefault(),
                "₹%.2f",
                amount
        );
    }


    // =========================================================
    // SERVER ERROR
    // =========================================================

    private void handleServerError(
            Response<DebitCardResponse> response
    ) {

        showCard(false);


        if (response == null) {

            showNoCard(
                    "Unable to load debit card."
            );

            return;
        }


        int statusCode =
                response.code();


        String message;


        switch (statusCode) {

            case 400:

                message =
                        "Invalid debit card request.";

                break;


            case 401:

                message =
                        "Session expired. Please login again.";

                break;


            case 403:

                message =
                        "You are not authorized to view this card.";

                break;


            case 404:

                message =
                        "Debit card details not found.";

                break;


            case 500:

                message =
                        "Server error while loading debit card.";

                break;


            default:

                message =
                        "Unable to load debit card. HTTP "
                                + statusCode;

                break;
        }


        String backendError =
                readErrorBody(
                        response
                );


        if (backendError != null
                && !backendError.trim().isEmpty()) {

            Log.e(
                    TAG,
                    "Backend error: "
                            + backendError
            );
        }


        showNoCard(
                message
        );
    }


    // =========================================================
    // NO CARD
    // =========================================================

    private void showNoCard(
            String message
    ) {

        showCard(false);


        if (tvNoCard != null) {

            tvNoCard.setVisibility(
                    View.VISIBLE
            );
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // READ ERROR BODY
    // =========================================================

    private String readErrorBody(
            Response<DebitCardResponse> response
    ) {

        if (response == null
                || response.errorBody() == null) {

            return null;
        }


        try {

            return response
                    .errorBody()
                    .string();

        } catch (IOException exception) {

            Log.e(
                    TAG,
                    "Unable to read backend error.",
                    exception
            );

            return null;
        }
    }


    // =========================================================
    // LOADING
    // =========================================================

    private void showLoading(
            boolean loading
    ) {

        if (progressBar == null) {

            return;
        }


        progressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
    }


    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        if (debitCardCall != null
                && !debitCardCall.isCanceled()) {

            debitCardCall.cancel();
        }


        super.onDestroy();
    }
}