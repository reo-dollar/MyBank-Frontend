package com.rohit.mybank;

import android.app.Application;

import com.rohit.mybank.security.AppLockManager;

/**
 * ============================================================
 * MyBank Application
 * ============================================================
 *
 * Application-level initialization.
 */
public class MyBankApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        /*
         * Initialize the application lock manager.
         *
         * This observes the lifecycle of the entire MyBank
         * application process.
         */
        AppLockManager.getInstance(this);
    }
}