package com.rohit.mybank.activities.pin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.model.pin.ApiResponse;
import com.rohit.mybank.model.pin.SetPinRequest;
import com.rohit.mybank.repository.PinRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetTransactionPinActivity extends AppCompatActivity {

    private static final String TAG = "SET_PIN";

    private TextInputEditText etPin;
    private TextInputEditText etConfirmPin;

    private MaterialButton btnSetPin;
    private ProgressBar progressBar;

    private PinRepository pinRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_transaction_pin);

        initializeViews();

        pinRepository = new PinRepository(this);

        btnSetPin.setOnClickListener(v -> setPin());
    }

    private void initializeViews() {

        etPin = findViewById(R.id.etPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);

        btnSetPin = findViewById(R.id.btnSetPin);

        progressBar = findViewById(R.id.progressBar);
    }

    private void setPin() {

        String pin = etPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        if (TextUtils.isEmpty(pin)) {
            etPin.setError("Enter Transaction PIN");
            etPin.requestFocus();
            return;
        }

        if (!pin.matches("\\d{6}")) {
            etPin.setError("Transaction PIN must be exactly 6 digits");
            etPin.requestFocus();
            return;
        }

        if (!pin.equals(confirmPin)) {
            etConfirmPin.setError("PIN does not match");
            etConfirmPin.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSetPin.setEnabled(false);

        SetPinRequest request = new SetPinRequest(pin);

        Log.d(TAG, "Sending Set PIN request...");

        pinRepository.setTransactionPin(request)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(Call<ApiResponse> call,
                                           Response<ApiResponse> response) {

                        progressBar.setVisibility(View.GONE);
                        btnSetPin.setEnabled(true);

                        Log.d(TAG, "HTTP Code : " + response.code());

                        if (response.body() != null) {
                            Log.d(TAG, "Response : " + response.body().getMessage());
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    SetTransactionPinActivity.this,
                                    response.body().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(
                                    SetTransactionPinActivity.this,
                                    DashboardActivity.class
                            );

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish();

                        } else {

                            String errorMessage = "Unknown Error";

                            try {

                                if (response.errorBody() != null) {
                                    errorMessage = response.errorBody().string();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.e(TAG, "Error : " + errorMessage);

                            Toast.makeText(
                                    SetTransactionPinActivity.this,
                                    "HTTP " + response.code() + "\n" + errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call,
                                          Throwable t) {

                        progressBar.setVisibility(View.GONE);
                        btnSetPin.setEnabled(true);

                        Log.e(TAG, "Network Failure", t);

                        Toast.makeText(
                                SetTransactionPinActivity.this,
                                "Network Error\n" + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}