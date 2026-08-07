package com.rohit.mybank.api;

import android.content.Context;

import com.rohit.mybank.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(Context context) {

        sessionManager = new SessionManager(context);

    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        // Do not attach Authorization header for login or refresh requests
        String path = originalRequest.url().encodedPath();

        if (path.endsWith("/auth/login")
                || path.endsWith("/auth/register")
                || path.endsWith("/auth/refresh")) {

            return chain.proceed(originalRequest);

        }

        String accessToken = sessionManager.getToken();

        // If user is not logged in, continue without Authorization header
        if (accessToken == null || accessToken.trim().isEmpty()) {

            return chain.proceed(originalRequest);

        }

        Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();

        return chain.proceed(authenticatedRequest);

    }

}