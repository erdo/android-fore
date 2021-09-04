plugins {
    id("fore-android-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-core")
    set("LIB_DESCRIPTION", "fore - android java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")
dependencies {
    api(project(":fore-jv-core"))
}

apply(from = "../publish-android-lib.gradle.kts")
