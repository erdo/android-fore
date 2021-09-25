import co.early.fore.Shared

plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = Shared.Android.javaVersion
    targetCompatibility = Shared.Android.javaVersion
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-core")
    set("LIB_DESCRIPTION", "fore - kotlin core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

sourceSets["main"].java.apply {
    srcDirs(
        "../fore-jv-core/src/main/java"
    )
    exclude(
        "co/early/fore/core/logging/**",
        "co/early/fore/core/observer/ObservableGroupImp.java",
        "co/early/fore/core/ui/SyncTrigger.java"
    )
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Shared.Versions.kotlin_version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")
}

apply(from = "../publish-lib.gradle.kts")
