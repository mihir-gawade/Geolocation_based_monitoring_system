plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.app006"
    compileSdk = 35 // Updated to API 35

    defaultConfig {
        applicationId = "com.example.app006"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Updated to Java 17 for better performance
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // AndroidX and UI components
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase Dependencies
    implementation(libs.firebase.firestore)  // Firestore Database
    implementation(libs.firebase.auth)      // Firebase Authentication
// Firebase Cloud Messaging (Optional)

    // Google Play Services
    implementation(libs.play.services.location)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
