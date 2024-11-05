plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // Google services plugin for Firebase
}

android {
    namespace = "com.example.dailybite"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dailybite"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Firebase BOM - automatically manages versions for all Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // Firebase and Google Sign-In dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth")

    // Other dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Apply the Google services plugin
apply(plugin = "com.google.gms.google-services")
