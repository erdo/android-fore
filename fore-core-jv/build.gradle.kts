import co.early.fore.Shared

plugins {
    id("java-library")
}

java {
    sourceCompatibility = Shared.Android.javaVersion
    targetCompatibility = Shared.Android.javaVersion
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core-jv")
    set("LIB_DESCRIPTION", "fore - core code for java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

apply(from = "../publish-lib.gradle.kts")
