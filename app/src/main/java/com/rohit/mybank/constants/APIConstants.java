package com.rohit.mybank.constants;

public final class APIConstants {

    private APIConstants() {
        // Prevent instantiation
    }

    // Android Emulator - local PC
    public static final String EMULATOR_URL =
            "http://10.0.2.2:8080/";

    // Physical Phone - same Wi-Fi as PC
    public static final String PHONE_URL =
            "http://192.168.0.202:8080/";

    // ngrok - accessible from different networks
    public static final String NGROK_URL =
            "https://trio-jacket-frostily.ngrok-free.dev/";

    // Current server
    public static final String BASE_URL = NGROK_URL;
}