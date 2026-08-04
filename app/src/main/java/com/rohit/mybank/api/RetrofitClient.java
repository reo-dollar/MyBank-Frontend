package com.rohit.mybank.api;

import android.content.Context;

import com.rohit.mybank.constants.APIConstants;

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

                    HttpLoggingInterceptor loggingInterceptor =
                            new HttpLoggingInterceptor();

                    loggingInterceptor.setLevel(
                            HttpLoggingInterceptor.Level.BODY
                    );

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(context))
                            .addInterceptor(loggingInterceptor)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(APIConstants.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }

        return retrofit;
    }
}