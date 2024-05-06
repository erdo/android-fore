import co.early.fore.Shared

plugins {
    alias(libs.plugins.androidLibrary)
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-adapters")
    set("LIB_DESCRIPTION", "fore - android adapter and diff util helpers, java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {
    namespace = "co.early.fore.adapters"

    // following was previously in BuildSrc

    compileSdk = Shared.Android.compileSdk

    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {
        minSdk = Shared.Android.minSdk
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../../proguard-library-consumer.pro")
        }
    }

    buildFeatures {
        buildConfig = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(project(":fore-jv:fore-jv-android-core"))
    compileOnly("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
}

apply(from = "../../publish-android-lib.gradle.kts")
