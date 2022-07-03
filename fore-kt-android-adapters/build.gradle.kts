import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-android-adapters")
    set("LIB_DESCRIPTION", "fore - android adapter and diff util helpers, kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {

    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }

    namespace = "co.early.fore.kt.adapters"
}

dependencies {
    api(project(":fore-kt-android-core"))
    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../publish-android-lib.gradle.kts")
