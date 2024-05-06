import co.early.fore.Shared

plugins {
    alias(libs.plugins.kotlin)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-network-okhttp3v4x")
    set("LIB_DESCRIPTION", "fore - network code for kotlin okhttp3 v4x")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    compileOnly("com.squareup.okhttp3:okhttp:${Shared.Versions.okhttp3v4}")
}

apply(from = "../../publish-lib.gradle.kts")
