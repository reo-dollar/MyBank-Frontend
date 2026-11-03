package com.rohit.mybank.activities.cards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.cards.DebitCardResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ============================================================
 * CARDS ACTIVITY
 * ============================================================
 *
 * Customer-side debit card landing screen.
 *
 * API:
 *
 * GET /api/debit-cards/my
 *
 * Flow:
 *
 * Login
 *    ↓
 * JWT
 *    ↓
 * CardsActivity
 *    ↓
 * GET /api/debit-cards/my
 *    ↓
 * Backend Authentication
 *    ↓
 * User
 *    ↓
 * Customer
 *    ↓
 * Account
 *    ↓
 * Debit Card
 *    ↓
 * DebitCardResponse
 *    ↓
 * Display card
 *
 * IMPORTANT:
 *
 * The Android application does NOT send:
 *
 * - customer ID
 * - account number
 * - card ID
 * - card number
 *
 * The backend determines the customer from the
 * authenticated JWT.
 *
 * ============================================================
 */
public class CardsActivity extends AppCompatActivity {

    private static final String TAG =
            "CardsActivity";

    // =========================================================
    // VIEWS
    // =========================================================

    private TextView tvCardNumber;
    private TextView tvCardHolderName;
    private TextView tvCardType;
    private TextView tvCardStatus;

    private TextView tvNoCard;

    private Button btnViewCard;

    private ProgressBar progressBar;

    // =========================================================
    // API
    // =========================================================

    private ApiService apiService;

    // =========================================================
    // CURRENT CARD
    // =========================================================

    private DebitCardResponse currentCard;

    // =========================================================
    // REQUEST
    // =========================================================

    private Call<DebitCardResponse> debitCardCall;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        // -----------------------------------------------------
        // LAYOUT
        // -----------------------------------------------------

        setContentView(
                R.layout.activity_cards
        );

        // -----------------------------------------------------
        // VIEWS
        // -----------------------------------------------------

        initializeViews();

        // -----------------------------------------------------
        // API
        // -----------------------------------------------------

        initializeApi();

        // -----------------------------------------------------
        // LISTENERS
        // -----------------------------------------------------

        setupClickListeners();

        // -----------------------------------------------------
        // LOAD CARD
        // -----------------------------------------------------

        loadDebitCard();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvCardNumber =
                findViewById(
                        R.id.tvCardNumber
                );

        tvCardHolderName =
                findViewById(
                        R.id.tvCardHolderName
                );

        tvCardType =
                findViewById(
                        R.id.tvCardType
                );

        tvCardStatus =
                findViewById(
                        R.id.tvCardStatus
                );

        tvNoCard =
                findViewById(
                        R.id.tvNoCard
                );

        btnViewCard =
                findViewById(
                        R.id.btnViewCard
                );

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
    // CLICK LISTENERS
    // =========================================================

    private void setupClickListeners() {

        if (btnViewCard != null) {

            btnViewCard.setOnClickListener(
                    view -> openDebitCard()
            );
        }
    }

    // =========================================================
    // LOAD DEBIT CARD
    // =========================================================

