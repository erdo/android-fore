import co.early.fore.Shared
import co.early.fore.applyPublishingConfig
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.composePlugin)
    alias(libs.plugins.dokkaPlugin)
    id("maven-publish")
    id("signing")
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(project(":lib:fore-core"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.compose)
                implementation(libs.androidx.window)
                implementation(libs.androidx.lifecycle)
            }
        }
    }
}

android {

    namespace = "co.early.fore.compose"

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

    buildFeatures {
        buildConfig = false
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            consumerProguardFiles("../../proguard-library-consumer.pro")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-compose-android")
    set("LIB_DESCRIPTION", "fore - compose android")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")


val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

applyPublishingConfig()
