import co.early.fore.Config_gradle.Shared

plugins {
    id("com.android.library")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core")
    set("LIB_DESCRIPTION", "android fore - core")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdkVersion(Shared.Android.compileSdkVersion)

    lintOptions {
        isAbortOnError = false
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

    api("androidx.annotation:annotation:${Shared.Versions.annotation}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("org.robolectric:robolectric:${Shared.Versions.robolectric}") {
        exclude(module = "maven-artifact")
    }
}

apply(from = "../bintraypublish.gradle")