    private void loadDebitCard() {

        // -----------------------------------------------------
        // CANCEL OLD REQUEST
        // -----------------------------------------------------

        if (debitCardCall != null
                && !debitCardCall.isCanceled()) {

            debitCardCall.cancel();
        }

        // -----------------------------------------------------
        // SHOW LOADING
        // -----------------------------------------------------

        showLoading(true);

        Log.d(
                TAG,
                "========================================"
        );

        Log.d(
                TAG,
                "Loading customer's debit card"
        );

        Log.d(
                TAG,
                "GET /api/debit-cards/my"
        );

        Log.d(
                TAG,
                "========================================"
        );

        // -----------------------------------------------------
        // API REQUEST
        // -----------------------------------------------------

        debitCardCall =
                apiService.getMyDebitCard();

        // -----------------------------------------------------
        // EXECUTE
        // -----------------------------------------------------

        debitCardCall.enqueue(
                new Callback<DebitCardResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<DebitCardResponse> call,
                            @NonNull Response<DebitCardResponse> response
                    ) {

                        showLoading(false);

                        Log.d(
                                TAG,
                                "HTTP CODE: "
                                        + response.code()
                        );

                        // =================================================
                        // SUCCESS
                        // =================================================

                        if (response.isSuccessful()) {

                            DebitCardResponse card =
                                    response.body();

                            // -------------------------------------------------
                            // EMPTY BODY
                            // -------------------------------------------------

                            if (card == null) {

                                Log.e(
                                        TAG,
                                        "HTTP request succeeded "
                                                + "but response body is NULL."
                                );

                                showError(
                                        "Server returned an empty card response."
                                );

                                return;
                            }

                            // -------------------------------------------------
                            // SAVE CARD
                            // -------------------------------------------------

                            currentCard =
                                    card;

                            // -------------------------------------------------
                            // DISPLAY
                            // -------------------------------------------------

                            displayDebitCard(
                                    card
                            );

                            return;
                        }

                        // =================================================
                        // NON SUCCESS RESPONSE
                        // =================================================

                        handleHttpError(
                                response
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DebitCardResponse> call,
                            @NonNull Throwable throwable
                    ) {

                        showLoading(false);

                        // -------------------------------------------------
                        // REQUEST CANCELLED
                        // -------------------------------------------------

                        if (call.isCanceled()) {

                            Log.d(
                                    TAG,
                                    "Debit card request cancelled."
                            );

                            return;
                        }

                        // -------------------------------------------------
                        // NETWORK FAILURE
                        // -------------------------------------------------

                        Log.e(
                                TAG,
                                "Debit card request failed.",
                                throwable
                        );

                        String message =
                                throwable.getMessage();

                        if (message == null
                                || message.trim().isEmpty()) {

                            message =
                                    "Unable to connect to server.";
                        }

                        showError(
                                "Network error: "
                                        + message
                        );
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

            showNoCard();

            return;
        }

        // -----------------------------------------------------
        // HIDE NO CARD MESSAGE
        // -----------------------------------------------------

        if (tvNoCard != null) {

            tvNoCard.setVisibility(
                    View.GONE
            );
        }

        // -----------------------------------------------------
        // SHOW CARD VIEWS
        // -----------------------------------------------------

        if (tvCardNumber != null) {

            tvCardNumber.setVisibility(
                    View.VISIBLE
            );
        }

        if (tvCardHolderName != null) {

            tvCardHolderName.setVisibility(
                    View.VISIBLE
            );
        }

        if (tvCardType != null) {

            tvCardType.setVisibility(
                    View.VISIBLE
            );
        }

        if (tvCardStatus != null) {

            tvCardStatus.setVisibility(
                    View.VISIBLE
            );
        }

        if (btnViewCard != null) {

            btnViewCard.setVisibility(
                    View.VISIBLE
            );
        }

        // =====================================================
        // CARD NUMBER
        // =====================================================

        if (tvCardNumber != null) {

            tvCardNumber.setText(
                    maskCardNumber(card.getCardNumber())
            );
        }

        // =====================================================
        // CARD HOLDER
        // =====================================================

        if (tvCardHolderName != null) {

            String holder =
                    card.getCardHolderName();

            if (holder == null
                    || holder.trim().isEmpty()) {

                holder =
                        "CARD HOLDER";
            }

            tvCardHolderName.setText(
                    holder
            );
        }

        // =====================================================
        // CARD TYPE
        // =====================================================

        if (tvCardType != null) {

            String type =
                    card.getCardType();

            if (type == null
                    || type.trim().isEmpty()) {

                type =
                        "DEBIT";
            }

            tvCardType.setText(
                    type
            );
        }

        // =====================================================
        // STATUS
        // =====================================================

        if (tvCardStatus != null) {

            String status =
                    card.getStatus();

            if (status == null
                    || status.trim().isEmpty()) {

                status =
                        "UNKNOWN";
            }

            tvCardStatus.setText(
                    status
            );
        }

        Log.d(
                TAG,
                "Debit card displayed successfully."
        );
    }

    // =========================================================
    // MASK CARD NUMBER FOR LANDING SCREEN
    // =========================================================

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return "•••• •••• •••• ••••";
        }

