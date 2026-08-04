package com.rohit.mybank.constants;

public final class APIConstants {

    private APIConstants() {
        // Prevent instantiation
    }

    // Android Emulator
    public static final String EMULATOR_URL =
            "http://10.0.2.2:8080/";

    // Physical Phone (Same Wi-Fi)
    public static final String PHONE_URL =
            "http://192.168.0.202:8080/";

    // Change only this line when switching devices
    public static final String BASE_URL = PHONE_URL;

}