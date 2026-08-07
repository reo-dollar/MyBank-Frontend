package com.rohit.mybank.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class SessionManager {

    private static final String PREF_NAME = "MyBank";

    // ===========================
    // Keys
    // ===========================

    private static final String KEY_TOKEN = "jwt_token";

    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private static final String KEY_USERNAME = "username";

    private static final String KEY_FINGERPRINT_ENABLED = "fingerprint_enabled";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {

        SharedPreferences tempPreferences;

        try {

            String masterKeyAlias = MasterKeys.getOrCreate(
                    MasterKeys.AES256_GCM_SPEC
            );

            tempPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (Exception e) {

            e.printStackTrace();

            tempPreferences = context.getSharedPreferences(
                    PREF_NAME,
                    Context.MODE_PRIVATE
            );

        }

        preferences = tempPreferences;
        editor = preferences.edit();

    }

    // ==========================================================
    // ACCESS TOKEN
    // ==========================================================

    public void saveToken(String token) {

        editor.putString(KEY_TOKEN, token);
        editor.apply();

    }

    public String getToken() {

        return preferences.getString(KEY_TOKEN, null);

    }

    public void clearToken() {

        editor.remove(KEY_TOKEN);
        editor.apply();

    }

    // ==========================================================
    // REFRESH TOKEN
    // ==========================================================

    public void saveRefreshToken(String refreshToken) {

        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();

    }

    public String getRefreshToken() {

        return preferences.getString(KEY_REFRESH_TOKEN, null);

    }

    public void clearRefreshToken() {

        editor.remove(KEY_REFRESH_TOKEN);
        editor.apply();

    }

    // ==========================================================
    // USERNAME
    // ==========================================================

    public void saveUsername(String username) {

        editor.putString(KEY_USERNAME, username);
        editor.apply();

    }

    public String getUsername() {

        return preferences.getString(KEY_USERNAME, "");

    }

    public void clearUsername() {

        editor.remove(KEY_USERNAME);
        editor.apply();

    }

    // ==========================================================
    // FINGERPRINT LOGIN
    // ==========================================================

    public void setFingerprintEnabled(boolean enabled) {

        editor.putBoolean(
                KEY_FINGERPRINT_ENABLED,
                enabled
        );

        editor.apply();

    }

    public boolean isFingerprintEnabled() {

        return preferences.getBoolean(
                KEY_FINGERPRINT_ENABLED,
                false
        );

    }

    // ==========================================================
    // LOGIN STATUS
    // ==========================================================

    public boolean isLoggedIn() {

        String token = getToken();

        return token != null && !token.isEmpty();

    }

    // ==========================================================
    // LOGOUT
    // ==========================================================

    public void logout() {

        clearToken();

        clearRefreshToken();

        clearUsername();

        setFingerprintEnabled(false);

    }

    public void clearSession() {

        logout();

    }

}