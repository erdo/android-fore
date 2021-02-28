import co.early.fore.Shared

plugins {
    id("fore-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core")
    set("LIB_DESCRIPTION", "android fore - core")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

apply(from = "../publish.gradle")
