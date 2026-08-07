package com.rohit.mybank.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.activities.dashboard.DashboardActivity;
import com.rohit.mybank.session.SessionManager;
import com.rohit.mybank.utils.BiometricHelper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // User is not logged in
            if (!sessionManager.isLoggedIn()) {

                startActivity(new Intent(
                        SplashActivity.this,
                        LoginActivity.class));

                finish();
                return;
            }

            // Logged in but fingerprint disabled
            if (!sessionManager.isFingerprintEnabled()) {

                startActivity(new Intent(
                        SplashActivity.this,
                        DashboardActivity.class));

                finish();
                return;
            }

            // Fingerprint enabled
            if (!BiometricHelper.isBiometricAvailable(this)) {

                Toast.makeText(
                        this,
                        BiometricHelper.getBiometricStatus(this),
                        Toast.LENGTH_LONG
                ).show();

                startActivity(new Intent(
                        SplashActivity.this,
                        LoginActivity.class));

                finish();
                return;
            }

            // Show fingerprint dialog
            BiometricHelper.authenticate(
                    this,
                    new BiometricHelper.AuthenticationListener() {

                        @Override
                        public void onAuthenticationSuccess() {

                            startActivity(new Intent(
                                    SplashActivity.this,
                                    DashboardActivity.class));

                            finish();

                        }

                        @Override
                        public void onAuthenticationFailed(String message) {

                            Toast.makeText(
                                    SplashActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(new Intent(
                                    SplashActivity.this,
                                    LoginActivity.class));

                            finish();

                        }
                    });

        }, SPLASH_TIME);

    }
}