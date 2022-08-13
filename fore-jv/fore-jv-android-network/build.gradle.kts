import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-network")
    set("LIB_DESCRIPTION", "fore - android network helpers, java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer-network.pro")
        }
    }
    namespace = "co.early.fore.net"
}

dependencies {
    api(project(":fore-jv:fore-jv-network"))
    api(project(":fore-jv:fore-jv-android-core"))
    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
}

apply(from = "../../publish-android-lib.gradle.kts")
