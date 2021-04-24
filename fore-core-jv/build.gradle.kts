plugins {
    id("java-library")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core-jv")
    set("LIB_DESCRIPTION", "fore - core code for java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

apply(from = "../publish-lib.gradle")
