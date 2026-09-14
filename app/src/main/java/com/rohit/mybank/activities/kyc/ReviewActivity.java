package com.rohit.mybank.activities.kyc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.model.customer.KycRequest;
import com.rohit.mybank.model.customer.KycResponse;
import com.rohit.mybank.repository.CustomerRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    private static final String EXTRA_KYC = "kyc";
    private static final String EXTRA_EDIT_MODE = "edit_mode";

    private static final int REQUEST_EDIT_PERSONAL = 101;
    private static final int REQUEST_EDIT_CONTACT = 102;
    private static final int REQUEST_EDIT_GOVERNMENT = 103;
    private static final int REQUEST_EDIT_ADDRESS = 104;
    private static final int REQUEST_EDIT_EMPLOYMENT = 105;

    // =========================================================
    // HEADER
    // =========================================================

    /*
     * IMPORTANT:
     *
     * activity_review.xml uses TextView for btnBack.
     * DO NOT change this to ImageButton.
     */
    private TextView btnBack;

    // =========================================================
    // PERSONAL DETAILS
    // =========================================================

    private TextView tvFirstName;
    private TextView tvMiddleName;
    private TextView tvLastName;
    private TextView tvDob;
    private TextView tvGender;

    private TextView btnEditPersonal;

    // =========================================================
    // CONTACT DETAILS
    // =========================================================

    private TextView tvMobile;
    private TextView tvEmail;

    private TextView btnEditContact;

    // =========================================================
    // GOVERNMENT DETAILS
    // =========================================================

    private TextView tvAadhaar;
    private TextView tvPan;

    private TextView btnEditGovernment;

    // =========================================================
    // ADDRESS DETAILS
    // =========================================================

    private TextView tvAddress;
    private TextView tvCity;
    private TextView tvState;
    private TextView tvPincode;

    private TextView btnEditAddress;

    // =========================================================
    // EMPLOYMENT DETAILS
    // =========================================================

    private TextView tvOccupation;
    private TextView tvAccountType;

    private TextView btnEditEmployment;

    // =========================================================
    // SUBMIT
    // =========================================================

    private Button btnSubmit;

    // =========================================================
    // DATA
    // =========================================================

    private KycRequest request;

    private CustomerRepository repository;

    private ProgressDialog progressDialog;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review);

        // ---------------------------------------------------------
        // Initialize views
        // ---------------------------------------------------------

        initializeViews();

        // ---------------------------------------------------------
        // Receive KYC request
        // ---------------------------------------------------------

        request = getKycRequestFromIntent();

        if (request == null) {

            Toast.makeText(
                    this,
                    "KYC data not found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // ---------------------------------------------------------
        // Initialize repository
        // ---------------------------------------------------------

        repository = new CustomerRepository(this);

        // ---------------------------------------------------------
        // Progress dialog
        // ---------------------------------------------------------

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Submitting KYC...");

        // ---------------------------------------------------------
        // Display review
        // ---------------------------------------------------------

        displayReview();

        // ---------------------------------------------------------
        // Listeners
        // ---------------------------------------------------------

        setupListeners();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        /*
         * XML:
         *
         * <TextView
         *     android:id="@+id/btnBack"
         * />
         *
         * Therefore this MUST be TextView.
         */
        btnBack = findViewById(R.id.btnBack);

        // ---------------------------------------------------------
        // PERSONAL
        // ---------------------------------------------------------

        tvFirstName = findViewById(R.id.tvFirstName);
        tvMiddleName = findViewById(R.id.tvMiddleName);
        tvLastName = findViewById(R.id.tvLastName);
        tvDob = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);

        btnEditPersonal = findViewById(
                R.id.btnEditPersonal
        );

        // ---------------------------------------------------------
        // CONTACT
        // ---------------------------------------------------------

        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);

        btnEditContact = findViewById(
                R.id.btnEditContact
        );

        // ---------------------------------------------------------
        // GOVERNMENT
        // ---------------------------------------------------------

        tvAadhaar = findViewById(R.id.tvAadhaar);
        tvPan = findViewById(R.id.tvPan);

        btnEditGovernment = findViewById(
                R.id.btnEditGovernment
        );

        // ---------------------------------------------------------
        // ADDRESS
        // ---------------------------------------------------------

        tvAddress = findViewById(R.id.tvAddress);
        tvCity = findViewById(R.id.tvCity);
        tvState = findViewById(R.id.tvState);
        tvPincode = findViewById(R.id.tvPincode);

        btnEditAddress = findViewById(
                R.id.btnEditAddress
        );

        // ---------------------------------------------------------
        // EMPLOYMENT
        // ---------------------------------------------------------

        tvOccupation = findViewById(
                R.id.tvOccupation
        );

        tvAccountType = findViewById(
                R.id.tvAccountType
        );

        btnEditEmployment = findViewById(
                R.id.btnEditEmployment
        );

        // ---------------------------------------------------------
        // SUBMIT
        // ---------------------------------------------------------

        btnSubmit = findViewById(R.id.btnSubmit);
    }

    // =========================================================
    // GET KYC REQUEST
    // =========================================================

    @SuppressWarnings("deprecation")
    private KycRequest getKycRequestFromIntent() {

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

    // =========================================================
    // SETUP LISTENERS
    // =========================================================

    private void setupListeners() {

        // =====================================================
        // BACK
        // =====================================================

        if (btnBack != null) {

            btnBack.setOnClickListener(v -> finish());
        }

        // =====================================================
        // EDIT PERSONAL
        // =====================================================

        if (btnEditPersonal != null) {

            btnEditPersonal.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ReviewActivity.this,
                        PersonalDetailsActivity.class
                );

                intent.putExtra(
                        EXTRA_KYC,
                        request
                );

                intent.putExtra(
                        EXTRA_EDIT_MODE,
                        true
                );

                startActivityForResult(
                        intent,
                        REQUEST_EDIT_PERSONAL
                );
            });
        }

        // =====================================================
        // EDIT CONTACT
        // =====================================================

        if (btnEditContact != null) {

            btnEditContact.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ReviewActivity.this,
                        ContactDetailsActivity.class
                );

                intent.putExtra(
                        EXTRA_KYC,
                        request
                );

                intent.putExtra(
                        EXTRA_EDIT_MODE,
                        true
                );

                startActivityForResult(
                        intent,
                        REQUEST_EDIT_CONTACT
                );
            });
        }

        // =====================================================
        // EDIT GOVERNMENT
        // =====================================================

        if (btnEditGovernment != null) {

            btnEditGovernment.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ReviewActivity.this,
                        GovernmentIdActivity.class
                );

                intent.putExtra(
                        EXTRA_KYC,
                        request
                );

                intent.putExtra(
                        EXTRA_EDIT_MODE,
                        true
                );

                startActivityForResult(
                        intent,
                        REQUEST_EDIT_GOVERNMENT
                );
            });
        }

        // =====================================================
        // EDIT ADDRESS
        // =====================================================

        if (btnEditAddress != null) {

            btnEditAddress.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ReviewActivity.this,
                        AddressActivity.class
                );

                intent.putExtra(
                        EXTRA_KYC,
                        request
                );

                intent.putExtra(
                        EXTRA_EDIT_MODE,
                        true
                );

                startActivityForResult(
                        intent,
                        REQUEST_EDIT_ADDRESS
                );
            });
        }

        // =====================================================
        // EDIT EMPLOYMENT
        // =====================================================

        if (btnEditEmployment != null) {

            btnEditEmployment.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ReviewActivity.this,
                        EmploymentActivity.class
                );

                intent.putExtra(
                        EXTRA_KYC,
                        request
                );

                intent.putExtra(
                        EXTRA_EDIT_MODE,
                        true
                );

                startActivityForResult(
                        intent,
                        REQUEST_EDIT_EMPLOYMENT
                );
            });
        }

        // =====================================================
        // SUBMIT
        // =====================================================

        if (btnSubmit != null) {

            btnSubmit.setOnClickListener(v -> submitKyc());
        }
    }

    // =========================================================
    // RECEIVE EDITED DATA
    // =========================================================

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        KycRequest updatedRequest = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            updatedRequest = data.getSerializableExtra(
                    EXTRA_KYC,
                    KycRequest.class
            );

        } else {

            Object object =
                    data.getSerializableExtra(EXTRA_KYC);

            if (object instanceof KycRequest) {
                updatedRequest = (KycRequest) object;
            }
        }

        if (updatedRequest != null) {

            request = updatedRequest;

            displayReview();

            Toast.makeText(
                    this,
                    "Details updated successfully",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // DISPLAY REVIEW
    // =========================================================

    private void displayReview() {

        if (request == null) {
            return;
        }

        // =====================================================
        // PERSONAL
        // =====================================================

        setText(
                tvFirstName,
                "First Name        " +
                        safeText(request.getFirstName())
        );

        setText(
                tvMiddleName,
                "Middle Name      " +
                        safeText(request.getMiddleName())
        );

        setText(
                tvLastName,
                "Last Name         " +
                        safeText(request.getLastName())
        );

        setText(
                tvDob,
                "Date of Birth     " +
                        safeText(request.getDateOfBirth())
        );

        setText(
                tvGender,
                "Gender             " +
                        safeText(request.getGender())
        );

        // =====================================================
        // CONTACT
        // =====================================================

        setText(
                tvMobile,
                "Mobile Number    " +
                        safeText(request.getMobile())
        );

        setText(
                tvEmail,
                "Email Address     " +
                        safeText(request.getEmail())
        );

        // =====================================================
        // GOVERNMENT
        // =====================================================

        setText(
                tvAadhaar,
                "Aadhaar Number   " +
                        safeText(request.getAadhaarNumber())
        );

        setText(
                tvPan,
                "PAN Number        " +
                        safeText(request.getPanNumber())
        );

        // =====================================================
        // ADDRESS
        // =====================================================

        setText(
                tvAddress,
                "Address            " +
                        safeText(request.getAddress())
        );

        setText(
                tvCity,
                "City                " +
                        safeText(request.getCity())
        );

        setText(
                tvState,
                "State               " +
                        safeText(request.getState())
        );

        setText(
                tvPincode,
                "Pincode             " +
                        safeText(request.getPincode())
        );

        // =====================================================
        // EMPLOYMENT
        // =====================================================

        setText(
                tvOccupation,
                "Occupation         " +
                        safeText(request.getOccupation())
        );

        setText(
                tvAccountType,
                "Account Type       " +
                        safeText(request.getAccountType())
        );
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(String value) {

        if (value == null) {
            return "--";
        }

        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            return "--";
        }

        return trimmed;
    }

    // =========================================================
    // SET TEXT
    // =========================================================

    private void setText(
            TextView textView,
            String value
    ) {

        if (textView != null) {
            textView.setText(value);
        }
    }

    // =========================================================
    // SUBMIT KYC
    // =========================================================

    private void submitKyc() {

        if (request == null) {

            Toast.makeText(
                    this,
                    "KYC data is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (repository == null) {

            Toast.makeText(
                    this,
                    "Unable to initialize KYC service.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // -----------------------------------------------------
        // Prevent double submission
        // -----------------------------------------------------

        if (btnSubmit != null) {
            btnSubmit.setEnabled(false);
        }

        // -----------------------------------------------------
        // Logging
        // -----------------------------------------------------

        Log.d(TAG, "========================================");
        Log.d(TAG, "KYC SUBMISSION STARTED");

        Log.d(
                TAG,
                "First Name  : " +
                        safeText(request.getFirstName())
        );

        Log.d(
                TAG,
                "Middle Name : " +
                        safeText(request.getMiddleName())
        );

        Log.d(
                TAG,
                "Last Name   : " +
                        safeText(request.getLastName())
        );

        Log.d(
                TAG,
                "DOB         : " +
                        safeText(request.getDateOfBirth())
        );

        Log.d(
                TAG,
                "Gender      : " +
                        safeText(request.getGender())
        );

        Log.d(
                TAG,
                "Mobile      : " +
                        safeText(request.getMobile())
        );

        Log.d(
                TAG,
                "Email       : " +
                        safeText(request.getEmail())
        );

        Log.d(
                TAG,
                "Aadhaar     : " +
                        safeText(request.getAadhaarNumber())
        );

        Log.d(
                TAG,
                "PAN         : " +
                        safeText(request.getPanNumber())
        );

        Log.d(
                TAG,
                "Address     : " +
                        safeText(request.getAddress())
        );

        Log.d(
                TAG,
                "City        : " +
                        safeText(request.getCity())
        );

        Log.d(
                TAG,
                "State       : " +
                        safeText(request.getState())
        );

        Log.d(
                TAG,
                "Pincode     : " +
                        safeText(request.getPincode())
        );

        Log.d(
                TAG,
                "Occupation  : " +
                        safeText(request.getOccupation())
        );

        Log.d(
                TAG,
                "AccountType : " +
                        safeText(request.getAccountType())
        );

        // NEVER log password.

        Log.d(TAG, "========================================");

        // -----------------------------------------------------
        // Show progress
        // -----------------------------------------------------

        if (progressDialog != null) {
            progressDialog.show();
        }

        // -----------------------------------------------------
        // Register customer
        // -----------------------------------------------------

        repository.registerCustomer(
                request,
                new Callback<KycResponse>() {

                    @Override
                    public void onResponse(
                            Call<KycResponse> call,
                            Response<KycResponse> response
                    ) {

                        // -----------------------------------------
                        // Hide progress
                        // -----------------------------------------

                        if (progressDialog != null
                                && progressDialog.isShowing()) {

                            progressDialog.dismiss();
                        }

                        // -----------------------------------------
                        // Re-enable button
                        // -----------------------------------------

                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(true);
                        }

                        // -----------------------------------------
                        // Successful HTTP response
                        // -----------------------------------------

                        if (response.isSuccessful()
                                && response.body() != null) {

                            KycResponse kycResponse =
                                    response.body();

                            Log.d(
                                    TAG,
                                    "HTTP Code : " +
                                            response.code()
                            );

                            // -------------------------------------
                            // Application success
                            // -------------------------------------

                            if (kycResponse.isSuccess()) {

                                Log.d(
                                        TAG,
                                        "KYC Registration Successful"
                                );

                                showRegistrationSuccess(
                                        kycResponse
                                );

                            }

                            // -------------------------------------
                            // Application failure
                            // -------------------------------------

                            else {

                                String message =
                                        safeText(
                                                kycResponse.getMessage()
                                        );

                                if (message.equals("--")) {
                                    message =
                                            "KYC registration failed.";
                                }

                                Toast.makeText(
                                        ReviewActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                        }

                        // -----------------------------------------
                        // HTTP error
                        // -----------------------------------------

                        else {

                            handleHttpError(response);
                        }
                    }

                    // =================================================
                    // NETWORK FAILURE
                    // =================================================

                    @Override
                    public void onFailure(
                            Call<KycResponse> call,
                            Throwable t
                    ) {

                        // -----------------------------------------
                        // Hide progress
                        // -----------------------------------------

                        if (progressDialog != null
                                && progressDialog.isShowing()) {

                            progressDialog.dismiss();
                        }

                        // -----------------------------------------
                        // Re-enable button
                        // -----------------------------------------

                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(true);
                        }

                        // -----------------------------------------
                        // Log
                        // -----------------------------------------

                        Log.e(
                                TAG,
                                "Network Error",
                                t
                        );

                        // -----------------------------------------
                        // Message
                        // -----------------------------------------

                        String message = t.getMessage();

                        if (message == null
                                || message.trim().isEmpty()) {

                            message =
                                    "Unable to connect to the server.";
                        }

                        Toast.makeText(
                                ReviewActivity.this,
                                "Network Error\n\n" +
                                        message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // HTTP ERROR
    // =========================================================

    private void handleHttpError(
            Response<KycResponse> response
    ) {

        String errorMessage =
                "Unknown server error.";

        try {

            if (response.errorBody() != null) {

                String serverError =
                        response.errorBody().string();

                if (serverError != null
                        && !serverError.trim().isEmpty()) {

                    errorMessage = serverError;
                }
            }

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Unable to read error response",
                    e
            );
        }

        Log.e(
                TAG,
                "HTTP Code : " +
                        response.code()
        );

        Log.e(
                TAG,
                "Server Response : " +
                        errorMessage
        );

        Toast.makeText(
                ReviewActivity.this,
                "HTTP " +
                        response.code() +
                        "\n\n" +
                        errorMessage,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // REGISTRATION SUCCESS
    // =========================================================

    private void showRegistrationSuccess(
            KycResponse kycResponse
    ) {

        StringBuilder message =
                new StringBuilder();

        message.append(
                "🎉 Registration Successful\n\n"
        );

        message.append(
                "Congratulations!\n\n"
        );

        message.append(
                "Your bank account has been created successfully.\n\n"
        );

        // -----------------------------------------------------
        // Customer name
        // -----------------------------------------------------

        message.append(
                "Customer Name : "
        );

        message.append(
                safeText(
                        kycResponse.getCustomerName()
                )
        );

        message.append("\n\n");

        // -----------------------------------------------------
        // Customer ID
        // -----------------------------------------------------

        message.append(
                "Customer ID : "
        );

        message.append(
                safeText(
                        kycResponse.getCustomerId()
                )
        );

        message.append("\n\n");

        // -----------------------------------------------------
        // Account number
        // -----------------------------------------------------

        message.append(
                "Account Number : "
        );

        message.append(
                safeText(
                        kycResponse.getAccountNumber()
                )
        );

        message.append("\n\n");

        // -----------------------------------------------------
        // Username
        // -----------------------------------------------------

        message.append(
                "Username : "
        );

        message.append(
                safeText(
                        kycResponse.getUsername()
                )
        );

        message.append("\n\n");

        message.append(
                "Please save your username.\n"
        );

        message.append(
                "You will need it to log in."
        );

        // -----------------------------------------------------
        // Success dialog
        // -----------------------------------------------------

        new AlertDialog.Builder(
                ReviewActivity.this
        )
                .setTitle("Success")
                .setMessage(message.toString())
                .setCancelable(false)
                .setPositiveButton(
                        "OK",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            ReviewActivity.this,
                                            LoginActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();
                        }
                )
                .show();
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        if (progressDialog != null
                && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }

        progressDialog = null;

        super.onDestroy();
    }
}