        String digits = cardNumber.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return "•••• •••• •••• ••••";
        }

        String lastFour = digits.length() <= 4
                ? digits
                : digits.substring(digits.length() - 4);

        return "•••• •••• •••• " + lastFour;
    }

    // =========================================================
    // OPEN DEBIT CARD DETAILS
    // =========================================================

    private void openDebitCard() {

        if (currentCard == null) {

            Toast.makeText(
                    this,
                    "Debit card information is not loaded.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        CardsActivity.this,
                        DebitCardActivity.class
                );

        startActivity(intent);
    }

    // =========================================================
    // SHOW NO CARD
    // =========================================================

    private void showNoCard() {

        currentCard =
                null;

        if (tvCardNumber != null) {

            tvCardNumber.setVisibility(
                    View.GONE
            );
        }

        if (tvCardHolderName != null) {

            tvCardHolderName.setVisibility(
                    View.GONE
            );
        }

        if (tvCardType != null) {

            tvCardType.setVisibility(
                    View.GONE
            );
        }

        if (tvCardStatus != null) {

            tvCardStatus.setVisibility(
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
                    "Debit card is not available."
            );
        }
    }

    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        Log.e(
                TAG,
                message
        );

        showNoCard();

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // HTTP ERROR
    // =========================================================

    private void handleHttpError(
            Response<DebitCardResponse> response
    ) {

        int code =
                response.code();

        String errorBody =
                readErrorBody(
                        response
                );

        Log.e(
                TAG,
                "========================================"
        );

        Log.e(
                TAG,
                "DEBIT CARD API ERROR"
        );

        Log.e(
                TAG,
                "HTTP CODE: "
                        + code
        );

        // Avoid logging server error payloads because they may
        // contain sensitive information.

        Log.e(
                TAG,
                "========================================"
        );

        String message;

        switch (code) {

            case 400:

                message =
                        "Invalid debit-card request.";

                break;

            case 401:

                message =
                        "Session expired. Please login again.";

                break;

            case 403:

                message =
                        "You are not authorized to access your debit card.";

                break;

            case 404:

                message =
                        "Debit card endpoint/card was not found.";

                break;

            case 500:

                message =
                        "Backend error while loading debit card.";

                break;

            default:

                message =
                        "Unable to load debit card. HTTP "
                                + code;

                break;
        }

        // -----------------------------------------------------
        // APPEND SERVER ERROR
        // -----------------------------------------------------

        if (errorBody != null
                && !errorBody.trim().isEmpty()) {

            message =
                    message
                            + "\n"
                            + errorBody;
        }

        showError(
                message
        );
    }

    // =========================================================
    // READ ERROR BODY
    // =========================================================

    private String readErrorBody(
            Response<?> response
    ) {

        if (response == null
                || response.errorBody() == null) {

            return "";
        }

        try {

            return response
                    .errorBody()
                    .string();

        } catch (IOException e) {

            Log.e(
                    TAG,
                    "Unable to read error body.",
                    e
            );

            return "";
        }
    }

    // =========================================================
    // LOADING STATE
    // =========================================================

    /**
     * Shows or hides the loading indicator and prevents repeated
     * taps on the View Card Details button while the request runs.
     */
    private void showLoading(boolean isLoading) {

        if (progressBar != null) {
            progressBar.setVisibility(
                    isLoading ? View.VISIBLE : View.GONE
            );
        }

        if (btnViewCard != null) {
            btnViewCard.setEnabled(!isLoading);
        }
    }

    /**
     * Compatibility helper for any existing call sites that use
     * showLoading() without an argument.
     */
    private void showLoading() {
        showLoading(true);
    }

    // =========================================================
    // REFRESH WHEN RETURNING
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        /*
         * Avoid calling the API twice on the first opening.
         *
         * onCreate() already loads the card.
         *
         * When returning from DebitCardActivity,
         * refresh the card.
         */

        if (apiService != null
                && currentCard != null) {

            loadDebitCard();
        }
    }

    // =========================================================
    // CANCEL REQUEST
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

