plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core-android-jv")
    set("LIB_DESCRIPTION", "fore - android java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    api(project(":fore-core-jv"))
}

apply(from = "../publish-android-lib.gradle.kts")
