import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-base-android-adapters")
    set("LIB_DESCRIPTION", "fore - android adapter and diff util helpers, base")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    namespace = "co.early.fore.adapters"
}

dependencies {
    api(project(":fore-base-core"))
    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../publish-android-lib.gradle.kts")
