plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-core")
    set("LIB_DESCRIPTION", "fore - android java core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(co.early.fore.Shared.Versions.jvm_toolchain))
    }
}

android {
    namespace = "co.early.fore.core"
}

dependencies {
    api(project(":fore-jv:fore-jv-core"))
}

apply(from = "../../publish-android-lib.gradle.kts")
