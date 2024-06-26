import co.early.fore.Shared

plugins {
    alias(libs.plugins.androidLibrary)
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android")
    set("LIB_DESCRIPTION", "android fore - everything in one aar for java android development")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {

    namespace = "co.early.fore"

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
            consumerProguardFiles("../../proguard-library-consumer-network.pro") // NB different consumer file
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

    api(project(":fore-jv:fore-jv-android-adapters"))
    api(project(":fore-jv:fore-jv-android-network"))

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("org.robolectric:robolectric:${Shared.Versions.robolectric}") {
        exclude(module = "maven-artifact")
    }
}

apply(from = "../../publish-android-lib.gradle.kts")
