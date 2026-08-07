package com.rohit.mybank.utils;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

public class AuthenticationDialog {

    public interface AuthenticationOptionListener {

        void onRetryFingerprint();

        void onUseTransactionPin();

        void onCancel();

    }

    public static void show(
            FragmentActivity activity,
            AuthenticationOptionListener listener
    ) {

        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(activity)

                .setTitle("Authentication Required")

                .setMessage(
                        "We couldn't verify your fingerprint.\n\n" +
                                "Choose how you want to continue."
                )

                .setCancelable(false)

                .setPositiveButton(
                        "Try Again",
                        (dialog, which) -> {

                            dialog.dismiss();

                            listener.onRetryFingerprint();

                        })

                .setNeutralButton(
                        "Use Transaction PIN",
                        (dialog, which) -> {

                            dialog.dismiss();

                            listener.onUseTransactionPin();

                        })

                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> {

                            dialog.dismiss();

                            listener.onCancel();

                        })

                .show();

    }

}