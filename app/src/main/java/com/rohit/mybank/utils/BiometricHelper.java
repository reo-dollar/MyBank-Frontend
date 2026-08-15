package com.rohit.mybank.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricHelper {

    // ==========================================================
    // Authentication Listener
    // ==========================================================

    public interface AuthenticationListener {

        void onAuthenticationSuccess();

        void onAuthenticationFailed(String message);
    }

    // ==========================================================
    // Authenticators
    // ==========================================================

    private static final int BIOMETRIC_STRONG =
            BiometricManager.Authenticators.BIOMETRIC_STRONG;

    private static final int DEVICE_CREDENTIAL =
            BiometricManager.Authenticators.DEVICE_CREDENTIAL;

    private static final int BIOMETRIC_AND_DEVICE_CREDENTIAL =
            BIOMETRIC_STRONG | DEVICE_CREDENTIAL;

    // ==========================================================
    // BIOMETRIC AVAILABILITY
    // ==========================================================

    public static boolean isBiometricAvailable(
            Context context
    ) {

        BiometricManager biometricManager =
                BiometricManager.from(context);

        int result =
                biometricManager.canAuthenticate(
                        BIOMETRIC_STRONG
                );

        return result ==
                BiometricManager.BIOMETRIC_SUCCESS;
    }

    // ==========================================================
    // DEVICE CREDENTIAL AVAILABILITY
    //
    // PIN / Pattern / Password
    // ==========================================================

    public static boolean isDeviceCredentialAvailable(
            Context context
    ) {

        KeyguardManager keyguardManager =
                (KeyguardManager)
                        context.getSystemService(
                                Context.KEYGUARD_SERVICE
                        );

        if (keyguardManager == null) {
            return false;
        }

        return keyguardManager.isDeviceSecure();
    }

    // ==========================================================
    // ANY AUTHENTICATION AVAILABLE
    // ==========================================================

    public static boolean isAuthenticationAvailable(
            Context context
    ) {

        return isBiometricAvailable(context)
                || isDeviceCredentialAvailable(context);
    }

    // ==========================================================
    // BIOMETRIC STATUS
    // ==========================================================

    public static String getBiometricStatus(
            Context context
    ) {

        BiometricManager biometricManager =
                BiometricManager.from(context);

        int result =
                biometricManager.canAuthenticate(
                        BIOMETRIC_STRONG
                );

        switch (result) {

            case BiometricManager.BIOMETRIC_SUCCESS:
                return "Fingerprint / Face Available";

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return "Biometric hardware not available";

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return "Biometric hardware unavailable";

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return "No fingerprint / face enrolled";

            default:
                return "Biometric authentication unavailable";
        }
    }

    // ==========================================================
    // AUTHENTICATION STATUS
    // ==========================================================

    public static String getAuthenticationStatus(
            Context context
    ) {

        boolean biometric =
                isBiometricAvailable(context);

        boolean deviceCredential =
                isDeviceCredentialAvailable(context);

        if (biometric && deviceCredential) {
            return "Fingerprint / Face or Device Lock available";
        }

        if (biometric) {
            return "Fingerprint / Face available";
        }

        if (deviceCredential) {
            return "Device PIN / Pattern / Password available";
        }

        return "Set a device PIN, pattern, or password";
    }

    // ==========================================================
    // AUTHENTICATE USING BIOMETRIC ONLY
    //
    // Fingerprint / Face
    // ==========================================================

    public static void authenticateBiometric(
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
                                    @NonNull BiometricPrompt.AuthenticationResult result
                            ) {

                                super.onAuthenticationSucceeded(result);

                                listener.onAuthenticationSuccess();
                            }

                            @Override
                            public void onAuthenticationFailed() {

                                super.onAuthenticationFailed();

                                /*
                                 * IMPORTANT:
                                 *
                                 * Do NOT close the authentication flow
                                 * after a wrong fingerprint/face attempt.
                                 *
                                 * Android will keep the biometric prompt
                                 * available for another attempt.
                                 */
                            }

                            @Override
                            public void onAuthenticationError(
                                    int errorCode,
                                    @NonNull CharSequence errString
                            ) {

                                super.onAuthenticationError(
                                        errorCode,
                                        errString
                                );

                                listener.onAuthenticationFailed(
                                        errString.toString()
                                );
                            }
                        }
                );

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(
                                "Biometric Authentication"
                        )
                        .setSubtitle(
                                "Verify your identity to access MyBank"
                        )
                        .setDescription(
                                "Use your fingerprint or face to continue."
                        )
                        .setAllowedAuthenticators(
                                BIOMETRIC_STRONG
                        )
                        .setNegativeButtonText(
                                "Cancel"
                        )
                        .build();

        biometricPrompt.authenticate(
                promptInfo
        );
    }

    // ==========================================================
    // AUTHENTICATE USING DEVICE LOCK
    //
    // PIN / Pattern / Password
    // ==========================================================

    public static void authenticateDeviceCredential(
            FragmentActivity activity,
            AuthenticationListener listener
    ) {

        // ======================================================
        // Check Device Credential
        // ======================================================

        if (!isDeviceCredentialAvailable(activity)) {

            listener.onAuthenticationFailed(
                    "No device PIN, pattern, or password is configured."
            );

            return;
        }

        // ======================================================
        // Android 11+
        //
        // Use BiometricPrompt DEVICE_CREDENTIAL
        // ======================================================

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            Executor executor =
                    ContextCompat.getMainExecutor(activity);

            BiometricPrompt biometricPrompt =
                    new BiometricPrompt(
                            activity,
                            executor,
                            new BiometricPrompt.AuthenticationCallback() {

                                @Override
                                public void onAuthenticationSucceeded(
                                        @NonNull BiometricPrompt.AuthenticationResult result
                                ) {

                                    super.onAuthenticationSucceeded(result);

                                    listener.onAuthenticationSuccess();
                                }

                                @Override
                                public void onAuthenticationFailed() {

                                    super.onAuthenticationFailed();

                                    /*
                                     * Wrong credential.
                                     *
                                     * Keep the system authentication
                                     * prompt active.
                                     */
                                }

                                @Override
                                public void onAuthenticationError(
                                        int errorCode,
                                        @NonNull CharSequence errString
                                ) {

                                    super.onAuthenticationError(
                                            errorCode,
                                            errString
                                    );

                                    listener.onAuthenticationFailed(
                                            errString.toString()
                                    );
                                }
                            }
                    );

            BiometricPrompt.PromptInfo promptInfo =
                    new BiometricPrompt.PromptInfo.Builder()
                            .setTitle(
                                    "Device Lock Authentication"
                            )
                            .setSubtitle(
                                    "Unlock MyBank"
                            )
                            .setDescription(
                                    "Use your device PIN, pattern, or password."
                            )
                            .setAllowedAuthenticators(
                                    DEVICE_CREDENTIAL
                            )
                            .build();

            biometricPrompt.authenticate(
                    promptInfo
            );

            return;
        }

        // ======================================================
        // Android 10 / API 29
        //
        // Legacy device credential flow
        // ======================================================

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            KeyguardManager keyguardManager =
                    (KeyguardManager)
                            activity.getSystemService(
                                    Context.KEYGUARD_SERVICE
                            );

            if (keyguardManager == null) {

                listener.onAuthenticationFailed(
                        "Device lock is unavailable."
                );

                return;
            }

            if (!keyguardManager.isKeyguardSecure()) {

                listener.onAuthenticationFailed(
                        "Please set a device PIN, pattern, or password."
                );

                return;
            }

            Intent intent =
                    keyguardManager
                            .createConfirmDeviceCredentialIntent(
                                    "Device Lock",
                                    "Use your PIN, pattern, or password to access MyBank."
                            );

            if (intent == null) {

                listener.onAuthenticationFailed(
                        "Unable to open device authentication."
                );

                return;
            }

            activity.startActivityForResult(
                    intent,
                    1001
            );
        }
    }

    // ==========================================================
    // ORIGINAL AUTHENTICATE METHOD
    //
    // Kept for compatibility with existing MyBank modules
    // ==========================================================

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
                                    @NonNull BiometricPrompt.AuthenticationResult result
                            ) {

                                super.onAuthenticationSucceeded(result);

                                listener.onAuthenticationSuccess();
                            }

                            @Override
                            public void onAuthenticationFailed() {

                                super.onAuthenticationFailed();

                                // Allow another attempt.
                            }

                            @Override
                            public void onAuthenticationError(
                                    int errorCode,
                                    @NonNull CharSequence errString
                            ) {

                                super.onAuthenticationError(
                                        errorCode,
                                        errString
                                );

                                listener.onAuthenticationFailed(
                                        errString.toString()
                                );
                            }
                        }
                );

        BiometricPrompt.PromptInfo.Builder builder =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(
                                "Authentication Required"
                        )
                        .setSubtitle(
                                "Verify your identity to access MyBank"
                        )
                        .setDescription(
                                "Use fingerprint, face, or your device PIN."
                        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            builder.setAllowedAuthenticators(
                    BIOMETRIC_AND_DEVICE_CREDENTIAL
            );

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            builder.setDeviceCredentialAllowed(
                    true
            );

        } else {

            builder.setAllowedAuthenticators(
                    BIOMETRIC_STRONG
            );

            builder.setNegativeButtonText(
                    "Cancel"
            );
        }

        biometricPrompt.authenticate(
                builder.build()
        );
    }

    // ==========================================================
    // OPEN DEVICE SECURITY SETTINGS
    // ==========================================================

    public static void openDeviceSecuritySettings(
            Context context
    ) {

        Intent intent =
                new Intent(
                        Settings.ACTION_SECURITY_SETTINGS
                );

        context.startActivity(intent);
    }
}