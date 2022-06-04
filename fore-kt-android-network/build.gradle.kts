import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-android-network")
    set("LIB_DESCRIPTION", "fore - network helpers, kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer-network.pro")
        }
    }
    namespace = "co.early.fore.kt.net"
}

dependencies {
    api(project(":fore-kt-android-core"))
    api(project(":fore-kt-network"))
    api(project(":fore-base-android-network"))
    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
}

apply(from = "../publish-android-lib.gradle.kts")
