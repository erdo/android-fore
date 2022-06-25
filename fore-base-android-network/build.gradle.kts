import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-base-android-network")
    set("LIB_DESCRIPTION", "fore - android base networking helpers")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    namespace = "co.early.fore.net"
}

dependencies {
    api(project(":fore-base-network"))
    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
}

apply(from = "../publish-android-lib.gradle.kts")
