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

    sourceSets["main"].java.srcDirs(
            "../fore-core/src/main/java",
            "../fore-adapters/src/main/java",
            "../fore-lifecycle/src/main/java",
            "../fore-retrofit/src/main/java"
    )

    sourceSets["main"].java.exclude(
            "co/early/fore/core/logging/**",
            "co/early/fore/core/observer/ObservableImp.java",
            "co/early/fore/retrofit/InterceptorLogging.java",
            "co/early/fore/retrofit/CallProcessor.java"
    )
}

dependencies {
    api("androidx.annotation:annotation:${Shared.Versions.annotation}")
    implementation("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
    implementation("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
    api("io.arrow-kt:arrow-core:${Shared.Versions.arrow_core}")

    api("androidx.core:core-ktx:${Shared.Versions.core_ktx}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Shared.Versions.kotlin_version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Shared.Versions.kotlinx_coroutines_core}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Shared.Versions.kotlinx_coroutines_android}")

    api("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:${Shared.Versions.coordinatorlayout}")
    implementation("androidx.cardview:cardview:${Shared.Versions.cardview}")
}

apply(from = "../bintraypublish.gradle")
