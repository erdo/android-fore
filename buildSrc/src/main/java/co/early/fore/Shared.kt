package co.early.fore

import org.gradle.api.JavaVersion
import java.io.File
import java.util.*

object Shared {

    object Android {
        const val minSdk = 16
        const val minComposeSdk = 21
        const val compileSdk = 34
        const val targetSdk = 34
    }

    object Versions {
        // fore core package dependencies
        const val android_gradle_plugin = "8.3.2" // must manually change buildSrc.build version
        const val kotlin_version = "1.9.22" // must manually change buildSrc.build version
        const val kotlinx_coroutines_core = "1.8.0"
        const val jvm_toolchain = 8
        // fore optional package dependencies
        const val kotlinx_coroutines_android = "1.8.0"
        const val androidx_lifecycle_common = "2.6.1"
        const val recyclerview = "1.3.2"
        const val okhttp3v3 = "3.14.9"
        const val okhttp3v4 = "4.12.0"
        const val okhttp3v5 = "5.0.0-alpha.12"
        const val apollo = "2.5.14"
        const val apollo3 = "3.8.3"
        const val apollo3v4 = "4.0.0-beta.5"
        const val retrofit = "2.11.0"
        const val composeCompiler = "1.5.10" // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        const val composeUi = "1.6.4"
        const val androidWindow = "1.2.0"
        // example app and test dependencies
        const val activityCompose = "1.8.2"
        const val composeBom = "2024.03.00"
        const val android_core = "1.8.0"
        const val annotation = "1.0.0"
        const val material = "1.7.0"
        const val appcompat = "1.5.1"
        const val coil = "1.1.0"
        const val perSista = "1.4.0"
        const val androidxtest = "1.4.0"
        const val androidxjunit = "1.1.2"
        const val room_version = "2.4.3"
        const val espresso_core = "3.5.0-alpha03"
        const val butterknife = "10.2.0"
        const val mockito_core = "2.23.0"
        const val mockk = "1.11.0"
        const val junit = "4.12"
        const val espresso = "3.0.2"
        const val hamcrest_library = "1.3"
        const val dexmaker_mockito = "2.28.1"
        const val robolectric = "4.9"
        const val gson = "2.8.5"
        const val constraintlayout = "2.1.4"
        const val ktor_client = "2.3.9"
        const val converter_gson = "2.6.0"
        const val kotlinxSerializationJson = "1.5.1"
    }

    object BuildTypes {
        const val DEBUG = "debug"
        const val RELEASE = "release"
        const val DEFAULT = DEBUG
    }

    object Publish {
        const val LIB_VERSION_NAME = "1.6.5" //"x.x.x-SNAPSHOT"
        const val LIB_VERSION_CODE = 99
        const val LIB_GROUP = "co.early.fore"
        const val PROJ_NAME = "fore"
        const val LIB_DEVELOPER_ID = "erdo"
        const val LIB_DEVELOPER_NAME = "E Donovan"
        const val LIB_DEVELOPER_EMAIL = "eric@early.co"
        const val POM_URL = "https://erdo.github.io/android-fore/"
        const val POM_SCM_URL = "https://github.com/erdo/android-fore"
        const val POM_SCM_CONNECTION = "scm:git@github.com:erdo/android-fore.git"
        const val LICENCE_SHORT_NAME = "Apache-2.0"
        const val LICENCE_NAME = "The Apache Software License, Version 2.0"
        const val LICENCE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"

        const val use_published_version = false
        const val published_fore_version_for_examples = "1.6.3"
    }

    object Secrets {

        private val secrets = readProperties(File("../secrets/secrets.properties"))

        val MAVEN_USER = (System.getenv("MAVEN_USER") ?: secrets.getProperty("MAVEN_USER")) ?: "MISSING"
        val MAVEN_PASSWORD = (System.getenv("MAVEN_PASSWORD") ?: secrets.getProperty("MAVEN_PASSWORD")) ?: "MISSING"
        val SONATYPE_STAGING_PROFILE_ID = (System.getenv("SONATYPE_STAGING_PROFILE_ID") ?: secrets.getProperty("SONATYPE_STAGING_PROFILE_ID")) ?: "MISSING"
        val SIGNING_KEY_ID = (System.getenv("SIGNING_KEY_ID") ?: secrets.getProperty("SIGNING_KEY_ID")) ?: "MISSING"
        val SIGNING_PASSWORD = (System.getenv("SIGNING_PASSWORD") ?: secrets.getProperty("SIGNING_PASSWORD")) ?: "MISSING"
        val SIGNING_KEY_RING_FILE = (System.getenv("SIGNING_KEY_RING_FILE") ?: secrets.getProperty("SIGNING_KEY_RING_FILE")) ?: "MISSING"
    }
}

fun readProperties(propertiesFile: File): Properties {
    return Properties().apply {
        try {
            propertiesFile.inputStream().use { fis ->
                load(fis)
            }
            println("[SECRETS LOADED]")
        } catch (exception: Exception) {
            println("WARNING $propertiesFile not found! \n")
            println("exception: $exception \n")
        }
    }
}
