import co.early.fore.Config_gradle.Shared

plugins {
    id("com.android.library")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-retrofit-kt")
    set("LIB_DESCRIPTION", "android fore - retrofit coroutine helpers")
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
            proguardFiles("proguard-library-consumer.pro")
        }
    }
}

dependencies {

    //implementation("co.early.fore:fore-core-kt:${Shared.Versions.fore_version_for_examples}")
    //implementation("co.early.fore:fore-retrofit:${Shared.Versions.fore_version_for_examples}")
    api(project(":fore-core"))
    api(project(":fore-core-kt"))
    api(project(":fore-retrofit"))

    api("io.arrow-kt:arrow-core-data:${Shared.Versions.arrow_core}")
}


apply(from = "../bintraypublish.gradle")
