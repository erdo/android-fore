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

dependencies {

    //implementation("co.early.fore:fore-core:${Shared.Versions.fore_version_for_examples}")
    api(project(":fore-core"))

    api("androidx.core:core-ktx:${Shared.Versions.core_ktx}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Shared.Versions.kotlin_version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")
}

apply(from = "../bintraypublish.gradle")
