package com.rohit.mybank.security;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.rohit.mybank.activities.auth.AuthenticationActivity;
import com.rohit.mybank.session.SessionManager;

/**
 * ============================================================
 * MyBank App Lock Manager
 * ============================================================
 *
 * Locks MyBank whenever the entire application goes to the
 * background.
 *
 * When the user returns to MyBank:
 *
 *      AuthenticationActivity
 *              ↓
 *      Fingerprint / Face
 *              ↓ Cancel
 *      Device PIN / Pattern / Password
 *              ↓
 *      Dashboard
 *
 * IMPORTANT:
 * This does NOT authenticate every time an Activity changes.
 *
 * Example:
 *
 * Dashboard → Deposit → Back
 *
 * will NOT ask for authentication.
 *
 * But:
 *
 * Dashboard → Home → MyBank
 *
 * WILL ask for authentication.
 */
public class AppLockManager
        implements DefaultLifecycleObserver {

    private static AppLockManager instance;

    private final Application application;
    private final SessionManager sessionManager;

    private boolean appInBackground = false;
    private boolean authenticationScreenOpen = false;

    private AppLockManager(Application application) {

        this.application = application;

        sessionManager =
                new SessionManager(application);

        ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .addObserver(this);
    }

    /**
     * Get singleton instance.
     */
    public static synchronized AppLockManager getInstance(
            Application application
    ) {

        if (instance == null) {

            instance =
                    new AppLockManager(application);
        }

        return instance;
    }

    // =========================================================
    // APP ENTERED FOREGROUND
    // =========================================================

    @Override
    public void onStart(
            @NonNull LifecycleOwner owner
    ) {

        /*
         * Ignore the very first application startup.
         *
         * Login / Splash / Authentication flow handles that.
         */
        if (!appInBackground) {
            return;
        }

        appInBackground = false;

        /*
         * User was already logged in before leaving MyBank.
         *
         * Therefore authentication is required again.
         */
        if (!sessionManager.isLoggedIn()) {
            return;
        }

        if (authenticationScreenOpen) {
            return;
        }

        openAuthenticationScreen();
    }

    // =========================================================
    // APP ENTERED BACKGROUND
    // =========================================================

    @Override
    public void onStop(
            @NonNull LifecycleOwner owner
    ) {

        /*
         * Mark the application as being in background.
         */
        appInBackground = true;
    }

    // =========================================================
    // OPEN AUTHENTICATION SCREEN
    // =========================================================

    private void openAuthenticationScreen() {

        authenticationScreenOpen = true;

        Intent intent =
                new Intent(
                        application,
                        AuthenticationActivity.class
                );

        /*
         * Clear the currently visible Activity stack.
         *
         * This prevents the user from simply pressing Back
         * and returning directly to Dashboard.
         */
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        application.startActivity(intent);
    }

    // =========================================================
    // AUTHENTICATION COMPLETED
    // =========================================================

    public void authenticationCompleted() {

        authenticationScreenOpen = false;

        /*
         * MyBank is currently active and authenticated.
         */
        appInBackground = false;
    }

    // =========================================================
    // AUTHENTICATION SCREEN CLOSED
    // =========================================================

    public void authenticationScreenClosed() {

        authenticationScreenOpen = false;
    }

    // =========================================================
    // MANUAL LOCK
    // =========================================================

    public void lockNow() {

        if (!sessionManager.isLoggedIn()) {
            return;
        }

        appInBackground = true;

        if (!authenticationScreenOpen) {
            openAuthenticationScreen();
        }
    }

    // =========================================================
    // CHECK STATUS
    // =========================================================

    public boolean isAuthenticationScreenOpen() {

        return authenticationScreenOpen;
    }
}