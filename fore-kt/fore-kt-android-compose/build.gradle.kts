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

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {
    defaultConfig {
        minSdk = 21
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Shared.Versions.composeCompiler
    }
    namespace = "co.early.fore.kt.compose"
}

dependencies {
    api(project(":fore-kt:fore-kt-android"))
    implementation("androidx.compose.ui:ui:${Shared.Versions.composeUi}")
    implementation("androidx.window:window:${Shared.Versions.androidWindow}")
}

apply(from = "../../publish-android-compose-lib.gradle.kts")
