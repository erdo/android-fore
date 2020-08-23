import co.early.fore.Shared

plugins {
    id("fore-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-core")
    set("LIB_DESCRIPTION", "android fore - core")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {

    api("androidx.annotation:annotation:${Shared.Versions.annotation}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("org.robolectric:robolectric:${Shared.Versions.robolectric}") {
        exclude(module = "maven-artifact")
    }
}

apply(from = "../bintraypublish.gradle")
