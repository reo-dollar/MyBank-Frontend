package com.rohit.mybank.activities.kyc;

import android.app.ProgressDialog;
import android.content.Intent;
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

    private TextView tvReview;
    private Button btnSubmit;

    private KycRequest request;

    private CustomerRepository repository;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initializeViews();

        request = (KycRequest) getIntent().getSerializableExtra("kyc");

        if (request == null) {
            Toast.makeText(this, "KYC data not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        repository = new CustomerRepository(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Submitting KYC...");

        displayReview();

        btnSubmit.setOnClickListener(v -> submitKyc());
    }

    private void initializeViews() {

        tvReview = findViewById(R.id.tvReview);
        btnSubmit = findViewById(R.id.btnSubmit);

    }

    private void displayReview() {

        StringBuilder review = new StringBuilder();

        review.append("========== PERSONAL DETAILS ==========\n\n");

        review.append("First Name : ")
                .append(request.getFirstName())
                .append("\n");

        review.append("Middle Name : ")
                .append(request.getMiddleName())
                .append("\n");

        review.append("Last Name : ")
                .append(request.getLastName())
                .append("\n");

        review.append("Date Of Birth : ")
                .append(request.getDateOfBirth())
                .append("\n");

        review.append("Gender : ")
                .append(request.getGender())
                .append("\n\n");

        review.append("========== CONTACT DETAILS ==========\n\n");

        review.append("Mobile : ")
                .append(request.getMobile())
                .append("\n");

        review.append("Email : ")
                .append(request.getEmail())
                .append("\n\n");

        review.append("========== GOVERNMENT DETAILS ==========\n\n");

        review.append("Aadhaar Number : ")
                .append(request.getAadhaarNumber())
                .append("\n");

        review.append("PAN Number : ")
                .append(request.getPanNumber())
                .append("\n\n");

        review.append("========== ADDRESS ==========\n\n");

        review.append("Address : ")
                .append(request.getAddress())
                .append("\n");

        review.append("City : ")
                .append(request.getCity())
                .append("\n");

        review.append("State : ")
                .append(request.getState())
                .append("\n");

        review.append("Pincode : ")
                .append(request.getPincode())
                .append("\n\n");

        review.append("========== EMPLOYMENT ==========\n\n");

        review.append("Occupation : ")
                .append(request.getOccupation())
                .append("\n");

        review.append("Account Type : ")
                .append(request.getAccountType());

        tvReview.setText(review.toString());
    }

    private void submitKyc() {

        Log.d(TAG, "========== KYC REQUEST ==========");
        Log.d(TAG, request.toString());
        Log.d(TAG, "Password : " + request.getPassword());

        progressDialog.show();

        repository.registerCustomer(request, new Callback<KycResponse>() {

            @Override
            public void onResponse(Call<KycResponse> call,
                                   Response<KycResponse> response) {

                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {

                    Log.d(TAG, "Registration Successful");

                    KycResponse kycResponse = response.body();

                    if (kycResponse.isSuccess()) {

                        StringBuilder message = new StringBuilder();

                        message.append("🎉 Registration Successful\n\n");

                        message.append("Congratulations!\n\n");

                        message.append("Your bank account has been created successfully.\n\n");

                        message.append("Customer Name : ")
                                .append(kycResponse.getCustomerName())
                                .append("\n\n");

                        message.append("Customer ID : ")
                                .append(kycResponse.getCustomerId())
                                .append("\n\n");

                        message.append("Account Number : ")
                                .append(kycResponse.getAccountNumber())
                                .append("\n\n");

                        message.append("Username : ")
                                .append(kycResponse.getUsername())
                                .append("\n\n");

                        message.append("Please save your username.\n");
                        message.append("You will need it to log in.");

                        new AlertDialog.Builder(ReviewActivity.this)
                                .setTitle("Success")
                                .setMessage(message.toString())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {

                                    Intent intent = new Intent(
                                            ReviewActivity.this,
                                            LoginActivity.class
                                    );

                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );

                                    startActivity(intent);
                                    finish();

                                })
                                .show();

                    } else {

                        Toast.makeText(
                                ReviewActivity.this,
                                kycResponse.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                } else {

                    String errorMessage = "Unknown Error";

                    try {

                        if (response.errorBody() != null) {

                            errorMessage = response.errorBody().string();

                        }

                    } catch (Exception e) {

                        errorMessage = e.getMessage();

                    }

                    Log.e(TAG, "HTTP Code : " + response.code());
                    Log.e(TAG, "Server Response : " + errorMessage);

                    Toast.makeText(
                            ReviewActivity.this,
                            "HTTP " + response.code() + "\n\n" + errorMessage,
                            Toast.LENGTH_LONG
                    ).show();

                }

            }

            @Override
            public void onFailure(Call<KycResponse> call,
                                  Throwable t) {

                progressDialog.dismiss();

                Log.e(TAG, "Network Error", t);

                Toast.makeText(
                        ReviewActivity.this,
                        "Network Error : " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

}