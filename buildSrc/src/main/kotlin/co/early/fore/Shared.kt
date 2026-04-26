package co.early.fore

import java.io.File
import java.util.Properties

object Shared {

    object Android {
        const val minSdk = 16
        const val minComposeSdk = 23
        const val compileSdk = 36
        const val targetSdk = 36
    }

    object ExampleAppVersions {
        const val annotation = "1.0.0"
        const val androidxtest = "1.4.0"
        const val androidxjunit = "1.1.2"
        const val espresso_core = "3.5.0-alpha03"
        const val mockk = "1.13.11"
        const val junit = "4.12"
        const val constraintlayout = "2.1.4"
        const val ktor_client = "3.1.1"
    }

    object BuildTypes {
        const val DEBUG = "debug"
        const val RELEASE = "release"
        const val DEFAULT = DEBUG
    }

    object Publish {
        const val LIB_VERSION_NAME = "2.1.0"
        const val LIB_GROUP = "co.early.fore"
        const val PROJ_NAME = "fore"
        const val LIB_DEVELOPER_ID = "erdo"
        const val LIB_DEVELOPER_NAME = "E Donovan"
        const val LIB_DEVELOPER_EMAIL = "eric@early.co"
        const val POM_URL = "https://erdo.github.io/android-fore/"
        const val POM_SCM_URL = "https://github.com/erdo/android-fore"
        const val POM_SCM_CONNECTION = "scm:git@github.com:erdo/android-fore.git"
        const val LICENCE_NAME = "The Apache Software License, Version 2.0"
        const val LICENCE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"

        const val use_published_version = false
    }

    private fun findSecretsFile(relativePaths: String): File {
        // This assumes the secrets are relative to the root of the project
        return File(System.getProperty("user.dir")).resolve(relativePaths)
    }

    object Secrets {

        private val secrets = readProperties(findSecretsFile("../secrets/secrets.properties"))

        val MAVEN_USER = (System.getenv("MAVEN_USER") ?: secrets.getProperty("MAVEN_USER")) ?: "MISSING"
        val MAVEN_PASSWORD = (System.getenv("MAVEN_PASSWORD") ?: secrets.getProperty("MAVEN_PASSWORD")) ?: "MISSING"
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
