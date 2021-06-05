import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-adapters-kt")
    set("LIB_DESCRIPTION", "fore - android adapter and diff util helpers, kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    sourceSets["main"].java.apply {
        srcDirs(
            "../fore-adapters-jv/src/main/java"
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

    api(project(":fore-core-android-kt"))

    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../publish-android-lib.gradle.kts")
