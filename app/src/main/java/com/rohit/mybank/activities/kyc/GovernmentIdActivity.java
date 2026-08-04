package com.rohit.mybank.activities.kyc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

public class GovernmentIdActivity extends AppCompatActivity {

    private EditText etAadhaar;
    private EditText etPan;

    private Button btnNext;

    private KycRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_government_id);

        request = (KycRequest) getIntent().getSerializableExtra("kyc");

        initializeViews();

        btnNext.setOnClickListener(v -> validateAndContinue());

    }

    private void initializeViews() {

        etAadhaar = findViewById(R.id.etAadhaar);
        etPan = findViewById(R.id.etPan);

        btnNext = findViewById(R.id.btnNext);

    }

    private void validateAndContinue() {

        String aadhaar = etAadhaar.getText().toString().trim();
        String pan = etPan.getText().toString().trim().toUpperCase();

        if (!aadhaar.matches("\\d{12}")) {
            etAadhaar.setError("Enter valid 12-digit Aadhaar");
            return;
        }

        if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
            etPan.setError("Enter valid PAN Number");
            return;
        }

        request.setAadhaarNumber(aadhaar);
        request.setPanNumber(pan);

        Intent intent = new Intent(
                GovernmentIdActivity.this,
                AddressActivity.class
        );

        intent.putExtra("kyc", request);

        startActivity(intent);

    }
}