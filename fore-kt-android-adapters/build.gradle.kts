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
    sourceSets["main"].java.apply {
        srcDirs(
            "../fore-jv-android-adapters/src/main/java"
        )
        exclude(
            "co/early/fore/adapters/mutable/ChangeAwareArrayList.java",
            "co/early/fore/adapters/mutable/ChangeAwareLinkedList.java",
            "co/early/fore/adapters/immutable/ImmutableListMgr.java",
            "co/early/fore/adapters/NotifyableImp.java"
        )
    }
}

dependencies {

    api(project(":fore-kt-android-core"))
    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../publish-android-lib.gradle.kts")
