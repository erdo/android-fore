import co.early.fore.Shared
import co.early.fore.applyPublishingConfig
import org.gradle.jvm.tasks.Jar

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

    androidTarget{
        publishLibraryVariants("release")
    }

    jvm()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":lib:fore-core"))
                implementation(libs.ktor.client.core)
                implementation(libs.okio)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.io)

                // compileOnly(libs.apollo3-v3)
               // compileOnly(libs.apollo4)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
              //  implementation(libs.okio)
            }
        }

//        androidMain.dependencies {
//            implementation(libs.ktor.client.okhttp)
//            implementation(libs.kotlinx.coroutines.android)
//        }
//
//        iosMain.dependencies {
//            implementation(libs.ktor.client.darwin)
//        }

//        val jvmMain by getting {
//            dependencies {
//                //     implementation(libs.okhttp3.v5)
//                //     implementation(libs.apollo4)
//                compileOnly(libs.okhttp3.v4)
//                // OkHttp3 v3.X.X used by Retrofit2 and Apollo2 has method calls: method(), body(), code() etc
//                // OkHttp3 v4.X.X used by Ktor and Apollo3 has fields: method, body, code etc instead
//                // we use reflection so that we can handle either case in InterceptorLogging.kt
//                implementation(libs.kotlin.reflect)
//                api(project(":lib:fore-net-okhttp3v3x"))
//                api(project(":lib:fore-net-okhttp3v4x"))
//            }
//        }
    }
}

android {

    namespace = "co.early.fore.net"

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
    set("LIB_ARTIFACT_ID", "fore-net")
    set("LIB_DESCRIPTION", "fore - network code")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")


val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

applyPublishingConfig()
