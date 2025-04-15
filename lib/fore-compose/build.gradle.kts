import co.early.fore.Shared
import co.early.fore.applyPublishingConfig
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.composeCompilerPlugin)
    alias(libs.plugins.dokkaPlugin)
    id("maven-publish")
    id("signing")
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }

    targets.withType<org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget> {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvm.target.get()))
                }
            }
        }
    }

    androidTarget{
        publishLibraryVariants("release")
    }

    applyDefaultHierarchyTemplate()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":lib:fore-core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                api(project(":lib:fore-test-fixtures"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.window)
                implementation(libs.androidx.lifecycle)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.ui)
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
    set("LIB_ARTIFACT_ID", "fore-compose")
    set("LIB_DESCRIPTION", "fore - compose code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")


val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

applyPublishingConfig()
