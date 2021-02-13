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

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("com.google.code.gson:gson:${Shared.Versions.gson}")

    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}

apply(from = "../publish.gradle")
