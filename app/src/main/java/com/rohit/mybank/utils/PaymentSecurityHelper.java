package com.rohit.mybank.utils;

import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.session.SessionManager;

public class PaymentSecurityHelper {

    private final FragmentActivity activity;
    private final SessionManager sessionManager;
    private final PaymentCallback callback;

    private final ActivityResultLauncher<Intent> pinLauncher;

    public PaymentSecurityHelper(
            FragmentActivity activity,
            ActivityResultLauncher<Intent> pinLauncher,
            PaymentCallback callback
    ) {

        this.activity = activity;
        this.pinLauncher = pinLauncher;
        this.callback = callback;

        this.sessionManager =
                new SessionManager(activity);

    }
    // =====================================================
    // Start Payment Authentication
    // =====================================================

    public void verifyPayment() {

        // Fingerprint disabled
        if (!sessionManager.isFingerprintEnabled()) {

            openTransactionPin();

            return;

        }

        // Biometric not available
        if (!BiometricHelper.isBiometricAvailable(activity)) {

            Toast.makeText(
                    activity,
                    BiometricHelper.getBiometricStatus(activity),
                    Toast.LENGTH_LONG
            ).show();

            showAuthenticationOptions();

            return;

        }

        authenticateFingerprint();

    }

    // =====================================================
    // Fingerprint Authentication
    // =====================================================

    private void authenticateFingerprint() {

        BiometricHelper.authenticate(

                activity,

                new BiometricHelper.AuthenticationListener() {

                    @Override
                    public void onAuthenticationSuccess() {

                        performAuthenticatedAction();

                    }

                    @Override
                    public void onAuthenticationFailed(String message) {

                        Toast.makeText(
                                activity,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();

                        showAuthenticationOptions();

                    }

                }

        );

    }

    // =====================================================
    // Authentication Completed
    // =====================================================

    private void performAuthenticatedAction() {

        callback.onSuccess();

    }
    // =====================================================
    // Authentication Options
    // =====================================================

    private void showAuthenticationOptions() {

        AuthenticationDialog.show(

                activity,

                new AuthenticationDialog.AuthenticationOptionListener() {

                    @Override
                    public void onRetryFingerprint() {

                        authenticateFingerprint();

                    }

                    @Override
                    public void onUseTransactionPin() {

                        openTransactionPin();

                    }

                    @Override
                    public void onCancel() {

                        Toast.makeText(
                                activity,
                                "Payment cancelled.",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                }

        );

    }

    // =====================================================
    // Open Transaction PIN Screen
    // =====================================================

    private void openTransactionPin() {

        Intent intent =
                new Intent(
                        activity,
                        VerifyTransactionPinActivity.class
                );

        pinLauncher.launch(intent);

    }

}