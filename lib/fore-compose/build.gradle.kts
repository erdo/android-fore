import co.early.fore.Shared
import co.early.fore.applyPublishingConfig
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.composeCompilerPlugin)
    alias(libs.plugins.dokkaPlugin)
    id("maven-publish")
    id("signing")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get()))
    }
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }

    compilerOptions {
        languageVersion.set(KotlinVersion.fromVersion(libs.versions.kotlin.floor.get()))
        apiVersion.set(KotlinVersion.fromVersion(libs.versions.kotlin.floor.get()))
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

    iosSimulatorArm64()
    iosArm64()
    iosX64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":lib:fore-core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(project(":lib:fore-test-fixtures"))
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

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.jvm.target.get().replace(".", "_")}")
        targetCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.jvm.target.get().replace(".", "_")}")
    }

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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (name.contains("android", ignoreCase = true)) {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvm.target.get()))
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
    from(tasks.named("dokkaGenerateHtml"))
}

applyPublishingConfig()
