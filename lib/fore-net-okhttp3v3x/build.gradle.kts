plugins {
    alias(libs.plugins.kotlinJvmPlugin)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-net-okhttp3v3x")
    set("LIB_DESCRIPTION", "fore - network code for okhttp3 v3x")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    compileOnly(libs.okhttp3.v3)
}

//apply(from = "../../publish-lib.gradle.kts")
