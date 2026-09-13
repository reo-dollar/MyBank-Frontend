package com.rohit.mybank.activities.cards;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.rohit.mybank.R;
import com.rohit.mybank.api.DebitCardApi;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.cards.DebitCardResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * ============================================================
 * MY CARD ACTIVITY
 * ============================================================
 *
 * Customer-side Debit Card screen.
 *
 * Loads the authenticated customer's debit card using:
 *
 * GET /api/debit-cards/my
 *
 * The customer/account/card number is NOT sent by the app.
 * Authentication is handled through the JWT.
 *
 * ============================================================
 */
public class MyCardActivity extends AppCompatActivity {

    // =========================================================
    // CARD SECTIONS
    // =========================================================

    private CardView cardDebitCard;
    private CardView cardAccountInformation;
    private CardView cardDailyLimit;
    private CardView cardControls;

    // =========================================================
    // BUTTON
    // =========================================================

    private Button btnViewCard;

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private TextView tvNoCard;

    // =========================================================
    // PROGRESS
    // =========================================================

    private ProgressBar progressBar;

    // =========================================================
    // CARD INFORMATION
    // =========================================================

    private TextView tvCardNumber;
    private TextView tvCardHolderName;
    private TextView tvCardExpiryDate;
    private TextView tvCardIssueDate;
    private TextView tvCardType;
    private TextView tvCardStatus;

    // =========================================================
    // ACCOUNT INFORMATION
    // =========================================================

    private TextView tvAccountNumber;

