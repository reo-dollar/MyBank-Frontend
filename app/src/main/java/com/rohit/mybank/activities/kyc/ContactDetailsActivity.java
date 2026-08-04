package com.rohit.mybank.activities.kyc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

public class ContactDetailsActivity extends AppCompatActivity {

    private EditText etMobile;
    private EditText etEmail;

    private Button btnNext;

    private KycRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        request = (KycRequest) getIntent().getSerializableExtra("kyc");

        initializeViews();

        btnNext.setOnClickListener(v -> validateAndContinue());

    }

    private void initializeViews() {

        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);

        btnNext = findViewById(R.id.btnNext);

    }

    private void validateAndContinue() {

        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (mobile.length() != 10) {

            etMobile.setError("Enter valid mobile number");
            return;

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter valid email");
            return;

        }

        request.setMobile(mobile);
        request.setEmail(email);

        Intent intent = new Intent(
                ContactDetailsActivity.this,
                GovernmentIdActivity.class
        );

        intent.putExtra("kyc", request);

        startActivity(intent);

    }

}