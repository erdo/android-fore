import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-android-core")
    set("LIB_DESCRIPTION", "fore - android kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
    namespace = "co.early.fore.kt.core"
}

dependencies {
    api(project(":fore-kt:fore-kt-core"))
    api("androidx.lifecycle:lifecycle-common-java8:${Shared.Versions.androidx_lifecycle_common}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")
    //promote the kotlin-reflect version used in the android lint tools to match kotlin_version used elsewhere
    implementation(kotlin("reflect"))
}

apply(from = "../../publish-android-lib.gradle.kts")
