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


/**
 * =========================================================
 * TRANSACTION PIN VERIFICATION DIALOG
 * =========================================================
 *
 * Responsibilities:
 *
 * 1. Show Transaction PIN dialog
 * 2. Accept exactly 6 digits
 * 3. Verify PIN with backend
 * 4. Keep dialog open when PIN is invalid
 * 5. Allow Android Back to close the dialog
 * 6. Return verified PIN to calling activity
 *
 * =========================================================
 */
public class PinVerificationDialog {


    // =========================================================
    // CALLBACK - WITHOUT PIN
    // =========================================================

    public interface OnPinVerifiedListener {

        void onSuccess();

        void onFailure();
    }


    // =========================================================
    // CALLBACK - WITH VERIFIED PIN
    // =========================================================

    public interface OnPinVerifiedWithPinListener {

        void onSuccess(String transactionPin);

        void onFailure();
    }


    // =========================================================
    // SIMPLE SHOW METHOD
    // =========================================================

    public static void show(
            Context context,
            OnPinVerifiedListener listener
    ) {

        if (context == null || listener == null) {
            return;
        }


        showInternal(
                context,
                new InternalListener() {

                    @Override
                    public void onSuccess(
                            String transactionPin
                    ) {

                        listener.onSuccess();
                    }


                    @Override
                    public void onFailure() {

                        listener.onFailure();
                    }
                }
        );
    }


    // =========================================================
    // TRANSACTION PIN METHOD
    // =========================================================

    public static void showForTransactionPin(
            Context context,
            OnPinVerifiedWithPinListener listener
    ) {

        if (context == null || listener == null) {
            return;
        }


        showInternal(
                context,
                new InternalListener() {

                    @Override
                    public void onSuccess(
                            String transactionPin
                    ) {

                        listener.onSuccess(
                                transactionPin
                        );
                    }


                    @Override
                    public void onFailure() {

                        listener.onFailure();
                    }
                }
        );
    }


    // =========================================================
    // INTERNAL CALLBACK
    // =========================================================

    private interface InternalListener {

        void onSuccess(
                String transactionPin
        );

        void onFailure();
    }


    // =========================================================
    // INTERNAL DIALOG
    // =========================================================

    private static void showInternal(
            Context context,
            InternalListener listener
    ) {


        // =====================================================
        // VALIDATION
        // =====================================================

        if (context == null || listener == null) {
            return;
        }


        // =====================================================
        // INFLATE DIALOG LAYOUT
        // =====================================================

        View view =
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.dialog_verify_pin,
                                null
                        );


        // =====================================================
        // FIND VIEWS
        // =====================================================

        TextInputEditText etPin =
                view.findViewById(
                        R.id.etPin
                );


        MaterialButton btnVerify =
                view.findViewById(
                        R.id.btnVerify
                );


        ProgressBar progressBar =
                view.findViewById(
                        R.id.progressBar
                );


        // =====================================================
        // SAFETY CHECK
        // =====================================================

        if (etPin == null
                || btnVerify == null
                || progressBar == null) {

            Toast.makeText(
                    context,
                    "Transaction PIN dialog configuration error.",
                    Toast.LENGTH_LONG
            ).show();

            listener.onFailure();

            return;
        }


        // =====================================================
        // DIALOG
        // =====================================================

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setView(view)

                        // =================================================
                        // IMPORTANT FIX
                        // =================================================
                        //
                        // OLD:
                        //
                        // .setCancelable(false)
                        //
                        // This prevented Android Back from closing
                        // the dialog.
                        //
                        // NEW:
                        //
                        .setCancelable(true)

                        .create();


        // =====================================================
        // OUTSIDE TOUCH
        // =====================================================
        //
        // Do NOT close the dialog when the user accidentally
        // taps outside it.
        //
        // Android Back still works.
        //
        // =====================================================

        dialog.setCanceledOnTouchOutside(
                false
        );


        // =====================================================
        // CANCEL / BACK HANDLING
        // =====================================================

        dialog.setOnCancelListener(
                dialogInterface -> {

                    // ---------------------------------------------
                    // Tell calling activity that PIN authentication
                    // was cancelled.
                    // ---------------------------------------------

                    listener.onFailure();
                }
        );


        // =====================================================
        // SHOW DIALOG
        // =====================================================

        dialog.show();


        // =====================================================
        // PIN REPOSITORY
        // =====================================================

        PinRepository repository =
                new PinRepository(context);


        // =====================================================
        // VERIFY BUTTON
        // =====================================================

