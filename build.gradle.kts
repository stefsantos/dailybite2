plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("com.google.gms:google-services:4.3.15") // Firebase services plugin
    }
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
