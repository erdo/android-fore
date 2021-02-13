import co.early.fore.Shared

plugins {
    id("fore-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt")
    set("LIB_DESCRIPTION", "android fore - everything in one aar for kotlin development")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer-network.pro")
        }
    }
    sourceSets["main"].java.apply {
        srcDirs(
                "../fore-lifecycle/src/main/java"
        )
    }
}

dependencies {

    api(project(":fore-core-kt"))
    api(project(":fore-adapters-kt"))
    api(project(":fore-network-kt"))

    //for the fore-lifecycle package
    api("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    compileOnly("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")
    compileOnly("androidx.coordinatorlayout:coordinatorlayout:${Shared.Versions.coordinatorlayout}")
    compileOnly("androidx.cardview:cardview:${Shared.Versions.cardview}")
}

apply(from = "../publish.gradle")
