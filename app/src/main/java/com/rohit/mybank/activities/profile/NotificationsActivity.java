package com.rohit.mybank.activities.profile;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.rohit.mybank.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "MyBankFCM";

    /*
     * Android 13+ notification permission request code.
     */
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    /*
     * Notification channel used by MyBank.
     */
    private static final String NOTIFICATION_CHANNEL_ID =
            "mybank_notifications";

    private static final String NOTIFICATION_CHANNEL_NAME =
            "MyBank Notifications";

    private static final String NOTIFICATION_CHANNEL_DESCRIPTION =
            "Account, transaction, payment, loan and security notifications";

    private ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * ============================================================
         * Initialize ViewBinding
         * ============================================================
         */

        binding = ActivityNotificationsBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        /*
         * ============================================================
         * Initialize Notification System
         * ============================================================
         */

        createNotificationChannel();

        requestNotificationPermission();

        getFirebaseMessagingToken();

        /*
         * ============================================================
         * Initialize UI
         * ============================================================
         */

        initializeUI();

        /*
         * ============================================================
         * Initialize Click Listeners
         * ============================================================
         */

        initializeClickListeners();
    }

    /**
     * ============================================================
     * Initialize UI
     * ============================================================
     */
    private void initializeUI() {

        /*
         * Master notification switch.
         *
         * Current behavior is retained.
         */
        binding.switchAllNotifications.setChecked(true);

        /*
         * Individual notification categories.
         */
        binding.switchTransactionAlerts.setChecked(true);
        binding.switchPaymentAlerts.setChecked(true);
        binding.switchLoanAlerts.setChecked(true);
        binding.switchSecurityAlerts.setChecked(true);
        binding.switchPromotionalAlerts.setChecked(false);

        updateNotificationStatus();
    }

    /**
     * ============================================================
     * Request Android Notification Permission
     * ============================================================
     *
     * Android 13 / API 33 and above requires the user to grant
     * POST_NOTIFICATIONS at runtime.
     *
     * The permission is already declared in AndroidManifest.xml.
     */
    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    /**
     * ============================================================
     * Handle Notification Permission Result
     * ============================================================
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode
                == NOTIFICATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        this,
                        "Notifications enabled",
                        Toast.LENGTH_SHORT
                ).show();

                Log.d(
                        TAG,
                        "POST_NOTIFICATIONS permission granted"
                );

            } else {

                Toast.makeText(
                        this,
                        "Notification permission denied",
                        Toast.LENGTH_LONG
                ).show();

                Log.w(
                        TAG,
                        "POST_NOTIFICATIONS permission denied"
                );
            }
        }
    }

    /**
     * ============================================================
     * Create Notification Channel
     * ============================================================
     *
     * Android 8.0 / API 26 and above requires notifications
     * to belong to a notification channel.
     */
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                    );

            channel.setDescription(
                    NOTIFICATION_CHANNEL_DESCRIPTION
            );

            NotificationManager notificationManager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(
                        channel
                );
            }
        }
    }

    /**
     * ============================================================
     * Get Firebase Cloud Messaging Token
     * ============================================================
     *
     * This token represents this installation of MyBank.
     *
     * Later:
     *
     * Android
     *      ↓
     * FCM token
     *      ↓
     * Spring Boot backend
     *      ↓
     * Firebase Cloud Messaging
     *      ↓
     * This device
     */
    private void getFirebaseMessagingToken() {

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.e(
                                TAG,
                                "Failed to obtain FCM token",
                                task.getException()
                        );

                        return;
                    }

                    String token = task.getResult();

                    if (token == null || token.trim().isEmpty()) {

                        Log.e(
                                TAG,
                                "FCM token is empty"
                        );

                        return;
                    }

                    /*
                     * For development/testing only.
                     *
                     * We will NOT display this token permanently
                     * in the application UI.
                     */
                    Log.d(
                            TAG,
                            "FCM TOKEN: " + token
                    );

                    Toast.makeText(
                            NotificationsActivity.this,
                            "Notification service connected",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    /**
     * ============================================================
     * Initialize Click Listeners
     * ============================================================
     */
    private void initializeClickListeners() {

        /*
         * Back button.
         */
        binding.btnBack.setOnClickListener(v ->
                finish()
        );

        /*
         * Master notification switch.
         */
        binding.switchAllNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    /*
                     * Enable/disable normal notification categories.
                     *
                     * Security alerts remain enabled.
                     */
                    binding.switchTransactionAlerts.setChecked(
                            isChecked
                    );

                    binding.switchPaymentAlerts.setChecked(
                            isChecked
                    );

                    binding.switchLoanAlerts.setChecked(
                            isChecked
                    );

                    binding.switchPromotionalAlerts.setChecked(
                            false
                    );

                    updateNotificationStatus();

                    Toast.makeText(
                            NotificationsActivity.this,
                            isChecked
                                    ? "Notifications enabled"
                                    : "Notifications disabled",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        /*
         * Transaction alerts.
         */
        binding.switchTransactionAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    updateMasterSwitchState();
                }
        );

        /*
         * Payment alerts.
         */
        binding.switchPaymentAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    updateMasterSwitchState();
                }
        );

        /*
         * Loan / EMI alerts.
         */
        binding.switchLoanAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    updateMasterSwitchState();
                }
        );

        /*
         * Promotional alerts.
         */
        binding.switchPromotionalAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    /*
                     * Promotional alerts remain independently
                     * controllable.
                     */
                }
        );

        /*
         * Security alerts cannot be disabled.
         */
        binding.switchSecurityAlerts.setOnClickListener(v -> {

            binding.switchSecurityAlerts.setChecked(true);

            Toast.makeText(
                    NotificationsActivity.this,
                    "Security alerts cannot be disabled",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    /**
     * ============================================================
     * Update Notification Status
     * ============================================================
     */
    private void updateNotificationStatus() {

        if (binding.switchAllNotifications.isChecked()) {

            binding.tvNotificationStatus.setText(
                    "You will receive account and service notifications"
            );

        } else {

            binding.tvNotificationStatus.setText(
                    "Normal notifications are disabled"
            );
        }
    }

    /**
     * ============================================================
     * Update Master Switch
     * ============================================================
     */
    private void updateMasterSwitchState() {

        boolean transaction =
                binding.switchTransactionAlerts.isChecked();

        boolean payment =
                binding.switchPaymentAlerts.isChecked();

        boolean loan =
                binding.switchLoanAlerts.isChecked();

        boolean allEnabled =
                transaction
                        && payment
                        && loan;

        /*
         * Avoid triggering the master switch listener unnecessarily.
         */
        if (binding.switchAllNotifications.isChecked()
                != allEnabled) {

            binding.switchAllNotifications.setChecked(
                    allEnabled
            );
        }

        updateNotificationStatus();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        binding = null;
    }
}