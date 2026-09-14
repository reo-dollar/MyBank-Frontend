package com.rohit.mybank.activities.kyc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

public class ContactDetailsActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private EditText etMobile;
    private EditText etEmail;

    private Button btnNext;

    // ============================================================
    // DATA
    // ============================================================

    private KycRequest request;

    /*
     * true  = opened from Review → Edit Contact
     * false = normal KYC flow
     */
    private boolean editMode = false;

    // ============================================================
    // CONSTANTS
    // ============================================================

    private static final String EXTRA_KYC = "kyc";
    private static final String EXTRA_EDIT_MODE = "edit_mode";

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_contact_details
        );

        // --------------------------------------------------------
        // Check edit mode
        // --------------------------------------------------------

        editMode = getIntent().getBooleanExtra(
                EXTRA_EDIT_MODE,
                false
        );

        // --------------------------------------------------------
        // Get KYC object
        // --------------------------------------------------------

        request = getKycFromIntent();

        if (request == null) {

            Toast.makeText(
                    this,
                    "KYC data not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // --------------------------------------------------------
        // Initialize views
        // --------------------------------------------------------

        initializeViews();

        // --------------------------------------------------------
        // Load existing values
        // --------------------------------------------------------

        populateExistingData();

        // --------------------------------------------------------
        // Next
        // --------------------------------------------------------

        btnNext.setOnClickListener(
                v -> validateAndContinue()
        );
    }

    // ============================================================
    // GET KYC FROM INTENT
    // ============================================================

    @SuppressWarnings("deprecation")
    private KycRequest getKycFromIntent() {

        Intent intent = getIntent();

        if (intent == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            return intent.getSerializableExtra(
                    EXTRA_KYC,
                    KycRequest.class
            );

        } else {

            Object object =
                    intent.getSerializableExtra(EXTRA_KYC);

            if (object instanceof KycRequest) {
                return (KycRequest) object;
            }
        }

        return null;
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        etMobile = findViewById(
                R.id.etMobile
        );

        etEmail = findViewById(
                R.id.etEmail
        );

        btnNext = findViewById(
                R.id.btnNext
        );
    }

    // ============================================================
    // POPULATE EXISTING DATA
    // ============================================================

    private void populateExistingData() {

        if (request == null) {
            return;
        }

        // --------------------------------------------------------
        // Mobile
        // --------------------------------------------------------

        if (request.getMobile() != null) {

            etMobile.setText(
                    request.getMobile()
            );
        }

        // --------------------------------------------------------
        // Email
        // --------------------------------------------------------

        if (request.getEmail() != null) {

            etEmail.setText(
                    request.getEmail()
            );
        }
    }

    // ============================================================
    // VALIDATE AND CONTINUE
    // ============================================================

    private void validateAndContinue() {

        // --------------------------------------------------------
        // Safety check
        // --------------------------------------------------------

        if (request == null) {

            Toast.makeText(
                    this,
                    "KYC data is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // --------------------------------------------------------
        // Read values
        // --------------------------------------------------------

        String mobile =
                etMobile
                        .getText()
                        .toString()
                        .trim();

        String email =
                etEmail
                        .getText()
                        .toString()
                        .trim();

        // ========================================================
        // MOBILE VALIDATION
        // ========================================================

        if (mobile.isEmpty()) {

            etMobile.setError(
                    "Mobile number is required"
            );

            etMobile.requestFocus();

            return;
        }

        if (!mobile.matches("\\d{10}")) {

            etMobile.setError(
                    "Enter valid 10-digit mobile number"
            );

            etMobile.requestFocus();

            return;
        }

        // ========================================================
        // EMAIL VALIDATION
        // ========================================================

        if (email.isEmpty()) {

            etEmail.setError(
                    "Email is required"
            );

            etEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etEmail.setError(
                    "Enter valid email"
            );

            etEmail.requestFocus();

            return;
        }

        // ========================================================
        // UPDATE SAME KYC REQUEST
        // ========================================================

        request.setMobile(mobile);
        request.setEmail(email);

        // ========================================================
        // EDIT MODE
        // ========================================================
        //
        // Review → Edit Contact
        //
        // Return the updated request directly to Review.
        // ========================================================

        if (editMode) {

            Intent resultIntent = new Intent();

            resultIntent.putExtra(
                    EXTRA_KYC,
                    request
            );

            setResult(
                    RESULT_OK,
                    resultIntent
            );

            finish();

            return;
        }

        // ========================================================
        // NORMAL KYC FLOW
        // ========================================================
        //
        // Contact → Government
        // ========================================================

        Intent intent = new Intent(
                ContactDetailsActivity.this,
                GovernmentIdActivity.class
        );

        intent.putExtra(
                EXTRA_KYC,
                request
        );

        startActivity(intent);
    }

    // ============================================================
    // BACK BUTTON
    // ============================================================

    @Override
    public void onBackPressed() {

        /*
         * If opened from Review, return directly to Review.
         */
        if (editMode) {

            setResult(
                    RESULT_CANCELED
            );

            finish();

            return;
        }

        /*
         * Normal KYC flow.
         */
        super.onBackPressed();
    }
}