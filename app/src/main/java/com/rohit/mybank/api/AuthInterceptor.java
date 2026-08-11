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

        String path = originalRequest.url().encodedPath();

        /*
         * Public authentication endpoints.
         *
         * These endpoints must NOT receive an Authorization header
         * because the user is not authenticated yet or is refreshing
         * the authentication session.
         */
        if (path.endsWith("/auth/login")
                || path.endsWith("/auth/register")
                || path.endsWith("/auth/refresh")) {

            return chain.proceed(originalRequest);
        }

        /*
         * Get the currently stored JWT access token.
         */
        String accessToken = sessionManager.getToken();

        /*
         * If the user is not authenticated, continue without
         * an Authorization header.
         */
        if (accessToken == null
                || accessToken.trim().isEmpty()) {

            return chain.proceed(originalRequest);
        }

        /*
         * Add JWT Authorization header.
         */
        Request authenticatedRequest =
                originalRequest.newBuilder()
                        .header(
                                "Authorization",
                                "Bearer " + accessToken
                        )
                        .build();

        return chain.proceed(authenticatedRequest);
    }
}