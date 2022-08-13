import co.early.fore.Shared

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
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
    set("LIB_ARTIFACT_ID", "fore-kt-network-okhttp3v3x")
    set("LIB_DESCRIPTION", "fore - network code for kotlin okhttp3 v3x")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    compileOnly("com.squareup.okhttp3:okhttp:${Shared.Versions.okhttp3v3}")
}

apply(from = "../../publish-lib.gradle.kts")
