import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-adapters")
    set("LIB_DESCRIPTION", "fore - android adapter and diff util helpers, java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {
    namespace = "co.early.fore.adapters"
}

dependencies {
    api(project(":fore-jv:fore-jv-android-core"))
    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../../publish-android-lib.gradle.kts")
