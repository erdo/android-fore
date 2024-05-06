import co.early.fore.Shared

plugins {
    alias(libs.plugins.androidLibrary)
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-jv-android-network")
    set("LIB_DESCRIPTION", "fore - android network helpers, java")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {

    namespace = "co.early.fore.net"

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
    api(project(":fore-jv:fore-jv-network"))
    api(project(":fore-jv:fore-jv-android-core"))
    compileOnly("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")
    compileOnly("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
}

apply(from = "../../publish-android-lib.gradle.kts")
