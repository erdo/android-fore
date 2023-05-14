import co.early.fore.Shared

plugins {
    id("java-library")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-core")
    set("LIB_DESCRIPTION", "fore - java core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

apply(from = "../../publish-lib.gradle.kts")
