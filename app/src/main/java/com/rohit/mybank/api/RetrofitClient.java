package com.rohit.mybank.api;

import android.content.Context;

import com.rohit.mybank.constants.APIConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ============================================================
 * RETROFIT CLIENT
 * ============================================================
 *
 * Central Retrofit configuration for My Bank.
 *
 * Responsibilities:
 * ------------------------------------------------------------
 * 1. Create Retrofit instance
 * 2. Configure OkHttp
 * 3. Add JWT authentication
 * 4. Add ngrok header
 * 5. Configure timeouts
 * 6. Configure Gson converter
 *
 * ============================================================
 */
public final class RetrofitClient {

    // =========================================================
    // SINGLE RETROFIT INSTANCE
    // =========================================================

    private static volatile Retrofit retrofit;

    // =========================================================
    // PRIVATE CONSTRUCTOR
    // =========================================================

    private RetrofitClient() {
        // Prevent object creation
    }

    // =========================================================
    // GET RETROFIT CLIENT
    // =========================================================

    public static Retrofit getClient(Context context) {

        // -----------------------------------------------------
        // SAFETY CHECK
        // -----------------------------------------------------

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null."
            );
        }

        // -----------------------------------------------------
        // DOUBLE-CHECKED SINGLETON
        // -----------------------------------------------------

        if (retrofit == null) {

            synchronized (RetrofitClient.class) {

                if (retrofit == null) {

                    // =================================================
                    // HTTP LOGGING
                    // =================================================

                    HttpLoggingInterceptor loggingInterceptor =
                            new HttpLoggingInterceptor();

                    /*
                     * BASIC logging only.
                     *
                     * Do NOT use BODY logging because it may expose:
                     *
                     * - JWT
                     * - passwords
                     * - account information
                     * - transaction information
                     * - card information
                     */

                    loggingInterceptor.setLevel(
                            HttpLoggingInterceptor.Level.BASIC
                    );

                    // =================================================
                    // APPLICATION CONTEXT
                    // =================================================

                    Context applicationContext =
                            context.getApplicationContext();

                    // =================================================
                    // OKHTTP CLIENT
                    // =================================================

                    OkHttpClient client =
                            new OkHttpClient.Builder()

                                    // ---------------------------------
                                    // JWT AUTHENTICATION
                                    // ---------------------------------

                                    .addInterceptor(
                                            new AuthInterceptor(
                                                    applicationContext
                                            )
                                    )

                                    // ---------------------------------
                                    // NGROK
                                    // ---------------------------------

                                    .addInterceptor(chain -> {

                                        okhttp3.Request originalRequest =
                                                chain.request();

                                        okhttp3.Request request =
                                                originalRequest
                                                        .newBuilder()
                                                        .header(
                                                                "ngrok-skip-browser-warning",
                                                                "1"
                                                        )
                                                        .build();

                                        return chain.proceed(
                                                request
                                        );
                                    })

                                    // ---------------------------------
                                    // LOGGING
                                    // ---------------------------------

                                    .addInterceptor(
                                            loggingInterceptor
                                    )

                                    // ---------------------------------
                                    // CONNECT TIMEOUT
                                    // ---------------------------------

                                    .connectTimeout(
                                            30,
                                            TimeUnit.SECONDS
                                    )

                                    // ---------------------------------
                                    // READ TIMEOUT
                                    // ---------------------------------

                                    .readTimeout(
                                            30,
                                            TimeUnit.SECONDS
                                    )

                                    // ---------------------------------
                                    // WRITE TIMEOUT
                                    // ---------------------------------

                                    .writeTimeout(
                                            30,
                                            TimeUnit.SECONDS
                                    )

                                    .build();

                    // =================================================
                    // RETROFIT
                    // =================================================

                    retrofit =
                            new Retrofit.Builder()

                                    /*
                                     * IMPORTANT:
                                     *
                                     * BASE_URL must end with "/"
                                     */

                                    .baseUrl(
                                            APIConstants.BASE_URL
                                    )

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