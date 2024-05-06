import co.early.fore.Shared

plugins {
    alias(libs.plugins.androidLibrary)
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-core")
    set("LIB_DESCRIPTION", "fore - android java core code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(co.early.fore.Shared.Versions.jvm_toolchain))
    }
}

android {
    namespace = "co.early.fore.core"

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
    api(project(":fore-jv:fore-jv-core"))
}

apply(from = "../../publish-android-lib.gradle.kts")
