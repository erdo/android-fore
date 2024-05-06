import co.early.fore.Shared

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Shared.Versions.composeCompiler
    }
    namespace = "co.early.fore.kt.compose"

    // following was previously in BuildSrc

    compileSdk = Shared.Android.compileSdk

    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {
        minSdk = 21 // NB higher minimum due to compose
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../../proguard-library-consumer.pro")
        }
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    api(project(":fore-kt:fore-kt-android"))
    implementation("androidx.compose.ui:ui:${Shared.Versions.composeUi}")
    implementation("androidx.window:window:${Shared.Versions.androidWindow}")
}

apply(from = "../../publish-android-lib.gradle.kts")
