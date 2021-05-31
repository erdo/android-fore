import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-adapters-jv")
    set("LIB_DESCRIPTION", "fore - android adapter and diff util helpers, java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {

    api(project(":fore-core-android-jv"))

    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../publish-android-lib.gradle.kts")
