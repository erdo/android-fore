import co.early.fore.Shared

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-android-core")
    set("LIB_DESCRIPTION", "fore - android kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {
    namespace = "co.early.fore.kt.core"

    // following was previously in BuildSrc

    compileSdk = Shared.Android.compileSdk

    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {
        minSdk = Shared.Android.minSdk
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(project(":fore-kt:fore-kt-core"))
    api("androidx.lifecycle:lifecycle-runtime-ktx:${Shared.Versions.androidx_lifecycle_common}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")
    //promote the kotlin-reflect version used in the android lint tools to match kotlin_version used elsewhere
    implementation(kotlin("reflect"))
}

apply(from = "../../publish-android-lib.gradle.kts")
