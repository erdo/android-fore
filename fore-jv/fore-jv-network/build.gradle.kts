import co.early.fore.Shared

plugins {
    id("java-library")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-network")
    set("LIB_DESCRIPTION", "fore - network code for java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

dependencies {
    api(project(":fore-jv:fore-jv-core"))
    compileOnly("com.squareup.okhttp3:okhttp:${Shared.Versions.okhttp3v3}")
}

apply(from = "../../publish-lib.gradle.kts")
