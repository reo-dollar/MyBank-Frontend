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

public class GovernmentIdActivity extends AppCompatActivity {

    private EditText etAadhaar;
    private EditText etPan;
    private Button btnNext;

    private KycRequest request;

    // True when this screen is opened from ReviewActivity for editing
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_government_id);

        // Get KYC request safely for all supported Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            request = getIntent().getSerializableExtra(
                    "kyc",
                    KycRequest.class
            );
        } else {
            request = (KycRequest) getIntent().getSerializableExtra("kyc");
        }

        // Check edit mode
        editMode = getIntent().getBooleanExtra("edit_mode", false);

        // Make sure KYC data exists
        if (request == null) {
            Toast.makeText(
                    this,
                    "KYC data not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        // If opened from ReviewActivity, load existing values
        if (editMode) {
            populateExistingData();
        }

        btnNext.setOnClickListener(v -> validateAndContinue());
    }

    private void initializeViews() {

        etAadhaar = findViewById(R.id.etAadhaar);
        etPan = findViewById(R.id.etPan);

        btnNext = findViewById(R.id.btnNext);
    }

    /**
     * Populate previously entered Aadhaar and PAN
     * when the user edits Government ID details.
     */
    private void populateExistingData() {

        if (request.getAadhaarNumber() != null) {
            etAadhaar.setText(request.getAadhaarNumber());
        }

        if (request.getPanNumber() != null) {
            etPan.setText(request.getPanNumber());
        }
    }

    private void validateAndContinue() {

        String aadhaar = etAadhaar
                .getText()
                .toString()
                .trim();

        String pan = etPan
                .getText()
                .toString()
                .trim()
                .toUpperCase();

        // Aadhaar validation
        if (!aadhaar.matches("\\d{12}")) {
            etAadhaar.setError("Enter valid 12-digit Aadhaar");
            etAadhaar.requestFocus();
            return;
        }

        // PAN validation
        if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
            etPan.setError("Enter valid PAN Number");
            etPan.requestFocus();
            return;
        }

        // Update the same KYC request object
        request.setAadhaarNumber(aadhaar);
        request.setPanNumber(pan);

        /*
         * EDIT MODE
         *
         * Government ID was opened from ReviewActivity.
         * Return the updated KYC request back to ReviewActivity.
         */
        if (editMode) {

            Intent resultIntent = new Intent();
            resultIntent.putExtra("kyc", request);

            setResult(RESULT_OK, resultIntent);
            finish();

            return;
        }

        /*
         * NORMAL REGISTRATION FLOW
         *
         * Government ID → Address
         */
        Intent intent = new Intent(
                GovernmentIdActivity.this,
                AddressActivity.class
        );

        intent.putExtra("kyc", request);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        /*
         * When editing from ReviewActivity, simply return
         * to ReviewActivity instead of going back through
         * the entire registration flow.
         */
        if (editMode) {
            finish();
            return;
        }

        super.onBackPressed();
    }
}