    // =========================================================
    // DAILY LIMIT
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
    // DATE FORMAT
    // =========================================================

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy",
                    Locale.getDefault()
            );


    // =========================================================
    // LIFECYCLE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_card);

        initializeViews();

        loadMyDebitCard();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // -----------------------------------------------------
        // CARD SECTIONS
        // -----------------------------------------------------

        cardDebitCard =
                findViewById(R.id.cardDebitCard);

        cardAccountInformation =
                findViewById(R.id.cardAccountInformation);

        cardDailyLimit =
                findViewById(R.id.cardDailyLimit);

        cardControls =
                findViewById(R.id.cardControls);


        // -----------------------------------------------------
        // BUTTON
        // -----------------------------------------------------

        btnViewCard =
                findViewById(R.id.btnViewCard);


        // -----------------------------------------------------
        // EMPTY STATE
        // -----------------------------------------------------

        tvNoCard =
                findViewById(R.id.tvNoCard);


        // -----------------------------------------------------
        // PROGRESS
        // -----------------------------------------------------

        progressBar =
                findViewById(R.id.progressBar);


        // -----------------------------------------------------
        // CARD INFORMATION
        // -----------------------------------------------------

        tvCardNumber =
                findViewById(R.id.tvCardNumber);

        tvCardHolderName =
                findViewById(R.id.tvCardHolderName);

        tvCardExpiryDate =
                findViewById(R.id.tvCardExpiryDate);

        tvCardIssueDate =
                findViewById(R.id.tvCardIssueDate);

        tvCardType =
                findViewById(R.id.tvCardType);

        tvCardStatus =
                findViewById(R.id.tvCardStatus);


        // -----------------------------------------------------
        // ACCOUNT
        // -----------------------------------------------------

        tvAccountNumber =
                findViewById(R.id.tvAccountNumber);


        // -----------------------------------------------------
        // DAILY LIMIT
        // -----------------------------------------------------

        tvDailyLimit =
                findViewById(R.id.tvDailyLimit);

        tvSpentToday =
                findViewById(R.id.tvSpentToday);

        tvRemainingDailyLimit =
                findViewById(R.id.tvRemainingDailyLimit);


        // -----------------------------------------------------
        // CARD CONTROLS
        // -----------------------------------------------------

        tvOnlineTransactions =
                findViewById(R.id.tvOnlineTransactions);

        tvContactless =
                findViewById(R.id.tvContactless);

        tvInternationalTransactions =
                findViewById(R.id.tvInternationalTransactions);

        tvAtmTransactions =
                findViewById(R.id.tvAtmTransactions);

        tvPosTransactions =
                findViewById(R.id.tvPosTransactions);


        // -----------------------------------------------------
        // VIEW CARD BUTTON
        // -----------------------------------------------------

        if (btnViewCard != null) {

            btnViewCard.setOnClickListener(v -> {

                Toast.makeText(
                        MyCardActivity.this,
                        "Debit card details are already displayed.",
                        Toast.LENGTH_SHORT
                ).show();

            });
        }
    }


    // =========================================================
    // LOAD MY DEBIT CARD
    // =========================================================

    private void loadMyDebitCard() {

        showLoading();


        // -----------------------------------------------------
        // CREATE API
        // -----------------------------------------------------

        DebitCardApi api =
                RetrofitClient
                        .getClient(this)
                        .create(DebitCardApi.class);


        // -----------------------------------------------------
        // API CALL
        // -----------------------------------------------------

        Call<DebitCardResponse> call =
                api.getMyDebitCard();


        call.enqueue(
                new Callback<DebitCardResponse>() {

                    @Override
                    public void onResponse(
                            Call<DebitCardResponse> call,
                            Response<DebitCardResponse> response
                    ) {

                        if (isFinishing()
                                || isDestroyed()) {

                            return;
                        }


                        hideLoading();


                        // -----------------------------------------
                        // SUCCESS
                        // -----------------------------------------

                        if (response.isSuccessful()
                                && response.body() != null) {

                            DebitCardResponse debitCard =
                                    response.body();

                            displayDebitCard(debitCard);

                            return;
                        }


                        // -----------------------------------------
                        // NOT FOUND
                        // -----------------------------------------

                        if (response.code() == 404) {

                            showNoCard(
                                    "Debit card is not available."
                            );

                            return;
                        }


                        // -----------------------------------------
                        // UNAUTHORIZED
                        // -----------------------------------------

                        if (response.code() == 401) {

                            showNoCard(
                                    "Your session has expired."
                            );

                            Toast.makeText(
                                    MyCardActivity.this,
                                    "Please login again.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }


                        // -----------------------------------------
                        // FORBIDDEN
                        // -----------------------------------------

                        if (response.code() == 403) {

                            showNoCard(
                                    "You are not authorized to view this card."
                            );

                            return;
                        }


                        // -----------------------------------------
                        // SERVER ERROR
                        // -----------------------------------------

                        showNoCard(
                                "Unable to load debit card."
                        );

                        Toast.makeText(
                                MyCardActivity.this,
                                "Server error: "
                                        + response.code(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }


                    @Override
                    public void onFailure(
                            Call<DebitCardResponse> call,
                            Throwable t
                    ) {

                        if (isFinishing()
                                || isDestroyed()) {

                            return;
                        }


                        hideLoading();


                        // -------------------------------------------------
                        // REQUEST CANCELLED
                        // -------------------------------------------------

                        if (call.isCanceled()) {

                            return;
                        }


                        // -------------------------------------------------
                        // NETWORK ERROR
                        // -------------------------------------------------

                        showNoCard(
                                "Unable to connect to the server."
                        );

                        Toast.makeText(
                                MyCardActivity.this,
                                "Network error. Please check your connection.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // DISPLAY DEBIT CARD
    // =========================================================

    private void displayDebitCard(
            DebitCardResponse card
    ) {

        if (card == null) {

            showNoCard(
                    "Debit card is not available."
            );

            return;
        }


        // =====================================================
        // CARD NUMBER
        // =====================================================

        if (tvCardNumber != null) {

            tvCardNumber.setText(
                    safeText(
                            card.getCardNumber(),
                            "**** **** **** ****"
                    )
            );
        }


        // =====================================================
        // CARD HOLDER
        // =====================================================

        if (tvCardHolderName != null) {

            tvCardHolderName.setText(
                    safeText(
                            card.getCardHolderName(),
                            "--"
                    )
            );
        }


        // =====================================================
        // EXPIRY DATE
        // =====================================================

        if (tvCardExpiryDate != null) {

            tvCardExpiryDate.setText(
                    formatDate(
                            card.getExpiryDate()
                    )
            );
        }


        // =====================================================
        // ISSUE DATE
        // =====================================================

        if (tvCardIssueDate != null) {

            tvCardIssueDate.setText(
                    formatDate(
                            card.getIssueDate()
                    )
            );
        }


        // =====================================================
        // CARD TYPE
        // =====================================================

        if (tvCardType != null) {

            tvCardType.setText(
                    safeText(
                            card.getCardType(),
                            "DEBIT"
                    )
            );
        }


        // =====================================================
        // STATUS
        // =====================================================

        if (tvCardStatus != null) {

            tvCardStatus.setText(
                    safeText(
                            card.getStatus(),
                            "--"
                    )
            );
        }


        // =====================================================
        // ACCOUNT NUMBER
        // =====================================================

        if (tvAccountNumber != null) {

            tvAccountNumber.setText(
                    safeText(
                            card.getAccountNumber(),
                            "--"
                    )
            );
        }


        // =====================================================
        // DAILY LIMIT
        // =====================================================

        if (tvDailyLimit != null) {

            tvDailyLimit.setText(
                    formatRupees(
                            card.getDailyLimit()
                    )
            );
        }


        // =====================================================
        // SPENT TODAY
        // =====================================================

        if (tvSpentToday != null) {

            tvSpentToday.setText(
                    formatRupees(
                            card.getSpentToday()
                    )
            );
        }


        // =====================================================
        // REMAINING DAILY LIMIT
        // =====================================================

        if (tvRemainingDailyLimit != null) {

            tvRemainingDailyLimit.setText(
                    formatRupees(
                            card.getRemainingDailyLimit()
                    )
            );
        }


        // =====================================================
        // ONLINE TRANSACTIONS
        // =====================================================

        setControlState(
                tvOnlineTransactions,
                card.isOnlineTransactionsEnabled()
        );


        // =====================================================
        // CONTACTLESS
        // =====================================================

        setControlState(
                tvContactless,
                card.isContactlessEnabled()
        );


        // =====================================================
        // INTERNATIONAL
        // =====================================================

        setControlState(
                tvInternationalTransactions,
                card.isInternationalTransactionsEnabled()
        );


        // =====================================================
        // ATM
        // =====================================================

        setControlState(
                tvAtmTransactions,
                card.isAtmTransactionsEnabled()
        );


        // =====================================================
        // POS
        // =====================================================

        setControlState(
                tvPosTransactions,
                card.isPosTransactionsEnabled()
        );


        // =====================================================
        // SHOW CARD CONTENT
        // =====================================================

        showCardContent();
    }


    // =========================================================
    // SHOW CARD CONTENT
    // =========================================================

    private void showCardContent() {

        if (cardDebitCard != null) {

            cardDebitCard.setVisibility(
                    View.VISIBLE
            );
        }


        if (cardAccountInformation != null) {

            cardAccountInformation.setVisibility(
                    View.VISIBLE
            );
        }


        if (cardDailyLimit != null) {

            cardDailyLimit.setVisibility(
                    View.VISIBLE
            );
        }


        if (cardControls != null) {

            cardControls.setVisibility(
                    View.VISIBLE
            );
        }


        if (btnViewCard != null) {

            btnViewCard.setVisibility(
                    View.VISIBLE
            );
        }


        if (tvNoCard != null) {

            tvNoCard.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // SHOW NO CARD
    // =========================================================

    private void showNoCard(
            String message
    ) {

        if (cardDebitCard != null) {

            cardDebitCard.setVisibility(
                    View.GONE
            );
        }


        if (cardAccountInformation != null) {

            cardAccountInformation.setVisibility(
                    View.GONE
            );
        }


        if (cardDailyLimit != null) {

            cardDailyLimit.setVisibility(
                    View.GONE
            );
        }


        if (cardControls != null) {

            cardControls.setVisibility(
                    View.GONE
            );
        }


        if (btnViewCard != null) {

            btnViewCard.setVisibility(
                    View.GONE
            );
        }


        if (tvNoCard != null) {

            tvNoCard.setVisibility(
                    View.VISIBLE
            );

            tvNoCard.setText(
                    safeText(
                            message,
                            "Debit card is not available."
                    )
            );
        }
    }


    // =========================================================
    // SHOW LOADING
    // =========================================================

    private void showLoading() {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.VISIBLE
            );
        }


        if (cardDebitCard != null) {

            cardDebitCard.setVisibility(
                    View.GONE
            );
        }


        if (cardAccountInformation != null) {

            cardAccountInformation.setVisibility(
                    View.GONE
            );
        }


        if (cardDailyLimit != null) {

            cardDailyLimit.setVisibility(
                    View.GONE
            );
        }


        if (cardControls != null) {

            cardControls.setVisibility(
                    View.GONE
            );
        }


        if (btnViewCard != null) {

            btnViewCard.setVisibility(
                    View.GONE
            );
        }


        if (tvNoCard != null) {

            tvNoCard.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // HIDE LOADING
    // =========================================================

    private void hideLoading() {

        if (progressBar != null) {

            progressBar.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // CARD CONTROL STATE
    // =========================================================

    private void setControlState(
            TextView textView,
            boolean enabled
    ) {

        if (textView == null) {

            return;
        }


        if (enabled) {

            textView.setText(
                    "ENABLED"
            );

            textView.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_green_dark
                    )
            );

        } else {

            textView.setText(
                    "DISABLED"
            );

            textView.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_red_dark
                    )
            );
        }
    }


    // =========================================================
    // FORMAT RUPEES
    // =========================================================

    private String formatRupees(
            double amount
    ) {

        return String.format(
                Locale.getDefault(),
                "₹%,.2f",
                amount
        );
    }


    // =========================================================
    // FORMAT DATE
    // =========================================================
    //
    // IMPORTANT:
    //
    // This method accepts Object because the current
    // DebitCardResponse model may expose the date as String
    // while older code expected LocalDate.
    //
    // Supported:
    //
    // 2026-09-01
    // 2026-09-01T14:30:00
    // 2026-09-01T14:30:00+05:30
    // LocalDate
    //
    // Output:
    //
    // 01/09/2026
    //
    // =========================================================

    private String formatDate(
            Object dateValue
    ) {

        if (dateValue == null) {

            return "--";
        }


        // =====================================================
        // LOCAL DATE
        // =====================================================

        if (dateValue instanceof LocalDate) {

            try {

                return ((LocalDate) dateValue)
                        .format(DISPLAY_DATE_FORMAT);

            } catch (Exception e) {

                return "--";
            }
        }


        // =====================================================
        // STRING
        // =====================================================

        if (dateValue instanceof String) {

            String value =
                    ((String) dateValue).trim();


            if (value.isEmpty()) {

                return "--";
            }


            // -------------------------------------------------
            // yyyy-MM-dd
            // -------------------------------------------------

            try {

                LocalDate date =
                        LocalDate.parse(
                                value,
                                DateTimeFormatter.ISO_LOCAL_DATE
                        );

                return date.format(
                        DISPLAY_DATE_FORMAT
                );

            } catch (DateTimeParseException ignored) {
                // Continue
            }


            // -------------------------------------------------
            // yyyy-MM-ddTHH:mm:ss
            // -------------------------------------------------

            try {

                LocalDateTime dateTime =
                        LocalDateTime.parse(
                                value,
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        );

                return dateTime
                        .toLocalDate()
                        .format(
                                DISPLAY_DATE_FORMAT
                        );

            } catch (DateTimeParseException ignored) {
                // Continue
            }


            // -------------------------------------------------
            // yyyy-MM-ddTHH:mm:ss+05:30
            // -------------------------------------------------

            try {

                OffsetDateTime dateTime =
                        OffsetDateTime.parse(
                                value,
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        );

                return dateTime
                        .toLocalDate()
                        .format(
                                DISPLAY_DATE_FORMAT
                        );

            } catch (DateTimeParseException ignored) {
                // Continue
            }


            // -------------------------------------------------
            // If backend sends an already formatted date,
            // return it instead of showing an error.
            // -------------------------------------------------

            return value;
        }


        // =====================================================
        // UNKNOWN TYPE
        // =====================================================

        return dateValue.toString();
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }


        return value.trim();
    }
}