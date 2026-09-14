package com.rohit.mybank.activities.kyc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

public class AddressActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private EditText etAddress;
    private EditText etCity;
    private EditText etState;
    private EditText etPincode;

    private Button btnNext;

    // ============================================================
    // DATA
    // ============================================================

    private KycRequest request;

    /*
     * true  = opened from Review → Edit Address
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

        setContentView(R.layout.activity_address);

        // --------------------------------------------------------
        // Check whether this is Edit mode
        // --------------------------------------------------------

        editMode = getIntent().getBooleanExtra(
                EXTRA_EDIT_MODE,
                false
        );

        // --------------------------------------------------------
        // Get existing KYC object
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
        // Next button
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

        etAddress = findViewById(
                R.id.etAddress
        );

        etCity = findViewById(
                R.id.etCity
        );

        etState = findViewById(
                R.id.etState
        );

        etPincode = findViewById(
                R.id.etPincode
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
        // Address
        // --------------------------------------------------------

        if (request.getAddress() != null) {

            etAddress.setText(
                    request.getAddress()
            );
        }

        // --------------------------------------------------------
        // City
        // --------------------------------------------------------

        if (request.getCity() != null) {

            etCity.setText(
                    request.getCity()
            );
        }

        // --------------------------------------------------------
        // State
        // --------------------------------------------------------

        if (request.getState() != null) {

            etState.setText(
                    request.getState()
            );
        }

        // --------------------------------------------------------
        // Pincode
        // --------------------------------------------------------

        if (request.getPincode() != null) {

            etPincode.setText(
                    request.getPincode()
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
        // Get values
        // --------------------------------------------------------

        String address =
                etAddress
                        .getText()
                        .toString()
                        .trim();

        String city =
                etCity
                        .getText()
                        .toString()
                        .trim();

        String state =
                etState
                        .getText()
                        .toString()
                        .trim();

        String pincode =
                etPincode
                        .getText()
                        .toString()
                        .trim();

        // ========================================================
        // ADDRESS VALIDATION
        // ========================================================

        if (address.isEmpty()) {

            etAddress.setError(
                    "Address is required"
            );

            etAddress.requestFocus();

            return;
        }

        // ========================================================
        // CITY VALIDATION
        // ========================================================

        if (city.isEmpty()) {

            etCity.setError(
                    "City is required"
            );

            etCity.requestFocus();

            return;
        }

        // ========================================================
        // STATE VALIDATION
        // ========================================================

        if (state.isEmpty()) {

            etState.setError(
                    "State is required"
            );

            etState.requestFocus();

            return;
        }

        // ========================================================
        // PINCODE VALIDATION
        // ========================================================

        if (!pincode.matches("\\d{6}")) {

            etPincode.setError(
                    "Enter valid 6-digit pincode"
            );

            etPincode.requestFocus();

            return;
        }

        // ========================================================
        // UPDATE SAME KYC REQUEST
        // ========================================================

        request.setAddress(address);
        request.setCity(city);
        request.setState(state);
        request.setPincode(pincode);

        // ========================================================
        // EDIT MODE
        // ========================================================
        //
        // Address was opened from Review.
        //
        // Therefore DO NOT open EmploymentActivity.
        //
        // Return the updated KycRequest to ReviewActivity.
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
        // Address → Employment
        // ========================================================

        Intent intent = new Intent(
                AddressActivity.this,
                EmploymentActivity.class
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
         * If opened from Review, simply return to Review.
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