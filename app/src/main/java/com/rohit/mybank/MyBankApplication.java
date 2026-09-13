package com.rohit.mybank;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.rohit.mybank.security.AppLockManager;

/**
 * ============================================================
 * MyBank Application
 * ============================================================
 *
 * Application-level initialization.
 *
 * Responsibilities:
 *
 * 1. Restore the user's saved Dark Mode preference.
 * 2. Apply the selected AppCompat night mode before
 *    activities are displayed.
 * 3. Initialize the application-wide AppLockManager.
 */
public class MyBankApplication extends Application {

    /*
     * SharedPreferences used for application settings.
     */
    private static final String PREFS_NAME =
            "MyBankPreferences";

    /*
     * Preference key for Dark Mode.
     */
    private static final String KEY_DARK_MODE =
            "dark_mode";

    @Override
    public void onCreate() {

        super.onCreate();

        /*
         * =====================================================
         * Restore Dark Mode
         * =====================================================
         *
         * Read the user's saved preference.
         *
         * false = Light Mode
         * true  = Dark Mode
         */
        boolean darkModeEnabled =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                ).getBoolean(
                        KEY_DARK_MODE,
                        false
                );

        /*
         * Apply the saved application theme.
         *
         * AppCompatDelegate automatically handles the
         * DayNight configuration and recreates activities
         * when the mode changes.
         */
        if (darkModeEnabled) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        /*
         * =====================================================
         * Initialize Application Lock Manager
         * =====================================================
         *
         * This observes the lifecycle of the entire
         * MyBank application process.
         */
        AppLockManager.getInstance(this);
    }
}