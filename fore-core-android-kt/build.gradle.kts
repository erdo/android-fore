import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core-android-kt")
    set("LIB_DESCRIPTION", "fore - android kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {

    api(project(":fore-core-kt"))

    api("androidx.lifecycle:lifecycle-common-java8:${Shared.Versions.androidx_lifecycle_common}")
    //promote the kotlin-reflect version used in the android lint tools to match kotlin_version used elsewhere
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Shared.Versions.kotlin_version}")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")
}

apply(from = "../publish-android-lib.gradle")
