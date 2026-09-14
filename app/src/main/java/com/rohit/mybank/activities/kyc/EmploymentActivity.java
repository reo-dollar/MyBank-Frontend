package com.rohit.mybank.activities.kyc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

public class EmploymentActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private Spinner spOccupation;
    private Spinner spAccountType;

    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnReview;

    // ============================================================
    // DATA
    // ============================================================

    private KycRequest request;

    // True when this screen was opened using
    // "Edit Employment" from ReviewActivity.
    private boolean editMode = false;

    // ============================================================
    // CONSTANTS
    // ============================================================

    private static final String EXTRA_KYC = "kyc";
    private static final String EXTRA_EDIT_MODE = "edit_mode";

    // ============================================================
    // ACTIVITY
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employment);

        // --------------------------------------------------------
        // Read edit mode
        // --------------------------------------------------------

        editMode = getIntent().getBooleanExtra(
                EXTRA_EDIT_MODE,
                false
        );

        // --------------------------------------------------------
        // Read existing KYC object
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
        // Setup dropdowns
        // --------------------------------------------------------

        setupSpinners();

        // --------------------------------------------------------
        // Load existing values
        // --------------------------------------------------------

        populateExistingData();

        // --------------------------------------------------------
        // Review button
        // --------------------------------------------------------

        btnReview.setOnClickListener(v -> validateAndContinue());
    }

    // ============================================================
    // GET KYC OBJECT
    // ============================================================

    private KycRequest getKycFromIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            return getIntent().getSerializableExtra(
                    EXTRA_KYC,
                    KycRequest.class
            );

        } else {

            return (KycRequest) getIntent()
                    .getSerializableExtra(EXTRA_KYC);
        }
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        spOccupation = findViewById(R.id.spOccupation);

        spAccountType = findViewById(R.id.spAccountType);

        etPassword = findViewById(R.id.etPassword);

        etConfirmPassword =
                findViewById(R.id.etConfirmPassword);

        btnReview = findViewById(R.id.btnReview);
    }

    // ============================================================
    // SETUP SPINNERS
    // ============================================================

    private void setupSpinners() {

        // --------------------------------------------------------
        // Occupation
        // --------------------------------------------------------

        ArrayAdapter<CharSequence> occupationAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.occupation_array,
                        android.R.layout.simple_spinner_item
                );

        occupationAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spOccupation.setAdapter(occupationAdapter);

        // --------------------------------------------------------
        // Account Type
        // --------------------------------------------------------

        ArrayAdapter<CharSequence> accountAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.account_type_array,
                        android.R.layout.simple_spinner_item
                );

        accountAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spAccountType.setAdapter(accountAdapter);
    }

    // ============================================================
    // POPULATE EXISTING DATA
    // ============================================================

    private void populateExistingData() {

        if (request == null) {
            return;
        }

        // --------------------------------------------------------
        // Occupation
        // --------------------------------------------------------

        String savedOccupation = request.getOccupation();

        if (savedOccupation != null &&
                !savedOccupation.trim().isEmpty()) {

            setSpinnerValue(
                    spOccupation,
                    savedOccupation
            );
        }

        // --------------------------------------------------------
        // Account Type
        // --------------------------------------------------------

        String savedAccountType = request.getAccountType();

        if (savedAccountType != null &&
                !savedAccountType.trim().isEmpty()) {

            setSpinnerValue(
                    spAccountType,
                    savedAccountType
            );
        }

        // --------------------------------------------------------
        // Password
        // --------------------------------------------------------
        //
        // During normal KYC flow this may already contain the
        // password entered earlier.
        //
        // During edit mode, if your existing request contains it,
        // populate it so the user can modify it.
        // --------------------------------------------------------

        String savedPassword = request.getPassword();

        if (savedPassword != null &&
                !savedPassword.isEmpty()) {

            etPassword.setText(savedPassword);

            etConfirmPassword.setText(savedPassword);
        }
    }

    // ============================================================
    // SET SPINNER VALUE
    // ============================================================

    private void setSpinnerValue(
            Spinner spinner,
            String value
    ) {

        if (value == null) {
            return;
        }

        for (int i = 0; i < spinner.getCount(); i++) {

            Object item = spinner.getItemAtPosition(i);

            if (item != null &&
                    value.equalsIgnoreCase(
                            item.toString().trim()
                    )) {

                spinner.setSelection(i);
                return;
            }
        }
    }

    // ============================================================
    // VALIDATION
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
        // Get spinner values
        // --------------------------------------------------------

        String occupation = "";

        if (spOccupation.getSelectedItem() != null) {

            occupation =
                    spOccupation
                            .getSelectedItem()
                            .toString()
                            .trim();
        }

        String accountType = "";

        if (spAccountType.getSelectedItem() != null) {

            accountType =
                    spAccountType
                            .getSelectedItem()
                            .toString()
                            .trim();
        }

        // --------------------------------------------------------
        // Get password
        // --------------------------------------------------------

        String password =
                etPassword
                        .getText()
                        .toString()
                        .trim();

        String confirmPassword =
                etConfirmPassword
                        .getText()
                        .toString()
                        .trim();

        // ========================================================
        // OCCUPATION VALIDATION
        // ========================================================

        if (occupation.isEmpty() ||
                occupation.equalsIgnoreCase("Select Occupation")) {

            Toast.makeText(
                    this,
                    "Please select occupation",
                    Toast.LENGTH_SHORT
            ).show();

            spOccupation.requestFocus();

            return;
        }

        // ========================================================
        // ACCOUNT TYPE VALIDATION
        // ========================================================

        if (accountType.isEmpty() ||
                accountType.equalsIgnoreCase("Select Account Type")) {

            Toast.makeText(
                    this,
                    "Please select account type",
                    Toast.LENGTH_SHORT
            ).show();

            spAccountType.requestFocus();

            return;
        }

        // ========================================================
        // PASSWORD VALIDATION
        // ========================================================

        if (password.isEmpty()) {

            etPassword.setError(
                    "Password is required"
            );

            etPassword.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Minimum length
        // --------------------------------------------------------

        if (password.length() < 8) {

            etPassword.setError(
                    "Password must be at least 8 characters"
            );

            etPassword.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Uppercase + lowercase + number
        // --------------------------------------------------------

        if (!password.matches(
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$"
        )) {

            etPassword.setError(
                    "Password must contain uppercase, lowercase and a number"
            );

            etPassword.requestFocus();

            return;
        }

        // ========================================================
        // CONFIRM PASSWORD
        // ========================================================

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.setError(
                    "Confirm password is required"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Password match
        // --------------------------------------------------------

        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        // ========================================================
        // UPDATE SAME KYC REQUEST
        // ========================================================

        request.setOccupation(occupation);

        request.setAccountType(accountType);

        request.setPassword(password);

        // ========================================================
        // EDIT MODE
        // ========================================================
        //
        // If EmploymentActivity was opened from ReviewActivity,
        // DO NOT open another ReviewActivity.
        //
        // Return the updated KycRequest back to ReviewActivity.
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

        Intent intent = new Intent(
                EmploymentActivity.this,
                ReviewActivity.class
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

        if (editMode) {

            setResult(RESULT_CANCELED);

            finish();

            return;
        }

        super.onBackPressed();
    }
}