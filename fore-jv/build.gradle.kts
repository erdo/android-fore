import co.early.fore.Shared

plugins {
    id("fore-android-plugin")
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

    api(project(":fore-adapters-jv"))
    api(project(":fore-network-jv"))

    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("org.robolectric:robolectric:${Shared.Versions.robolectric}") {
        exclude(module = "maven-artifact")
    }
}

apply(from = "../publish-android-lib.gradle.kts")
