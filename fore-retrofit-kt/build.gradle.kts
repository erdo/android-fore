import co.early.fore.Shared

plugins {
    id("fore-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-retrofit-kt")
    set("LIB_DESCRIPTION", "android fore - retrofit coroutine helpers")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {

    //implementation("co.early.fore:fore-core-kt:${Shared.Versions.fore_version_for_examples}")
    //implementation("co.early.fore:fore-retrofit:${Shared.Versions.fore_version_for_examples}")
    api(project(":fore-core-kt"))
    api(project(":fore-retrofit"))

    api("io.arrow-kt:arrow-core-data:${Shared.Versions.arrow_core}")
}


apply(from = "../bintraypublish.gradle")
