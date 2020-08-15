import co.early.fore.Config_gradle.Shared

plugins {
    id("com.android.library")
    kotlin("android")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-adapters-kt")
    set("LIB_DESCRIPTION", "android fore - adapter and diff util helpers")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

android {

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdkVersion(Shared.Android.compileSdkVersion)

    lintOptions {
        isAbortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {

        minSdkVersion(Shared.Android.minSdkVersion)
        targetSdkVersion(Shared.Android.targetSdkVersion)

        versionCode = Shared.Publish.LIB_VERSION_CODE
        versionName = Shared.Publish.LIB_VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-library-consumer.pro")
        }
    }
}

dependencies {

    //api("co.early.fore:fore-core:${Shared.Versions.fore_version_for_examples}")
    //api("co.early.fore:fore-core-kt:${Shared.Versions.fore_version_for_examples}")
    //api("co.early.fore:fore-adapters:${Shared.Versions.fore_version_for_examples}")

    api(project(":fore-core"))
    api(project(":fore-core-kt"))
    api(project(":fore-adapters"))

    api("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
    api("androidx.core:core-ktx:${Shared.Versions.core_ktx}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Shared.Versions.kotlin_version}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")

    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}

apply(from = "../bintraypublish.gradle")
