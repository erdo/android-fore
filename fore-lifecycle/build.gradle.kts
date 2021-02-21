import co.early.fore.Shared

plugins {
    id("fore-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-lifecycle")
    set("LIB_DESCRIPTION", "android fore - lifecycle helpers")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {

    api(project(":fore-core"))

    api("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    compileOnly("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")
    compileOnly("androidx.coordinatorlayout:coordinatorlayout:${Shared.Versions.coordinatorlayout}")
    compileOnly("androidx.cardview:cardview:${Shared.Versions.cardview}")
}

apply(from = "../publish.gradle")
