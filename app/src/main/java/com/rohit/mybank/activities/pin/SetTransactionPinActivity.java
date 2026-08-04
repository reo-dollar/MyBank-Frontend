package com.rohit.mybank.activities.pin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetTransactionPinActivity extends AppCompatActivity {

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
        String confirm = etConfirmPin.getText().toString().trim();

        if (TextUtils.isEmpty(pin)) {
            etPin.setError("Enter Transaction PIN");
            return;
        }

        if (!pin.matches("\\d{6}")) {
            etPin.setError("PIN must be exactly 6 digits");
            return;
        }

        if (!pin.equals(confirm)) {
            etConfirmPin.setError("PIN does not match");
            return;
        }

        SetPinRequest request = new SetPinRequest();
        request.setPin(pin);

        progressBar.setVisibility(View.VISIBLE);
        btnSetPin.setEnabled(false);

        pinRepository.setTransactionPin(request)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(Call<ApiResponse> call,
                                           Response<ApiResponse> response) {

                        progressBar.setVisibility(View.GONE);
                        btnSetPin.setEnabled(true);

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

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    SetTransactionPinActivity.this,
                                    "Unable to set PIN",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call,
                                          Throwable t) {

                        progressBar.setVisibility(View.GONE);
                        btnSetPin.setEnabled(true);

                        Toast.makeText(
                                SetTransactionPinActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}