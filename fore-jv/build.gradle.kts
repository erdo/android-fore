import co.early.fore.Shared

plugins {
    id("fore-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv")
    set("LIB_DESCRIPTION", "android fore - everything in one aar for java development")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer-network.pro")
        }
    }
}

dependencies {
    api(project(":fore-core"))
    api(project(":fore-adapters"))
    api(project(":fore-network"))
    api(project(":fore-lifecycle"))
}

apply(from = "../bintraypublish.gradle")
