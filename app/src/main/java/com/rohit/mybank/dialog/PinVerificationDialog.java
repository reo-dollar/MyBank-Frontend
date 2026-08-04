package com.rohit.mybank.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rohit.mybank.R;
import com.rohit.mybank.model.pin.VerifyPinRequest;
import com.rohit.mybank.model.pin.VerifyPinResponse;
import com.rohit.mybank.repository.PinRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PinVerificationDialog {

    public interface OnPinVerifiedListener {
        void onSuccess();
        void onFailure();
    }

    public static void show(
            Context context,
            OnPinVerifiedListener listener
    ) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_verify_pin, null);

        TextInputEditText etPin =
                view.findViewById(R.id.etPin);

        MaterialButton btnVerify =
                view.findViewById(R.id.btnVerify);

        ProgressBar progressBar =
                view.findViewById(R.id.progressBar);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        dialog.show();

        PinRepository repository =
                new PinRepository(context);

        btnVerify.setOnClickListener(v -> {

            String pin = etPin.getText().toString().trim();

            if (TextUtils.isEmpty(pin)) {

                etPin.setError("Enter Transaction PIN");
                return;
            }

            if (pin.length() != 6) {

                etPin.setError("PIN must be 6 digits");
                return;
            }

            VerifyPinRequest request =
                    new VerifyPinRequest();

            request.setPin(pin);

            progressBar.setVisibility(View.VISIBLE);
            btnVerify.setEnabled(false);

            repository.verifyTransactionPin(request)
                    .enqueue(new Callback<VerifyPinResponse>() {

                        @Override
                        public void onResponse(
                                Call<VerifyPinResponse> call,
                                Response<VerifyPinResponse> response) {

                            progressBar.setVisibility(View.GONE);
                            btnVerify.setEnabled(true);

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().isSuccess()) {

                                dialog.dismiss();

                                listener.onSuccess();

                            } else {

                                Toast.makeText(
                                        context,
                                        "Invalid Transaction PIN",
                                        Toast.LENGTH_SHORT
                                ).show();

                                listener.onFailure();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<VerifyPinResponse> call,
                                Throwable t) {

                            progressBar.setVisibility(View.GONE);
                            btnVerify.setEnabled(true);

                            Toast.makeText(
                                    context,
                                    "Network Error : " + t.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            listener.onFailure();
                        }
                    });

        });

    }

}