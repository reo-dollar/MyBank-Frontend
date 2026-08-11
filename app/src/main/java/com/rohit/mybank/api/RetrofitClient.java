package com.rohit.mybank.api;

import android.content.Context;

import com.rohit.mybank.constants.APIConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static volatile Retrofit retrofit;

    private RetrofitClient() {
        // Prevent instantiation
    }

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            synchronized (RetrofitClient.class) {

                if (retrofit == null) {

                    /*
                     * Logging
                     *
                     * Use BASIC instead of BODY.
                     *
                     * BODY logging can expose:
                     * - passwords
                     * - JWT access tokens
                     * - refresh tokens
                     * - account information
                     * - transaction information
                     */
                    HttpLoggingInterceptor loggingInterceptor =
                            new HttpLoggingInterceptor();

                    loggingInterceptor.setLevel(
                            HttpLoggingInterceptor.Level.BASIC
                    );

                    /*
                     * OkHttp Client
                     */
                    OkHttpClient client =
                            new OkHttpClient.Builder()

                                    /*
                                     * Add JWT Authorization header
                                     */
                                    .addInterceptor(
                                            new AuthInterceptor(context)
                                    )

                                    /*
                                     * ngrok Free Browser Warning
                                     *
                                     * This allows programmatic API requests
                                     * from Retrofit to bypass ngrok's
                                     * browser warning/interstitial.
                                     */
                                    .addInterceptor(chain -> {

                                        okhttp3.Request originalRequest =
                                                chain.request();

                                        okhttp3.Request request =
                                                originalRequest.newBuilder()
                                                        .header(
                                                                "ngrok-skip-browser-warning",
                                                                "1"
                                                        )
                                                        .build();

                                        return chain.proceed(request);
                                    })

                                    /*
                                     * HTTP logging
                                     */
                                    .addInterceptor(
                                            loggingInterceptor
                                    )

                                    /*
                                     * Connection timeout
                                     */
                                    .connectTimeout(
                                            30,
                                            TimeUnit.SECONDS
                                    )

                                    /*
                                     * Server response timeout
                                     */
                                    .readTimeout(
                                            30,
                                            TimeUnit.SECONDS
                                    )

                                    /*
                                     * Request upload timeout
                                     */
                                    .writeTimeout(
                                            30,
                                            TimeUnit.SECONDS
                                    )

                                    .build();

                    /*
                     * Retrofit
                     */
                    retrofit =
                            new Retrofit.Builder()
                                    .baseUrl(APIConstants.BASE_URL)
                                    .client(client)
                                    .addConverterFactory(
                                            GsonConverterFactory.create()
                                    )
                                    .build();
                }
            }
        }

        return retrofit;
    }
}