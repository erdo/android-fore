import co.early.fore.Shared

plugins {
    id("java-library")
}

java {
    sourceCompatibility = Shared.Android.javaVersion
    targetCompatibility = Shared.Android.javaVersion
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-base-network")
    set("LIB_DESCRIPTION", "fore - base network code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    api(project(":fore-jv-core"))
    compileOnly("com.squareup.okhttp3:okhttp:3.14.9")
}

apply(from = "../publish-lib.gradle.kts")
