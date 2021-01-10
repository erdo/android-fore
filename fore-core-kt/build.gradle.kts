import co.early.fore.Shared

plugins {
    id("fore-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core-kt")
    set("LIB_DESCRIPTION", "android fore - a few kotlin additions esp around coroutines")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    sourceSets["main"].java.apply {
        srcDirs(
            "../fore-core/src/main/java"
        )
        exclude(
            "co/early/fore/core/logging/**",
            "co/early/fore/core/observer/ObservableImp.java"
        )
    }
}

dependencies {

    //promote the kotlin-reflect version used in the android lint tools to match kotlin_version used elsewhere
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Shared.Versions.kotlin_version}")

    api("androidx.core:core-ktx:${Shared.Versions.core_ktx}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Shared.Versions.kotlin_version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")
}

apply(from = "../bintraypublish.gradle")
