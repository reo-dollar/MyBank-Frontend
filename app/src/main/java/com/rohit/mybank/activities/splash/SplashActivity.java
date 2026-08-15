package com.rohit.mybank.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.auth.AuthenticationActivity;
import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000;

    private SessionManager sessionManager;

    // ==========================================================
    // Activity Lifecycle
    // ==========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(
                this::checkSession,
                SPLASH_TIME
        );
    }

    // ==========================================================
    // Check Session
    // ==========================================================

    private void checkSession() {

        // ======================================================
        // User is NOT logged in
        // ======================================================

        if (!sessionManager.isLoggedIn()) {

            goToLogin();

            return;
        }

        // ======================================================
        // User is already logged in
        //
        // IMPORTANT:
        // Never open Dashboard directly.
        //
        // Authentication is required before entering MyBank.
        // ======================================================

        goToAuthentication();
    }

    // ==========================================================
    // Go To Authentication
    // ==========================================================

    private void goToAuthentication() {

        Intent intent = new Intent(
                SplashActivity.this,
                AuthenticationActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // ==========================================================
    // Go To Login
    // ==========================================================

    private void goToLogin() {

        Intent intent = new Intent(
                SplashActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}