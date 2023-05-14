import co.early.fore.Shared

plugins {
    id("kotlin")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-core")
    set("LIB_DESCRIPTION", "fore - kotlin core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("io.mockk:mockk:${Shared.Versions.mockk}")
}

apply(from = "../../publish-lib.gradle.kts")
