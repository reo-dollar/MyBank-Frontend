package com.rohit.mybank.activities.kyc;

import android.content.Intent;
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

    private Spinner spOccupation;
    private Spinner spAccountType;

    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnReview;

    private KycRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employment);

        request = (KycRequest) getIntent().getSerializableExtra("kyc");

        if (request == null) {
            Toast.makeText(this, "KYC data not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();

        setupSpinners();

        btnReview.setOnClickListener(v -> validateAndContinue());
    }

    private void initializeViews() {

        spOccupation = findViewById(R.id.spOccupation);
        spAccountType = findViewById(R.id.spAccountType);

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnReview = findViewById(R.id.btnReview);
    }

    private void setupSpinners() {

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

    private void validateAndContinue() {

        String occupation =
                spOccupation.getSelectedItem().toString();

        String accountType =
                spAccountType.getSelectedItem().toString();

        String password =
                etPassword.getText().toString().trim();

        String confirmPassword =
                etConfirmPassword.getText().toString().trim();

        if (occupation.equals("Select Occupation")) {

            Toast.makeText(
                    this,
                    "Please select occupation",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (accountType.equals("Select Account Type")) {

            Toast.makeText(
                    this,
                    "Please select account type",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (password.isEmpty()) {

            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;

        }

        if (password.length() < 8) {

            etPassword.setError("Password must be at least 8 characters");
            etPassword.requestFocus();
            return;

        }

        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$")) {

            etPassword.setError(
                    "Password must contain uppercase, lowercase and a number"
            );

            etPassword.requestFocus();
            return;

        }

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.setError("Confirm password is required");
            etConfirmPassword.requestFocus();
            return;

        }

        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;

        }

        request.setOccupation(occupation);
        request.setAccountType(accountType);
        request.setPassword(password);

        Intent intent = new Intent(
                EmploymentActivity.this,
                ReviewActivity.class
        );

        intent.putExtra("kyc", request);

        startActivity(intent);

    }

}