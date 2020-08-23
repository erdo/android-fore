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

    sourceSets["main"].java.srcDirs(
            "../fore-core/src/main/java",
            "../fore-adapters/src/main/java",
            "../fore-lifecycle/src/main/java",
            "../fore-retrofit/src/main/java"
    )
}

dependencies {
    api("androidx.annotation:annotation:${Shared.Versions.annotation}")
    implementation("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
    implementation("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")

    api("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:${Shared.Versions.coordinatorlayout}")
    implementation("androidx.cardview:cardview:${Shared.Versions.cardview}")
}

apply(from = "../bintraypublish.gradle")
