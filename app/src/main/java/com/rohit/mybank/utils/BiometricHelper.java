package com.rohit.mybank.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricHelper {

    public interface AuthenticationListener {
        void onAuthenticationSuccess();
        void onAuthenticationFailed(String message);
    }

    /**
     * Check biometric availability
     */
    public static boolean isBiometricAvailable(Context context) {

        BiometricManager biometricManager =
                BiometricManager.from(context);

        int result = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
        );

        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * Returns status message
     */
    public static String getBiometricStatus(Context context) {

        BiometricManager biometricManager =
                BiometricManager.from(context);

        switch (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG)) {

            case BiometricManager.BIOMETRIC_SUCCESS:
                return "Fingerprint Available";

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return "Fingerprint sensor not available";

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return "Fingerprint hardware unavailable";

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return "No fingerprint enrolled";

            default:
                return "Biometric authentication unavailable";
        }
    }

    /**
     * Show Fingerprint Dialog
     */
    public static void authenticate(
            FragmentActivity activity,
            AuthenticationListener listener
    ) {

        Executor executor =
                ContextCompat.getMainExecutor(activity);

        BiometricPrompt biometricPrompt =
                new BiometricPrompt(
                        activity,
                        executor,
                        new BiometricPrompt.AuthenticationCallback() {

                            @Override
                            public void onAuthenticationSucceeded(
                                    @NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);

                                listener.onAuthenticationSuccess();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();

                                listener.onAuthenticationFailed(
                                        "Fingerprint not recognized"
                                );
                            }

                            @Override
                            public void onAuthenticationError(
                                    int errorCode,
                                    @NonNull CharSequence errString) {

                                super.onAuthenticationError(
                                        errorCode,
                                        errString
                                );

                                listener.onAuthenticationFailed(
                                        errString.toString()
                                );
                            }
                        });

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Fingerprint Authentication")
                        .setSubtitle("Verify your identity")
                        .setDescription("Use your fingerprint to continue")
                        .setNegativeButtonText("Cancel")
                        .build();

        biometricPrompt.authenticate(promptInfo);
    }

}