import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-android-compose")
    set("LIB_DESCRIPTION", "fore - android kotlin compose")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
    defaultConfig {
        minSdk = 21
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Shared.Versions.compose
    }
    namespace = "co.early.fore.kt.compose"
}

dependencies {
    api(project(":fore-kt-android"))
    implementation("androidx.compose.ui:ui:${Shared.Versions.compose}")
}

//apply(from = "../publish-android-compose-lib.gradle.kts")
