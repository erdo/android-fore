import co.early.fore.Shared

plugins {
    id("fore-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-network")
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
}

dependencies {

    api(project(":fore-core"))

    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("com.google.code.gson:gson:${Shared.Versions.gson}")

    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}

apply(from = "../bintraypublish.gradle")