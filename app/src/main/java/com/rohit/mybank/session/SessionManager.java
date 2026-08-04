package com.rohit.mybank.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "MyBank";

    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USERNAME = "username";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        editor = preferences.edit();

    }

    // Save JWT Token
    public void saveToken(String token) {

        editor.putString(KEY_TOKEN, token);
        editor.apply();

    }

    // Get JWT Token
    public String getToken() {

        return preferences.getString(KEY_TOKEN, null);

    }

    // Save Username
    public void saveUsername(String username) {

        editor.putString(KEY_USERNAME, username);
        editor.apply();

    }

    // Get Username
    public String getUsername() {

        return preferences.getString(KEY_USERNAME, "");

    }

    // Check Login Status
    public boolean isLoggedIn() {

        return getToken() != null && !getToken().isEmpty();

    }

    // Logout
    public void logout() {

        editor.clear();
        editor.apply();

    }

    // Alternative method for compatibility
    public void clearSession() {

        logout();

    }

}