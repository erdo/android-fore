import co.early.fore.Shared

plugins {
    id("fore-plugin")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-adapters")
    set("LIB_DESCRIPTION", "android fore - adapter helpers")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {

    api(project(":fore-core"))

    api("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")

    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}

apply(from = "../bintraypublish.gradle")
