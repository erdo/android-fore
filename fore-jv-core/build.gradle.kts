import co.early.fore.Shared

plugins {
    id("java-library")
}

java {
    sourceCompatibility = Shared.Android.javaVersion
    targetCompatibility = Shared.Android.javaVersion
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-core")
    set("LIB_DESCRIPTION", "fore - java core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

apply(from = "../publish-lib.gradle.kts")
