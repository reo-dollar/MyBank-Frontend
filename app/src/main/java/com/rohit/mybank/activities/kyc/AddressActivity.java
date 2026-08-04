package com.rohit.mybank.activities.kyc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

public class AddressActivity extends AppCompatActivity {

    private EditText etAddress;
    private EditText etCity;
    private EditText etState;
    private EditText etPincode;

    private Button btnNext;

    private KycRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        request = (KycRequest) getIntent().getSerializableExtra("kyc");

        initializeViews();

        btnNext.setOnClickListener(v -> validateAndContinue());
    }

    private void initializeViews() {

        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);

        btnNext = findViewById(R.id.btnNext);
    }

    private void validateAndContinue() {

        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();

        if (address.isEmpty()) {
            etAddress.setError("Address is required");
            return;
        }

        if (city.isEmpty()) {
            etCity.setError("City is required");
            return;
        }

        if (state.isEmpty()) {
            etState.setError("State is required");
            return;
        }

        if (!pincode.matches("\\d{6}")) {
            etPincode.setError("Enter valid 6-digit pincode");
            return;
        }

        request.setAddress(address);
        request.setCity(city);
        request.setState(state);
        request.setPincode(pincode);

        Intent intent = new Intent(
                AddressActivity.this,
                EmploymentActivity.class
        );

        intent.putExtra("kyc", request);

        startActivity(intent);
    }
}