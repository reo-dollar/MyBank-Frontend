plugins {
    alias(libs.plugins.android.application)
}

android {

    namespace = "com.rohit.mybank"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.rohit.mybank"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            optimization {
                enable = false
            }

        }

    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }

    buildFeatures {

        viewBinding = true

    }

}

dependencies {

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp Logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    // Material Components
    implementation("com.google.android.material:material:1.12.0")
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

// CardView
    implementation("androidx.cardview:cardview:1.0.0")

// Biometric
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

// Android Security Crypto
    implementation("androidx.security:security-crypto:1.0.0")

}