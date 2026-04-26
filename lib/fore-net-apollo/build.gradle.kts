import co.early.fore.Shared
import co.early.fore.applyPublishingConfig
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.dokkaPlugin)
    id("maven-publish")
    id("signing")
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

    jvm()

    // Tier 1
    macosArm64()
    iosSimulatorArm64()
    iosArm64()

    // Tier 2
    watchosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosArm64()

    // Tier 3
    iosX64()
    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":lib:fore-net"))
                compileOnly(libs.apollo.runtime)
                api(libs.apollo.runtime) // remove the api line if KMP ever supports compileOnly
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {

    namespace = "co.early.fore.net.apollo"

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
    set("LIB_ARTIFACT_ID", "fore-net-apollo")
    set("LIB_DESCRIPTION", "fore - network code, apollo call wrapper")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")


val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaGenerateHtml"))
}

applyPublishingConfig()
