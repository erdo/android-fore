import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-android-network")
    set("LIB_DESCRIPTION", "fore - network helpers, kotlin")
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
                "../fore-jv-android-network/src/main/java"
        )
        exclude(
                "co/early/fore/net/apollo/CallProcessorApollo.java",
                "co/early/fore/net/apollo/ApolloCaller.java",
                "co/early/fore/net/retrofit2/CallProcessorRetrofit2.java",
                "co/early/fore/net/retrofit2/Retrofit2Caller.java"
        )
    }
}

dependencies {

    api(project(":fore-kt-network"))
    api(project(":fore-kt-android-core"))
    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
}

apply(from = "../publish-android-lib.gradle.kts")
