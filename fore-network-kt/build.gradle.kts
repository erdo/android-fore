import co.early.fore.Shared

plugins {
    id("fore-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-network-kt")
    set("LIB_DESCRIPTION", "android fore - network helpers")
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
                "../fore-network/src/main/java"
        )
        exclude(
                "co/early/fore/net/InterceptorLogging.java",
                "co/early/fore/net/apollo/CallProcessorApollo.java",
                "co/early/fore/net/retrofit2/CallProcessorRetrofit2.java"
        )
    }
}

dependencies {

    api(project(":fore-core-kt"))

    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")

    implementation("com.squareup.okhttp3:okhttp:${Shared.Versions.okhttp}")
}

apply(from = "../publish.gradle")