        btnVerify.setOnClickListener(
                v -> {


                    // =================================================
                    // CLEAR PREVIOUS ERROR
                    // =================================================

                    etPin.setError(null);


                    // =================================================
                    // READ PIN
                    // =================================================

                    String enteredPin = "";


                    if (etPin.getText() != null) {

                        enteredPin =
                                etPin.getText()
                                        .toString()
                                        .trim();
                    }


                    // =================================================
                    // EMPTY PIN
                    // =================================================

                    if (TextUtils.isEmpty(
                            enteredPin
                    )) {

                        etPin.setError(
                                "Enter Transaction PIN"
                        );

                        etPin.requestFocus();

                        return;
                    }


                    // =================================================
                    // PIN FORMAT
                    // =================================================

                    if (!enteredPin.matches(
                            "\\d{6}"
                    )) {

                        etPin.setError(
                                "PIN must be exactly 6 digits"
                        );

                        etPin.requestFocus();

                        return;
                    }


                    // =================================================
                    // FINAL PIN
                    // =================================================

                    final String verifiedPin =
                            enteredPin;


                    // =================================================
                    // CREATE REQUEST
                    // =================================================

                    VerifyPinRequest request =
                            new VerifyPinRequest();

                    request.setPin(
                            verifiedPin
                    );


                    // =================================================
                    // SHOW LOADING
                    // =================================================

                    progressBar.setVisibility(
                            View.VISIBLE
                    );


                    // =================================================
                    // DISABLE CONTROLS
                    // =================================================

                    btnVerify.setEnabled(
                            false
                    );

                    etPin.setEnabled(
                            false
                    );


                    // =================================================
                    // VERIFY PIN THROUGH BACKEND
                    // =================================================

                    repository
                            .verifyTransactionPin(
                                    request
                            )
                            .enqueue(
                                    new Callback<VerifyPinResponse>() {


                                        // =================================
                                        // SERVER RESPONSE
                                        // =================================

                                        @Override
                                        public void onResponse(
                                                Call<VerifyPinResponse> call,
                                                Response<VerifyPinResponse> response
                                        ) {


                                            // =================================
                                            // STOP LOADING
                                            // =================================

                                            progressBar.setVisibility(
                                                    View.GONE
                                            );


                                            // =================================
                                            // RE-ENABLE CONTROLS
                                            // =================================

                                            btnVerify.setEnabled(
                                                    true
                                            );

                                            etPin.setEnabled(
                                                    true
                                            );


                                            // =================================
                                            // HTTP ERROR
                                            // =================================

                                            if (!response.isSuccessful()) {

                                                Toast.makeText(
                                                        context,
                                                        "Unable to verify Transaction PIN. "
                                                                + "Server Error: "
                                                                + response.code(),
                                                        Toast.LENGTH_LONG
                                                ).show();

                                                return;
                                            }


                                            // =================================
                                            // EMPTY RESPONSE
                                            // =================================

                                            if (response.body() == null) {

                                                Toast.makeText(
                                                        context,
                                                        "Empty server response.",
                                                        Toast.LENGTH_LONG
                                                ).show();

                                                return;
                                            }


                                            // =================================
                                            // RESPONSE
                                            // =================================

                                            VerifyPinResponse verifyResponse =
                                                    response.body();


                                            // =================================
                                            // SUCCESS
                                            // =================================

                                            if (verifyResponse.isSuccess()) {


                                                // ---------------------------------
                                                // CLOSE DIALOG
                                                // ---------------------------------

                                                if (dialog.isShowing()) {

                                                    dialog.dismiss();
                                                }


                                                // ---------------------------------
                                                // RETURN VERIFIED PIN
                                                // ---------------------------------

                                                listener.onSuccess(
                                                        verifiedPin
                                                );

                                                return;
                                            }


                                            // =================================
                                            // INVALID PIN
                                            // =================================

                                            String message =
                                                    verifyResponse.getMessage();


                                            if (TextUtils.isEmpty(
                                                    message
                                            )) {

                                                message =
                                                        "Invalid Transaction PIN.";
                                            }


                                            // =================================
                                            // SHOW ERROR
                                            // =================================

                                            etPin.setError(
                                                    message
                                            );


                                            etPin.requestFocus();


                                            // =================================
                                            // CLEAR OLD PIN
                                            // =================================

                                            etPin.setText("");


                                            // =================================
                                            // KEEP DIALOG OPEN
                                            // =================================
                                        }


                                        // =================================
                                        // NETWORK FAILURE
                                        // =================================

                                        @Override
                                        public void onFailure(
                                                Call<VerifyPinResponse> call,
                                                Throwable t
                                        ) {


                                            // =================================
                                            // STOP LOADING
                                            // =================================

                                            progressBar.setVisibility(
                                                    View.GONE
                                            );


                                            // =================================
                                            // RE-ENABLE CONTROLS
                                            // =================================

                                            btnVerify.setEnabled(
                                                    true
                                            );

                                            etPin.setEnabled(
                                                    true
                                            );


                                            // =================================
                                            // ERROR MESSAGE
                                            // =================================

                                            String errorMessage =
                                                    "Unable to connect to the server.";


                                            if (t != null
                                                    && !TextUtils.isEmpty(
                                                    t.getMessage()
                                            )) {

                                                errorMessage =
                                                        "Network Error: "
                                                                + t.getMessage();
                                            }


                                            Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    }
                            );
                }
        );
    }
}