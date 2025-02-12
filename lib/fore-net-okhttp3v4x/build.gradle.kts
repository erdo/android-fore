plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-net-okhttp3v4x")
    set("LIB_DESCRIPTION", "fore - network code for okhttp3 v4x")
}

dependencies {
    compileOnly(libs.okhttp3.v4)
}

//apply(from = "../../publish-lib.gradle.kts")
