import co.early.fore.Shared

plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = Shared.Android.javaVersion
    targetCompatibility = Shared.Android.javaVersion
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-core")
    set("LIB_DESCRIPTION", "fore - kotlin core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    api(project(":fore-base-core"))
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Shared.Versions.kotlin_version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")
}

apply(from = "../publish-lib.gradle.kts")
