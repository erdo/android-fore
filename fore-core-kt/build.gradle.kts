import co.early.fore.Config_gradle.Shared

plugins {
    id("com.android.library")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core-kt")
    set("LIB_DESCRIPTION", "android fore - a few kotlin additions esp around coroutines")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdkVersion(Shared.Android.compileSdkVersion)

    lintOptions {
        isAbortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {

        minSdkVersion(Shared.Android.minSdkVersion)
        targetSdkVersion(Shared.Android.targetSdkVersion)

        versionCode = Shared.Publish.LIB_VERSION_CODE
        versionName = Shared.Publish.LIB_VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer.pro")
        }
    }
}

dependencies {

    //implementation("co.early.fore:fore-core:${Shared.Versions.fore_version_for_examples}")
    api(project(":fore-core"))

    api("androidx.core:core-ktx:${Shared.Versions.core_ktx}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Shared.Versions.kotlin_version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")
}

apply(from = "../bintraypublish.gradle")
